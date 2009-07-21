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

import java.util.Properties;

import junit.framework.TestCase;

import oracle.jdbc.driver.OracleConnection;

import org.openscience.cdk.exception.CDKException;

import uk.ac.ebi.orchem.PropertyLoader;


/**
 * Junit test for OrChem Convert<P>
 *
 * Contains a list of test methods, each for some "query compound" with the
 * number in the method is the database id (primary key) of that compound in
 * table ORCHEM_COMPOUND_SAMPLE.<P>
 * You can add more query compounds to this table by adding them to
 * the queries mol file in the unit test directory. They will get loaded by {@link LoadCompounds}.<P>
 * Target compounds are loaded from a second file with an id offset of 1000.<P>
 *
 */
public class TestConvert extends TestCase {


    static OracleConnection conn;
    /* connect and suk all the unittest compounds into a working list (for performance)*/
    static {
        try {
            System.out.println("___ static : Begin set up target list (once) ");
            Properties properties = PropertyLoader.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = (OracleConnection)DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
            System.out.println("___ static : End set up target list");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUp() throws SQLException, CDKException {
    }

    public void tearDown() throws SQLException {
    }

    public static void Molfiletosmiles(int id) throws SQLException, ClassNotFoundException {
        PreparedStatement pStmt = conn.prepareStatement("select id, orchem_convert.molfiletosmiles(molfile) as molfile from orchem_compound_sample where id = ?");
        pStmt.setInt(1, id);
        ResultSet res = pStmt.executeQuery();
        Clob molFileClob = null;
        int clobLen = 0; 
        String mdl = null;
        if (res.next()) {
            System.out.println("\n______________________________________________");
            System.out.println("db id is " + res.getInt("id"));
            molFileClob = res.getClob("molfile");
            clobLen = new Long(molFileClob.length()).intValue();
            mdl = (molFileClob.getSubString(1, clobLen));
            System.out.println("results # : " + mdl);
            System.out.println("length # : " + clobLen);
        }
        assertTrue("Result test:",resultlen(clobLen));

        res.close();
        pStmt.close();
    }
  
    private static boolean resultlen(int hcloblen) {
      if (hcloblen>0) { return true; } else {return false;}
    }
    
    public void testCompoundID_1() throws Exception {
     Molfiletosmiles(1);
    }

    public void testCompoundID_2() throws Exception {
     Molfiletosmiles(2);
    }

    public void testCompoundID_3() throws Exception {
     Molfiletosmiles(3);
    }
}
