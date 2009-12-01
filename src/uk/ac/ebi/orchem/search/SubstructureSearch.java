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

import java.sql.Clob;
import java.sql.SQLException;

import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.isomorphism.IsomorphismSort;
import uk.ac.ebi.orchem.isomorphism.SubgraphIsomorphism;
import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * Class that provides functionality for performing an OrChem substructure search.<BR>
 * The substructure search code is split between PL/SQL and Java, for performance reasons
 * and to have the ability to stream (or 'pipe') query results.<BR>
 * Some methods in this class are called from PL/SQL and are wrapped as "Java stored procedures".
 *
 * @author markr@ebi.ac.uk, 2009
 *
 */
public class SubstructureSearch {

    final static Map<Integer, UserQueryMolecule> queries = new ConcurrentHashMap<Integer, UserQueryMolecule>();
   
    /**
     * Translates a user query (such as for example a Smiles string "O=S=O") into
     * a CDK atomcontainer
     * 
     * @param userQuery 
     * @param queryType 
     * @return user query represented as CDK atom container
     * @throws Exception
     */
     static IAtomContainer translateUserQueryClob(Clob userQuery, String queryType) throws SQLException, CDKException {
        IAtomContainer atc = null;
        int clobLen = new Long(userQuery.length()).intValue();
        String query = (userQuery.getSubString(1, clobLen));

        if (queryType.equals(Utils.QUERY_TYPE_MOL)) {
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            atc = MoleculeCreator.getNNMolecule(mdlReader, query);
        } else if (queryType.equals(Utils.QUERY_TYPE_SMILES)) {
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            atc = sp.parseSmiles(query);
            try {
                atc=AtomContainerManipulator.removeHydrogens(atc);
            } catch (NullPointerException e) {
                throw new CDKException("Error - nullpointer exception on removeHydrogens()");
            }
        }
        return atc;
    }



    /**
     * 
     * Builds a string to be used as an SQL "WHERE" clause for queries
     * that filter possible candidates in an OrChem substructure search. <BR>
     * 
     * The WHERE clause will look like "and bit7='1' and bit56='1' and bit108='1' and bit143='1'",
     * depending on the user query - the more elaborate the query, the more bits 
     * are likely to be set. <BR>
     * The WHERE clause refers to columns in table ORCHEM_FINGPRINT_SUBSEARCH.<P>
     *
     * @param atc
     * @param debugYN
     * @return content for an SQL WHERE clause on ORCHEM_FINGPRINT_SUBSEARCH
     * @throws CDKException
     */
     static String whereClauseFromFingerPrint(IAtomContainer atc, String debugYN) throws CDKException {

        BitSet fingerprint = FingerPrinterAgent.FP.getFingerPrinter().getFingerprint(atc);

        String whereCondition = "";
        StringBuffer builtCondition = new StringBuffer();
        int fpSize = FingerPrinterAgent.FP.getFpSize();
        for (int i = 1; i < fpSize; i++) { // ignore bit 0 of OrchemFingerprinter = always set
            if ((fingerprint.get(i))) {
                builtCondition.append(" and bit" + i + "='1'");
            }
        }
        whereCondition += builtCondition.toString();
        whereCondition = whereCondition.trim();
        debug("where condition is : " + whereCondition, debugYN);
        return whereCondition;

    }


    /**
     * Calls {@link #whereClauseFromFingerPrint(IAtomContainer,String)}
     * <BR>
     * Method scope=public -> used as Oracle Java stored procedure
     *
     * @param userQuery
     * @param queryType
     * @param debugYN
     * @return content for an SQL WHERE clause on ORCHEM_FINGPRINT_SUBSEARCH
     * @throws Exception
     */
    public static String getWhereClause(Clob userQuery, String queryType, String debugYN) throws CDKException,
                                                                                                 SQLException {
        boolean debugging = false;
        if (debugYN.toLowerCase().equals("y"))
            debugging = true;
        return whereClauseFromFingerPrint(translateUserQueryClob(userQuery, queryType), debugYN);
    }


    /**
     * Puts a CDK atomcontainter in a Map, from where it can be retrieved by 
     * other methods later on during the substructure search.
     * 
     * @param mapKey key value for the Java Map
     * @param queryMolecule
     * @throws Exception
     */
    public static void stash(Integer mapKey, IAtomContainer queryMolecule){
        IAtom[] sortedAtoms = (IsomorphismSort.atomsByFrequency(queryMolecule));
        queryMolecule.setAtoms(sortedAtoms);
        Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(queryMolecule);
        UserQueryMolecule uqm = new UserQueryMolecule();
        uqm.mol = queryMolecule;
        uqm.atomsAndBonds = atomAndBondCounts;
        queries.put(mapKey, uqm);
    }

    /**
     * Calls {@link #stash(Integer, IAtomContainer)}
     * <BR>
     * Method scope=public -> used as Oracle Java stored procedure
     * @param mapKey
     * @param userQuery a Smiles string, or Mol..
     * @param queryType 
     * @param debugYN
     * @throws Exception
     */
    public static void stashMoleculeInMap(Integer mapKey, Clob userQuery, String queryType, String debugYN) throws SQLException, CDKException {

        boolean debugging = false;
        IAtomContainer queryMolecule = translateUserQueryClob(userQuery, queryType);
        if (debugYN.toLowerCase().equals("y"))
            debugging = true;
        stash (mapKey, queryMolecule);
    }


    /**
     * Method to get a quickly see if a candidate compound can be a 
     * a superstructure of the user's query. This is done using various simple 
     * counts.<BR>
     * For example, if the candidate has two Sulphur atoms and the query
     * has three, the candidate can be ruled out immediately.
     * 
     * Double/aromatic/single counts are problematic - not taken into account.
     * 
     * @param mapKey key to user query structure
     * @param compoundId the database id for the database compound
     * @param tripleBondCount count of triplex bonds of database compound
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
    public static String isPossibleCandidate(Integer mapKey, String compoundId,
                                             Integer tripleBondCount,
                                             Integer sCount, Integer oCount, Integer nCount,
                                             Integer fCount, Integer clCount, Integer brCount, Integer iCount,
                                             Integer cCount, String debugYN) {
        String retVal = "Y";

        debug("check possible candidate " + compoundId, debugYN);

        UserQueryMolecule qc = queries.get(mapKey);
        Map atomAndBondCounts = qc.atomsAndBonds;

        // !!! For now, do not include double, single and aromatic bond count. SSSR bug - inconsistency in results
        if (tripleBondCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.TRIPLE_BOND_COUNT) ||
            sCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.S_COUNT) ||
            oCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.O_COUNT) ||
            nCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.N_COUNT) ||
            fCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.F_COUNT) ||
            clCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.CL_COUNT) ||
            brCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.BR_COUNT) ||
            iCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.I_COUNT) ||
            cCount < (Integer)atomAndBondCounts.get(AtomsBondsCounter.C_COUNT)) {

            debug("discarded compound " + compoundId, debugYN);
            retVal = "N";
        }
        return retVal;
    }


    /**
     * Performs an isomorphism check between a query molecule and database compound
     * @param mapKey key to user query compound
     * @param compoundId the database id for the database compound
     * @param atoms atoms of database compound, see more at {@ link uk.ac.ebi.orchem.OrchemMoleculeBuilder }
     * @param bonds bonds atoms of database compound, see more at {@ link uk.ac.ebi.orchem.OrchemMoleculeBuilder }
     * @param debugYN
     * @param strictStereoIsomorphismYN
     * @return Y or N to indicate is-substructure
     */
    public static String isomorphismCheck
    (Integer mapKey, String compoundId, Clob atoms, Clob bonds, 
     String debugYN, String strictStereoIsomorphismYN) {

        String retVal = null;
        try {
            debug("isomorphism check for "+compoundId,debugYN);
            // Get user query structure
            UserQueryMolecule qc = queries.get(mapKey);
            IAtomContainer queryMolecule = qc.mol;

            // Get candidate compound
            int clobLenAtoms = new Long(atoms.length()).intValue();
            String atString = (atoms.getSubString(1, clobLenAtoms));
            int clobLenBonds = new Long(bonds.length()).intValue();
            String bondString = (bonds.getSubString(1, clobLenBonds));
            IAtomContainer databaseMolecule = OrchemMoleculeBuilder.getBasicAtomContainer(atString, bondString);

            //Invoke the CDK implementation of the VF2 algorithm
            SubgraphIsomorphism s =
                new SubgraphIsomorphism(databaseMolecule, queryMolecule, strictStereoIsomorphismYN);

            if (s.matchSingle()) {
                retVal = compoundId;
            }
        } catch (Exception e) {
            debug("CDK Error - " + compoundId + ": " + e.getMessage(), debugYN);
        }
        return retVal;
    }

    /**
     * Remove a query structure from the user query compound map
     * @param mapKey
     */
    public static void removeFromMap(Integer mapKey) {
        queries.remove(mapKey);
    }

    /**
     * Debug method (to stdout)
     * @param debugMessage
     * @param debugYN
     */
    static void debug(String debugMessage, String debugYN) {
        if (debugYN.toUpperCase().equals("Y")) {
            System.out.println(new java.util.Date() + " debug: " + debugMessage);
        }
    }
    public static void debug(String debugMessage) {
        debug(debugMessage,"Y");
    }

    /**
     * Little bogus method. Shows content of the user query compound map.
     * For dev purpose only.
     */
    public static void showKeys() {
        System.out.println("map keys\n============");
        Iterator iterator = queries.keySet().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }


}


