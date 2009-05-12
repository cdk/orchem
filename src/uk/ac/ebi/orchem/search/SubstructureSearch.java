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

package uk.ac.ebi.orchem.search;

import java.sql.Clob;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.IsomorphismSort;
import org.openscience.cdk.isomorphism.SubgraphIsomorphism;
import org.openscience.cdk.smiles.SmilesParser;

import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * Class that provides functionality for performing an OrChem substructure search.<BR>
 * The substructure search code is split between PL/SQL and Java, for performance reasons
 * and to have the ability to stream (or 'pipe') query results.<BR>
 * The methods in this class are called from PL/SQL and are wrapped as "Java stored procedures".
 * @author markr@ebi.ac.uk
 *
 */
public class SubstructureSearch {

    final static Map<Integer, UserQueryMolecule> queries = new ConcurrentHashMap<Integer, UserQueryMolecule>();


    /**
     * TODO proper doc
     * TODO make constant "MOL" "SMILES"
     */
    public static IAtomContainer translateUserQueryClob(Clob userQuery, String queryType) throws Exception {

        IAtomContainer atc = null;
        int clobLen = new Long(userQuery.length()).intValue();
        String query = (userQuery.getSubString(1, clobLen));

        if (queryType.equals("MOL")) {
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            atc = MoleculeCreator.getNNMolecule(mdlReader, query);
        } else if (queryType.equals("SMILES")) {
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            atc = sp.parseSmiles(query);
        }
        return atc;
    }


    /*
     * TODO proper doc
     */
    public static String whereClauseFromFingerPrint(IAtomContainer atc, String debugYN) throws CDKException {

        boolean debugging = false;
        if (debugYN.toLowerCase().equals("y"))
            debugging = true;

        BitSet fingerprint = FingerPrinterAgent.FP.getFingerPrinter().getFingerprint(atc);

        /* Build up the where clause for the query using the fingerprint one bits */
        String whereCondition = "";
        StringBuffer builtCondition = new StringBuffer();
        int fpSize = FingerPrinterAgent.FP.getFpSize();
        for (int i = 1; i < fpSize; i++) { // ignore bit 0 of OrchemFingerprinter
            if ((fingerprint.get(i))) {
                builtCondition.append(" and bit" + i + "='1'");
            }
        }

        whereCondition += builtCondition.toString();
        whereCondition = whereCondition.trim();
        debug("where condition is : " + whereCondition, debugging);
        return whereCondition;

    }


    /**
     * TODO proper doc
     */
    public static String getWhereClause(Clob userQuery, String queryType, String debugYN) throws Exception {

        boolean debugging = false;
        if (debugYN.toLowerCase().equals("y"))
            debugging = true;
        return whereClauseFromFingerPrint(translateUserQueryClob(userQuery, queryType), debugYN);
    }


    /**
     * TODO proper doc, proper name etc etc
     */
    public static void stash(Integer mapKey, IAtomContainer queryMolecule) throws Exception {
        IAtom[] sortedAtoms = (IsomorphismSort.atomsByFrequency(queryMolecule));
        queryMolecule.setAtoms(sortedAtoms);
        Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(queryMolecule);
        UserQueryMolecule uqm = new UserQueryMolecule();
        uqm.mol = queryMolecule;
        uqm.atomsAndBonds = atomAndBondCounts;
        queries.put(mapKey, uqm);
    }

    /**
     * TODO proper doc, proper name etc etc
     */
    public static void stashMoleculeInMap(Integer mapKey, Clob userQuery, String queryType, String debugYN) throws Exception {

        boolean debugging = false;
        IAtomContainer queryMolecule = translateUserQueryClob(userQuery, queryType);
        if (debugYN.toLowerCase().equals("y"))
            debugging = true;
        stash (mapKey, queryMolecule);
    }


    /**
     * TODO document this
     * @param mapKey key to user query compound
     * @param compoundId the database id for the database compound
     * @param singleBondCount count of single bonds of database compound
     * @param doubleBondCunt count of double bonds of database compound
     * @param tripleBondCount count of triplex bonds of database compound
     * @param aromaticBondCount count of aromatic bonds of database compound
     * @param sCount sulphur count of database compound
     * @param oCount oxygen count of database compound
     * @param nCount nitrogen count of database compound
     * @param fCount fluor count of database compound
     * @param clCount chlorine count of database compound
     * @param brCount brome count of database compound
     * @param iCount iodine count of database compound
     * @param cCount carbon count of database compound
     * @param debugYN
     */

    public static String isPossibleCandidate(Integer mapKey, String compoundId, Integer singleBondCount,
                                             Integer doubleBondCunt, Integer tripleBondCount,
                                             Integer aromaticBondCount, Integer sCount, Integer oCount, Integer nCount,
                                             Integer fCount, Integer clCount, Integer brCount, Integer iCount,
                                             Integer cCount, String debugYN) {
        String retVal = "Y";
        boolean debugging = false;
        if (debugYN.toLowerCase().equals("y"))
            debugging = true;

        UserQueryMolecule qc = queries.get(mapKey);
        Map atomAndBondCounts = qc.atomsAndBonds;

        // For now, do not include double, single and aromatic bond count. SSSR bug - inconsistency in results
        if (tripleBondCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.TRIPLE_BOND_COUNT) ||
            sCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.S_COUNT) ||
            oCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.O_COUNT) ||
            nCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.N_COUNT) ||
            fCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.F_COUNT) ||
            clCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.CL_COUNT) ||
            brCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.BR_COUNT) ||
            iCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.I_COUNT) ||
            cCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.C_COUNT)) {

            //debug("discarded compound " + compoundId, debugging);
            retVal = "N";
        }
        return retVal;
    }


    /**
     * Performs an isomorphim check between a query molecule and database compound
     * @param mapKey key to user query compound
     * @param compoundId the database id for the database compound
     * @param atoms atoms of database compound, see more at {@ link uk.ac.ebi.orchem.OrchemMoleculeBuilder }
     * @param bonds bonds atoms of database compound, see more at {@ link uk.ac.ebi.orchem.OrchemMoleculeBuilder }
     * @param debugYN
     * @return Y or N to indicate is-substructure
     */
    public static String isomorphismCheck(Integer mapKey, String compoundId, Clob atoms, Clob bonds, String debugYN) {

        String retVal = null;
        boolean debugging = false;
        try {

            if (debugYN.toLowerCase().equals("y"))
                debugging = true;

            UserQueryMolecule qc = queries.get(mapKey);

            IAtomContainer queryMolecule = qc.mol;
            int clobLenAtoms = new Long(atoms.length()).intValue();
            String atString = (atoms.getSubString(1, clobLenAtoms));
            int clobLenBonds = new Long(bonds.length()).intValue();
            String bondString = (bonds.getSubString(1, clobLenBonds));
            IAtomContainer databaseMolecule = OrchemMoleculeBuilder.getBasicAtomContainer(atString, bondString);

            //Test if the match made is truly a substructure using VF2 algorithm
            SubgraphIsomorphism s =
                new SubgraphIsomorphism(databaseMolecule, queryMolecule, SubgraphIsomorphism.Algorithm.VF2);

            if (s.matchSingle()) {
                retVal = compoundId;
            }
        } catch (Exception e) {
            debug("CDK Error - " + compoundId + ": " + e.getMessage(), debugging);
        }
        return retVal;
    }


    /**
     * Show content of the user query compound map
     */
    public static void showKeys() {
        System.out.println("map keys\n============");
        Iterator iterator = queries.keySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }

    /**
     *Remove a query compound from the user query compound map
     * @param mapKey
     */
    public static void removeFromMap(Integer mapKey) {
        queries.remove(mapKey);
        
        
    }


    /**
     * Debug method (to stdout)
     * @param debugMessage
     * @param debug
     */
    private static void debug(String debugMessage, boolean debug) {
        if (debug) {
            System.out.println(new java.util.Date() + " debug: " + debugMessage);
        }
    }


}


