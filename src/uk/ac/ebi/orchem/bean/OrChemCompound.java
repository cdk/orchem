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
package uk.ac.ebi.orchem.bean;


import java.io.Serializable;

import java.sql.Clob;
import java.sql.SQLData;

import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;

/**
 *
 * Compound bean for basic compound data.<BR>
 * Implements SQLData: this class is used to pass results coming
 * from the Java stored procedures that perform substructure and similarity
 * searching in the (Oracle) database.
 *
 * @author markr@ebi.ac.uk
 *
 */
public class OrChemCompound implements SQLData, Serializable, Comparable {

    public boolean equals(Object obj) {
        return this.getId().equals(((OrChemCompound)obj).getId());
    }

    /* SQLData section _________________________________________________*/

    private String sql_type = "ORCHEM_COMPOUND";

    public String getSQLTypeName() throws SQLException {
        return sql_type;
    }

    public void readSQL(SQLInput stream, String typeName) throws SQLException {
        sql_type = typeName;
        id = stream.readString();
        molFileClob = stream.readClob();
        score = stream.readFloat();

    }

    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeString(id);
        stream.writeClob(molFileClob);
        stream.writeFloat(score);
    }

    /* regular bean attributes _________________________________________*/

    private String id;
    private Clob molFileClob;
    private float score;


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMolFileClob(Clob molFile) {
        this.molFileClob = molFile;
    }

    public Clob getMolFileClob() {
        return molFileClob;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getScore() {
        return score;
    }

    /**
     * Convenient getter method to get molfile as a String instead of a Clob
     * @return molfile as a String
     */
    public String getMolfile() {
        String molfile = null;
        int clobLen;
        if (molFileClob != null) {
            try {
                clobLen = new Long(molFileClob.length()).intValue();
                molfile = (molFileClob.getSubString(1, clobLen));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return molfile;
    }


    public int compareTo(Object o) {

        int comp=0;
        if (o instanceof OrChemCompound)  {
            OrChemCompound that = (OrChemCompound )o;
            try {
                Integer thisId = new Integer(id);
                Integer thatId = new Integer(that.getId());
                comp = thisId.compareTo(thatId);
                
            } catch (Exception ex) {
                comp = id.compareTo(that.getId());
            }   
        }
        return comp;        
    }
}

