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

//import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.io.MDLV2000Reader;

import org.openscience.cdk.nonotify.NNMolecule;

import uk.ac.ebi.orchem.SimpleMail;
import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


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
     * <B> TODO replace mail sending - hard coded. Move into pl/sql=easier</B>
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

        /* Commit point is kept low due to big size of prepared statements. Anything much higher will fail; see
         * the large prepared statements */
        int COMMIT_POINT=25;     
        /* Default row prefetch also low due to big row sizes (Clob data) */
        int DEF_ROW_PREFETCH=10;

        /* Log message to be sent to user through e-mail */
        StringBuilder logMsg = new StringBuilder();
        
        /* A number of long variable to capture elapse time for the log message */
        long getClobTime=0;
        long makeMolTime=0;
        long makeFpTime=0;
        long serializeTime=0;
        long sqlInsTime=0;  

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
        " select /*+ PARALLEL ("+compoundTableName +",3) */" +
        "        " + compoundTablePkColumn       +
        "      , " + compoundTableMolfileColumn  +
        "      , dbms_lob.getlength(" + compoundTableMolfileColumn+") leng "  +
        " from   " + compoundTableName;
        if (startId!=null && endId!=null)  {
            compoundQuery+= "  where "+compoundTablePkColumn+" between "+startId+" and "+endId ;    
        }

        /* Statement for inserts into the orchem similarity search table */
        PreparedStatement psInsertSimiFp = conn.prepareStatement(
        "insert /*+ APPEND */ into orchem_fingprint_simsearch (id, bit_count,fp) values (?,?,?)"
        );

        /* Statement for inserts into the substructure search table */
        PreparedStatement psInsertSubstrFp = conn.prepareStatement(
        "insert /*+ APPEND */ into orchem_fingprint_subsearch values " +
        "(" +
        //id
        "?," +
        //512 bits, see fingerprinter agent
        //TODO : put this in a loop using a stringbuffer. do not hard code 512 anywhere..
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

        /* Statement for inserts into the OrChem compound table */
        String insertCompounds = 
        " insert /*+ APPEND */ into orchem_compounds " +
        "   (id, single_bond_count, double_bond_count, triple_bond_count, aromatic_bond_count, " +
        "    s_count, o_count, n_count, f_count, cl_count,  br_count, i_count, c_count, p_count, " +
        "    saturated_bond_count ";

        if (serializeYN.equals("Y"))  
            insertCompounds+=",cdk_molecule) ";
        else
            insertCompounds+=") ";

        insertCompounds+=" values ( ?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,? ";

        if (serializeYN.equals("Y"))  
            insertCompounds+=",? )";
        else
            insertCompounds+=")";

        PreparedStatement psInsertCompound = conn.prepareStatement(insertCompounds);

        Statement stmtQueryCompounds = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet res = stmtQueryCompounds.executeQuery(compoundQuery);
        long bef;
        Clob molFileClob = null;
        String molfile=null;

        /* Start the main loop over the base compound table */
        while (res.next()) {
            try {

                /* Check if the clob data is over the varchar2 bound of 4000. If not, get it as
                 * as String (which is much faster) from the resultSet */
                bef=System.currentTimeMillis();      
                if (res.getInt("leng")>4000 )  {
                    molFileClob = res.getClob(compoundTableMolfileColumn);
                    int clobLen = new Long(molFileClob.length()).intValue();
                    molfile = (molFileClob.getSubString(1, clobLen));
                }
                else
                    molfile=res.getString(compoundTableMolfileColumn); 
                getClobTime+=(System.currentTimeMillis()-bef);
                
                if (molfile != null) {
                    bef=System.currentTimeMillis();
                    /* Create a CDK molecule from the molfile */
                    NNMolecule molecule = Utils.getNNMolecule(mdlReader, molfile);
                    makeMolTime+=(System.currentTimeMillis()-bef);

                    /* Fingerprint the molecule */
                    bef=System.currentTimeMillis();
                    fpBitset = fingerPrinter.getFingerprint(molecule);
                    byte[] bytes = Utils.toByteArray(fpBitset, fpSize);
                    makeFpTime+=(System.currentTimeMillis()-bef);

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

                    /* Prepare statement for OrChem compound table */
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

                    //Serialization (optional)
                    if (serializeYN.equals("Y"))  {
                        bef=System.currentTimeMillis();
                        psInsertCompound.setBlob(++idx,serializeToBlob(conn, molecule));
                        serializeTime+=(System.currentTimeMillis()-bef);
                    }

                    psInsertSimiFp.addBatch();
                    psInsertSubstrFp.addBatch();
                    psInsertCompound.addBatch();
                    commitCount++;

                    if (commitCount >= COMMIT_POINT) {
                        bef=System.currentTimeMillis();
                        psInsertSimiFp.executeBatch();
                        psInsertSubstrFp.executeBatch();
                        psInsertCompound.executeBatch();
                        conn.commit();
                        sqlInsTime+=(System.currentTimeMillis()-bef);
                        commitCount = 0;
                    }
                }

            } catch (Exception e) {
                System.err.println("Loop warning for " + compoundTablePkColumn + ": " + res.getString(compoundTablePkColumn) +e.getMessage());
                logMsg.append("\nLoop err " + compoundTablePkColumn + ": " + res.getString(compoundTablePkColumn) +" "+e.getMessage());
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
        
        System.out.println("\n\nOverall elapse time (ms) : "+ (end-start));
        System.out.println("Getting clobs     (ms) :"+getClobTime);
        System.out.println("Make molecules    (ms) :"+makeMolTime);
        System.out.println("Make fingerprints (ms) :"+makeFpTime);
        System.out.println("Serialization     (ms) :"+serializeTime);
        System.out.println("SQL DML           (ms) :"+sqlInsTime);


        logMsg.append(
        "\nOverall elapse time (ms) : "+ (end-start) +
        "\nGetting clobs     (ms) :"+getClobTime +
        "\nMake molecules    (ms) :"+makeMolTime +
        "\nMake fingerprints (ms) :"+makeFpTime +
        "\nSerialization     (ms) :"+serializeTime+
        "\nSQL DML           (ms) :"+sqlInsTime);
        SimpleMail.sendMail("smtp","smtp.ebi.ac.uk","markr@ebi.ac.uk","orchem load fingerprints",logMsg.toString());

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
        load( null,null,"N" );
    }
    
    /*
    public static void main(String[] args) throws Exception {
        LoadCDKFingerprints l  = new LoadCDKFingerprints();
        l.load(args[0],args[1]);
        // begin delete orchem_compounds where id < 9999; delete orchem_fingprint_subsearch where id < 9999; delete orchem_fingprint_simsearch where id < 9999; commit; end;
    }
    */
}





