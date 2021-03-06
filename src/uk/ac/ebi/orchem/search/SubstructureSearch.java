/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2010  Mark Rijnbeek
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.sql.Clob;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.FormatFactory;
import org.openscience.cdk.io.RGroupQueryReader;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.RGroupQueryFormat;
import org.openscience.cdk.isomorphism.matchers.RGroupQuery;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.isomorphism.IsomorphismSort;
import uk.ac.ebi.orchem.isomorphism.SubgraphIsomorphism;
import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;
import uk.ac.ebi.orchem.tautomers.TautomerUtility;


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

    final static Map<Integer, List<UserQueryMolecule>> queries =
        new ConcurrentHashMap<Integer, List<UserQueryMolecule>>();

    /**
     * Translates a user query (such as for example a Smiles string "O=S=O") into
     * a CDK atomcontainer.<BR>
     *
     * However, if the query if an RGFile (a special case of a MOL file), there
     * can be any number of queries generated (valid substitutes of the R-Group)
     *
     * @param userQuery mdl or smiles clob
     * @param queryType  SMILES or MOL
     * @param tautomers Y/N indicating to query for tautomers
     * @return user query represented as CDK atom container
     * @throws Exception
     */
    static List<IAtomContainer> translateUserQueryClob(Clob userQuery, String queryType,
                                                       String tautomers) throws Exception {

        List<IAtomContainer> userQueries = null;
        int clobLen = new Long(userQuery.length()).intValue();
        String query = (userQuery.getSubString(1, clobLen));

        debug("# tautomers arg Y/N =  " + tautomers);

        if (queryType.equals(Utils.QUERY_TYPE_MOL)) {

            InputStream ins = new ByteArrayInputStream(query.getBytes());
            IChemFormat format = null;
            try {
                format = new FormatFactory().guessFormat(ins);

            } catch (IOException e) {
                //continue, gues MDL molfile
                e.printStackTrace();
            }
            if (format != null && format.getClass().equals(RGroupQueryFormat.class)) {
                RGroupQueryReader reader = new RGroupQueryReader(ins);
                RGroupQuery rGroupQuery = reader.read(new RGroupQuery());
                List<IAtomContainer> allConfigurations = rGroupQuery.getAllConfigurations();
                userQueries = new ArrayList<IAtomContainer>();
                // Detect aromaticity etc on RGroup Query configurations
                for (IAtomContainer atc : allConfigurations) {
                    IAtomContainer nnMolecule = null;
                    nnMolecule = new NNMolecule(atc);
                    if (nnMolecule != null && nnMolecule.getAtomCount() != 0) {
                        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(nnMolecule);
                        CDKHueckelAromaticityDetector.detectAromaticity(nnMolecule);
                        userQueries.add(nnMolecule);
                    }
                }
            } else {
                if (tautomers.equals("N")) {
                    userQueries = new ArrayList<IAtomContainer>();
                    userQueries.add(MoleculeCreator.getMoleculeFromMolfile(query, false));
                } else {
                    userQueries = TautomerUtility.getTautomersFromMolfile(query);
                }
            }


        } else if (queryType.equals(Utils.QUERY_TYPE_SMILES)) {
            if (tautomers.equals("N")) {
                SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
                IAtomContainer atc = sp.parseSmiles(query);
                userQueries = new ArrayList<IAtomContainer>();
                userQueries.add(atc);
            }
            else {
                userQueries = TautomerUtility.getTautomersFromSmiles(query);
            }
        }
        debug("# queries " + userQueries.size());
        return userQueries;
    }


    /**
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
     * @param exactYN exact search (identity search) indicator
     * @param atomCount count of atoms in the query (hydrogens stripped)
     * @return content for an SQL WHERE clause on ORCHEM_FINGPRINT_SUBSEARCH
     * @throws CDKException
     */
    static String whereClauseFromFingerPrint(IAtomContainer atc, String debugYN, String exactYN,
                                             int atomCount) throws CDKException {

        BitSet fingerprint = FingerPrinterAgent.FP.getFingerPrinter().getFingerprint(atc);

        String whereCondition = "";
        StringBuffer builtCondition = new StringBuffer();
        int fpSize = FingerPrinterAgent.FP.getFpSize();
        for (int i = 1; i < fpSize; i++) { // ignore bit 0 of OrchemFingerprinter = always set
            if ((fingerprint.get(i))) {
                builtCondition.append(" and bit" + i + "='1'");
            }
        }
        if (exactYN.equals("Y")) {
            builtCondition.append(" and nonh_atom_count=" + atomCount);
        }
        whereCondition += builtCondition.toString();
        whereCondition = whereCondition.trim();
        debug("where condition is : " + whereCondition, debugYN);
        return whereCondition;
    }


    /**
     * Calls {@link #whereClauseFromFingerPrint(IAtomContainer,String,String,int)}
     * <BR>
     * Method scope=public -> used as Oracle Java stored procedure
     *
     * @param queryKey key for the queries map
     * @param qIndex index indicating which "query" from queries map we're after
     * @param debugYN
     * @param exactYN exact search (identity search) indicator
     * @return a SQL WHERE clause
     * @throws CDKException
     * @throws SQLException
     */

    public static String getWhereClause(Integer queryKey, Integer qIndex, String debugYN,
                                        String exactYN) throws CDKException, SQLException {
        IAtomContainer queryAtc = queries.get(queryKey).get(qIndex).mol;
        int atomCount = queries.get(queryKey).get(qIndex).atomCount;
        return whereClauseFromFingerPrint(queryAtc, debugYN, exactYN, atomCount);
    }


    /**
     * Puts CDK atomcontainer(s) in a Map, from where it can be retrieved by
     * other methods later on during the substructure search.
     *
     * @param queryKey key value for the Java Map
     * @param queryMolecules
     * @param debugYN
     */
    static void stash(Integer queryKey, List<IAtomContainer> queryMolecules, String debugYN) {
        List<UserQueryMolecule> uqmList = new ArrayList<UserQueryMolecule>();
        for (IAtomContainer queryMolecule : queryMolecules) {

            MoleculeCreator.backupExplicitHcount(queryMolecule);
            queryMolecule = AtomContainerManipulator.removeHydrogens(queryMolecule);
            IAtom[] sortedAtoms = (IsomorphismSort.atomsByFrequency(queryMolecule));
            queryMolecule.setAtoms(sortedAtoms);
            int[] explHydrogenCountBackup = MoleculeCreator.createExplHydrogenArray(queryMolecule);
            Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(queryMolecule);

            UserQueryMolecule uqm = new UserQueryMolecule();
            uqm.mol = queryMolecule;
            uqm.atomsAndBonds = atomAndBondCounts;
            uqm.explicitHydrogenCountBackup = explHydrogenCountBackup;
            uqm.atomCount = queryMolecule.getAtomCount();
            uqmList.add(uqm);
        }
        debug("stashed # queries = " + uqmList.size(), debugYN);
        queries.put(queryKey, uqmList);
    }

    /**
     * Calls {@link #stash}, to stash query atomcontainer(s) in a map to be used later on.
     * There can be more than one query atom container coming from the userQuery CLOB, for
     * example when it's a RGroup query (a type of CTFile) or when tautomers are to be included.
     * <BR>
     * Method scope=public -> used as Oracle Java stored procedure
     * @param queryKey key for the queries map
     * @param userQuery a Smiles string, or Mol..
     * @param queryType smiles or mol
     * @param tautomers
     * @param debugYN
     * @throws Exception
     */
    public static Integer stashQueriesInMap(Integer queryKey, Clob userQuery, String queryType, String tautomers,
                                            String debugYN) throws Exception {
        List<IAtomContainer> queryMolecules = translateUserQueryClob(userQuery, queryType, tautomers);
        stash(queryKey, queryMolecules, debugYN);
        return queryMolecules.size();
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
     * @param queryKey map key to queries
     * @param qIdx index for contaner list in queries
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
    public static String isPossibleCandidate(Integer queryKey, Integer qIdx, String compoundId,
                                             Integer tripleBondCount, Integer sCount, Integer oCount, Integer nCount,
                                             Integer fCount, Integer clCount, Integer brCount, Integer iCount,
                                             Integer cCount, String debugYN) {
        String retVal = "Y";

        debug("check possible candidate " + compoundId, debugYN);

        UserQueryMolecule qc = queries.get(queryKey).get(qIdx);
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
     * @param queryKey map key to queries
     * @param qIdx index for contaner list in queries
     * @param compoundId the database id for the database compound
     * @param atoms atoms of database compound, see more at {@ link uk.ac.ebi.orchem.OrchemMoleculeBuilder }
     * @param bonds bonds atoms of database compound, see more at {@ link uk.ac.ebi.orchem.OrchemMoleculeBuilder }
     * @param debugYN
     * @param strictStereoIsomorphismYN
     * @return Y or N to indicate is-substructure
     */
    public static String isomorphismCheck(Integer queryKey, Integer qIdx, String compoundId, Clob atoms, Clob bonds,
                                          String debugYN, String strictStereoIsomorphismYN, String swapYN) {

        String retVal = null;
        try {
            debug("isomorphism check for " + compoundId, debugYN);
            // Get user query structure
            UserQueryMolecule qc = queries.get(queryKey).get(qIdx);
            IAtomContainer queryMolecule = qc.mol;

            // Get candidate compound
            int clobLenAtoms = new Long(atoms.length()).intValue();
            String atString = (atoms.getSubString(1, clobLenAtoms));
            int clobLenBonds = new Long(bonds.length()).intValue();
            String bondString = (bonds.getSubString(1, clobLenBonds));
            IAtomContainer databaseMolecule = OrchemMoleculeBuilder.getBasicAtomContainer(atString, bondString);

            //Invoke the CDK implementation of the VF2 algorithm
            SubgraphIsomorphism s = null;
            if (swapYN.equals("N"))
                s =
   new SubgraphIsomorphism(databaseMolecule, queryMolecule, strictStereoIsomorphismYN, qc.explicitHydrogenCountBackup);
            else
                s =
   new SubgraphIsomorphism(queryMolecule, databaseMolecule, strictStereoIsomorphismYN, new int[queryMolecule.getAtomCount()]);

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
     * @param queryKey
     */
    public static void removeFromMap(Integer queryKey) {
        queries.remove(queryKey);
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
        debug(debugMessage, "Y");
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


