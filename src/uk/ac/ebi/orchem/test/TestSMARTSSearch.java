package uk.ac.ebi.orchem.test;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import oracle.jdbc.driver.OracleConnection;

import org.openscience.cdk.exception.CDKException;

import uk.ac.ebi.orchem.PropertyLoader;
import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.shared.DatabaseAccess;


public class TestSMARTSSearch extends TestCase {
 
    private DatabaseAccess dbApi = new DatabaseAccess();
    private static OracleConnection conn;

    /* connect and get all the unittest compounds into a working list (for performance)*/
    static {
        try {
            System.out.println("___ static : Begin set up target list (once) ");
            Properties properties = PropertyLoader.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = (OracleConnection)DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void smartsQuery(String SMARTS, int expectedResults) throws CDKException, SQLException, ClassNotFoundException {
        /* part 1: do a substructure search using the fingerprinter */            
        System.out.println("Fingerprint SMARTS search:");
        List<OrChemCompound> fprintSearchResults = dbApi.smartsSearch(SMARTS, conn);
        System.out.println("results # : "+fprintSearchResults.size());
        Collections.sort(fprintSearchResults);
        for (OrChemCompound oc : fprintSearchResults) {
            System.out.print(oc.getId() + " ");
        }
        assertEquals("Smarts search expected number of results "+expectedResults,
                     fprintSearchResults.size(),expectedResults);
    }


    /*
     * Actual Junit test methods 
     */

    public void testSMARTS_1() throws Exception {
        smartsQuery("N1CC[O,N]CC1",89);
    }

    public void testSMARTS_2() throws Exception {
        smartsQuery("c1c[nH]nn1",1);
    }

    public void testSMARTS_3() throws Exception {
        smartsQuery("NC(CS)C(O)=O",19);
    }

    public void testSMARTS_4() throws Exception {
        smartsQuery("[H][C@@]1(CCC(C)=CC1=O)C(C)=C",3);
    }

    public void testSMARTS_5() throws Exception {
        smartsQuery("N#Cc1ccc(cc1)C(=O)c2ccc(cc2C)N3N=CC(=O)[O,N]C3(=O)",1);
    }

    public void testSMARTS_6() throws Exception {
        smartsQuery("N1ANC(=O)C1",1);
    }

    public void testSMARTS_7() throws Exception {
        smartsQuery("n1ancc1",93);
    }

}
