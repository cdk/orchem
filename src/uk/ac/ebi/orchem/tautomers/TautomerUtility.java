/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2011  Mark Rijnbeek
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

package uk.ac.ebi.orchem.tautomers;

import java.io.StringReader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import uk.ac.ebi.orchem.convert.ConvertMolecule;


/**
 * Front class for the CDK tautomer generator.<br>
 * Java stored procedure.
 *
 */
public class TautomerUtility {

    /**
     * Produces a list of tautomers generated based on the input molecule.
     * @param kekuleMDL molecule in MDL format, use kekule notation for any sort of success
     * @return tautomers generated for the input molecule
     * @throws Exception
     */
    public static List<IAtomContainer> getTautomersFromMolfile(String kekuleMDL) throws Exception {

        // Create an InChi to be used by the CDK tautomer generator (its algorithm is InChI based)
        String inchi = getInChI(kekuleMDL);

        //Process the molecule based on the molfile input file
        MDLV2000Reader reader = new MDLV2000Reader(new StringReader(kekuleMDL));
        IMolecule molecule = reader.read(new Molecule());
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        CDKHydrogenAdder hAdder = CDKHydrogenAdder.getInstance(molecule.getBuilder());
        hAdder.addImplicitHydrogens(molecule);

        //Generate tautomers
        uk.ac.ebi.orchem.tautomers.InchiTautomerGenerator tautomerGenerator = new InchiTautomerGenerator();
        List<IAtomContainer> tautomers = tautomerGenerator.getTautomers(molecule, inchi); 
        System.out.println("inchi is "+inchi);
        System.out.println("tautomer count is "+tautomers.size());
        return tautomers;
    }

    public static List<IAtomContainer> getTautomersFromSmiles(String kekuleSmiles) throws Exception {
        String molfile = ConvertMolecule.smilesToMolfile(kekuleSmiles, "N", "N");
        return getTautomersFromMolfile(molfile);
    }

    /**
     * Retrieve an InChI based on an MDL molfile. 
     * @param mdl molfile
     * @return InChI string
     */
    private static String getInChI(String mdl) throws Exception {
        OracleConnection conn = null;
        PreparedStatement pstmtInchi = null;
        String querySeq = "SELECT orchem_sequence_tmp_filenums.nextval FROM dual";
        String queryDir = "SELECT tmp_dir_on_server FROM orchem_parameters";
        //TODO test with invalid or empty queryDir etc - pretty error messages wanted
        ResultSet resFp = null;
        String inchi = null;
        try {
            conn = (OracleConnection)new OracleDriver().defaultConnection();
            pstmtInchi = conn.prepareStatement(querySeq);
            resFp = pstmtInchi.executeQuery();
            resFp.next();
            String fileNum = resFp.getString(1);
            pstmtInchi = conn.prepareStatement(queryDir);
            resFp = pstmtInchi.executeQuery();
            resFp.next();
            String outputDir = resFp.getString(1);
            inchi = ConvertMolecule.molfileToInchi(mdl, fileNum, outputDir, "INCHI", null);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw (ex);
       } finally {
            if (resFp != null)
                resFp.close();
            if (pstmtInchi != null)
                pstmtInchi.close();
            if (conn != null)
                conn.close();
        }
        return inchi;
    }

}
