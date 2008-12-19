package uk.ac.ebi.orchem.web;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.db.StarliteConnection;
import uk.ac.ebi.orchem.singleton.DbAgent;


public class DBSearchInvoker_workaround {
    /*
    public List similaritySearchMol(String molfile, Connection conn, float tanimotoCutoff, int topN) throws SQLException,
                                                                                                            ClassNotFoundException {

        String sql = "select * from table(orchem.similarity_search_mol(?,?,?,?))";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, molfile);
        ps.setFloat(2, tanimotoCutoff);
        ps.setInt(3, topN);
        ps.setString(4, "N");
        return executePS(ps, conn);
    }


    public List similaritySearchSmiles(String smiles, Connection conn, float tanimotoCutoff, int topN) throws SQLException,
                                                                                                              ClassNotFoundException {

        String sql = "select * from table(orchem.similarity_search_smiles(?,?,?,?))";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, smiles);
        ps.setFloat(2, tanimotoCutoff);
        ps.setInt(3, topN);
        ps.setString(4, "N");
        return executePS(ps, conn);
    }


    public List substructureSearchMol(String molfile, Connection conn, int topN) throws SQLException, ClassNotFoundException {

        String sql = "select * from table(orchem.substructure_search_mol(?,?,?))";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, molfile);
        ps.setInt(2, topN);
        ps.setString(3, "N");
        return executePS(ps, conn);
    }


    public List substructureSearchSmiles(String smiles, Connection conn, int topN) throws SQLException,
                                                                                          ClassNotFoundException {

        String sql = "select * from table(orchem.substructure_search_smiles(?,?,?))";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, smiles);
        ps.setInt(2, topN);
        ps.setString(3, "N");
        return executePS (ps, conn);
    }

    private List executePS(PreparedStatement ps, Connection conn) throws SQLException {
        ResultSet res = ps.executeQuery();
        List compoundList = new ArrayList();
        while (res.next()) {
            OrChemCompound comp = new OrChemCompound();
            comp.setId(res.getString("id"));
            comp.setMolFileClob(res.getClob("mol_file"));
            comp.setScore(res.getFloat("score"));
            compoundList.add(comp);
        }
        res.close();
        ps.close();
        return compoundList;
    }


    public String getMolfile(String id) throws SQLException {
        String molfile = null;
        Connection conn = null;
        conn = DbAgent.DB_AGENT.getCachedConnection();

        String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
        String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
        String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

        String query =
            " select " + compoundTableMolfileColumn + " from " + compoundTableName + " where " + compoundTablePkColumn +
            "=? ";

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
  }
*/
}
