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
 */

package uk.ac.ebi.orchem.test;

import java.sql.Clob;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import oracle.jdbc.driver.OracleConnection;

import org.openscience.cdk.exception.CDKException;

import uk.ac.ebi.orchem.PropertyLoader;
import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.shared.DatabaseAccess;
import uk.ac.ebi.orchem.shared.WrappedAtomContainer;


/**
 * Junit test for OrChem similarity searching<P>
 *
 * Contains a list of test methods, each for some "query compound" with the
 * number in the method is the database id (primary key) of that compound in
 * table ORCHEM_COMPOUND_SAMPLE.<P>
 * You can add more query compounds to this table by adding them to
 * the queries mol file in the unit test directory. They will get loaded by {@link LoadCompounds}.<P>
 * Target compounds are loaded from a second file with an id offset of 1000.<P>
 * Tests are done with query compounds against the target compounds <B>plus</B> the query compounds, to
 * test if the substructure search at least establishes that a query compound is similar to itself (score 1.0)
 *
 */
public class TestSimilaritySearch extends TestCase {

    private DatabaseAccess dbApi = new DatabaseAccess();

    static OracleConnection conn;
    static List<WrappedAtomContainer> targetMolecules;
    /* connect and suk all the unittest compounds into a working list (for performance)*/
    static {
        try {
            System.out.println("___ static : Begin set up target list (once) ");
            Properties properties = PropertyLoader.getUnittestProperties();
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
     *
     * @param dbId
     * @param minScore
     * @param expectedResultCount
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private List<OrChemCompound> similaritySearch(int dbId, float minScore, int expectedResultCount) throws SQLException, ClassNotFoundException {
        PreparedStatement pStmt = conn.prepareStatement("select id, molfile from orchem_compound_sample where id=?");
        pStmt.setInt(1, dbId);
        ResultSet res = pStmt.executeQuery();
        Clob molFileClob = null;
        String mdl = null;
        List<OrChemCompound> similaritySearchResults=null;

        if (res.next()) {
            System.out.println("\n______________________________________________");
            System.out.println("db id is " + res.getInt("id"));

            molFileClob = res.getClob("molfile");
            int clobLen = new Long(molFileClob.length()).intValue();
            mdl = (molFileClob.getSubString(1, clobLen));

            System.out.println("Similarity search:");
            similaritySearchResults = dbApi.similaritySearch(mdl, "MOL",conn, minScore,9999999);
            System.out.println("results # : " + similaritySearchResults.size());

            Collections.sort(similaritySearchResults);
            for (OrChemCompound oc : similaritySearchResults) {
                System.out.print(oc.getId() + " ");
            }
            assertEquals("Similarity search expected results ", similaritySearchResults.size(), expectedResultCount);
        }
        res.close();
        pStmt.close();
        return similaritySearchResults;
    }

   /**
    * Check if a compound is found to be similar to itself with (maximum) score 1.0
    * @param similaritySearchResults compound found to be similar to compound with id "compoundId"
    * @param compoundId
    * @return true if compoundId occurs in the result list with a score of 1.0f
    */
    private void findSelf(List<OrChemCompound> similaritySearchResults, int compoundId) {
        boolean foundSelf=false;
        for (OrChemCompound oc : similaritySearchResults) {
            if (oc.getId().equals(compoundId + "")) {
                if (oc.getScore() == 1) {
                    foundSelf=true;                }
            }
        }
        assertTrue("Compound must be similar to itself with score 1 ", foundSelf);
    }

   /*
    * Start of Junit test methods
    */
    public void testCompoundID_1() throws Exception {
        findSelf (similaritySearch(1, 0.8f, 2),1);
    }

    public void testCompoundID_1_bogus() throws Exception {
        similaritySearch(1, 2f, 0); //impossible tanimoto score - result list size must be 0. will not find self in results
    }

    public void testCompoundID_2() throws Exception {
        findSelf(similaritySearch(2, 0.75f, 5),2);
    }

    public void testCompoundID_15() throws Exception {
        findSelf(similaritySearch(15, 1f, 2),15);
    }

    public void testCompoundID_22() throws Exception {
        findSelf(similaritySearch(22, 0.8f, 5),22);
    }

    public void testCompoundID_27() throws Exception {
        findSelf(similaritySearch(27, 0.85f, 6),27);
    }

    public void testCompoundID_31() throws Exception {
        findSelf(similaritySearch(31, 0.95f, 3),31);
    }

    public void testCompoundID_34() throws Exception {
        findSelf(similaritySearch(34, 0.5f, 7),34);
    }

    public void testCompoundID_35() throws Exception {
        findSelf(similaritySearch(35, 0.7f, 4),35);
    }

    // add more if u like

}
