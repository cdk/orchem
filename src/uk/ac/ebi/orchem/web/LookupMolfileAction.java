package uk.ac.ebi.orchem.web;

import com.opensymphony.xwork2.ActionSupport;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.db.StarliteConnection;
import uk.ac.ebi.orchem.singleton.DbAgent;

/**
 * Struts Action : given an "id", finds a compound in the database and returns its MDL (Molfile).
 * Part of mock web-application. Not core cartridge functionality.
 * 
 */
public class LookupMolfileAction extends ActionSupport {

    public String execute() throws Exception {
        setMolFile(new DbSearchInvoker().getMolfile(getId()));
        return "lookupDone";
    }

    private String id;
    private String molFile;


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMolFile(String molFile) {
        this.molFile = molFile;
    }

    public String getMolFile() {
        return molFile;
    }

}

