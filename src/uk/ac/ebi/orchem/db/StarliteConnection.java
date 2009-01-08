package uk.ac.ebi.orchem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Connection to Starlite schema
 */

public class StarliteConnection implements DbConnection{
    public Connection getDbConnection() throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        //return DriverManager.getConnection("jdbc:oracle:oci:@marx", "starlite28p", "star");
        return DriverManager.getConnection("jdbc:oracle:thin:@172.22.69.17:1521:marx", "starlite28p", "star");
    }
}
