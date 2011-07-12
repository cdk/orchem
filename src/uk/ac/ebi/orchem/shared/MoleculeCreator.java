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

package uk.ac.ebi.orchem.shared;

import java.io.StringReader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.DefaultChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV3000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.db.OrChemParameters;


/**
 * Class for creating molecules
 *
 * @author markr@ebi.ac.uk
 */
public class MoleculeCreator {

    /**
     * Creates a molecule using an MDL string and an MDL reader
     * @param mdlString
     * @return
     * @throws CDKException
     */
    public static NNMolecule getMoleculeFromMolfile(String mdlString,boolean removeHydrogens) throws CDKException {
        DefaultChemObjectReader mdlReader=null;
 
        if (mdlString.contains("M  V30 BEGIN CTAB"))
            mdlReader = new MDLV3000Reader();
        else
            mdlReader = new MDLV2000Reader();

        try {
            NNMolecule molecule = new NNMolecule();
            mdlReader.setReader(new StringReader(mdlString));

            try {
                molecule = mdlReader.read(molecule);
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
                molecule = mdlReader.read(molecule);
            }

            catch (java.lang.ClassCastException ccEx) {
            /* A hack for query bond types until that works in the CDK. Query bond types get replaced by a single bond. */
                mdlString = Utils.removeSSSQueryBondFromMolfile(mdlString);
                mdlReader.setReader(new StringReader(mdlString));
                molecule = new NNMolecule();
                molecule = mdlReader.read(molecule);
            }

            NNMolecule nnMolecule = null;
            if (removeHydrogens) {
                try {
                    nnMolecule = new NNMolecule(AtomContainerManipulator.removeHydrogens(molecule));
                } catch (NullPointerException e) {
                    throw new CDKException("Error - nullpointer exception on removeHydrogens()");
                }
                if (nnMolecule == null || nnMolecule.getAtomCount() == 0)
                    throw new CDKException("Warning - Molfile is empty or atom count after hydrogen stripping is zero. ");
            } else {
                nnMolecule = molecule;
            }

            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(nnMolecule);
            CDKHueckelAromaticityDetector.detectAromaticity(nnMolecule);
            return nnMolecule;
        } catch (Exception err) {
            throw new CDKException("Failed to create CDK molecule from MDL Molfile \n"+err.getClass()+"\n"+Utils.getErrorString(err));        
        }
    }

    /**
     * Overload for {@link MoleculeCreator#getMoleculeFromMolfile(java.lang.String,boolean)}
     */
    public static NNMolecule getMoleculeFromMolfile(String mdlString) throws CDKException {
        return getMoleculeFromMolfile(mdlString, true);
    }

    /**
     * Gets a moleule from the database for a given id. <B>To be used as stored procedure</B>
     * @param id
     * @return CDK molecule
     * @throws SQLException
     */
    public static NNMolecule getMolecule(String id, boolean removeHydrogens) throws SQLException, CDKException {
        String molfile = null;
        NNMolecule result = null;
        OracleConnection conn = null;
        conn = (OracleConnection)new OracleDriver().defaultConnection();


        String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
        String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
        String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

        String query =
            " select " + compoundTableMolfileColumn + " from " + compoundTableName + " where " + compoundTablePkColumn +
            "=? ";

        PreparedStatement psst = conn.prepareStatement(query);
        psst.setString(1, id);
        ResultSet res = psst.executeQuery();
        if (res.next() && res.getClob(compoundTableMolfileColumn) != null) {
            molfile = res.getString(1);
        }
        res.close();
        psst.close();
        result = getMoleculeFromMolfile(molfile, removeHydrogens);
        return result;
    }

    public final static String BACKUP_ORIGINAL_HYDROGEN_COUNT="BOHC";
    /**
     * Backup the explicit hydrogen count before stripping them off..
     * Uses one of the properties of the atom to store the explicit H count
     * @param molecule
     */
    public static void backupExplicitHcount(IAtomContainer molecule) {
        
        for (IAtom atom : molecule.atoms() ) {
            int explicitHydrogens = AtomContainerManipulator.countExplicitHydrogens(molecule,atom);
            if (explicitHydrogens!=0)
                atom.setProperty(BACKUP_ORIGINAL_HYDROGEN_COUNT, new Integer(explicitHydrogens));
            else
                atom.setProperty(BACKUP_ORIGINAL_HYDROGEN_COUNT, null);
        }
    }
    
    /**
     * Related to previous method. Creates an array corresponding to the atom array,
     * each position holds the original explicit H count for that atom.
     * This can then be used in the isomorpism algorithm, to make sure that any
     * explicitly set hydrogens in the query matches the database compound.
     * It prevents a query CO[H] matching COC
     * 
     * @param queryStructure
     * @return array with counts for each atom (default 0)
     */
    public static int[] createExplHydrogenArray (IAtomContainer queryStructure) {
        int atCount=queryStructure.getAtomCount();
        int[] explHydrogenCountBackup=new int[atCount];
        for (int i = 0; i < atCount; i++) {
            IAtom atom =queryStructure.getAtom(i);
            
            Object explHCount = atom.getProperty(BACKUP_ORIGINAL_HYDROGEN_COUNT);
            if (explHCount!=null) 
                explHydrogenCountBackup[i]=(Integer)explHCount;
            else
                explHydrogenCountBackup[i]=0;
        }
        return explHydrogenCountBackup;
    }


}


