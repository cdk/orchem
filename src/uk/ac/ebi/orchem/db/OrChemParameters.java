/*  
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Mark Rijnbeek
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
package uk.ac.ebi.orchem.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class linked to the "orchem_parameters" table containing
 * information regarding the base compound table in the schema.
 * 
 * @author markr@ebi.ac.uk
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
