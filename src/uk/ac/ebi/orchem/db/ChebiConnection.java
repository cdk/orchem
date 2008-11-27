package uk.ac.ebi.orchem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ChebiConnection implements DbConnection{
    public Connection getDbConnection() throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        return DriverManager.getConnection("jdbc:oracle:thin:@172.22.68.24:1521:marx", "chebi", "chebi");
    }
}
