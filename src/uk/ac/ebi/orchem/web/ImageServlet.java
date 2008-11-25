package uk.ac.ebi.orchem.web;

import chemaxon.formats.MolConverter;

import chemaxon.formats.MolFormatException;

import chemaxon.marvin.io.MolExportException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.io.InputStream;

import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ImageServlet extends HttpServlet {
    public ImageServlet() {
        System.out.println("ImageServlet constructed ... ");
    }


    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String id = req.getParameter("id") != null ? req.getParameter("id") : "0";
        //System.out.println("GOT IMAGE REQUEST FOR ID: " + id);
        String mdl=null;

        try {
            mdl = new DbSearchInvoker().getMolfile(id);
        } catch (SQLException e) {
            
        }
        byte[] imageData=null;
        if (mdl!=null)  {
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
