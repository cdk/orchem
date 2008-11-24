package uk.ac.ebi.orchem.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection to Pubchem schema
 */
public class PubChemConnection implements DbConnection {

    public Connection getDbConnection() throws SQLException {
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        return DriverManager.getConnection("jdbc:oracle:thin:@ora-clu1a-vip:1531:litpub1", "crossref", "crossref");
        
    }

}
                        