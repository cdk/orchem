/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Mark Rijnbeek
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Properties;

import oracle.jdbc.OraclePreparedStatement;

import oracle.sql.CLOB;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.iterator.IteratingMDLReader;

import uk.ac.ebi.orchem.PropertyLoader;


/**
 * Loads test set of compounds into test schema.
 * Part of unit test.
 *
 */
public class LoadCompounds {

    /**
     * Loads data
     *
     * @param args args[0] = MDL file with test compounds, args[1] = offset for ID
     * @throws Exception
     */
    public static void main(String[] args) {
        Connection conn = null;
        try {

            Properties properties = PropertyLoader.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
            conn.setAutoCommit(false);
    
            String insertCommand = " insert into orchem_compound_sample values (?,?) ";
            OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement(insertCommand);

            File molFile = new File(args[0]);
            InputStream ins = new FileInputStream(molFile);
            IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());

            System.out.println("Inserting test compounds from mol file ..\n\n");
            int molCount = new Integer(args[1]); // use offset

            while (reader.hasNext()) {
                Object object = reader.next();
                if (object != null && object instanceof Molecule) {

                    molCount++;
                    System.out.print(" id:"+molCount);
                    if(molCount%15 == 0)
                        System.out.println();

                    try {
                        Molecule m = (Molecule)object;
                        StringWriter writer = new StringWriter();
                        MDLV2000Writer mdlWriter = new MDLV2000Writer(writer);

                        mdlWriter.write(m);
                        String mdl = writer.toString();
                        CLOB mdlClob = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
                        mdlClob.open(CLOB.MODE_READWRITE);
                        mdlClob.setString(1, mdl);

                        pstmt.setInt(1, molCount);
                        pstmt.setCLOB(2, mdlClob);
                        pstmt.executeUpdate();
                        mdlClob.close();
                        mdlClob.freeTemporary();

                    } catch (Exception e) {
                        System.out.println("\nError! Message="+e.getMessage());                        
                    }

                }
            }
            conn.commit();
            System.out.println("\n\nDone !");
                
        } catch (Exception ex) {
            if(conn!=null)
                try {
                    conn.rollback();
                    conn.close();
                } catch (SQLException sqlEx) {
                    ex.printStackTrace();
                } 
            System.err.println("\n\nERROR, load aborted..\n\n");
            ex.printStackTrace();
        }


    }
    

}
