package uk.ac.ebi.orchem.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class linked to the "orchem_parameters" table containing
 * information regarding the base compound table in the schema.
 */
public class OrChemParameters {


    public static final String COMPOUND_TABLE = new String("comp_tab_name");
    public static final String COMPOUND_PK = new String("comp_tab_pk_col");
    public static final String COMPOUND_MOL = new String("comp_tab_molfile_col");
    
    
    //TODO make static, do ONCE on init (after DB agent or something ..
    
    
    public static String getParameterValue(String paramName, Connection conn) {
        Statement stmtParam = null;
        ResultSet res = null;
        String val=null;
        try {
            stmtParam = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            res = stmtParam.executeQuery("select * from orchem_parameters");

            if (res.next())  {
                val=res.getString(paramName);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {

            try {
                res.close();
                stmtParam.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return val;

    }

}
