package uk.ac.ebi.orchem;

import java.util.Properties;

public class Constants {
    // prevent inlining
    // See http://www.javaspecialists.eu/archive/el_GR/Issue114.html
    public static final String SESSION_WEB_SEARCH_RESULTS = new String("wsr");

    private static  Properties databaseProperties = PropertyLoader.loadProperties("database.properties");
    public static Properties getDatabaseProperties() {
        return databaseProperties;
    }
}
