package uk.ac.ebi.orchem.web;

import uk.ac.ebi.orchem.Utils;


/**
 * Struts Action : given an "id", finds a compound in the database and returns its MDL (Molfile).
 * Part of mock web-application. Not core cartridge functionality.
 *
 */
public class LookupMolfileAction extends SessionAwareAction {

   public LookupMolfileAction () {}

    public String execute() throws Exception {
        try {
            setMolFile(new DbSearchInvoker().getMolfile(getId()));
            return "lookupDone";
        } catch (Exception ex) {
            this.setExceptionMsg(Utils.getErrorString(ex));
            return "error";
        }
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

