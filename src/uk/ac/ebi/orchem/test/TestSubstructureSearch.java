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

import java.sql.Array;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtomContainer;

import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.SubgraphIsomorphism;
import org.openscience.cdk.nonotify.NNMolecule;

import uk.ac.ebi.orchem.Constants;
import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;

/**
 * Test class for OrChem substructure searching
 * @author sinterklaas
 */

public class TestSubstructureSearch extends TestCase {

    Connection conn;

    public void setUp() throws SQLException {
        Properties properties = Constants.getUnittestProperties();
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        conn = DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
    }

    public void tearDown() throws SQLException {
        conn.close();
    }

    public void testFingerprintFilterFindsAllCandidates() throws Exception {

        PreparedStatement pStmt = conn.prepareStatement("select id, molfile from orchem_compound_sample where id=29 --id> 1 and id < 1000");
        ResultSet res = pStmt.executeQuery();
        Clob molFileClob = null;
        String mdl = null;
        CompleteIsomorphismCheck c = new CompleteIsomorphismCheck();

        while (res.next()) {
            System.out.println("\n\n\n______________________________________________");
            System.out.println("db id is "+res.getInt("id"));
            
            molFileClob = res.getClob("molfile");
            int clobLen = new Long(molFileClob.length()).intValue();
            mdl = (molFileClob.getSubString(1, clobLen));
           
            
            System.out.println("Fingerprint substructure search:");
            List<OrChemCompound> l1 = substructureSearchMol(mdl, conn, 9999999);
            System.out.println("results # : "+l1.size());
            Collections.sort(l1);
        
            for (OrChemCompound oc : l1) {
                System.out.print(oc.getId() + " ");
            }

            System.out.println("\nFull isomorphism test : ");
            List<Integer> l2 = c.check(res.getInt("id"), SubgraphIsomorphism.Algorithm.VF2, conn);
            Collections.sort(l2);
            for (Integer id : l2) {
                System.out.print(id + " ");
            }
            System.out.println();

            if (l2.size() != l1.size()) {
                System.err.println("BUG found");
            }

            assertEquals(l1.size(),l2.size());
            //TODO assert list content same !

        }
        res.close();
        pStmt.close();
    }


    /**
     * Run a database substructure search using a mol file as input
     *
     * @param molfile
     * @param conn
     * @param topN
     * @return list of {@link uk.ac.ebi.orchem.bean.OrChemCompound}
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<OrChemCompound> substructureSearchMol(String molfile, Connection conn, int topN) throws SQLException, ClassNotFoundException {

        String plsqlCall = "begin ?:=orchem.substructure_search_mol(?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall);
        ocs.registerOutParameter(1, OracleTypes.ARRAY, "ORCHEM_COMPOUND_LIST");
        ocs.setString(2, molfile);
        ocs.setInt(3, topN);
        ocs.setString(4, "N");
        return executeOCS(ocs, conn);
    }


    /**
     * Run a database substructure search using a Smiles string as input
     * @param smiles
     * @param conn
     * @param topN
     * @return list of {@link uk.ac.ebi.orchem.bean.OrChemCompound}
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<OrChemCompound> substructureSearchSmiles(String smiles, Connection conn, int topN) throws SQLException, ClassNotFoundException {
        String plsqlCall = "begin ?:=orchem.substructure_search_smiles(?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ocs.registerOutParameter(1, OracleTypes.ARRAY, "ORCHEM_COMPOUND_LIST");
        ocs.setString(2, smiles);
        ocs.setInt(3, topN);
        ocs.setString(4, "N");
        return executeOCS(ocs, conn);
    }

    private List executeOCS(OracleCallableStatement ocs, Connection conn) throws SQLException, ClassNotFoundException {
        ocs.executeUpdate();
        Array cARRAY = ocs.getArray(1);
        Map map = conn.getTypeMap();
        map.put("ORCHEM_COMPOUND", Class.forName("uk.ac.ebi.orchem.bean.OrChemCompound", false, Thread.currentThread().getContextClassLoader()));
        Object[] compoundArray = (Object[])cARRAY.getArray();
        return Arrays.asList(compoundArray);
    }

}


/*
 
 db id is 20
Fingerprint substructure search:
results # : 33
2 5 20 21 22 1071 1130 1131 1144 1145 1179 1180 1348 1351 1352 1360 1361 1367 1369 1378 1379 1438 1446 1452 1455 1456 1457 1494 1498 1502 1515 1519 1520 
Full isomorphism test : 
BUG found
2 5 20 21 22 1062 1071 1130 1131 1144 1145 1179 1180 1348 1351 1352 1360 1361 1367 1369 1378 1379 1438 1446 1452 1455 1456 1457 1494 1498 1502 1519 1520 1540 1541 
Process exited with exit code 0.

db id is 20
Fingerprint substructure search:
results # : 33
2 5 20 21 22 1071 1130 1131 1144 1145 1179 1180 1348 1351 1352 1360 1361 1367 1369 1378 1379 1438 1446 1452 1455 1456 1457 1494 1498 1502 1515 1519 1520 
Full isomorphism test : 
2 5 20 21 22 1062 1071 1130 1131 1144 1145 1179 1180 1348 1351 1352 1360 1361 1367 1369 1378 1379 1438 1446 1452 1455 1456 1457 1494 1498 1502 1519 1520 1538 
BUG found

db id is 20
Fingerprint substructure search:
results # : 33
2 5 20 21 22 1071 1130 1131 1144 1145 1179 1180 1348 1351 1352 1360 1361 1367 1369 1378 1379 1438 1446 1452 1455 1456 1457 1494 1498 1502 1515 1519 1520 
Full isomorphism test : 
2 5 20 21 22 1062 1071 1130 1131 1144 1145 1179 1180 1348 1351 1352 1360 1361 1367 1369 1378 1379 1438 1446 1452 1455 1456 1457 1494 1498 1502 1519 1520 1540 1542 
BUG found
 
 */