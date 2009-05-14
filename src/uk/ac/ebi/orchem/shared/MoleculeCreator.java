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

package uk.ac.ebi.orchem.shared;

import java.io.StringReader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;


/**
 * Class for creating molecules
 *
 * @author markr@ebi.ac.uk
 */
public class MoleculeCreator {

    /**
     * Creates a molecule using an MDL string and an MDL reader
     * @param mdlReader
     * @param mdlString
     * @return
     * @throws CDKException
     * 
     * 
     * 
     */
    public static NNMolecule getNNMolecule(MDLV2000Reader mdlReader, String mdlString) throws CDKException {
        NNMolecule molecule = null;
        mdlReader.setReader(new StringReader(mdlString));
        molecule = new NNMolecule();

        try {
            molecule = (NNMolecule)mdlReader.read(molecule);
        } catch (NullPointerException e) {
            /* Fix for NullPointer due to occurence of D or T (Deuterium or Tritium) with massNumber null
             * as happens now and then in Starlite (Chembl)
             * Regex fix to replace D or T with a H (Hydrogen) symbol as replacement.
             */
            Pattern p = Pattern.compile("[D|T](\\s+\\d){6}");
            Matcher m = p.matcher(mdlString);
            StringBuilder sb = new StringBuilder(mdlString);
            while (m.find()) {
                sb.replace(m.start(), m.start() + 1, "H");
            }
            //retry, hopefully NullPointer fixed...
            mdlString = sb.toString();
            mdlReader.setReader(new StringReader(mdlString));
            molecule = new NNMolecule();
            molecule = (NNMolecule)mdlReader.read(molecule);
        }


        NNMolecule nnMolecule =null;
        try {
            nnMolecule = new NNMolecule(AtomContainerManipulator.removeHydrogens(molecule));
        } catch (NullPointerException e) {
            throw new CDKException("Error - nullpointer exception on removeHydrogens()");
        }

        if (nnMolecule == null || nnMolecule.getAtomCount() == 0)
            throw new CDKException("Error - molfile is null or mol atom count is zero. ");

        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(nnMolecule);
        CDKHueckelAromaticityDetector.detectAromaticity(nnMolecule);
        return nnMolecule;
    }
    
    
    
    
    
    public static void main(String[] args) throws CDKException {
        /*
        String mol=
            " \n" +
            "Marvin  11170523502D\n"+
            "  \n"+
            "  1  0  0  0  0  0            999 V2000\n"+
            "    0.0000    0.0000    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n"+
            "M  END\n";
        System.out.println(mol);
        MDLV2000Reader r = new MDLV2000Reader();
        getNNMolecule(r,mol);        
        */
        String mol=
        " \n" + 
        "  Marvin  09100521432D\n" + 
        "  \n" + 
        "  2  1  0  0  0  0            999 V2000\n" + 
        "   -1.0938    1.0312    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n" + 
        "   -0.2688    1.0312    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n" + 
        "  1  2  1  0  0  0  0\n" + 
        "M  END\n";
        System.out.println(mol);
        MDLV2000Reader r = new MDLV2000Reader();
        getNNMolecule(r,mol);        
    
    



    }
}


/*
     //Alternative
     Object object = reader.next();
     if (object != null && object instanceof Molecule) {
         m = (Molecule)object;
         nnMolecule = new NNMolecule(AtomContainerManipulator.removeHydrogens(m));
         AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(nnMolecule);
         CDKHueckelAromaticityDetector.detectAromaticity(nnMolecule);
     }
 */

