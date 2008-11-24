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
public class  OrChemCompound implements SQLData, Serializable {

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
        formula = stream.readString();
        molFileClob = stream.readClob();
        score = stream.readFloat();
        
    }

    public void writeSQL(SQLOutput stream) throws SQLException {
        stream.writeString(id);
        stream.writeString(formula);
        stream.writeClob(molFileClob);
        stream.writeFloat(score);
    }

    /* regular bean attributes _________________________________________*/

    private String id;
    private String formula;
    private Clob molFileClob;
    private float score;


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
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
        String molfile=null;
        int clobLen;
        if (molFileClob!=null)  {
            try {
                clobLen = new Long(molFileClob.length()).intValue();
                molfile = (molFileClob.getSubString(1, clobLen));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return molfile;
    }

    
    
}

