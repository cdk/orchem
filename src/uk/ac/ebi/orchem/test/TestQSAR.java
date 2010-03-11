/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2010  Duan Lian
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

import junit.framework.TestCase;
import oracle.jdbc.driver.OracleConnection;
import oracle.sql.NUMBER;
import uk.ac.ebi.orchem.PropertyLoader;

import java.sql.*;
import java.util.Properties;


/**
 * Junit test for OrChem QSAR Descriptors<P>
 *
 *
 */
public class TestQSAR extends TestCase {


    static OracleConnection conn;
    static {
        try {
            Properties properties = PropertyLoader.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = (OracleConnection)DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Calculate the given descriptor in SQL
     * @param id test set primary key
     * @param descriptorName expected descriptor name, i.e. ALOGP,AMP..
     * @throws java.sql.SQLException
     * @throws ClassNotFoundException
     */

    private static double evalDescriptor(int id, String descriptorName) throws SQLException,ClassNotFoundException {
        PreparedStatement pStmt = conn.prepareStatement
                ("select id, orchem_qsar." + descriptorName + "(molfile) as " + descriptorName +
                        " from orchem_compound_sample where id = ?");

        pStmt.setInt(1, id);
        ResultSet res = pStmt.executeQuery();
        double value = 0;
        if (res.next()) {
            System.out.println("\n______________________________________________");
            System.out.println("db id is " + res.getInt("id"));
            value = res.getDouble(descriptorName);
        }
        System.out.println(descriptorName + " created is " + value);

        res.close();
        pStmt.close();
        return value;
    }


    public void testCompoundID_10_ALOGP() throws Exception {
        assertEquals(-0.0466,evalDescriptor(10,"ALOGP"),0.0001);
    }

    public void testCompoundID_10_ALOGP2() throws Exception {
        assertEquals(0.0021,evalDescriptor(10,"ALOGP2"),0.0001);
    }

    public void testCompoundID_10_AMR() throws Exception {
        assertEquals(2.8858,evalDescriptor(10,"AMR"),0.0001);
    }

    public void testCompoundID_11_ALOGP() throws Exception {
        assertEquals(-0.5268,evalDescriptor(11,"ALOGP"),0.0001);
    }

    public void testCompoundID_11_ALOGP2() throws Exception {
        assertEquals(0.2775,evalDescriptor(11,"ALOGP2"),0.0001);
    }

    public void testCompoundID_11_AMR() throws Exception {
        assertEquals(22.4206,evalDescriptor(11,"AMR"),0.0001);
    }

    public void testCompoundID_1111_ALOGP() throws Exception {
        assertEquals(0,evalDescriptor(1111,"ALOGP"),0.0001);
    }


}