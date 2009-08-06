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
package uk.ac.ebi.orchem.load;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import uk.ac.ebi.orchem.db.OrChemParameters;

/**
 * Synchronizing orchem fingerprint tables with the base compound data. <br>
 * This requires the trigger and audit table to be installed in the master
 * schema.<br>
 * This class must be uploaded (loadjava) and run inside an Oracle database as Java stored procedure.
 *
 * @author markr@ebi.ac.uk
 *
 */
public class FingerprintSynchronization {

    /**
     *  This method creates a ResultSet on the base compound table, and then 
     *  calls out to create and store fingerprints for each compound in the
     *  resultset.
     *  <BR>
     *  The resultset contains all compounds for which the fingerprints need 
     *  synchronization, relying on the orchem audit table in the master schema. 
     *  These compounds may have been deleted, updated or newly inserted.
     *  <br>
     *  Methods uses a timestamp as a cutoff date, to make the program deal with 
     *  concurrency issues (for example, during the synchronization new
     *  rows may come into the audit table, but these will be ignored)
     *
     * @throws Exception
     */
    public static void synchronize() throws Exception {

        OracleConnection conn = null;
        conn = (OracleConnection)new OracleDriver().defaultConnection();
        //conn = (OracleConnection)new UnitTestConnection().getDbConnection();

        Statement stmtMaxTimestamp = null;
        ResultSet rsMaxTimestamp = null;
        PreparedStatement stmtQueryCompounds =null;
        ResultSet compounds=null;

        try {
            // Find cut-off date for current synchronization; 
            // this makes the program safer to work whilst new changes 
            // are possibly triggered through
            stmtMaxTimestamp = conn.createStatement();
            rsMaxTimestamp = stmtMaxTimestamp.executeQuery
                ("select max(timestamp) from orchem_audit_compound_changes ");
            rsMaxTimestamp.next();
            Timestamp maxTimeStamp = rsMaxTimestamp.getTimestamp(1);

            if (maxTimeStamp!=null) {
                
                // Delete the existing fingerprints for compounds that were updated or deleted
                PreparedStatement deleteStaleData = null;

                String[] tables =
                { "orchem_fingprint_subsearch", "orchem_fingprint_simsearch", "orchem_big_molecules" };
                for (String table : tables) {
                    deleteStaleData = conn.prepareStatement(
                       "delete " + table + " where id in " + 
                       "(select id from orchem_audit_compound_changes where timestamp <= ?) ");
                    deleteStaleData.setTimestamp(1, maxTimeStamp);
                    deleteStaleData.executeUpdate();
                }
                deleteStaleData.close();


                // Now (re)insert fingerprint data for updated and inserted fingerprints as logged in the audit
                String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
                String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
                String compoundTableMolfileColumn =
                    OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

                String compoundQuery =
                    " select " + compoundTablePkColumn + 
                    "   ,"     + compoundTableMolfileColumn + 
                    " from "   + compoundTableName + 
                    " where " + compoundTablePkColumn + " in " +
                    "          ( select id " + 
                    "            from   orchem_audit_compound_changes " +
                    "            where  action in ('U','I') " + 
                    "            and    timestamp <= ? )   ";

                stmtQueryCompounds = conn.prepareStatement(compoundQuery);
                stmtQueryCompounds.setTimestamp(1, maxTimeStamp);
                compounds = stmtQueryCompounds.executeQuery();

                DatabaseFingerprintPersistence dbfp = new DatabaseFingerprintPersistence();
                dbfp.persist(compounds, "synchronization");


                // done fingerprinting, now clean out the audit table..
                deleteStaleData = conn.prepareStatement(
                   "delete orchem_audit_compound_changes where timestamp <= ? ");
                deleteStaleData.setTimestamp(1, maxTimeStamp);
                deleteStaleData.executeUpdate();
                
                conn.commit();

            } else {
                System.out.println("Nothing to do, bye !");
            }
        } finally {
            if(rsMaxTimestamp!=null)
                rsMaxTimestamp.close();
            if (stmtMaxTimestamp!=null) 
                stmtMaxTimestamp.close();
            if (compounds!=null)
                compounds.close();
            if(stmtQueryCompounds!=null)
                stmtQueryCompounds.close();
            
        }
    }
}
