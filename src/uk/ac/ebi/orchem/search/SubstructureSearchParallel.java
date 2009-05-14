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

package uk.ac.ebi.orchem.search;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Iterator;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OraclePreparedStatement;

import oracle.sql.CLOB;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


/**
 * Class that provides functionality for performing an OrChem substructure search.<BR>
 * Extends {@link SubstructureSearch} with extra shit to perform the search in parallel.
 * Some methods in this class are called from PL/SQL and are wrapped as "Java stored procedures".
 *
 * @author markr@ebi.ac.uk, 2009
 *
 */

public class SubstructureSearchParallel extends SubstructureSearch {

    /**
     * Stores a user's query structure in database table "orchem_user_queries"
     * This query can then be retrieved by independent threads when necessary.
     * 
     * @param queryKey   id to store query with
     * @param userQuery  the user query (a string like "O=S=O", or a Mol file)
     * @param queryType 
     * @throws Exception
     */
    public static void storeUserQueryInDB(Integer queryKey, Clob userQuery, String queryType) throws SQLException,
                                                                                                     CDKException {

        IAtomContainer atc = translateUserQueryClob(userQuery, queryType);
        int pos=0;
        IAtom[] atoms = new IAtom[atc.getAtomCount()];
        for (Iterator<IAtom> atItr = atc.atoms().iterator(); atItr.hasNext(); ) {
            IAtom atom = atItr.next();
            atoms[pos] = atom;
            pos++;
        }
        String atomString = OrchemMoleculeBuilder.atomsAsString(atoms); 
        String bondString = OrchemMoleculeBuilder.bondsAsString(atoms,atc);

        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();

        OraclePreparedStatement psDelUserQuery = 
            (OraclePreparedStatement)conn.prepareStatement(
              "delete orchem_user_queries where id = ?");
        psDelUserQuery.setInt(1,queryKey);
        psDelUserQuery.executeUpdate();

        CLOB largeAtomsClob= CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
        CLOB largeBondsClob= CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
        largeAtomsClob.open(CLOB.MODE_READWRITE);
        largeBondsClob.open(CLOB.MODE_READWRITE);

        largeAtomsClob.setString(1, atomString);
        largeBondsClob.setString(1, bondString);
        
        OraclePreparedStatement psInsertUserQuery = 
            (OraclePreparedStatement)conn.prepareStatement(
              "insert into orchem_user_queries (id, timestamp, atoms,bonds) values (?,sysdate,?,?)");
        
        psInsertUserQuery.setInt(1,queryKey);
        psInsertUserQuery.setCLOB(2, largeAtomsClob);
        psInsertUserQuery.setCLOB(3, largeBondsClob);
        psInsertUserQuery.executeUpdate();

        largeAtomsClob.close();
        largeAtomsClob.freeTemporary();
        largeBondsClob.close();
        largeBondsClob.freeTemporary();

        psInsertUserQuery.close();
        conn.commit();
    }
    
    /**
     * Using a query key (primary key value) selects a user query structure from the database
     * @param queryKey
     * @return user query as CDK IAtomContainer
     * @throws SQLException
     */
    private static IAtomContainer retrieveQueryAtcontainerFromDB (Integer queryKey) throws SQLException {
        IAtomContainer atc=null;
        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();
        OraclePreparedStatement psFindUserQuery = 
            (OraclePreparedStatement)conn.prepareStatement(
              "select id, timestamp, atoms, bonds from orchem_user_queries where id = ?");
        psFindUserQuery.setInt(1,queryKey);
        ResultSet res = psFindUserQuery.executeQuery();
        if (res.next())  {
            String atoms = res.getString("atoms");
            String bonds = res.getString("bonds");
            atc=OrchemMoleculeBuilder.getBasicAtomContainer(atoms,bonds);
        }
        else {
            throw new RuntimeException("Orchem parallel query issue! Could not find query with key "+queryKey);
        }
        res.close();
        psFindUserQuery.close();
        return atc;
    }

    /**
     * Run by each thread, verifies the user query is available in the queries Map
     * @param queryKey
     * @throws SQLException
     */
    public static void checkThreadEnvironment (Integer queryKey) throws SQLException {
        if (!queries.containsKey(queryKey))  {
            IAtomContainer queryMolecule = retrieveQueryAtcontainerFromDB(queryKey);
            stash(queryKey,queryMolecule);
        }
    }

    /**
     * Calls {@link #whereClauseFromFingerPrint(IAtomContainer,String)}
     * <BR>
     * Method scope=public -> used as Oracle Java stored procedure
     *
     * @param queryKey
     * @param debugYN
     * @return
     * @throws CDKException
     * @throws SQLException
     */
    public static String getWhereClause(Integer queryKey, String debugYN) throws CDKException, SQLException {
        return whereClauseFromFingerPrint(retrieveQueryAtcontainerFromDB(queryKey), debugYN);
    }


    /**
     * Debug method (to stdout)
     * @param debugMessage
     * @param debug
     */
    private static void debug(String debugMessage, boolean debug) {
        if (debug) {
            System.out.println(new java.util.Date() + " debug: " + debugMessage);
        }
    }

}



