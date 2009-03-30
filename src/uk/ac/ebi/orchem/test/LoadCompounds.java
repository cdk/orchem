package uk.ac.ebi.orchem.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.SQLException;

import java.util.Properties;

import oracle.jdbc.OraclePreparedStatement;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

import oracle.sql.CLOB;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.iterator.IteratingMDLReader;

import uk.ac.ebi.orchem.Constants;
import uk.ac.ebi.orchem.PropertyLoader;
import uk.ac.ebi.orchem.imp.PubChemImport;
import uk.ac.ebi.orchem.scratch.PubChemConnection;

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

            Properties properties = Constants.getUnittestProperties();
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
                    Molecule m = (Molecule)object;

                    StringWriter writer = new StringWriter();
                    MDLWriter mdlWriter = new MDLWriter(writer);
                    mdlWriter.write(m);
                    String mdl = writer.toString();
                    CLOB mdlClob = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
                    mdlClob.open(CLOB.MODE_READWRITE);
                    mdlClob.setString(1, mdl);

                    System.out.print(" id:"+molCount);
                    if(molCount%15==0)
                        System.out.println();
                    
                    pstmt.setInt(1, molCount);
                    pstmt.setCLOB(2, mdlClob);
                    pstmt.executeUpdate();
                    mdlClob.close();
                    mdlClob.freeTemporary();

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
