package uk.ac.ebi.orchem.web;

import java.sql.Array;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.db.StarliteConnection;
import uk.ac.ebi.orchem.singleton.DbAgent;


/**
 * Invokes a stored java procedure to perform a similarity or substructure search
 * Part of mock web-application. Not core cartridge functionality.
 *
 * @author markr@ebi.ac.uk
 *
 */
public class DbSearchInvoker {


    public List similaritySearchMol(String molfile, Connection conn, float tanimotoCutoff, int topN) throws SQLException,
                                                                                                         ClassNotFoundException {
        String plsqlCall = "begin ?:= orchem.similarity_search_mol(?,?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall);
        ocs.registerOutParameter(1, OracleTypes.ARRAY,"ORCHEM_COMPOUND_LIST");
        ocs.setString(2, molfile);
        ocs.setFloat(3, tanimotoCutoff);
        ocs.setInt(4, topN);
        ocs.setString(5, "N");

        return executeOCS(ocs, conn);
    }


    public List similaritySearchSmiles(String smiles, Connection conn, float tanimotoCutoff, int topN) throws SQLException,
                                                                                                         ClassNotFoundException {
        String plsqlCall = "begin ?:= orchem.similarity_search_smiles(?,?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall);
        ocs.registerOutParameter(1, OracleTypes.ARRAY,"ORCHEM_COMPOUND_LIST");
        ocs.setString(2, smiles);
        ocs.setFloat(3, tanimotoCutoff);
        ocs.setInt(4, topN);
        ocs.setString(5, "N");

        return executeOCS(ocs, conn);
    }


    public List substructureSearchMol(String molfile, Connection conn, int topN) throws SQLException, ClassNotFoundException {

        String plsqlCall = "begin ?:=orchem.substructure_search_mol(?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall);
        ocs.registerOutParameter(1, OracleTypes.ARRAY,"ORCHEM_COMPOUND_LIST");
        ocs.setString(2, molfile);
        ocs.setInt(3, topN);
        ocs.setString(4, "N");

        return executeOCS(ocs, conn);
    }


    public List substructureSearchSmiles(String smiles, Connection conn, int topN) throws SQLException, ClassNotFoundException {

        String plsqlCall = "begin ?:=orchem.substructure_search_smiles(?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ocs.registerOutParameter(1, OracleTypes.ARRAY,"ORCHEM_COMPOUND_LIST");
        ocs.setString(2, smiles);
        ocs.setInt(3, topN);
        ocs.setString(4, "N");

        return executeOCS(ocs, conn);
    }

    private List executeOCS(OracleCallableStatement ocs, Connection conn) throws SQLException, ClassNotFoundException {
        ocs.executeUpdate();
        Array cARRAY = ocs.getArray(1);
        Map map = conn.getTypeMap();

        map.put("ORCHEM_COMPOUND", Class.forName("uk.ac.ebi.orchem.bean.OrChemCompound",false,Thread.currentThread().getContextClassLoader())); 
        Object[] compoundArray = (Object[])cARRAY.getArray();
        return Arrays.asList(compoundArray);
    }


    public String getMolfile(String id) throws SQLException {
        String molfile =null;
        Connection conn = null;
        conn = DbAgent.DB_AGENT.getCachedConnection();

        String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
        String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
        String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

        String query = 
        " select " +  compoundTableMolfileColumn +
        " from "+compoundTableName+
        " where "+compoundTablePkColumn+"=? ";

        try {
            PreparedStatement psst = conn.prepareStatement(query);
            psst.setString(1, id);
            ResultSet res = psst.executeQuery();
            if (res.next() && res.getClob(compoundTableMolfileColumn) != null) {
                Clob molFileClob = res.getClob(compoundTableMolfileColumn);
                int clobLen = new Long(molFileClob.length()).intValue();
                molfile = (molFileClob.getSubString(1, clobLen));
            }
            res.close();
            psst.close();
        } finally {
            if (conn != null)
                DbAgent.DB_AGENT.returnCachedConnection(conn);
        }
        return molfile;
    }


    //
    // TEMP main() method for standalone testing
    // TODO remove
    public static void main(String[] args) throws SQLException, Exception {
        System.out.println("1"+new java.util.Date());

        Connection conn = new StarliteConnection().getDbConnection();
        Statement stmtQueryCompounds = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet res = stmtQueryCompounds.executeQuery
        ("select molfile from compounds where molregno=11002");

        Clob molFileClob = null;
        if (res.next()) {

            molFileClob = res.getClob("molfile");
            int clobLen = new Long(molFileClob.length()).intValue();
            String molfile = (molFileClob.getSubString(1, clobLen));
            System.out.println("2"+new java.util.Date());

            DbSearchInvoker inv = new DbSearchInvoker();
            List<OrChemCompound> cl = inv.similaritySearchMol(molfile, conn, 0.9f,50);
            System.out.println("3"+new java.util.Date());


        }
    }
}
