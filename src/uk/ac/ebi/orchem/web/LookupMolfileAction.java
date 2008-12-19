package uk.ac.ebi.orchem.web;

import uk.ac.ebi.orchem.Utils;

/**
 * Struts Action class
 *
 */
public class LookupMolfileAction extends SessionAwareAction {

    public LookupMolfileAction() {
    }

    /** 
     * Given an "id", finds a compound in the database and returns its Mol file.
     * Part of demo web-application. Not core cartridge functionality.
     * @return navigation string
     * @throws Exception
     */
    public String execute() throws Exception {
        try {
            setMolFile(new DbSearchInvoker().getMolfile(getId()));
            return "lookupDone";
        } catch (Exception ex) {
            this.setExceptionMsg(Utils.getErrorString(ex));
            return "error";
        }
    }

    /* Getters and setters follow _____________________________________________________*/

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

