/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Federico Paoli
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

package uk.ac.ebi.orchem.convert;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
//import java.io.File;
import java.io.Reader;

import java.io.StringWriter;

/*import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
*/


//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.util.Properties;

import javax.imageio.ImageIO;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
/*import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.interfaces.IAtomContainer;
*/
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.renderer.Java2DRenderer;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;

import uk.ac.ebi.orchem.PropertyLoader;
import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * To be used as java stored procedure,
 */

public class ConvertMolecule {
    /**
     * Convert Molfile to Smiles 
     * 
     * @author federico_paoli
     *
     * @param Molfile
     * @return CLOB
     * @throws Exception
     */

    public static CLOB MolfileToSmiles(CLOB Molfile) throws Exception {
        CLOB psmiles=null;
        try {
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            String molfile=ClobToString(Molfile);
            if (molfile != null) {
                    NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                    SmilesGenerator sg = new SmilesGenerator();
                    fixCarbonHCount(molecule);
                    String smiles = sg.createSMILES(molecule);
                    psmiles=StringToClob(smiles);
             } else {
               psmiles=null;
             }
  
        } catch (Exception e) {
            psmiles=null;
            System.out.println(Utils.getErrorString(e));
      }
     return psmiles;
    }

  /**
   * @param Smiles
   * @return
   * @throws Exception
   */
  public static CLOB SmilesToMolfile(CLOB Smiles) throws Exception {
      CLOB cmolfile=null;
      try {
          String smiles= ClobToString(Smiles);
          if (smiles != null) {
            if (!smiles.trim().equals("")) {
              SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
              StructureDiagramGenerator sdg = new StructureDiagramGenerator();
              IMolecule molecule=sp.parseSmiles(smiles);
              StringWriter out = new StringWriter();
              sdg.setMolecule(molecule);
              sdg.generateCoordinates();
              molecule = sdg.getMolecule(); 
              MDLWriter mdlWriter = new MDLWriter(out);
              mdlWriter.setWriter(out);
              mdlWriter.write(molecule);
              mdlWriter.close();
              cmolfile=StringToClob(out.toString());
              out.close();
            }
          }
      } catch (Exception e) {
          cmolfile=null;
          System.out.println(Utils.getErrorString(e));
      }
      return cmolfile;
    }

 
  public static BLOB MolfileToJpeg(CLOB Molfile, Integer hsize, Integer vsize) throws Exception {
      BLOB pjpeg=null;
      try {
          MDLV2000Reader mdlReader = new MDLV2000Reader();
          String molfile=ClobToString(Molfile);
          if (molfile != null) {
                  NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                  fixCarbonHCount(molecule);
                  
                  StructureDiagramGenerator gen2d = new StructureDiagramGenerator(molecule);
                  gen2d.generateCoordinates(); 
                  
                  Renderer2DModel hrenderModel = new Renderer2DModel(); 
                  Java2DRenderer renderer = new Java2DRenderer(hrenderModel);
                  
                  Rectangle2D bounds = new Rectangle2D.Double(0, 0, hsize, vsize);
                  BufferedImage bufferedImage = new BufferedImage( hsize, vsize,BufferedImage.TYPE_INT_RGB );
                  Graphics2D graphics = bufferedImage.createGraphics();
                  graphics.setBackground(Color.WHITE);
                  graphics.setColor(Color.WHITE);
                  graphics.fillRect(0, 0, hsize, hsize);
                  renderer.paintMolecule(molecule, graphics, bounds);
                  bufferedImage.flush();
                  /*File file = new File("/tmp/newimage.jpg");
                  ImageIO.write(bufferedImage, "jpg", file);*/
                  ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
                  ImageIO.write(bufferedImage, "jpg", baos);
                  byte[] bytesOut = baos.toByteArray();
                  pjpeg=ByteToBlob(bytesOut);
           } else {
             pjpeg=null;
           }
  
      } catch (Exception e) {
          pjpeg=null;
          System.out.println(Utils.getErrorString(e));
    }
   return pjpeg;
  }

  
 /* public static CLOB MolfileToInChi(CLOB Molfile) throws Exception {
      CLOB pinchi=null;
      try {
          MDLV2000Reader mdlReader = new MDLV2000Reader();
          String molfile=ClobToString(Molfile);
          if (molfile != null) {
                  // Generate InChi from molfile
                  NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile.toString());
                  fixCarbonHCount(molecule);
                  IAtomContainer atomContainer= molecule;
                  CDKHueckelAromaticityDetector.detectAromaticity(atomContainer);
                  //CDKHydrogenAdder hdx.addImplicitHydrogens(atomContainer);
                  AtomContainerManipulator.convertImplicitToExplicitHydrogens(atomContainer);
                  //Molecule m = builder.newMolecule(atomContainer);
                  InChIGeneratorFactory factory = new InChIGeneratorFactory();
                  InChIGenerator gen = factory.getInChIGenerator(atomContainer);
                  String inchi = gen.getInchi();
                  // Convert String to CLOB
                  pinchi=StringToClob(inchi)
           } else {
             pinchi=null;
           }

      } catch (Exception e) {
          pinchi=null;
          System.out.println(Utils.getErrorString(e));
    }
   return pinchi;
  }
*/

  /**
   * @param hclob
   * @return
   * @throws Exception
   */
  public static String ClobToString(CLOB hclob) throws Exception  {
    StringBuffer hstringbuffer;
    hstringbuffer = new StringBuffer();
    Reader clobReader = hclob.getCharacterStream( );
      char[] buffer = new char[ hclob.getBufferSize( ) ];
      int read = 0;
      int bufflen = buffer.length;
      while( (read = clobReader.read(buffer,0,bufflen)) > 0 ) {
        hstringbuffer.append(new String( buffer, 0, read ));
      }
      clobReader.close( );
      return hstringbuffer.toString();
    }

  /**
   * @param hstring
   * @return
   * @throws Exception
   */
  public static CLOB StringToClob(String hstring) throws Exception  {
     /* Properties properties = PropertyLoader.getUnittestProperties();
      DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
      Connection conn = DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
      */CLOB hclob;
      OracleConnection conn = null;
      conn = (OracleConnection)new OracleDriver().defaultConnection();
      hclob=CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
      hclob.open(CLOB.MODE_READWRITE);
      hclob.setString(1, hstring);
      hclob.close();
      return hclob;
    }

  /**
   * @param hbyte
   * @return
   * @throws Exception
   */
  public static BLOB ByteToBlob(byte[] hbyte) throws Exception  {
      /*Properties properties = PropertyLoader.getUnittestProperties();
      DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
      Connection conn = DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
      */BLOB hblob;
      OracleConnection conn = null;
      conn = (OracleConnection)new OracleDriver().defaultConnection();
      hblob=BLOB.createTemporary(conn, false, BLOB.DURATION_SESSION);
      hblob.open(CLOB.MODE_READWRITE);
      hblob.setBytes(1,hbyte);
      hblob.close();
      return hblob;
    }  

    private static void fixCarbonHCount(Molecule mol) {
        double bondCount = 0;
        org.openscience.cdk.interfaces.IAtom atom;
        for (int f = 0; f < mol.getAtomCount(); f++) {
            atom = mol.getAtom(f);
            bondCount = mol.getBondOrderSum(atom);
            int correction = (int)bondCount - (atom.getCharge() != null ? atom.getCharge().intValue() : 0);
            if (atom.getSymbol().equals("C")) {
                atom.setHydrogenCount(4 - correction);
            } else if (atom.getSymbol().equals("N")) {
                atom.setHydrogenCount(3 - correction);
            }
        }
    }

    
}
