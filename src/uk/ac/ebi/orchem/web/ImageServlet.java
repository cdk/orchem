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
package uk.ac.ebi.orchem.web;


import chemaxon.formats.MolConverter;
import chemaxon.formats.MolFormatException;

import chemaxon.marvin.io.MolExportException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.SQLException;

import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import uk.ac.ebi.orchem.Constants;
import uk.ac.ebi.orchem.bean.OrChemCompound;


/**
 * Servlet to render the compound search results in the OrChem
 * demo web application.
 * @author markr@ebi.ac.uk
 *
 */
public class ImageServlet extends HttpServlet {
    public ImageServlet() {
        System.out.println("ImageServlet constructed ... ");
    }


    /**
     * Simple implementation, only deals with requests for images. 
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String id = req.getParameter("id") != null ? req.getParameter("id") : "0";
        //System.out.println("GOT IMAGE REQUEST FOR ID: " + id);
        String mdl = null;

        // alternative 1 - refetch from database. why is this necessary anyway...
        /*
        try {
            mdl = new DbSearchInvoker().getMolfile(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
        // alternative 1 - ends

        // alternative 2 - use session cache
        WebSearchResults wsr = (WebSearchResults)(req.getSession().getAttribute(Constants.SESSION_WEB_SEARCH_RESULTS));
        Iterator itr = wsr.getSearchResults().iterator();
        boolean found = false;
        while (itr.hasNext() && !found) {
            OrChemCompound oc = (OrChemCompound)itr.next();
            if (oc.getId().equals(id)) {
                mdl = oc.getMolfile();
                found = true;
            }
        }
        // alternative 2 - ends

        byte[] imageData = null;
        if (mdl != null) {
            imageData = getImageFromMol(mdl);
        }

        try {
            resp.setHeader("Expires", "0");
            resp.setHeader("Pragma", "No-cache");
            resp.addHeader("Cache-Control", "no-store, no-cache, max-age=0, must-revalidate");

            resp.setContentType("image/png");
            resp.getOutputStream().write(imageData, 0, imageData.length);
            resp.getOutputStream().close();
        } catch (Exception e) {
            System.out.println("Error creating image:" + e.getMessage());
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    /**
     * Uses chemaxon library to render an image for a give mol file
     * @param mol
     * @return image as byte array
     * @throws MolFormatException
     * @throws MolExportException
     * @throws IOException
     */
    public static byte[] getImageFromMol(String mol) throws MolFormatException, MolExportException, IOException {
        byte[] image;
        InputStream is = new ByteArrayInputStream(mol.getBytes());
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MolConverter molConverter = new MolConverter(is, os, "png", false);
        molConverter.convert();
        image = os.toByteArray();
        return image;
    }

}
