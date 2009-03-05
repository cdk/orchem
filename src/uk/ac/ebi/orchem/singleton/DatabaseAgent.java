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
package uk.ac.ebi.orchem.singleton;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Properties;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

import uk.ac.ebi.orchem.Constants;


/**
 * 
 * Singleton to manage db connection for the OrChem demo web application.
 * <B>This class is not part of the Java stored procedure set </B>.
 * 
 * @author markr@ebi.ac.uk
 */
public class DatabaseAgent extends Thread {

    public static final DatabaseAgent DB_AGENT = new DatabaseAgent();

    private OracleConnectionCacheManager connMgr = null;

    private OracleDataSource ods = null;
    Exception constructorException;
    public static final String CACHE_NAME = "myCache";

    private String dbName;

    public String getDbName() {
        return this.DB_AGENT.dbName;
    }

    /** Private constructor  */
    private DatabaseAgent() {
        try {

            /* Set up an Oracle Connection cache for the demo web application  */

            Properties props = Constants.getWebAppProperties();

            ods = new OracleDataSource();
            ods.setURL(props.getProperty("dbUrl"));
            ods.setUser(props.getProperty("dbUser"));
            ods.setPassword(props.getProperty("dbPass"));
            dbName = props.getProperty("dbLabel");
            ods.setConnectionCachingEnabled(true);
            ods.setConnectionCacheName(CACHE_NAME);

            Properties cacheProperties = new Properties();
            cacheProperties.setProperty("MinLimit", props.getProperty("connCacheMinLimit"));
            cacheProperties.setProperty("MaxLimit", props.getProperty("connCacheMaxLimit"));
            cacheProperties.setProperty("InitialLimit", props.getProperty("connCacheIniLimit"));


            connMgr = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
            if (connMgr.existsCache(CACHE_NAME)) {
                System.out.println("Cache " + CACHE_NAME + " already exists, cleaning up first.");
                connMgr.purgeCache(CACHE_NAME, true);
                connMgr.removeCache(CACHE_NAME, 0);
            }

            connMgr.createCache(CACHE_NAME, ods, cacheProperties);


        } catch (Exception fatal) {
            System.out.println("Error in singleton dao agent :" + fatal);
            constructorException = fatal;
        }
    }


    /**
     * Retrieve a connection from the general connection pool, for non Toplink
     * database access. Don't forget to return it when you are done. See {@link #returnCachedConnection(Connection)}
     * Sleeps short while if the connection cache has run dry and then tries again.
     *
     * @return a database connection
     * @throws SQLException
     */
    public Connection getCachedConnection() throws SQLException {
        //return connectionCache.getConnection(); // deprecated

        Connection conn = null;
        boolean verifiedConnection = false;
        while (!verifiedConnection) {
            conn = ods.getConnection();
            if (conn != null) {
                verifiedConnection = true;
            } else {
                try {
                    System.out.println("On hold - no connection available from OracleConnectionCacheManager ");
                    sleep(1000);
                } catch (InterruptedException ir) {
                    ir = null;
                }
            }
        }
        return conn;
    }

    /**
     * Return a connection back to the connection pool. ALWAYS call this method
     * eventually after calling {@link #getCachedConnection() }.
     * @param cachedConnection, back to the pool
     * @throws SQLException
     */

    public void returnCachedConnection(Connection cachedConnection) throws SQLException {
        if (cachedConnection != null) {
            cachedConnection.close();
        }
    }


    public static String getUser() {
        return DB_AGENT.ods.getUser();
    }

}

