/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2010  Mark Rijnbeek
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
 */

package uk.ac.ebi.orchem.calc;

import oracle.sql.CLOB;
import oracle.sql.NUMBER;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * Calculates various chemical proprties using the CDK.
 * @author: markr@ebi.ac.uk
 */
public class ChemicalProperties {

    /**
     * Returns formula string (like "C7H12O2") for given molecular input.
     * @param inputType "SMILES","MOL"
     * @param input the Smiles or MDL Molfile
     * @return calculated chemical formula
     */
    public static String calculateFormula(CLOB input, String inputType, String addH) throws Exception {
        IAtomContainer mol= getMolecule(input,inputType);
        if (mol!=null) {
            if (addH.toUpperCase().equals("Y"))
                addExplicitHydrogens (mol);
            IMolecularFormula molecularFormula = MolecularFormulaManipulator.getMolecularFormula(mol);
            return MolecularFormulaManipulator.getString(molecularFormula);
        }
        else {
            throw new RuntimeException("Could not construct formula. Is input valid?");
        }
    }


    /**
     * Returns mass for given molecular input.
     * 
     * @param inputType "SMILES","MOL"
     * @param input the Smiles or MDL Molfile
     * @return calculated mass
     */
    public static oracle.sql.NUMBER calculateMass(CLOB input, String inputType, String addH) throws Exception {
        oracle.sql.NUMBER mass=null;
        IAtomContainer mol= getMolecule(input,inputType);
        if (mol!=null) {
            if (addH.toUpperCase().equals("Y"))
                addExplicitHydrogens (mol);
            double totalExactMass = AtomContainerManipulator.getNaturalExactMass(mol);     
            if(!Double.isNaN(totalExactMass)){
                mass= new NUMBER(totalExactMass);
            }
            return mass;
        }
        else {
            throw new RuntimeException("Could not construct formula. Is input valid?");
        }

    }


    /**
     * Returns charge for given molecular input.
     * 
     * @param inputType "SMILES","MOL"
     * @param input the Smiles or MDL Molfile
     * @return calculated charge
     */
    public static oracle.sql.NUMBER calculateCharge(CLOB input, String inputType) throws Exception {
        oracle.sql.NUMBER charge=null;
        IAtomContainer mol= getMolecule(input,inputType);
        double totalExactMass = AtomContainerManipulator.getTotalFormalCharge(mol);     
        if(!Double.isNaN(totalExactMass)){
            charge= new NUMBER(totalExactMass);
        }
        return charge;
    }
    
    /**
     * Helper method to produce a CDK atom container from the user input.
     * @param input
     * @param inputType
     * @return atom container (or molecule)
     * @throws Exception
     */
    private static IAtomContainer getMolecule(CLOB input, String inputType ) throws Exception{
        String moleculeString = Utils.ClobToString(input);
        IAtomContainer mol =null;

        if (inputType.equals("SMILES")) {
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            mol = sp.parseSmiles(moleculeString);
        }
        else if (inputType.equals("MOL")) {
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            mol = MoleculeCreator.getNNMolecule(mdlReader, moleculeString);
        }
        return mol;
    }


    /**
     * Helper method to add explicit hydrogens (so that they appear in the formula).
     * @param mol molecule to add explicit hydrogens for
     * 
     */
    private static void addExplicitHydrogens (IAtomContainer mol) {
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(mol);
            CDKHydrogenAdder hydrogenAdder = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
            for (IAtom atom : mol.atoms()) {
                try {
                    hydrogenAdder.addImplicitHydrogens(mol, atom);
                } catch (CDKException cdke) {
                    atom.setHydrogenCount(0);
                }
            }
            AtomContainerManipulator.convertImplicitToExplicitHydrogens(mol);
        } catch (Exception cdke) {
            System.err.println("Error - could not determine explicit hydrogens.. ");
        }
    }
}
