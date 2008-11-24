package uk.ac.ebi.orchem.db;

import java.sql.Connection;
import java.sql.SQLException;

public interface DbConnection {

    Connection getDbConnection() throws SQLException;
}
