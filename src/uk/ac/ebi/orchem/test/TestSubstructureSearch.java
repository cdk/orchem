/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  OrChem project
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *
 */
package uk.ac.ebi.orchem.test;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import oracle.jdbc.driver.OracleConnection;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.SubgraphIsomorphism;

import uk.ac.ebi.orchem.Constants;
import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.shared.DatabaseAccess;
import uk.ac.ebi.orchem.shared.WrappedAtomContainer;


/**
 * Junit test for OrChem substructure searching<P>
 *
 * Contains a long list of test methods, each for a "query compound" with the
 * number in the method is the database id (primary key) of that compound in
 * table ORCHEM_COMPOUND_SAMPLE.<P>
 * You can add more query compounds to this table by adding them to
 * the queries mol file in the unit test directory. They will get loaded by {@link LoadCompounds}.<P>
 * Target compounds are loaded from a second file with an id offset of 1000.<P>
 * Tests are done with query compounds against the target compounds <B>plus</B> the query compounds, to
 * test if the substructure search at least establishes that a query compound is a substructure of itself.
 *
 */

public class TestSubstructureSearch extends TestCase {

    private DatabaseAccess dbApi = new DatabaseAccess();

    static OracleConnection conn;
    static List<WrappedAtomContainer> targetMolecules;
    /* connect and suk all the unittest compounds into a working list (for performance)*/
    static {
        try {
            System.out.println("___ static : Begin set up target list (once) ");
            Properties properties = Constants.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = (OracleConnection)DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
            targetMolecules = new DatabaseAccess().getAllCompounds(conn);
            System.out.println("___ static : End set up target list");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setUp() throws SQLException, CDKException {
    }

    public void tearDown() throws SQLException {
    }

    /**
     * Tests how a substructure search using a pre-filter plus fingerprint relates to
     * a full non filtered search. These two should give exactly the same result - if 
     * not, then the fingerprinter is incorrectly filtering (false negatives)
     * 
     * @param dbId database id of query molecule in database test set
     * @throws Exception
     */
    private void fingerprintVersusFullScan(int dbId) throws Exception {

        PreparedStatement pStmt = conn.prepareStatement("select id, molfile from orchem_compound_sample where id=?");
        pStmt.setInt(1,dbId);
        ResultSet res = pStmt.executeQuery();
        Clob molFileClob = null;
        String mdl = null;

        if (res.next()) {
            System.out.println("\n______________________________________________");
            System.out.println("db id is "+res.getInt("id"));
            
            molFileClob = res.getClob("molfile");
            int clobLen = new Long(molFileClob.length()).intValue();
            mdl = (molFileClob.getSubString(1, clobLen));

            /* part 1: do a substructure search using the fingerprinter */            
            System.out.println("Fingerprint substructure search:");
            List<OrChemCompound> fprintSearchResults = dbApi.substructureSearch(mdl, "MOL", conn, 9999999);
            System.out.println("results # : "+fprintSearchResults.size());
            Collections.sort(fprintSearchResults);
            for (OrChemCompound oc : fprintSearchResults) {
                System.out.print(oc.getId() + " ");
            }

            /* part 2: find all substructures by doing a full scan on the data set */            
            System.out.println("\nFull isomorphism test : ");
            List<Integer> fullScanResults = fullScan(res.getInt("id"), targetMolecules, SubgraphIsomorphism.Algorithm.VF2, conn);
            Collections.sort(fullScanResults);
            System.out.println("results # : "+fullScanResults.size());

            for (Integer id : fullScanResults) {
                System.out.print(id + " ");
            }
            System.out.println();

            assertEquals("Fingerprint search and full scan must have same results size list ",fprintSearchResults.size(),fullScanResults.size());
            assertTrue  ("Fingerprint search and full scan must have same result id's in list ",equalIds(fprintSearchResults,fullScanResults));
        }
        res.close();
        pStmt.close();
    }

    /**
     * Performs a complete substructure search between a query compound 
     * and target compounds. This can be used to find all substructures and 
     * validate this against the prefilter result from a fingerprinter.
     *
     * @param id query compound database id
     * @param alg isomorphism algorithm {@link org.openscience.cdk.isomorphism.SubgraphIsomorphism.Algorithm }
     * @param conn database connection
     * @return list of ids of compounds of which compound with arg "id" is a substructure
     * @throws SQLException
     * @throws CDKException
     * @throws CloneNotSupportedException
     */
    private List<Integer> fullScan(int id, List<WrappedAtomContainer> targetMolecules, SubgraphIsomorphism.Algorithm alg, Connection conn) throws SQLException, CDKException, CloneNotSupportedException {
        List<Integer> result = new ArrayList<Integer>();

        System.out.println("+++++++++++++++++++++++++");
        PreparedStatement pStmt = conn.prepareStatement("select id, molfile from orchem_compound_sample where id = ?");
        pStmt.setInt(1, id);

        List<WrappedAtomContainer> queryMol = dbApi.getMols(pStmt);
        if (queryMol.size() == 1) {

            WrappedAtomContainer query = queryMol.get(0);
            //QueryAtomContainer queryAtomContainer = QueryAtomContainerCreator.createBasicQueryContainer(query.getAtomContainer());

            for (WrappedAtomContainer target : targetMolecules) {
                SubgraphIsomorphism s = new SubgraphIsomorphism(target.getAtomContainer(), query.getAtomContainer(), alg);
                if (s.matchSingle()) {
                    result.add(target.getDbId());
                }
            }
        }
        pStmt.close();
        return result;
    }

    /**
     * Helper method to assert if lists have same database Ids in them
     * @param compList
     * @param ids
     * @return 
     */
    private boolean equalIds (List<OrChemCompound> compList, List<Integer> ids) {
        for(OrChemCompound c : compList) {
            if (!ids.contains(new Integer(c.getId())))  {
                 return false;   
            }
        }
        return true;
    }

    /*
     * Start of Junit test methods 
     */
    public void testCompoundID_1() throws Exception {
        fingerprintVersusFullScan(1);
    }

    public void testCompoundID_2() throws Exception {
        fingerprintVersusFullScan(2);
    }

    public void testCompoundID_3() throws Exception {
        fingerprintVersusFullScan(3);
    }

    public void testCompoundID_4() throws Exception {
        fingerprintVersusFullScan(4);
    }

    public void testCompoundID_5() throws Exception {
        fingerprintVersusFullScan(5);
    }

    public void testCompoundID_6() throws Exception {
        fingerprintVersusFullScan(6);
    }

    public void testCompoundID_7() throws Exception {
        fingerprintVersusFullScan(7);
    }

    public void testCompoundID_8() throws Exception {
        fingerprintVersusFullScan(8);
    }

    public void testCompoundID_9() throws Exception {
        fingerprintVersusFullScan(9);
    }

    public void testCompoundID_10() throws Exception {
        fingerprintVersusFullScan(10);
    }

    public void testCompoundID_11() throws Exception {
        fingerprintVersusFullScan(11);
    }

    public void testCompoundID_12() throws Exception {
        fingerprintVersusFullScan(12);
    }

    public void testCompoundID_13() throws Exception {
        fingerprintVersusFullScan(13);
    }

    public void testCompoundID_14() throws Exception {
        fingerprintVersusFullScan(14);
    }

    public void testCompoundID_15() throws Exception {
        fingerprintVersusFullScan(15);
    }

    public void testCompoundID_16() throws Exception {
        fingerprintVersusFullScan(16);
    }

    public void testCompoundID_17() throws Exception {
        fingerprintVersusFullScan(17);
    }

    public void testCompoundID_18() throws Exception {
        fingerprintVersusFullScan(18);
    }

    public void testCompoundID_19() throws Exception {
        fingerprintVersusFullScan(19);
    }

    public void testCompoundID_20() throws Exception {
        fingerprintVersusFullScan(20); 
    }

    public void testCompoundID_21() throws Exception {
        fingerprintVersusFullScan(21);
    }

    public void testCompoundID_22() throws Exception {
        fingerprintVersusFullScan(22);
    }
    public void testCompoundID_23() throws Exception {
        fingerprintVersusFullScan(23);
    }

    public void testCompoundID_24() throws Exception {
        fingerprintVersusFullScan(24);
    }

    public void testCompoundID_25() throws Exception {
        fingerprintVersusFullScan(25);
    }

    public void testCompoundID_26() throws Exception {
        fingerprintVersusFullScan(26);
    }

    public void testCompoundID_27() throws Exception {
        fingerprintVersusFullScan(27);
    }

    public void testCompoundID_28() throws Exception {
        fingerprintVersusFullScan(28);
    }

    public void testCompoundID_29() throws Exception {
        fingerprintVersusFullScan(29);
    }

    public void testCompoundID_30() throws Exception {
        fingerprintVersusFullScan(30);
    }

    public void testCompoundID_31() throws Exception {
        fingerprintVersusFullScan(31);
    }

    public void testCompoundID_32() throws Exception {
        fingerprintVersusFullScan(32);
    }

}


