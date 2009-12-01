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
import java.sql.Connection;
import java.sql.SQLException;

import java.util.Map;

import oracle.jdbc.driver.OracleDriver;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * Class that provides functionality for performing SMARTS searches.<BR>
 * Extends {@link SubstructureSearch}.
 * Some methods in this class are called from PL/SQL and are wrapped as "Java stored procedures".
 *
 * @author markr@ebi.ac.uk, 2009
 */

public class SMARTS_Search extends SubstructureSearch {

    /**
     * Calls {@link #whereClauseFromFingerPrint(IAtomContainer,String)}
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
        IAtomContainer qat = SMARTSParser.parse(query);
        IAtomContainer hStrippedAtc = new NNMolecule(AtomContainerManipulator.removeHydrogens(qat));
        return whereClauseFromFingerPrint(hStrippedAtc, debugYN);
    }


    /**
     * TODO JAVADOC
     * @param smartsQuery
     * @param vReturnArray
     * @throws Exception
     */
    public static void getAtomAndBondCounts(Clob smartsQuery, oracle.sql.ARRAY[] vReturnArray) throws Exception {

        int clobLen = new Long(smartsQuery.length()).intValue();
        String query = (smartsQuery.getSubString(1, clobLen));
        IAtomContainer qat = SMARTSParser.parse(query);
        Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(qat);
        
        Integer[] countsArray = new Integer[9];
        int idx=0;
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.TRIPLE_BOND_COUNT);
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.S_COUNT);
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.O_COUNT);
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.N_COUNT);
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.F_COUNT);
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.CL_COUNT);
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.BR_COUNT);
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.I_COUNT);
        countsArray[idx++]=(Integer)atomAndBondCounts.get(AtomsBondsCounter.C_COUNT);

        Connection conn = new OracleDriver().defaultConnection();
        ArrayDescriptor desc = ArrayDescriptor.createDescriptor("ORCHEM_NUMBER_TABLE", conn);
        vReturnArray[0] = new ARRAY(desc, conn, countsArray);
    }



    /**
     * TODO javadoc
     *
     *
     */
    public static String smartsMatch(String compoundId, Clob dbMolecule, Clob smartsQuery) {

        String retVal = "N";
        try {
            int clobLenAtoms = new Long(dbMolecule.length()).intValue();
            String dbMol = (dbMolecule.getSubString(1, clobLenAtoms));
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            NNMolecule databaseMolecule = MoleculeCreator.getNNMolecule(mdlReader, dbMol, false);

            try {
                CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(NoNotificationChemObjectBuilder.getInstance());
                adder.addImplicitHydrogens(databaseMolecule);
            } catch (CDKException e) {
                debug("CDK warning for ID " + compoundId + ":" + e.getMessage());
            }

            int clobLenBonds = new Long(smartsQuery.length()).intValue();
            String SMARTS = (smartsQuery.getSubString(1, clobLenBonds));

            //SMARTSQueryTool querytool=null;
            OrchemSMARTSQueryTool querytool=null;
            try {
                //querytool = new SMARTSQueryTool(SMARTS);
                querytool = new OrchemSMARTSQueryTool(SMARTS);

            } catch (Exception e) {
                throw new RuntimeException ("Could not instantiate SMARTSQueryTool "+ e.getMessage());
            }

            try {
                if (querytool.matches(databaseMolecule))
                    retVal = "Y";
            } catch (Exception e) {
                throw new RuntimeException ("Could not perform match with SMARTSQueryTool "+ e.getMessage());
            }

        } catch (Exception e) {
            debug("Error for ID " + compoundId + ": " + e.getMessage());
        }

        return retVal;
    }


}


