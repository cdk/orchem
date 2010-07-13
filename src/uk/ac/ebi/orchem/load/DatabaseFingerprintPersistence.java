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

package uk.ac.ebi.orchem.load;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.BitSet;
import java.util.Calendar;
import java.util.Map;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OraclePreparedStatement;

import oracle.sql.CLOB;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.tools.CDKHydrogenAdder;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.isomorphism.IsomorphismSort;
import uk.ac.ebi.orchem.search.OrchemMoleculeBuilder;
import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * Calculates fingerprints and persist these in the database
 *
 * @author Mark Rijnbeek
 */
public class DatabaseFingerprintPersistence {

    /**
     * Create a fingerprint for each compound in the provided ResultSet 
     * and store it in the database.
     * <br>
     *
     * @param compounds ResultSet of compounds to fingerprint and persist the fingerprint
     * @param logInfo can be used to log/identify calling class 
     * @throws Exception
     */
    public void persist(ResultSet compounds, String logInfo ) throws Exception {

        OracleConnection conn = null;
        String logCurrID = "";

        /* Log message to be fed back to user */
        StringBuilder logMsg = new StringBuilder();

        try {
            long start = System.currentTimeMillis();
            logMsg.append("\nStarted at " + now());

            /* A number of long variable to capture elapse time for the log message */
            long getClobTime = 0;
            long makeMolTime = 0;
            long makeFpTime = 0;

            conn = (OracleConnection)new OracleDriver().defaultConnection();
            //conn = (OracleConnection)new UnitTestConnection().getDbConnection();

            //http://www.oracle-base.com/articles/10g/Commit_10gR2.php
            Statement sql = conn.createStatement();
            sql.executeUpdate("ALTER SESSION SET COMMIT_WRITE='BATCH' ");
            conn.setAutoCommit(false);

            MDLV2000Reader mdlReader = new MDLV2000Reader();


            /* The extended fingerprint is used, but for the substructure search we only capture the basic fingerprint */
            IFingerprinter extendedFingerPrinter = FingerPrinterAgent.FP.getExtendedFingerPrinter();
            BitSet extendedFingerPrint;
            final int extendedFingerprintSize = FingerPrinterAgent.FP.getExtendedFpSize();
            final int basicFingerprintSize = FingerPrinterAgent.FP.getFpSize();

            /* Prepare the (flexible) query on the base compound table in the schema */
            String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
            String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

            /* Statement for inserts into the orchem similarity search table */
            PreparedStatement psInsertSimiFp = conn.prepareStatement(
            "insert " + "into orchem_fingprint_simsearch (id, bit_count,fp) values (?,?,?)");

            /* Statement for inserts into the substructure search table */
            StringBuffer sb = new StringBuffer();
            sb.append
            (
            " insert into orchem_fingprint_subsearch " + 
            "   ( id, atoms, bonds, nonh_atom_count, single_bond_count, " +
                " double_bond_count, triple_bond_count, aromatic_bond_count, s_count, o_count, " +
                " n_count, f_count, cl_count,  br_count, i_count, " +
                " c_count, p_count, saturated_bond_count "
            );
            for (int idx = 0; idx < basicFingerprintSize; idx++)
                sb.append(",bit" + (idx));

            final int NON_BIT_COLUMN_COUNT=18;
            sb.append(") values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?,? ");

            for (int idx = 0; idx < basicFingerprintSize; idx++)
                sb.append(",?");
            sb.append(")");
            PreparedStatement psInsertSubstrFp = conn.prepareStatement(sb.toString());

            /* Statement for inserts of atoms and bonds that need to go into a clob (2 large) */
            OraclePreparedStatement psInsertBigAtomsBonds = (OraclePreparedStatement)conn.prepareStatement(
            "insert " + "into orchem_big_molecules (id, atoms,bonds) values (?,?,?)");

            long bef;
            String molfile = null;
            logMsg.append("\nSet up at " + now());
            CLOB largeAtomsClob=null;
            CLOB largeBondsClob=null;

            CDKHydrogenAdder hydrogenAdder = CDKHydrogenAdder.getInstance(DefaultChemObjectBuilder.getInstance());
            /* Start the main loop over the base compound table */
            while (compounds.next()) {
                try {
                    logCurrID = compounds.getString(compoundTablePkColumn);

                    bef = System.currentTimeMillis();
                    molfile = compounds.getString(compoundTableMolfileColumn);
                    getClobTime += (System.currentTimeMillis() - bef);

                    if (molfile != null) {
                        bef = System.currentTimeMillis();
                        /* Create a CDK molecule from the molfile */
                        NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                        
                        for(IAtom atom : molecule.atoms()){
                            try {
                                hydrogenAdder.addImplicitHydrogens(molecule,atom);
                            } catch (CDKException cdke) {
                                atom.setHydrogenCount(99);
                            }
                        }

                        makeMolTime += (System.currentTimeMillis() - bef);

                        /* Sort the container to speed up substructure searches */
                        IAtom[] sortedAtoms = (IsomorphismSort.atomsByFrequency(molecule));
                        molecule.setAtoms(sortedAtoms);

                        /* Fingerprint the molecule */
                        bef = System.currentTimeMillis();
                        extendedFingerPrint = extendedFingerPrinter.getFingerprint(molecule);

                        byte[] bytes = Utils.toByteArray(extendedFingerPrint, extendedFingerprintSize);
                        makeFpTime += (System.currentTimeMillis() - bef);

                        /* Prepare statement for Similarity search helper table */
                        psInsertSimiFp.setString(1, compounds.getString(compoundTablePkColumn));
                        psInsertSimiFp.setInt(2, extendedFingerPrint.cardinality());
                        psInsertSimiFp.setBytes(3, bytes);

                        /* Prepare statement for OrChem compound table */
                        Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(molecule);

                        int pos = 0;
                        IAtom[] atoms = new IAtom[molecule.getAtomCount()];
                        for (IAtom atom : molecule.atoms() ) {
                            atoms[pos] = atom;
                            pos++;
                        }
                        String atomString = OrchemMoleculeBuilder.atomsAsString(atoms);
                        String bondString = OrchemMoleculeBuilder.bondsAsString(atoms,molecule);
                        
                        if (atomString.length()>4000 || bondString.length() > 4000)  {
                            largeAtomsClob= CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
                            largeBondsClob= CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);

                            largeAtomsClob.open(CLOB.MODE_READWRITE);
                            largeBondsClob.open(CLOB.MODE_READWRITE);

                            largeAtomsClob.setString(1, atomString);
                            largeBondsClob.setString(1, bondString);
        
                            psInsertBigAtomsBonds.setString(1,compounds.getString(compoundTablePkColumn));
                            psInsertBigAtomsBonds.setCLOB(2, largeAtomsClob);
                            psInsertBigAtomsBonds.setCLOB(3, largeBondsClob);
                            psInsertBigAtomsBonds.executeUpdate();

                            largeAtomsClob.close();
                            largeAtomsClob.freeTemporary();

                            largeBondsClob.close();
                            largeBondsClob.freeTemporary();

                            atomString=null;
                            bondString=null;
                        }

                        /* Prepare statement for Substructure search helper table */
                        int idx = 1;
                        psInsertSubstrFp.setString(idx, compounds.getString(compoundTablePkColumn));
                        psInsertSubstrFp.setString(++idx, atomString);
                        psInsertSubstrFp.setString(++idx, bondString);
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.ATOM_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.SINGLE_BOND_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.DOUBLE_BOND_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.TRIPLE_BOND_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.AROMATIC_BOND_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.S_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.O_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.N_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.F_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.CL_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.BR_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.I_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.C_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.P_COUNT));
                        psInsertSubstrFp.setInt(++idx, (Integer)atomAndBondCounts.get(AtomsBondsCounter.SATURATED_COUNT));


                        for (int i = 0; i < basicFingerprintSize; i++) {
                            idx = i + NON_BIT_COLUMN_COUNT +1 ;

                            if (extendedFingerPrint.get(i)) {
                                psInsertSubstrFp.setString(idx, "1");
                            } else {
                                psInsertSubstrFp.setString(idx, null);
                            }
                        }

                        try {
                            psInsertSimiFp.executeUpdate();
                            psInsertSubstrFp.executeUpdate();
                            conn.commit();

                        } catch (SQLException e) {
                            conn.rollback();
                            System.err.println("SQL error for " + compoundTablePkColumn + ": " + compounds.getString(compoundTablePkColumn) + " ="+ e.getMessage());
                            logMsg.append("\nLoop err " + compoundTablePkColumn + ": " + compounds.getString(compoundTablePkColumn) + " " + e.getMessage());
                            //TODO program should terminate on too many SQL errors (perhaps a counter)
                        }
                    }

                } catch (CDKException e) {
                    System.err.println("Loop error for " + compoundTablePkColumn + ": " + compounds.getString(compoundTablePkColumn) + " ="+ e.getMessage());
                    logMsg.append("\n" +
                            System.currentTimeMillis() + " Loop err " + compoundTablePkColumn + ": " + compounds.getString(compoundTablePkColumn) + " " + e.getMessage());
                }
            }

            psInsertSimiFp.close();
            psInsertSubstrFp.close();

            long end = System.currentTimeMillis();

            System.out.println("\n\nOverall elapse time (ms) : " + (end - start));
            System.out.println("Getting clobs     (ms) :" + getClobTime);
            System.out.println("Make molecules    (ms) :" + makeMolTime);
            System.out.println("Make fingerprints (ms) :" + makeFpTime);

            logMsg.append("\nOverall elapse time (ms) :" + (end - start) + "\nGetting clobs       (ms) :" + getClobTime + "\nMake molecules      (ms) :" + makeMolTime +
                          "\nMake fingerprints   (ms) :" + makeFpTime);

            logMsg.append("\nFinished at " + now());

        } catch (Exception e) {
            e.printStackTrace();
            logMsg.append("\nError at time " + now());
            logMsg.append("\n\nERROR - program aborted..\n\n" +
                    "Loop was on compound ID " + logCurrID + "\nMsg: " + e.getMessage() + "\nStack\n" +
                    Utils.getErrorString(e));
        } finally {
            OraclePreparedStatement psInsertLog =
                (OraclePreparedStatement)conn.prepareStatement("insert into orchem_log (log_id, who, when, what) values (orchem_sequence_log_id.nextval,?,sysdate,?)");
            psInsertLog.setString(1, "Load fingerprints " + logInfo);

            CLOB msgClob = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
            msgClob.open(CLOB.MODE_READWRITE);
            msgClob.setString(2, logMsg.toString());
            psInsertLog.setCLOB(2, msgClob);

            psInsertLog.executeUpdate();

            if (msgClob.isTemporary() && msgClob.isOpen()) {
                msgClob.close();
                msgClob.freeTemporary();
            }
            conn.commit();
            //System.gc();
        }
    }

    /**
     * Format current time for logging purposes
     * @return
     */
    private static String now() {
        String DATE_FORMAT_NOW = "dd MMM HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }


}
