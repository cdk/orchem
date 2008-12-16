package uk.ac.ebi.orchem.load;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.BitSet;
import java.util.Map;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import oracle.sql.BLOB;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.io.MDLV2000Reader;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * Populates the orchem fingerprint tables (one for similarity and one for substructure searching)<P>
 * This class must be loaded and run inside an Oracle database as Java stored procedure.
 *
 * @author markr@ebi.ac.uk
 *
 */
public class LoadCDKFingerprints {

    /**
     *  Loops over a compound table specified by input arguments.<BR>
     *  Fetches the primary key and the mol file from the compound table, and stores
     *  a fingerprint for each compound into the two orchem fingerprint tables.<BR>
     *  Can performs the loop over a limited range of primary key values, from startId to endId.
     *
     * @param startId id from which to start select in compound table. See {@link #load() alternative} 
     * @param endId  id up to which to select in compound table. See {@link #load() alternative} 
     * @throws Exception
     */
    public static void load(String startId, String endId ) throws Exception {

        //try {

        long start=System.currentTimeMillis();
        int DEF_ROW_PREFETCH=100;
        int COMMIT_POINT=25;     // kept low due to big size of prepared statements

        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();
        //OracleConnection conn = (OracleConnection)new StarliteConnection().getDbConnection();

        conn.setDefaultRowPrefetch(DEF_ROW_PREFETCH);
        conn.setAutoCommit(false);
        int commitCount=0;

        MDLV2000Reader mdlReader = new MDLV2000Reader();
        IFingerprinter fingerPrinter = FingerPrinterAgent.FP.getFingerPrinter();
        BitSet fpBitset;
        final int fpSize = FingerPrinterAgent.FP.getFpSize();

        /* Prepare the (flexible) query on the base compound table in the schema */
        String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
        String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
        String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);
        String compoundQuery = 
        " select " + compoundTablePkColumn       +
        "      , " + compoundTableMolfileColumn  +
        " from   " + compoundTableName;
        if (startId!=null && endId!=null)  {
            compoundQuery+= "  where "+compoundTablePkColumn+" between "+startId+" and "+endId ;    
        }

        /* Statement for inserts into the orchem similarity search table */
        PreparedStatement psInsertSimiFp = conn.prepareStatement(
        "insert into orchem_fingprint_simsearch (id, bit_count,fp) values (?,?,?)"
        );

        /* Statement for inserts into the substructure search table */
        PreparedStatement psInsertSubstrFp = conn.prepareStatement(
        "insert into orchem_fingprint_subsearch values " +
        "(" +
        //id
        "?," +
        //512 bits
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,? " +
         ")" 
         );

        /* Statement for inserts into the orchem serialized molecules table */
        PreparedStatement psInsertCompound = conn.prepareStatement(
        " insert into orchem_compounds " +
        "   (id, single_bond_count, double_bond_count, triple_bond_count, aromatic_bond_count, " +
        "    s_count, o_count, n_count, f_count, cl_count,  br_count, i_count, c_count, p_count, " +
        "    saturated_bond_count, cdk_molecule) "+
        " values " +
        "   (" +
        "      ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ? "+
        "    )" 
        );

        Statement stmtQueryCompounds = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet res = stmtQueryCompounds.executeQuery(compoundQuery);
        Clob molFileClob = null;

        long bef;
        /* Start the main loop over the base compound table */
        while (res.next()) {
            try {
                /* Get molecule and fingerprints */
                molFileClob = res.getClob(compoundTableMolfileColumn);
                if (molFileClob != null) {

                    int clobLen = new Long(molFileClob.length()).intValue();
                    String molfile = (molFileClob.getSubString(1, clobLen));

                    bef=System.currentTimeMillis();
                    Molecule molecule = Utils.getNNMolecule(mdlReader, molfile);
                    System.out.println("ms make mol "+(System.currentTimeMillis()-bef));

                    bef=System.currentTimeMillis();
                    
                    fpBitset = fingerPrinter.getFingerprint(molecule);
                    byte[] bytes = Utils.toByteArray(fpBitset, fpSize);
                    System.out.println("ms FP "+(System.currentTimeMillis()-bef));

                    /* Prepare statement for Similarity search helper table */
                    psInsertSimiFp.setString(1, res.getString(compoundTablePkColumn));
                    psInsertSimiFp.setInt(2, fpBitset.cardinality());
                    psInsertSimiFp.setBytes(3, bytes);

                    /* Prepare statement for Substructure search helper table */
                    int idx = 1;
                    psInsertSubstrFp.setString(idx, res.getString(compoundTablePkColumn));

                    for (int i = 0; i < fpBitset.size(); i++) {
                        idx = i + 2;
                        if (fpBitset.get(i))
                            psInsertSubstrFp.setString(idx, "1");
                        else
                            psInsertSubstrFp.setString(idx, null);
                    } 

                    Map atomAndBondCounts = Utils.atomAndBondCount(molecule);
                    idx = 1;
                    psInsertCompound.setString(idx, res.getString(compoundTablePkColumn));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.SINGLE_BOND_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.DOUBLE_BOND_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.TRIPLE_BOND_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.AROMATIC_BOND_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.S_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.O_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.N_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.F_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.CL_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.BR_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.I_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.C_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.P_COUNT));
                    psInsertCompound.setInt(++idx, (Integer)atomAndBondCounts.get(Utils.SATURATED_COUNT));
                    psInsertCompound.setBlob(++idx,serializeToBlob(conn, molecule));

                    psInsertSimiFp.addBatch();
                    psInsertSubstrFp.addBatch();
                    psInsertCompound.addBatch();
                    commitCount++;

                    if (commitCount >= COMMIT_POINT) {
                        psInsertSimiFp.executeBatch();
                        psInsertSubstrFp.executeBatch();
                        psInsertCompound.executeBatch();
                        conn.commit();
                        commitCount = 0;
                    }
                }

            } catch (Exception e) {
                System.err.println("Loop warning for " + compoundTablePkColumn + ": " + res.getString(compoundTablePkColumn) +e.getMessage());
                //throw e;
            }
        }
        psInsertSimiFp.executeBatch();
        psInsertSubstrFp.executeBatch();
        psInsertCompound.executeBatch();

        conn.commit();
        psInsertSimiFp.close();
        res.close();
        stmtQueryCompounds.close();
        conn.close();
        long end=System.currentTimeMillis();
        
        System.out.println("Elapse time "+ (end-start));

        //}
        //catch (Exception e) {
        //    System.err.println("ERROR:" + e.getMessage()+"\n"+Utils.getErrorString(e));
        //    // do not throw error; re-throwing may lead to dbms_job rescheduling over and over, java bug?
        //}
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
        BLOB blob_ = BLOB.createTemporary(connection, true, BLOB.DURATION_SESSION);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        byte[] data = baos.toByteArray();
        OutputStream os = blob_.setBinaryStream(0);
        os.write(data);
        os.flush();
        return blob_;
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
        load( null,null );
    }
    public static void main(String[] args) throws Exception {
        LoadCDKFingerprints l  = new LoadCDKFingerprints();
        l.load(args[0],args[1]);
    }
}

