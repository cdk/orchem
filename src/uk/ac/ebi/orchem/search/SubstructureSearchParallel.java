/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2010  Mark Rijnbeek
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

import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OraclePreparedStatement;

import org.openscience.cdk.exception.CDKException;
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
     * @param queryType  SMILES, MOL etc
     * @throws Exception
     */
    public static void storeUserQueryInDB(Integer queryKey, Clob userQuery, String queryType) throws SQLException,
                                                                                                     CDKException {

        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();

        OraclePreparedStatement psDelUserQuery = 
            (OraclePreparedStatement)conn.prepareStatement(
              "delete orchem_user_queries where id = ?");
        psDelUserQuery.setInt(1,queryKey);
        psDelUserQuery.executeUpdate();
        psDelUserQuery.close();

        OraclePreparedStatement psInsertUserQuery = 
            (OraclePreparedStatement)conn.prepareStatement( 
              " insert into orchem_user_queries (id, timestamp, query, query_type ) " +
              " values (?,sysdate,?,?)");

        psInsertUserQuery.setInt(1,queryKey);
        psInsertUserQuery.setClob(2, userQuery);
        psInsertUserQuery.setString(3, queryType);

        psInsertUserQuery.executeUpdate();
        psInsertUserQuery.close();
        conn.commit();
    }
    


    /**
     * Using a query key (primary key value) selects a user query structure from the database.
     * @param queryKey
     * @return user queries (as CDK IAtomContainer(s) )
     * @throws SQLException
     */
    private static List<IAtomContainer> retrieveQueriesFromDB (Integer queryKey) throws Exception {
        List<IAtomContainer> queries = new ArrayList<IAtomContainer>();

        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();
        OraclePreparedStatement psFindUserQuery = 
            (OraclePreparedStatement)conn.prepareStatement(
              "select query,query_type from orchem_user_queries where id = ?");
        psFindUserQuery.setInt(1,queryKey);
        ResultSet res = psFindUserQuery.executeQuery();

        if (res.next())  {
            queries = translateUserQueryClob(res.getClob("query"), res.getString("query_type"));
        }
        else {
            throw new RuntimeException("Orchem parallel query issue! Could not find query with key "+queryKey);
        }
        res.close();
        psFindUserQuery.close();
        return queries;
    }

    /**
     * Run by each thread, verifies the user query is available in the queries map.
     * @param queryKey
     * @return the number of queries in the map (1..n)
     * @throws SQLException
     */
    public static int setUpEnvironment (Integer queryKey) throws Exception {

        if (!queries.containsKey(queryKey))  {
            List<IAtomContainer> queries = retrieveQueriesFromDB(queryKey);
            stash(queryKey,queries,"N");
            return queries.size();
        }
        else {
            return queries.get(queryKey).size();
        }
    }

    /**
     * Calls {@link #whereClauseFromFingerPrint(IAtomContainer,String)}
     * <BR>
     * Method scope=public -> used as Oracle Java stored procedure
     * 
     * @param queryKey
     * @param queryIdx
     * @return SQL where clause
     * @throws CDKException
     * @throws SQLException
     */
    public static String getWhereClause(Integer queryKey, Integer queryIdx) throws CDKException, SQLException {
        return whereClauseFromFingerPrint(queries.get(queryKey).get(queryIdx).mol, "N" );
    }

}



