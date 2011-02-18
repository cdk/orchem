package uk.ac.ebi.orchem.test;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;

import junit.framework.Assert;
import junit.framework.TestCase;

import oracle.jdbc.driver.OracleConnection;

import uk.ac.ebi.orchem.PropertyLoader;

/**
 *
 */
public class TestCalculation extends TestCase {


    static OracleConnection conn;
    static {
        try {
            Properties properties = PropertyLoader.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = (OracleConnection)DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*

 orchem_calculate.mass('C','SMILES','Y') M from dual;

  F                             M
  -------------------- ----------
  CH4                  16.0424989
  
orchem_calculate.mass('C','SMILES','N') M from dual;
  
  F                             M
  -------------------- ----------
  C                    12.0107359


> select id, orchem_calculate.charge(molfile,'MOL') as chrg 
  from orchem_compound_sample 
  where   orchem_calculate.charge(molfile,'MOL') != 0;

        ID       CHRG
---------- ----------
      1051          2
      1052          2
      1053          2
      1055          2
      1056          2
      1057         -1
*/


    /**
     * Calculates whatever for a given input SMILES
     * @param what what to calculate
     * @param smiles input molecule
     * @param addHydrogens true or false for adding hydrogens to the output formula
     * @param expected value to be compare to output
     * @throws Exception
     */
    private void calcFormula(String what, String smiles, Boolean addHydrogens, String expected) throws Exception {
        PreparedStatement pStmt =null;
        if (addHydrogens!=null)
            pStmt = conn.prepareStatement
                ("select orchem_calculate."+what+"(?,'SMILES',?) as calc from dual");
        else
            pStmt = conn.prepareStatement
                ("select orchem_calculate."+what+"(?,'SMILES') as calc from dual");
            

        pStmt.setString(1, smiles);

        if (addHydrogens!=null) 
            if (addHydrogens) 
                pStmt.setString(2, "Y");
            else
                pStmt.setString(2, "N");

        ResultSet res = pStmt.executeQuery();
        String calculated="";
        if (res.next()) {
            calculated=res.getString("calc");
            System.out.println("\ninput: " + smiles + "\n" +
                               what+": " + calculated);
            System.out.println("_______________________________________________________________");

        }
        else {
            throw new RuntimeException("calculation call failed");
        }
        
        res.close();
        pStmt.close();
        Assert.assertEquals(expected, calculated);
    }


    public void testFormula1 () throws Exception {
        calcFormula("formula","O5CCN(CC=3C=1C=CC=CC=1C(=C2C=CC=CC2=3)CN4CCOCC4)CC5",true,"C24H28N2O2");
    }     

    public void testFormula2 () throws Exception {
        calcFormula("formula","O5CCN(CC=3C=1C=CC=CC=1C(=C2C=CC=CC2=3)CN4CCOCC4)CC5",false,"C24N2O2");
    }     

    public void testFormula3 () throws Exception {
        calcFormula("formula","CC(C)(C)NCC(O)c1cc(Cl)c(N)c(Cl)c1",true,"C12H18Cl2N2O");
    }     

    public void testMass1 () throws Exception {
        calcFormula("mass","C",true,"16.0424989120912");
    }     

    public void testMass2 () throws Exception {
        calcFormula("mass","C",false,"12.010735896788");
    }     

    public void testCharge1 () throws Exception {
        calcFormula("charge","FC=C(C1=CC=C(OC)C(OC)=C1)CN.[Cl-]",null,"-1");
    }     

    public void testCharge2 () throws Exception {
        calcFormula("charge","C3CCCCCNC6=CC=[N+](CC=1C=CC(=CC=1)CC2=CC=C(C=C2)C[N+]=5C=CC(NCCCC3)=C4C=CC=CC4=5)C7=CC=CC=C67",null,"2");
    }     


}
