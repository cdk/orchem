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
package uk.ac.ebi.orchem.test;

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
import oracle.jdbc.OracleTypes;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;

import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * Database access methods for unit testing
 *
 */
public class DbApi {

    /**
     * Retrieve a list of molecules from the database
     * @param pStmt
     * @return
     * @throws SQLException
     * @throws CDKException
     */
    List<MyAtomContainer> getMols(PreparedStatement pStmt) throws SQLException, CDKException {
        MDLV2000Reader mdlReader = new MDLV2000Reader();
        List<MyAtomContainer> molecules = new ArrayList<MyAtomContainer>();
        ResultSet res = pStmt.executeQuery();
        String mdl = null;
        while (res.next()) {
            mdl = res.getString("molfile");
            try {
                NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, mdl);
                molecules.add(new MyAtomContainer(molecule, res.getInt("id")));
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
    List<MyAtomContainer> getAllCompounds(Connection conn) throws SQLException, CDKException {
        PreparedStatement pStmt = conn.prepareStatement("select id, molfile from orchem_compound_sample");
        List<MyAtomContainer> ret = getMols(pStmt);
        pStmt.close();
        return ret;
    }

    /**
     * Run a database stored procedure substructure search using a mol file as input
     *
     * @param molfile
     * @param conn
     * @param topN
     * @return list of {@link uk.ac.ebi.orchem.bean.OrChemCompound}
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    List<OrChemCompound> substructureSearchMol(String molfile, Connection conn, int topN) throws SQLException, ClassNotFoundException {
        String plsqlCall = "begin ?:=orchem.substructure_search_mol(?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall);
        ocs.registerOutParameter(1, OracleTypes.ARRAY, "ORCHEM_COMPOUND_LIST");
        ocs.setString(2, molfile);
        ocs.setInt(3, topN);
        ocs.setString(4, "N");
        return executeOCS(ocs, conn);
    }

    /**
     * Run a database stored procedure similarity search using a mol file as input
     *
     * @param molfile
     * @param conn
     * @param topN
     * @param minScore
     * @return list of {@link uk.ac.ebi.orchem.bean.OrChemCompound}
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    List<OrChemCompound> similaritySearchMol(String molfile, Connection conn, int topN, float minScore) throws SQLException, ClassNotFoundException {
        String plsqlCall = "begin ?:=orchem.similarity_search_mol(?,?,?,?); end;";
        OracleCallableStatement ocs = (OracleCallableStatement)conn.prepareCall(plsqlCall);
        ocs.registerOutParameter(1, OracleTypes.ARRAY, "ORCHEM_COMPOUND_LIST");
        ocs.setString(2, molfile);
        ocs.setFloat(3,minScore);
        ocs.setInt(4, topN);
        ocs.setString(5, "N");
        return executeOCS(ocs, conn);
    }


    /**
     * Execute oracle callable statement
     * @param ocs
     * @param conn
     * @return a list of {@link uk.ac.ebi.orchem.bean.OrChemCompound orchem compounds }
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    List executeOCS(OracleCallableStatement ocs, Connection conn) throws SQLException, ClassNotFoundException {
        ocs.executeUpdate();
        Array cARRAY = ocs.getArray(1);
        Map map = conn.getTypeMap();
        map.put("ORCHEM_COMPOUND", Class.forName("uk.ac.ebi.orchem.bean.OrChemCompound", false, Thread.currentThread().getContextClassLoader()));
        Object[] compoundArray = (Object[])cARRAY.getArray();
        return Arrays.asList(compoundArray);
    }


}
