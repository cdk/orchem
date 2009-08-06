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
package uk.ac.ebi.orchem.web;

import com.opensymphony.xwork2.ActionContext;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.shared.DatabaseAccess;

/**
 * Struts Action class to find a molecule in database
 * @author markr@ebi.ac.uk
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
            if (getId() != null && !getId().equals("")) {
                setMolFile(new DatabaseAccess().getMolfile(getId()));
            } else {
                HttpServletRequest request = ServletActionContext.getRequest();
                id = (String)(request.getAttribute("id"));
            }

            setMolFile(new DatabaseAccess().getMolfile(getId()));
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

