package uk.ac.ebi.orchem.singleton;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Properties;

import oracle.jdbc.pool.OracleConnectionCacheManager;
import oracle.jdbc.pool.OracleDataSource;

import uk.ac.ebi.orchem.Constants;


/**
 * Singleton to manage db connection for the mock web application.
 */
public class DbAgent extends Thread {

    public static final DbAgent DB_AGENT = new DbAgent();

    private OracleConnectionCacheManager connMgr = null;

    private OracleDataSource ods = null;
    Exception constructorException;
    public static final String CACHE_NAME = "myCache";

    private String dbName;

    public String getDbName() {
        return this.DB_AGENT.dbName;
    }

    /** Private constructor  */
    private DbAgent() {
        try {

            /* Set up an Oracle Connection cache to provide connections to non-Toplink database actions */

            Properties databaseProperties = Constants.getDatabaseProperties();

            ods = new OracleDataSource();
            ods.setURL(databaseProperties.getProperty("dbUrl"));
            ods.setUser(databaseProperties.getProperty("dbUser"));
            ods.setPassword(databaseProperties.getProperty("dbPass"));
            dbName = databaseProperties.getProperty("dbLabel");
            ods.setConnectionCachingEnabled(true);
            ods.setConnectionCacheName(CACHE_NAME);

            Properties cacheProperties = new Properties();
            cacheProperties.setProperty("MinLimit", databaseProperties.getProperty("connCacheMinLimit"));
            cacheProperties.setProperty("MaxLimit", databaseProperties.getProperty("connCacheMaxLimit"));
            cacheProperties.setProperty("InitialLimit", databaseProperties.getProperty("connCacheIniLimit"));


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

