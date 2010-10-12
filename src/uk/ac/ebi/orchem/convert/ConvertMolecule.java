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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;

import java.util.Properties;

import javax.imageio.ImageIO;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import org.iupac.StdInChI;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.jchempaint.controller.PhantomBondGenerator;
import org.openscience.jchempaint.renderer.AtomContainerRenderer;
import org.openscience.jchempaint.renderer.font.AWTFontManager;
import org.openscience.jchempaint.renderer.generators.ExtendedAtomGenerator;
import org.openscience.jchempaint.renderer.generators.ExternalHighlightAtomGenerator;
import org.openscience.jchempaint.renderer.generators.ExternalHighlightBondGenerator;
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
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * To be used as java stored procedure,
 */

public class ConvertMolecule {

    private static List<IGenerator> generators;
    static {
        generators = new ArrayList<IGenerator>();
        generators.add(new RingGenerator());
        generators.add(new ExtendedAtomGenerator());
        generators.add(new LonePairGenerator());
        generators.add(new RadicalGenerator());
        generators.add(new ExternalHighlightAtomGenerator());
        generators.add(new ExternalHighlightBondGenerator());
        generators.add(new HighlightAtomGenerator());
        generators.add(new HighlightBondGenerator());
        generators.add(new SelectAtomGenerator());
        generators.add(new SelectBondGenerator());
        generators.add(new MergeAtomsGenerator());
        generators.add(new PhantomBondGenerator());
    }

    /**
     * Convert Molfile to Smiles
     *
     * @author federico_paoli
     *
     * @param Molfile
     * @return CLOB
     * @throws Exception
     */

    public static CLOB molfileToSmiles(CLOB Molfile) throws Exception {
        CLOB psmiles = null;
        try {
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            String molfile = Utils.ClobToString(Molfile);
            if (molfile != null) {
                NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                SmilesGenerator sg = new SmilesGenerator();
                fixCarbonHCount(molecule);
                String smiles = sg.createSMILES(molecule);
                psmiles = Utils.StringToClob(smiles);

            } else {
                psmiles = null;
            }
        } catch (Exception e) {
            psmiles = null;
            System.out.println(Utils.getErrorString(e));
        }
        return psmiles;
    }

    /**
     * Convert Smiles to Molfile
     *
     * @param Smiles
     * @param generateCoords Y/N (overhead, can be expensive to calc coords!)
     * @return
     * @throws Exception
     */
    public static CLOB smilesToMolfile(CLOB Smiles, String generateCoords, String useBondType4 ) throws Exception {
        CLOB cmolfile = null;
        try {
            String smiles = Utils.ClobToString(Smiles);
            if (smiles != null) {
                if (!smiles.trim().equals("")) {
                    SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
                    IMolecule molecule = sp.parseSmiles(smiles);
                    
                    // why are valencies being set???
                    for(IAtom atom : molecule.atoms()){
                        atom.setValency(null);
                    }

                    if (generateCoords.equals("Y")) {
                        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                        sdg.setMolecule(molecule);
                        sdg.generateCoordinates();
                        molecule = sdg.getMolecule();
                    }

                    StringWriter out = new StringWriter();
                    MDLV2000Writer mdlWriter = new MDLV2000Writer(out);
                    
                    if (useBondType4.equals("Y")) {
                        Properties prop = new Properties();
                        prop.setProperty("WriteAromaticBondTypes","true");
                        PropertiesListener listener = new PropertiesListener(prop);
                        mdlWriter.addChemObjectIOListener(listener);
                        mdlWriter.customizeJob();
                    }

                    mdlWriter.setWriter(out);
                    mdlWriter.write(molecule);
                    mdlWriter.close();
                    cmolfile = Utils.StringToClob(out.toString());
                    out.close();
                }
            }
        } catch (Exception e) {
            cmolfile = null;
            System.out.println(Utils.getErrorString(e));
        }
        return cmolfile;
    }


    /**
     * Invoke JChempaint to create a picture for a given molecule
     * @param Molfile
     * @param hsize horizontal size of picture
     * @param vsize vertical size of picture
     * @return Joint Photographic Experts Group file type
     * @throws Exception
     */

    /**
     * Invoke JChempaint to create a picture for a given molecule
     * @param Molfile
     * @param hsize horizontal size of picture
     * @param vsize vertical size of picture
     * @return Joint Photographic Experts Group file type
     * @throws Exception
     */
    public static BLOB molfileToJpeg(CLOB Molfile, Integer hsize, Integer vsize) throws Exception {
        BLOB pjpeg = null;
        try {
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            String molfile = Utils.ClobToString(Molfile);
            if (molfile != null) {
                NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                fixCarbonHCount(molecule);

                for (IAtom atom : molecule.atoms()) {
                    atom.setValency(null); // otherwise ugly picture
                }

                AtomContainerRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager(),false);

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
                pjpeg = Utils.ByteToBlob(bytesOut);
            } else {
                pjpeg = null;
            }

        } catch (Exception e) {
            pjpeg = null;
            System.out.println(Utils.getErrorString(e));
        }
        return pjpeg;
    }


    /**
     * Converts MolFile to an InChi string.
     * The convertor is a straight port from C-code, and it needs to
     * write/read/delete temporary files. You need to give this method
     * the output directory name, like "/tmp/" or "c:\temp\", and also
     * give permission for Orchem to write to these server directories.
     * Refer to the documentation for more details.
     *
     * @param molfile Molfile to convert
     * @param fileNum unique number to make this thing work concurrent
     * @param outputDir output direcory for temp files
     * @return the InChi descriptor
     * @throws Exception
     */
    public static CLOB molfileToInchi(CLOB molfile, String fileNum, String outputDir) throws Exception {
        // Set up
        String tempMolFile = outputDir + "orchem" + fileNum + ".mol";
        String tempOutFile = outputDir + "orchem" + fileNum + ".out";
        String tempLogFile = outputDir + "orchem" + fileNum + ".log";
        String tempProblemFile = outputDir + "orchem" + fileNum + ".prb";

        //Write the molfile into the output dir so that the Inchi convertor can
        //open it from there
        FileWriter fstream = new FileWriter(tempMolFile);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(Utils.ClobToString(molfile));
        out.close();

        //Prepare an array to shove into the Inchi generator
        String[] args = new String[5];
        args[0] = "";
        args[1] = tempMolFile;
        args[2] = tempOutFile;
        args[3] = tempLogFile;
        args[4] = tempProblemFile;

        //Call the actual Inchi
        StdInChI i = new StdInChI();
        i.run(args);

        /* Read the generated InChi from the output file */
        FileInputStream fInstream = new FileInputStream(tempOutFile);
        DataInputStream in = new DataInputStream(fInstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        StringBuffer inchi = new StringBuffer();
        int lineCount = 0;
        while ((strLine = br.readLine()) != null) {
            lineCount++;
            if (lineCount == 3) {
                inchi.append(strLine);
            }
        }
        in.close();

        /* Delete temporary files */
        deleteFile(tempMolFile);
        deleteFile(tempOutFile);
        deleteFile(tempLogFile);
        deleteFile(tempProblemFile);

        CLOB inchiAsClob = Utils.StringToClob(inchi.toString());
        return inchiAsClob;
    }

    /**
     * Helper method for InChi conversion, gets rid of temporary conversion
     * files.
     * @param fileName
     * @throws IOException
     */
    private static void deleteFile(String fileName) throws IOException {
        File f = new File(fileName);
        if (f.exists()) {
            boolean success = f.delete();
            if (!success) {
                System.err.println("Warning - could not remove file " + fileName);
            }
        }
    }

    private static void fixCarbonHCount(Molecule mol) {
        double bondCount = 0;
        org.openscience.cdk.interfaces.IAtom atom;
        for (int f = 0; f < mol.getAtomCount(); f++) {
            atom = mol.getAtom(f);
            bondCount = mol.getBondOrderSum(atom);
            int correction = (int)bondCount - (atom.getCharge() != null ? atom.getCharge().intValue() : 0);
            if (atom.getSymbol().equals("C")) {
                atom.setImplicitHydrogenCount(4 - correction);
            } else if (atom.getSymbol().equals("N")) {
                atom.setImplicitHydrogenCount(3 - correction);
            }
        }
    }


}
