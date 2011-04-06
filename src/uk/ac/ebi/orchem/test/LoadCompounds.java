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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Properties;

import oracle.jdbc.OraclePreparedStatement;

import oracle.sql.CLOB;

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
        String currentLine = null;
        int molCount = new Integer(args[1]); // use offset

        try {

            Properties properties = PropertyLoader.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn =DriverManager.getConnection(properties.getProperty("dbUrl"), 
                  properties.getProperty("dbUser"), properties.getProperty("dbPass"));
            conn.setAutoCommit(false);

            String insertCommand = " insert into orchem_compound_sample values (?,?) ";
            OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement(insertCommand);

            File molFile = new File(args[0]);
            BufferedReader input = new BufferedReader(new FileReader(molFile));

            boolean moreToDo = true;

            while (moreToDo) {
                currentLine = input.readLine();
                if (currentLine == null)
                    moreToDo = false;
                else {
                    StringBuffer buffer = new StringBuffer();
                    while (currentLine != null && !currentLine.equals("M  END")) {
                        buffer.append(currentLine);
                        buffer.append(System.getProperty("line.separator"));
                        currentLine = input.readLine();
                    }
                    buffer.append(currentLine);
                    buffer.append(System.getProperty("line.separator"));

                    String mdl = buffer.toString();

                    molCount++;
                    System.out.print(" id:" + molCount);
                    if (molCount % 15 == 0)
                        System.out.println();

                    try {
                        CLOB mdlClob = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
                        mdlClob.open(CLOB.MODE_READWRITE);
                        mdlClob.setString(1, mdl);
                        pstmt.setInt(1, molCount);
                        pstmt.setCLOB(2, mdlClob);
                        pstmt.executeUpdate();
                        mdlClob.close();
                        mdlClob.freeTemporary();

                    } catch (Exception e) {
                        System.out.println("\nError! Message=" + e.getMessage());
                    }

                    do {
                        currentLine = input.readLine();
                    } while (currentLine != null && !currentLine.equals("$$$$"));

                }
            }
            conn.commit();
            System.out.println("\n\nDone !");

        } catch (Exception ex) {
            if (conn != null)
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
