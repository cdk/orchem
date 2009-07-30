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
package uk.ac.ebi.orchem.shared;

import java.io.Writer;

import java.sql.Array;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;

import oracle.sql.CLOB;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;

import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.singleton.DatabaseAgent;


/**
 * OrChem database interaction.
 * Used by demo web-application and Unit testing - so not really "core" OrChem functionality (not stored in DB !)
 *
 * @author markr@ebi.ac.uk
 */
public class DatabaseAccess {


    /**
     * 
     * Run a database stored procedure similarity search 
     *
     * @param userQuery
     * @param queryType see {@link uk.ac.ebi.orchem.Utils}
     * @param conn
     * @param tanimotoCutoff
     * @param topN
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List similaritySearch(String userQuery, String queryType, OracleConnection conn, float tanimotoCutoff, int topN) throws SQLException,
                                                                                                        ClassNotFoundException {
        String plsqlCall = "begin ?:=orchem_simsearch.search(?,?,?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall);
        ocs.registerOutParameter(1, OracleTypes.ARRAY,"ORCHEM_COMPOUND_LIST");
        ocs.setString(2, userQuery);
        ocs.setString(3, queryType);
        ocs.setFloat(4, tanimotoCutoff);
        ocs.setInt(5, topN);
        ocs.setString(6, "N");
        //ocs.executeUpdate();
        ocs.execute();
        Array cARRAY = ocs.getArray(1);
        Map map = conn.getTypeMap();
        map.put("ORCHEM_COMPOUND", Class.forName("uk.ac.ebi.orchem.bean.OrChemCompound",false,Thread.currentThread().getContextClassLoader())); 
        Object[] compoundArray = (Object[])cARRAY.getArray();
        return Arrays.asList(compoundArray);
    }


    /**
     * Run a database stored procedure substructure search 
     *
     * @param userQuery
     * @param queryType see {@link uk.ac.ebi.orchem.Utils}
     * @param conn
     * @param topN
     * @return list of {@link uk.ac.ebi.orchem.bean.OrChemCompound}
     * @throws SQLException
     * @throws ClassNotFoundException
     */
     public List substructureSearch(String userQuery, String queryType, OracleConnection conn, int topN) throws SQLException, ClassNotFoundException {

        conn.setDefaultRowPrefetch(1); //?? todo necessary?
        List<OrChemCompound> compounds = new ArrayList<OrChemCompound>();
        OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement("select id, mol_file from table(orchem_subsearch.search(?,?,?,'N'))   ",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

        //pstmt.setString(1, userQuery);  // Do not use setString !
        //                                // Works for small queries, but ORA-01460 when > 4000

        pstmt.setCLOB(1, getCLOB (userQuery,conn)); 
        pstmt.setString(2, queryType); 
        pstmt.setInt(3, topN);

        ResultSet res = pstmt.executeQuery();
        while (res.next())  {
           OrChemCompound cmp = new OrChemCompound();
            cmp.setId(res.getString("id"));
            cmp.setMolFileClob(res.getClob("mol_file"));
            compounds.add(cmp);
        }
        res.close();
        pstmt.close();
        return compounds;
    }



    /**
     * Substructure search in parallel mode (mind the overhead)
     * @param userQuery
     * @param queryType see {@link uk.ac.ebi.orchem.Utils}
     * @param conn
     * @param topN
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<OrChemCompound> substructureSearchParallel(String userQuery, String queryType, OracleConnection conn, int topN) throws SQLException, ClassNotFoundException {

       conn.setDefaultRowPrefetch(25);
       String setUp = "begin ?:= orchem_subsearch_par.setup (?,?); end;";
       OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(setUp);
       ocs.registerOutParameter(1, OracleTypes.INTEGER);
       //ocs.setString(2, userQuery); // Don't use. MAY cause ORA-01460
       ocs.setCLOB(2, getCLOB (userQuery,conn));
       ocs.setString(3, queryType);

       //ocs.executeUpdate();
       ocs.execute();
       int key = ocs.getInt(1);
       ocs.close();

       List<OrChemCompound> compounds = new ArrayList<OrChemCompound>();
       //choose FORCE FULL SCAN Y for smaller databases, N for larger ones (millions of compounds)
       PreparedStatement pstmt = conn.prepareStatement("select id, mol_file from table(orchem_subsearch_par.search(?,?,'N'))   ",ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
       pstmt.setInt(1,key);
       pstmt.setInt(2,topN);

       ResultSet res = pstmt.executeQuery();
       while (res.next())  {
          OrChemCompound cmp = new OrChemCompound();
           cmp.setId(res.getString("id"));
           cmp.setMolFileClob(res.getClob("mol_file"));
           compounds.add(cmp);
       }
       res.close();
       pstmt.close();
       return compounds;
    }




    /**
     * Gets a molfile (clob) from the database for a given compound id
     * @param id
     * @return
     * @throws SQLException
     */
    public String getMolfile(String id) throws SQLException {
        String molfile =null;
        Connection conn = null;
        conn = DatabaseAgent.DB_AGENT.getCachedConnection();

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
                DatabaseAgent.DB_AGENT.returnCachedConnection(conn);
        }
        return molfile;
    }

    /**
     * Retrieve a list of molecules from the database
     * @param pStmt
     * @return
     * @throws SQLException
     * @throws CDKException
     */
    public List<WrappedAtomContainer> getMols(PreparedStatement pStmt) throws SQLException, CDKException {
        MDLV2000Reader mdlReader = new MDLV2000Reader();
        List<WrappedAtomContainer> molecules = new ArrayList<WrappedAtomContainer>();
        ResultSet res = pStmt.executeQuery();
        String mdl = null;
        while (res.next()) {
            mdl = res.getString("molfile");
            try {
                NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, mdl);
                molecules.add(new WrappedAtomContainer(molecule, res.getInt("id")));
            } catch (Exception e) {
                System.err.println("Error for ID " + res.getInt("id") + ": " + e.getMessage());
            }
        }
        res.close();
        return molecules;

    }


    /**
     * Build list of all compounds ('queries' and 'targets')
     * @param conn
     * @return
     * @throws SQLException
     * @throws CDKException
     */
    public List<WrappedAtomContainer> getAllCompounds(OracleConnection conn) throws SQLException, CDKException {
        PreparedStatement pStmt = conn.prepareStatement("select id, molfile from orchem_compound_sample");
        List<WrappedAtomContainer> ret = getMols(pStmt);
        pStmt.close();
        return ret;
    }


    /**
     * Creates CLOB with a String as input
     * <br>
     * Taken from<br>
     * http://www.oracle.com/technology/sample_code/tech/java/codesnippet/jdbc/lob/LobToSP.html
     * 
     * @param clobData String input
     * @param conn ection
     * @return the String as a Clob
     * @throws Exception
     */

    private CLOB getCLOB(String clobData, OracleConnection conn) {
        CLOB tempClob = null;

        try {
            //  create a new temporary CLOB
            tempClob = CLOB.createTemporary(conn, true, CLOB.DURATION_SESSION);
            // Open the temporary CLOB in readwrite mode to enable writing
            tempClob.open(CLOB.MODE_READWRITE);

            // Get the output stream to write
            //deprecated Writer tempClobWriter = tempClob.getCharacterOutputStream();
            Writer tempClobWriter = tempClob.setCharacterStream(0L);

            // Write the data into the temporary CLOB
            tempClobWriter.write(clobData);
            // Flush and close the stream
            tempClobWriter.flush();
            tempClobWriter.close();
            // Close the temporary CLOB
            tempClob.close();
        } catch (Exception exp) {
            // Free CLOB object
            // do something
            try {
                tempClob.freeTemporary();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tempClob;
    }
}
