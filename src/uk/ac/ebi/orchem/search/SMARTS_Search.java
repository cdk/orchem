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

package uk.ac.ebi.orchem.search;

import ambit2.smarts.SmartsParser;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oracle.jdbc.driver.OracleDriver;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;

import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * Class that provides functionality for performing SMARTS searches.<BR>
 * Uses the (the CDK based) ambit2 implementation of SMARTS matching, as this turned out
 * to be faster than the CDK SMARTS matching.
 * Extends {@link SubstructureSearch}.
 * Some methods in this class are called from PL/SQL and are wrapped as "Java stored procedures".
 *
 * @author markr@ebi.ac.uk, 2010
 */

public class SMARTS_Search extends SubstructureSearch {

    /**
     * Calls {@link #whereClauseFromFingerPrint(IAtomContainer,String,String,int)}
     * <BR>
     * Method scope=public -> used as Oracle Java stored procedure
     *
     * @param smartsQuery
     * @param debugYN
     * @return
     * @throws CDKException
     * @throws SQLException
     */
    public static String getWhereClause(Clob smartsQuery, String debugYN) throws CDKException, SQLException {
        int clobLen = new Long(smartsQuery.length()).intValue();
        String query = (smartsQuery.getSubString(1, clobLen));
        SmartsParser smartsParser = new SmartsParser();
        IAtomContainer qat = smartsParser.parse(query);
        IAtomContainer hStrippedAtc = new NNMolecule(removeHydrogens(qat));
        return whereClauseFromFingerPrint(hStrippedAtc, debugYN, "N", -1);
    }


    /**
     * Calculates certain elements and bond types and returns the counted
     * results in an array.
     * @param smartsQuery
     * @param vReturnArray
     * @throws Exception
     */
    public static void getAtomAndBondCounts(Clob smartsQuery, oracle.sql.ARRAY[] vReturnArray) throws Exception {

        int clobLen = new Long(smartsQuery.length()).intValue();
        String query = (smartsQuery.getSubString(1, clobLen));
        SmartsParser smartsParser = new SmartsParser();
        IAtomContainer qat = smartsParser.parse(query);

        Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(qat);

        Integer[] countsArray = new Integer[9];
        int idx = 0;
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.TRIPLE_BOND_COUNT);
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.S_COUNT);
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.O_COUNT);
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.N_COUNT);
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.F_COUNT);
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.CL_COUNT);
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.BR_COUNT);
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.I_COUNT);
        countsArray[idx++] = (Integer)atomAndBondCounts.get(AtomsBondsCounter.C_COUNT);

        Connection conn = new OracleDriver().defaultConnection();
        ArrayDescriptor desc = ArrayDescriptor.createDescriptor("ORCHEM_NUMBER_TABLE", conn);
        vReturnArray[0] = new ARRAY(desc, conn, countsArray);
    }


    /**
     * Tries to match a SMARTS pattern with a database compound.
     * @param compoundId
     * @param dbMolecule
     * @param smartsQuery
     * @return Y/N indicating yes or no
     */
    public static String smartsMatch(String compoundId, Clob dbMolecule, Clob smartsQuery) {

        String retVal = "N";
        try {

            int clobLenAtoms = new Long(dbMolecule.length()).intValue();
            String dbMol = (dbMolecule.getSubString(1, clobLenAtoms));
            IMolecule databaseMolecule=null;

            if (!dbMol.contains(System.getProperty("line.separator"))) { // one line ? guess SMILES 
                SmilesParser sp= new SmilesParser(DefaultChemObjectBuilder.getInstance());
                databaseMolecule = sp.parseSmiles(dbMol);
            }
            else {
                databaseMolecule = MoleculeCreator.getMoleculeFromMolfile(dbMol, false);
            }

            //databaseMolecule = new NNMolecule(AtomContainerManipulator.removeHydrogens(databaseMolecule));

            try {
                CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(NoNotificationChemObjectBuilder.getInstance());
                adder.addImplicitHydrogens(databaseMolecule);
            } catch (CDKException e) {
                debug("CDK warning for ID " + compoundId + ":" + e.getMessage());
            }

            //Parse SMARTS
            int clobLenBonds = new Long(smartsQuery.length()).intValue();
            String SMARTS = (smartsQuery.getSubString(1, clobLenBonds));
            SmartsParser smartsParser = new SmartsParser();
            QueryAtomContainer query = smartsParser.parse(SMARTS);

            smartsParser.setSMARTSData(databaseMolecule);
            List matchingAtoms = null;

            if (query.getAtomCount() == 1) {
                IQueryAtom queryAtom = (IQueryAtom)query.getAtom(0);
                matchingAtoms = new ArrayList<List<Integer>>();
                for (IAtom atom : databaseMolecule.atoms()) {
                    if (queryAtom.matches(atom)) {
                        List<Integer> tmp = new ArrayList<Integer>();
                        tmp.add(databaseMolecule.getAtomNumber(atom));
                        matchingAtoms.add(tmp);
                    }
                }
            } else {
                List bondMapping = UniversalIsomorphismTester.getSubgraphMaps(databaseMolecule, query);
                matchingAtoms = getAtomMappings(bondMapping, databaseMolecule);
            }
            if (matchingAtoms.size() != 0)
                retVal = "Y";


        } catch (Exception e) {
            debug("Error for ID " + compoundId + ": " + e.getMessage());
        }

        return retVal;
    }


    /**
     * Helper method for the SMARTS matching. Taken from Ambit.
     * @param bondMapping
     * @param atomContainer
     * @return
     */
    private static List<List<Integer>> getAtomMappings(List bondMapping, IAtomContainer atomContainer) {
        List<List<Integer>> atomMapping = new ArrayList<List<Integer>>();
        // loop over each mapping
        for (Object aBondMapping : bondMapping) {
            List list = (List)aBondMapping;

            List<Integer> tmp = new ArrayList<Integer>();
            IAtom atom1 = null;
            IAtom atom2 = null;
            // loop over this mapping
            for (Object aList : list) {
                RMap map = (RMap)aList;
                int bondID = map.getId1();

                // get the atoms in this bond
                IBond bond = atomContainer.getBond(bondID);
                atom1 = bond.getAtom(0);
                atom2 = bond.getAtom(1);

                Integer idx1 = atomContainer.getAtomNumber(atom1);
                Integer idx2 = atomContainer.getAtomNumber(atom2);

                if (!tmp.contains(idx1))
                    tmp.add(idx1);
                if (!tmp.contains(idx2))
                    tmp.add(idx2);
            }
            if (tmp.size() > 0)
                atomMapping.add(tmp);

            // If there is only one bond, check if it matches both ways.
            if (list.size() == 1 && atom1.getAtomicNumber() == atom2.getAtomicNumber()) {
                List<Integer> tmp2 = new ArrayList<Integer>();
                tmp2.add(tmp.get(0));
                tmp2.add(tmp.get(1));
                atomMapping.add(tmp2);
            }
        }
        return atomMapping;
    }


    /**
     * HACK HACK - should be taken from CDK but need a fix first for the builder,
     * because we can't get a DefaultChemObjectBuilder from a query atom container.
     * Produces an AtomContainer without explicit Hs but with H count from one with Hs.
     * The new molecule is a deep copy.
     *
     * @param atomContainer The AtomContainer from which to remove the hydrogens
     * @return              The molecule without Hs.
     * @cdk.keyword         hydrogens, removal
     */
    @TestMethod("testRemoveHydrogens_IAtomContainer")
    public static IAtomContainer removeHydrogens(IAtomContainer atomContainer) {
        Map<IAtom, IAtom> map = new HashMap<IAtom, IAtom>(); // maps original atoms to clones.
        List<IAtom> remove = new ArrayList<IAtom>(); // lists removed Hs.
        
        IChemObjectBuilder bob = DefaultChemObjectBuilder.getInstance();
        IAtomContainer mol = bob.newInstance(IAtomContainer.class);
        int count = atomContainer.getAtomCount();
        for (int i = 0; i < count; i++) {
            IAtom atom = atomContainer.getAtom(i);
            if (!atom.getSymbol().equals("H")) {
                IAtom clonedAtom = null;
                try {
                    clonedAtom = (IAtom)atom.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                mol.addAtom(clonedAtom);
                map.put(atom, clonedAtom);
            } else {
                remove.add(atom); // maintain list of removed H.
            }
        }
        count = atomContainer.getBondCount();
        for (int i = 0; i < count; i++) {
            final IBond bond = atomContainer.getBond(i);
            boolean removedBond = false;
            final int length = bond.getAtomCount();
            for (int k = 0; k < length; k++) {
                if (remove.contains(bond.getAtom(k))) {
                    removedBond = true;
                    break;
                }
            }
            if (!removedBond) {
                IBond clone = null;
                try {
                    clone = (IBond)atomContainer.getBond(i).clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                assert clone != null;
                clone.setAtoms(new IAtom[] { (IAtom)map.get(bond.getAtom(0)), (IAtom)map.get(bond.getAtom(1)) });
                mol.addBond(clone);
            }
        }
        for (IAtom aRemove : remove) {
            for (IAtom iAtom : atomContainer.getConnectedAtomsList(aRemove)) {
                final IAtom neighb = map.get(iAtom);
                if (neighb == null)
                    continue; // since for the case of H2, neight H has a heavy atom neighbor
                neighb.setImplicitHydrogenCount((neighb.getImplicitHydrogenCount() == null ? 0 :
                                                 neighb.getImplicitHydrogenCount()) + 1);
            }
        }
        for (IAtom atom : mol.atoms()) {
            if (atom.getImplicitHydrogenCount() == null)
                atom.setImplicitHydrogenCount(0);
        }
        mol.setProperties(atomContainer.getProperties());
        mol.setFlags(atomContainer.getFlags());
        return (mol);
    }

}


