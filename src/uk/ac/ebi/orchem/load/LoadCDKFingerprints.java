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
package uk.ac.ebi.orchem.load;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import oracle.sql.BLOB;

import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


//import org.openscience.cdk.Molecule;


/**
 * Populates the orchem fingerprint tables.
 * <P>
 * This class must be loaded and run inside an Oracle database as Java stored procedure.
 *
 * @author markr@ebi.ac.uk
 *
 */
public class LoadCDKFingerprints {

    /**
     * <BR><BR>
     *  This method loops over a given compound table, and fetches the primary key and molfile for each compound. 
     *  It then creates and stores a fingerprint for each compound into the OrChem fingerprint tables.<BR>
     *  <BR>
     *  Can optionally perform the loop over a limited range of primary key values, from argumens startId to endId.<BR>
     *  <BR>
     *  It can also optionally serialize (Y or N ) the CDK molecule. The serialized
     *  Java object will be stored in the database, with the benefit that substrcuture validations can be 
     *  done faster because the molecule is then readily available from Oracle. <BR>
     *  However it also means this method will take longer to complete, and that there will be more 
     *  demand on storage space for OrChem. From practice, the serialized objects can claim as much space (or more) 
     *  as the original compound table clobs, and this may be undesirable for large compound tables.
     *
     * @param startId primary key from which to start select in compound table. See {@link #load() alternative} 
     * @param endId primary key up to which to select in compound table. See {@link #load() alternative} 
     * @param serializeYN indicates whether or not to serialize the CDK molecule into the database
     * @throws Exception
     */
    public static void load(String startId, String endId, String serializeYN ) throws Exception {

        long start=System.currentTimeMillis();
        String logCurrID="";

        /* Commit point is kept low due to big size of prepared statements. Anything much higher will fail; see
         * the large prepared statements */
        int COMMIT_POINT=30;   
        /* Default row prefetch also low due to big row sizes (Clob data) */
        //int DEF_ROW_PREFETCH=10;

        /* Log message to be fed back to user */
        StringBuilder logMsg = new StringBuilder();
        logMsg.append("\nStarted at "+now());
        OracleConnection conn = null;

        try {
            /* A number of long variable to capture elapse time for the log message */
            long getClobTime = 0;
            long makeMolTime = 0;
            long makeFpTime = 0;
            long serializeTime = 0;
            long sqlInsTime = 0;

            conn = (OracleConnection)new OracleDriver().defaultConnection();
            //conn = (OracleConnection)new PubChemConnection().getDbConnection();

            conn.setAutoCommit(false);
            //conn.setDefaultRowPrefetch(DEF_ROW_PREFETCH);
            int commitCount = 0;

            MDLV2000Reader mdlReader = new MDLV2000Reader();
            IFingerprinter fingerPrinter = FingerPrinterAgent.FP.getFingerPrinter();
            BitSet fpBitset;
            final int fpSize = FingerPrinterAgent.FP.getFpSize();


            /* Prepare the (flexible) query on the base compound table in the schema */
            String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
            String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
            String compoundTableMolfileColumn =
                OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

            String compoundQuery =
                " select /*+ full(c) parallel(c,2) */ "+ 
                "    " + compoundTablePkColumn +
                "   ," + compoundTableMolfileColumn + 
              //"   ,    dbms_lob.getlength(" + compoundTableMolfileColumn + ") leng " +  //archived
                " from   " + compoundTableName + " c ";
            if (startId != null && endId != null) {
                compoundQuery += "  where " + compoundTablePkColumn + " between " + startId + " and " + endId;
            }

            /* Note:
              /*+ APPEND .. tried this, but the append hint creates a table lock for DML .. do not use :( 
                  Also may introduce 2 much empty space and scattered data. Bad!
                  http://www.freelists.org/post/oracle-l/Question-about-Append-hint-in-Insert,3 
            */

            /* Statement for inserts into the orchem similarity search table */
            PreparedStatement psInsertSimiFp =
                conn.prepareStatement("insert " +
                "into orchem_fingprint_simsearch (id, bit_count,fp) values (?,?,?)");

            /* Statement for inserts into the substructure search table */
            StringBuffer sb = new StringBuffer();
            sb.append("insert into orchem_fingprint_subsearch values (? ");
            for (int idx = 0; idx < fpSize; idx++) 
                sb.append(",?");
            sb.append(")");
            PreparedStatement psInsertSubstrFp = conn.prepareStatement(sb.toString());
            

            /* Statement for inserts into the OrChem compound table */
            String insertCompounds =
                " insert into orchem_compounds " + 
                "   (id, single_bond_count, double_bond_count, triple_bond_count, aromatic_bond_count, " +
                "    s_count, o_count, n_count, f_count, cl_count,  br_count, i_count, c_count, p_count, " +
                "    saturated_bond_count ";

            if (serializeYN.equals("Y"))
                insertCompounds += ",cdk_molecule) ";
            else
                insertCompounds += ") ";

            insertCompounds += " values ( ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,? ";

            if (serializeYN.equals("Y"))
                insertCompounds += ",? )";
            else
                insertCompounds += ")";
            List<BLOB> blobList = new ArrayList<BLOB>();

            PreparedStatement psInsertCompound = conn.prepareStatement(insertCompounds);

            Statement stmtQueryCompounds =
                conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet res = stmtQueryCompounds.executeQuery(compoundQuery);
            long bef;

            //Clob molFileClob = null;
            String molfile = null;

            logMsg.append("\nSet up at "+now());

            /* Start the main loop over the base compound table */
            while (res.next()) {
                try {
                    logCurrID=res.getString(compoundTablePkColumn);

                    /* Check if the clob data is over the varchar2 bound of 4000. If not, get it as
                     * as String (which is much faster) from the resultSet */
                    bef = System.currentTimeMillis();
                    
                    /* In 11g looks like a Clob can be treated as a String .. ? */
                    //if (res.getInt("leng") > 4000) { // 
                    //     molFileClob = res.getClob(compoundTableMolfileColumn);
                    //     int clobLen = new Long(molFileClob.length()).intValue();
                    //    molfile = (molFileClob.getSubString(1, clobLen));
                    //} else
                    molfile = res.getString(compoundTableMolfileColumn);
                    getClobTime += (System.currentTimeMillis() - bef);

                    if (molfile != null) {
                        bef = System.currentTimeMillis();
                        /* Create a CDK molecule from the molfile */
                        NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                        makeMolTime += (System.currentTimeMillis() - bef);

                        /* Fingerprint the molecule */
                        bef = System.currentTimeMillis();
                        fpBitset = fingerPrinter.getFingerprint(molecule);
                        byte[] bytes = Utils.toByteArray(fpBitset, fpSize);
                        makeFpTime += (System.currentTimeMillis() - bef);

                        /* Prepare statement for Similarity search helper table */
                        psInsertSimiFp.setString(1, res.getString(compoundTablePkColumn));
                        psInsertSimiFp.setInt(2, fpBitset.cardinality());
                        psInsertSimiFp.setBytes(3, bytes);

                        /* Prepare statement for Substructure search helper table */
                        int idx = 1;
                        psInsertSubstrFp.setString(idx, res.getString(compoundTablePkColumn));

                        /* Hash the l fingerprint into a condensed fingerprint */
                        for (int i = 0; i < fpSize; i++) { 
                            idx = i + 2;
                            if (fpBitset.get(i) ) // || fpBitset.get(i + fpCondensedSize))
                                psInsertSubstrFp.setString(idx, "1");
                            else
                                psInsertSubstrFp.setString(idx, null);
                        }

                        /* Prepare statement for OrChem compound table */
                        Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(molecule);
                        idx = 1;
                        psInsertCompound.setString(idx, res.getString(compoundTablePkColumn));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.SINGLE_BOND_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.DOUBLE_BOND_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.TRIPLE_BOND_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.AROMATIC_BOND_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.S_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.O_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.N_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.F_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.CL_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.BR_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.I_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.C_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.P_COUNT));
                        psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.SATURATED_COUNT));

                        //Serialization (optional)
                        if (serializeYN.equals("Y")) {
                            bef = System.currentTimeMillis();
                            BLOB serializedMolecule = serializeToBlob(conn, molecule);
                            blobList.add(serializedMolecule);
                            psInsertCompound.setBlob(++idx, serializedMolecule);
                            serializeTime += (System.currentTimeMillis() - bef);
                        }

                        psInsertSimiFp.addBatch();
                        psInsertSubstrFp.addBatch();
                        psInsertCompound.addBatch();
                        commitCount++;

                        if (commitCount >= COMMIT_POINT) {
                            bef = System.currentTimeMillis();
                            psInsertSimiFp.executeBatch();
                            psInsertSubstrFp.executeBatch();
                            psInsertCompound.executeBatch();
                            conn.commit();
                            sqlInsTime += (System.currentTimeMillis() - bef);
                            commitCount = 0;

                            /*Hack - you really need to free the clobs to prevent the Temp tablespace
                             *from overflowing. Either that, or do short runs and get new connections
                             * every time*/
                            if (serializeYN.equals("Y")) {
                                for (BLOB blobForRemoval : blobList) {
                                    blobForRemoval.freeTemporary();
                                    blobForRemoval=null;
                                }
                                blobList.clear();
                            }
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Loop warning for " + compoundTablePkColumn + ": " +
                                       res.getString(compoundTablePkColumn) + e.getMessage());
                    logMsg.append("\nLoop err " + compoundTablePkColumn + ": " + res.getString(compoundTablePkColumn) +
                                  " " + e.getMessage());
                }
            }
            psInsertSimiFp.executeBatch();
            psInsertSubstrFp.executeBatch();
            psInsertCompound.executeBatch();

            psInsertSimiFp.close();
            res.close();
            stmtQueryCompounds.close();
            long end = System.currentTimeMillis();

            System.out.println("\n\nOverall elapse time (ms) : " + (end - start));
            System.out.println("Getting clobs     (ms) :" + getClobTime);
            System.out.println("Make molecules    (ms) :" + makeMolTime);
            System.out.println("Make fingerprints (ms) :" + makeFpTime);
            System.out.println("Serialization     (ms) :" + serializeTime);
            System.out.println("SQL DML           (ms) :" + sqlInsTime);


            logMsg.append("\nOverall elapse time (ms) :" + (end - start) + 
                          "\nGetting clobs       (ms) :" + getClobTime   + 
                          "\nMake molecules      (ms) :" + makeMolTime + 
                          "\nMake fingerprints   (ms) :" + makeFpTime + 
                          "\nSerialization       (ms) :" + serializeTime + 
                          "\nSQL DML             (ms) :" + sqlInsTime);

            logMsg.append("\nFinished at "+now());

        } catch (Exception e) {
            e.printStackTrace();
            logMsg.append("\nError at time "+now());
            logMsg.append ( "\n\nERROR - program aborted..\n\n"+"Loop was on compound ID "+logCurrID+"\nMsg: "+e.getMessage()+"\nStack\n"+Utils.getErrorString(e));
        }
        finally {
            PreparedStatement psInsertLog =  
            conn.prepareStatement("insert into orchem_log (log_id, who, when, what) values (orchem_sequence_log_id.nextval,?,sysdate,?)");
            psInsertLog.setString(1,"Load fingerprints "+startId+" "+endId);
            psInsertLog.setString(2,logMsg.toString());
            psInsertLog.executeUpdate();
            
            conn.commit();
            conn.close();
            
            System.gc();
        }

    }

    /**
     * Serializes an object to a Blob to be stored into the database.
     * @param connection
     * @param obj
     * @return serialize object in Oracle blob
     * @throws SQLException
     * @throws IOException
     */
    private static BLOB serializeToBlob(OracleConnection connection, Object obj) throws SQLException, IOException {
        // found on web : DURATION_CALL should only be used in PL/SQL code.  Stick to DURATION_SESSION for JDBC clients.
        BLOB blob_ = BLOB.createTemporary(connection, true, BLOB.DURATION_SESSION);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        byte[] data = baos.toByteArray();
        OutputStream os = blob_.setBinaryStream(0);
        os.write(data);
        os.flush();
        //os.close() ?
        return blob_;
    }

     public static String now() {
       String DATE_FORMAT_NOW = "dd MMM HH:mm:ss";
       Calendar cal = Calendar.getInstance();
       SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
       return sdf.format(cal.getTime());
     }

    /**
     *  Loops over a compound table specified by input arguments.<BR>
     *  Fetch the primary key and the mol file from the compound table, and stores
     *  a fingerprint for each compound into the orchem fingerprint table.<BR>
     *  Loops over <b>all</b> rows in input compound table.
     *
     * @throws Exception
     */
    public static void load( ) throws Exception {
        load( null,null,"N" );
    }
    /*
    public static void main(String[] args) throws Exception {
        LoadCDKFingerprints l  = new LoadCDKFingerprints();
        //l.load(args[0],args[1],"N");
        l.load("261001","261200","Y");
        // begin delete orchem_compounds where id < 9999; delete orchem_fingprint_subsearch where id < 9999; delete orchem_fingprint_simsearch where id < 9999; commit; end;
    }
    */
}


