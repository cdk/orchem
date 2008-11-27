package uk.ac.ebi.orchem.load;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.BitSet;
import java.util.Map;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.ExtendedFingerprinter;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.io.MDLV2000Reader;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.db.OrChemParameters;


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

        int DEF_ROW_PREFETCH=100;
        int COMMIT_POINT=25;     // kept low due to big size of prepared statements
        final int FP_1024=1024;  // used for the similarity search table
        final int FP_512=512;    // used for the substructure search table

        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();
        //OracleConnection conn = (OracleConnection)new ChebiConnection().getDbConnection();

        conn.setDefaultRowPrefetch(DEF_ROW_PREFETCH);
        conn.setAutoCommit(false);
        int commitCount=0;

        /* Two fingerprinters will be used. 1024 bits for the similarity search table, 512
         * for the substructure search table*/
        MDLV2000Reader mdlReader = new MDLV2000Reader();
        IFingerprinter fingerPrinter1024 = new ExtendedFingerprinter(FP_1024);
        BitSet fp1024;
        IFingerprinter fingerPrinter512 = new ExtendedFingerprinter(FP_512);
        BitSet fp512;

        /* Prepare the (flexible) query on the base compound table in the schema */
        String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
        String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
        String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);
        String compoundQuery = 
        " select " + compoundTablePkColumn       +
        "      , " + compoundTableMolfileColumn  +
        " from   " + compoundTableName;
        if (startId!=null && endId!=null)  {
            compoundQuery+= "  where "+compoundTablePkColumn+" between "+startId+" and "+endId;    
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
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?," +
        "?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?,?, ?,?,?,?,?,   ?,?,?,?," +
        //Atom and bond counts
        "?,?,?,?,?, ?,?,?,?,?, ?,? "+
        ")" 
        );

        Statement stmtQueryCompounds = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet res = stmtQueryCompounds.executeQuery(compoundQuery);
        Clob molFileClob = null;

        /* Start the main loop over the base compound table */        
        while (res.next()) {
            try {
                /* Get molecule and fingerprints */
                molFileClob = res.getClob(compoundTableMolfileColumn);
                int clobLen = new Long(molFileClob.length()).intValue();
                String molfile = (molFileClob.getSubString(1, clobLen));
                Molecule molecule = Utils.getMolecule(mdlReader, molfile);
                fp1024 = fingerPrinter1024.getFingerprint(molecule);
                fp512 = fingerPrinter512.getFingerprint(molecule);
                byte[] bytes = Utils.toByteArray(fp1024, FP_1024);


                /* Prepare statement for Similarity search helper table */
                psInsertSimiFp.setString(1, res.getString(compoundTablePkColumn));
                psInsertSimiFp.setInt(2, fp1024.cardinality());
                psInsertSimiFp.setBytes(3, bytes);


                /* Prepare statement for Substructure search helper table */
                int idx=1;
                psInsertSubstrFp.setString(idx,res.getString(compoundTablePkColumn));

                for (int i = 0; i < fp512.size(); i++) {
                    idx=i+2;
                    if (fp512.get(i))
                        psInsertSubstrFp.setString(idx,"1");
                    else
                        psInsertSubstrFp.setString(idx,null);
                }
                Map atomAndBondCounts = Utils.atomAndBondCount(molecule);
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.SINGLE_BOND_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.DOUBLE_BOND_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.TRIPLE_BOND_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.S_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.O_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.N_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.F_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.CL_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.BR_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.I_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.C_COUNT));
                psInsertSubstrFp.setInt(++idx,(Integer)atomAndBondCounts.get(Utils.P_COUNT));

                psInsertSimiFp.addBatch();
                psInsertSubstrFp.addBatch();
                commitCount++;

                if (commitCount >= COMMIT_POINT) {
                    psInsertSimiFp.executeBatch();
                    psInsertSubstrFp.executeBatch();
                    conn.commit();
                    commitCount = 0;
                }

            } catch (Exception e) {
                System.err.println("Loop warning for " + compoundTablePkColumn + ": "+res.getString(compoundTablePkColumn)+ e.getMessage());
            }
        }
        psInsertSimiFp.executeBatch();
        psInsertSubstrFp.executeBatch();

        conn.commit();
        psInsertSimiFp.close();
        res.close();
        stmtQueryCompounds.close();
        conn.close();

        //}
        //catch (Exception e) {
        //    System.err.println("ERROR:" + e.getMessage()+"\n"+Utils.getErrorString(e));
        //    // do not throw error; re-throwing may lead to dbms_job rescheduling over and over, java bug?
        //}
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

    /*
    public static void main(String[] args) throws Exception {
        LoadCDKFingerprints l  = new LoadCDKFingerprints();
        l.load(args[0],args[1]);
        l.load(null,null);

    }
    */
   

}

