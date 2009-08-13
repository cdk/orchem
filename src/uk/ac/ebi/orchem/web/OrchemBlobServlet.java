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
import java.io.InputStream;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.sql.BLOB;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.renderer.Java2DRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;

import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * Servlet class to display a compound as a graphical image.
 *
 *
 * TODO:
 * Status=experimental; works, but seems slow. Perhaps
 * the java should be executed outside the database instead of
 * as a java stored procedure? Worth a try to compare
 *
 *
 */
public class OrchemBlobServlet extends HttpServlet {

    static String url = "jdbc:oracle:thin:@localhost:1521:marx";
    static String username = "testuser";
    static String password = "testpw";

    static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection getOracleConnection() throws Exception {
        return conn;
    }

    /**
     * Servlet request handler
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     */
    /*
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String id = request.getParameter("id");
        if (id != null && !id.equals("")) {

            String query =
            "select orchem_convert.molfiletojpeg(molfile,256,256) as image " +
            "from orchem_compound_sample where id=?";
            ServletOutputStream out = response.getOutputStream();

            Blob molecule = null;
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = getOracleConnection();
                stmt = conn.prepareStatement(query);
                stmt.setString(1, id);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    molecule = rs.getBlob(1);
                    response.setContentType("image/jpeg");
                    InputStream in = molecule.getBinaryStream();
                    int length = (int)molecule.length();

                    int bufferSize = 1024;
                    byte[] buffer = new byte[bufferSize];

                    while ((length = in.read(buffer)) != -1) {
                        System.out.println("writing " + length + " bytes");
                        out.write(buffer, 0, length);
                    }

                    in.close();
                    out.flush();
                }

            } catch (Exception e) {
                System.out.println("Error creating image:" + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    */


    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        try {
            String id = request.getParameter("id");
            if (id != null && !id.equals("")) {

                MDLV2000Reader mdlReader = new MDLV2000Reader();

                String query = "select molfile from orchem_compound_sample where id=?";

                Connection conn = null;
                PreparedStatement stmt = null;
                ResultSet rs = null;

                ServletOutputStream out = response.getOutputStream();
                int hsize = 256;
                int vsize = 256;
                conn = getOracleConnection();
                stmt = conn.prepareStatement(query);
                stmt.setString(1, id);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    String molfile = rs.getString(1);

                    NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);

                    StructureDiagramGenerator gen2d = new StructureDiagramGenerator(molecule);
                    gen2d.generateCoordinates();

                    Renderer2DModel hrenderModel = new Renderer2DModel();
                    Java2DRenderer renderer = new Java2DRenderer(hrenderModel);

                    Rectangle2D bounds = new Rectangle2D.Double(0, 0, hsize, vsize);
                    BufferedImage bufferedImage = new BufferedImage(hsize, vsize, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = bufferedImage.createGraphics();
                    graphics.setBackground(Color.WHITE);
                    graphics.setColor(Color.WHITE);
                    graphics.fillRect(0, 0, hsize, hsize);
                    renderer.paintMolecule(molecule, graphics, bounds);
                    bufferedImage.flush();
                    ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", baos);
                    byte[] bytesOut = baos.toByteArray();
                    out.write(bytesOut);
                    out.flush();
                }
                rs.close();
                stmt.close();
            }

        } catch (Exception e) {
          e.printStackTrace();  
        }
    }
}
