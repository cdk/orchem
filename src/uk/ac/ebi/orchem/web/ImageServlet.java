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


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openscience.jchempaint.controller.PhantomBondGenerator;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.jchempaint.renderer.AtomContainerRenderer;
import org.openscience.jchempaint.renderer.font.AWTFontManager;
import org.openscience.jchempaint.renderer.generators.ExtendedAtomGenerator;
import org.openscience.jchempaint.renderer.generators.ExternalHighlightGenerator;
import org.openscience.jchempaint.renderer.generators.HighlightAtomGenerator;
import org.openscience.jchempaint.renderer.generators.HighlightBondGenerator;
import org.openscience.jchempaint.renderer.generators.IGenerator;
import org.openscience.jchempaint.renderer.generators.LonePairGenerator;
import org.openscience.jchempaint.renderer.generators.MergeAtomsGenerator;
import org.openscience.jchempaint.renderer.generators.RadicalGenerator;
import org.openscience.jchempaint.renderer.generators.RingGenerator;
import org.openscience.jchempaint.renderer.generators.SelectAtomGenerator;
import org.openscience.jchempaint.renderer.generators.SelectBondGenerator;
import org.openscience.jchempaint.renderer.visitor.AWTDrawVisitor;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * Servlet to render the compound search results in the OrChem
 * using CDK JChempaint.
 * 
 * Used for demo web application.
 * 
 * @author markr@ebi.ac.uk
 *
 */
public class ImageServlet extends HttpServlet {

    private static MDLV2000Reader mdlReader = new MDLV2000Reader();

    private static List<IGenerator> generators;
    static {
       generators = new ArrayList<IGenerator>();
       generators.add(new RingGenerator());
       generators.add(new ExtendedAtomGenerator());
       generators.add(new LonePairGenerator());
       generators.add(new RadicalGenerator());
       generators.add(new ExternalHighlightGenerator());
       generators.add(new HighlightAtomGenerator());
       generators.add(new HighlightBondGenerator());
       generators.add(new SelectAtomGenerator());
       generators.add(new SelectBondGenerator());
       generators.add(new MergeAtomsGenerator());
       generators.add(new PhantomBondGenerator());
    }


    public ImageServlet() {
        System.out.println("ImageServlet constructed ... ");
    }


    /**
     * Simple implementation, only deals with requests for images. 
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp)  {

        String id = req.getParameter("id") != null ? req.getParameter("id") : "0";
        //System.out.println("GOT IMAGE REQUEST FOR ID: " + id);
        String mdl = null;


        // alternative 1 - refetch from database. why is this necessary anyway...
        /*
        try {
            mdl = new DatabaseAccess().getMolfile(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
        // alternative 1 - ends

        // alternative 2 - use session cache, prefered
        WebSearchResults wsr = (WebSearchResults)(req.getSession().getAttribute(Utils.SESSION_WEB_SEARCH_RESULTS));
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
            try {
                imageData = getImageFromMol(mdl);
            } catch (Exception e) {
                throw new RuntimeException (e.getMessage());
            }
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

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
       doGet(req, resp);
    }

    /**
     * Uses JChmepaint to render an image for a given mol file
     *
     * @param molfile
     * @return picture of molecule
     * @throws Exception
     */
    public static byte[] getImageFromMol(String molfile) throws Exception {

        byte[] image;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int hsize = 256;
        int vsize = 256;

        NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
        for(IAtom atom : molecule.atoms()) {
            atom.setValency(null); // otherwise ugly picture
        }

        // Inspect the first atom, check for 2D coords. If there, assume all present
        IAtom firstAtom = molecule.getAtom(0);
        if(firstAtom.getPoint2d()==null) {
            System.out.println("generating 2d coords");
            StructureDiagramGenerator gen2d = new StructureDiagramGenerator(molecule);
            gen2d.generateCoordinates();
        }

        AtomContainerRenderer renderer = new AtomContainerRenderer
            (generators,new AWTFontManager());

        Rectangle2D bounds = new Rectangle2D.Double(0, 0, hsize, vsize);
        BufferedImage bufferedImage = new BufferedImage(hsize, vsize, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.setBackground(Color.WHITE);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, hsize, hsize);

        renderer.paintMolecule(molecule, new AWTDrawVisitor(graphics), bounds, true);

        bufferedImage.flush();
        ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        byte[] bytesOut = baos.toByteArray();
        out.write(bytesOut);
        out.flush();

        image = out.toByteArray();
        return image;
    }
}
