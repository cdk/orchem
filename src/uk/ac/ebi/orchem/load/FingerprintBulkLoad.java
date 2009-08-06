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

import java.sql.ResultSet;
import java.sql.Statement;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import uk.ac.ebi.orchem.db.OrChemParameters;


/**
 * Bulk loading of data into the orchem fingerprint tables.<br>
 * Intended to run for the initial creation of fingerprints.<br><br>
 * This class must be uploaded (loadjava) and run inside an Oracle database as Java stored procedure.
 *
 * @author markr@ebi.ac.uk
 *
 */
public class FingerprintBulkLoad {

    /**
     *  This method creates a resultSet on a given compound table, and then 
     *  calls out to create and store fingerprints for each compound in the
     *  resultset.
     *  <BR><BR>
     *  It can optionally perform the loop on a limited range of primary key 
     *  values, from argumens startId to endId.
     *  <BR><BR>
     *
     * @param startId primary key from which to start select in compound table. See {@link #load() alternative}
     * @param endId primary key up to which to select in compound table. See {@link #load() alternative}
     * @throws Exception
     */
    public static void bulkLoad(String startId, String endId) throws Exception {

        OracleConnection conn = null;
        conn = (OracleConnection)new OracleDriver().defaultConnection();
        //conn = (OracleConnection)new UnitTestConnection().getDbConnection();

        Statement stmtQueryCompounds =null;
        ResultSet compounds = null;

        try {
            /* Prepare the (flexible) query on the base compound table in the schema */
            String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
            String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
            String compoundTableMolfileColumn =
                OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

            String compoundQuery =
                " select /*+ full(c) parallel(c,2) */ " + 
                "    " + compoundTablePkColumn + 
                "   ," + compoundTableMolfileColumn +
                " from   " + compoundTableName + " c ";

            if (startId != null && endId != null) {
                compoundQuery += "  where " + compoundTablePkColumn + 
                                 " between " + startId + " and " + endId;
            }
            stmtQueryCompounds = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            compounds = stmtQueryCompounds.executeQuery(compoundQuery);

            DatabaseFingerprintPersistence dbfp = new DatabaseFingerprintPersistence();

            String logMsg=null;
            if (startId != null && endId != null) 
                logMsg=startId+" to "+endId;
            else 
                logMsg="All compounds";

            dbfp.persist(compounds, logMsg);

        } finally {
            if (compounds!=null)  {
                compounds.close();
            }
            if (stmtQueryCompounds!=null)  {
                stmtQueryCompounds.close();
            }
        }
    }

    /**
     *  Alternative call to bulkLoad, will loops over <b>all</b> rows in input compound table.
     *  Suitable for smaller databases only.
     *
     * @throws Exception
     */
    public static void load() throws Exception {
        bulkLoad(null, null);
    }


}


