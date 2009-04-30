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
package uk.ac.ebi.orchem.load;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.BitSet;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.OraclePreparedStatement;

import oracle.sql.CLOB;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * Populates the orchem fingerprint tables.
 * <P>
 * This class must be loaded and run inside an Oracle database as Java stored procedure.
 *
 * @author markr@ebi.ac.uk
 *
 */
public class LoadCDKFingerprints {

    /**
     * <BR><BR>
     *  This method loops over a given compound table, and fetches the primary key and molfile for each compound.
     *  It then creates and stores a fingerprint for each compound into the OrChem fingerprint tables.<BR>
     *  <BR>
     *  Can optionally perform the loop over a limited range of primary key values, from argumens startId to endId.<BR>
     *  <BR>
     *
     * @param startId primary key from which to start select in compound table. See {@link #load() alternative}
     * @param endId primary key up to which to select in compound table. See {@link #load() alternative}
     * @throws Exception
     */
    public static void load(String startId, String endId) throws Exception {


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
            IFingerprinter fingerPrinter = FingerPrinterAgent.FP.getFingerPrinter();
            BitSet fpBitset;
            final int fpSize = FingerPrinterAgent.FP.getFpSize();

            /* Prepare the (flexible) query on the base compound table in the schema */
            String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
            String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
            String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

            String compoundQuery =
                " select /*+ full(c) parallel(c,2) */ " + 
                "    "     + compoundTablePkColumn + 
                "   ,"     + compoundTableMolfileColumn + 
                " from   " + compoundTableName + " c ";
            
            if (startId != null && endId != null) {
                compoundQuery += "  where " + compoundTablePkColumn + " between " + startId + " and " + endId;
            }

            /* Note:
              /*+ APPEND .. tried this, but the append hint creates a table lock for DML .. do not use :(
                  Also may introduce 2 much empty space and scattered data. Bad!
                  http://www.freelists.org/post/oracle-l/Question-about-Append-hint-in-Insert,3
            */

            /* Statement for inserts into the orchem similarity search table */
            PreparedStatement psInsertSimiFp = conn.prepareStatement(
            "insert " + "into orchem_fingprint_simsearch (id, bit_count,fp) values (?,?,?)");

            /* Statement for inserts into the substructure search table */
            StringBuffer sb = new StringBuffer();

            //sb.append("insert into orchem_fingprint_subsearch ( id ");
            sb.append
            (
            " insert into orchem_fingprint_subsearch " + 
            "   (id, atoms, bonds, single_bond_count, double_bond_count, triple_bond_count, " +
            "    aromatic_bond_count, s_count, o_count, n_count, f_count, cl_count,  br_count, " +
            "    i_count, c_count, p_count, saturated_bond_count "
            );
            //TODO ! CLOB escape for long Strings!!!!!! = extra table ffs

            for (int idx = 0; idx < fpSize; idx++)
                sb.append(",bit" + (idx));
            sb.append(") values (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,? ");
            for (int idx = 0; idx < fpSize; idx++)
                sb.append(",?");
            sb.append(")");
            PreparedStatement psInsertSubstrFp = conn.prepareStatement(sb.toString());


            Statement stmtQueryCompounds = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            ResultSet res = stmtQueryCompounds.executeQuery(compoundQuery);
            long bef;

            String molfile = null;
            logMsg.append("\nSet up at " + now());

            /* Start the main loop over the base compound table */
            while (res.next()) {
                try {
                    logCurrID = res.getString(compoundTablePkColumn);

                    /* Check if the clob data is over the varchar2 bound of 4000. If not, get it as
                     * as String (which is much faster) from the resultSet */
                    bef = System.currentTimeMillis();

                    molfile = res.getString(compoundTableMolfileColumn);
                    getClobTime += (System.currentTimeMillis() - bef);

                    if (molfile != null) {
                        bef = System.currentTimeMillis();
                        /* Create a CDK molecule from the molfile */
                        NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                        makeMolTime += (System.currentTimeMillis() - bef);

                        /* Fingerprint the molecule */
                        bef = System.currentTimeMillis();
                        fpBitset = fingerPrinter.getFingerprint(molecule);

                        byte[] bytes = Utils.toByteArray(fpBitset, fpSize);
                        makeFpTime += (System.currentTimeMillis() - bef);

                        /* Prepare statement for Similarity search helper table */
                        psInsertSimiFp.setString(1, res.getString(compoundTablePkColumn));
                        psInsertSimiFp.setInt(2, fpBitset.cardinality());
                        psInsertSimiFp.setBytes(3, bytes);

                        /* Prepare statement for OrChem compound table */
                        Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(molecule);

                        int pos = 0;
                        IAtom[] atoms = new IAtom[molecule.getAtomCount()];
                        for (Iterator<IAtom> atItr = molecule.atoms().iterator(); atItr.hasNext(); ) {
                            IAtom atom = atItr.next();
                            atoms[pos] = atom;
                            pos++;
                        }
                        String atomString = atomsAsString(atoms);
                        String bondString = bondsAsString(atoms,molecule);

                        /* Prepare statement for Substructure search helper table */
                        int idx = 1;
                        psInsertSubstrFp.setString(idx, res.getString(compoundTablePkColumn));
                        psInsertSubstrFp.setString(++idx, atomString);
                        psInsertSubstrFp.setString(++idx, bondString);
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

                        /* Hash the l fingerprint into a condensed fingerprint */
                        for (int i = 0; i < fpSize; i++) {
                            idx = i + 18;

                            if (fpBitset.get(i)) {
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
                            System.err.println("SQL error for " + compoundTablePkColumn + ": " + res.getString(compoundTablePkColumn) + " ="+ e.getMessage());
                            logMsg.append("\n" + System.currentTimeMillis() + " Loop err " + compoundTablePkColumn + ": " + res.getString(compoundTablePkColumn) + " " + e.getMessage());
                            //TODO program should terminate on too many SQL errors (perhaps a counter)
                        }
                    }

                } catch (CDKException e) {
                    System.err.println("Loop error for " + compoundTablePkColumn + ": " + res.getString(compoundTablePkColumn) + " ="+ e.getMessage());
                    logMsg.append("\n" +
                            System.currentTimeMillis() + " Loop err " + compoundTablePkColumn + ": " + res.getString(compoundTablePkColumn) + " " + e.getMessage());
                }
            }

            psInsertSimiFp.close();
            psInsertSubstrFp.close();

            res.close();
            stmtQueryCompounds.close();
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
            psInsertLog.setString(1, "Load fingerprints " + startId + " " + endId);

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
            conn.close();
            System.gc();
        }
    }

    public static String now() {
        String DATE_FORMAT_NOW = "dd MMM HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    /**
     * Method that iterates an atom array and puts the atom symbols in a String, space separated.<br>
     * The result could look for example like this:<br>
     *  "N N C N C N C C C C C O C C C C C C"<P>
     * These strings are persisted in the db and can be used to quickly materialize a molecule
     * by {@link uk.ac.ebi.orchem.search.OrchemMoleculeBuilder}
     *  
     * @param atoms
     * @return atom String listing the periodic element symbols in the atomcontainer
     * @throws SQLException
     */
    private static String atomsAsString(IAtom[] atoms) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < atoms.length; i++) {
            sb.append(atoms[i].getSymbol()).append(" ");
        }
        return sb.toString().trim();

    }

    /**
     * Method that iterates bonds in an atom container and puts the bond info in a String, space separated.<br>
     * The result could look for example like this:<br>
     * " 1 10 S N 0 2 S N 1 3 S Y 0 4 S N 5 14 S N "<br>
     * Read as follows: 1 10 S N means atoms at atomArray[1] and atomArray[10] have
     * a single (S) bond that is not (N) aromatic.<br>
     * This repeats for each group of 4 tokens. Valid values for the third token are S|D|T|Q and 
     * Y|N for the fourth token.<P>
     * 
     * These strings are persisted in the db and can be used to quickly materialize a molecule
     * by {@link uk.ac.ebi.orchem.search.OrchemMoleculeBuilder}
     * 
     * @param atomArray the atom array used in {@link #atomsAsString} to create atom String info
     * @param atContainer
     * @return
     * @throws SQLException
     */
    private static String bondsAsString(IAtom[] atomArray, IAtomContainer atContainer) throws SQLException {

        StringBuilder sb = new StringBuilder();

        for (Iterator<IBond> bondItr = atContainer.bonds().iterator(); bondItr.hasNext(); ) {
            IBond bond = bondItr.next();
            IAtom a1 = null, a2 = null;
            for (Iterator<IAtom> itr = bond.atoms().iterator(); itr.hasNext(); ) {
                a1 = itr.next();
                a2 = itr.next();
            }

            for (int i = 0; i < atomArray.length; i++) {
                if (atomArray[i] == a1)
                    sb.append(i).append(" ");
                if (atomArray[i] == a2)
                    sb.append(i).append(" ");
            }
            if (bond.getOrder() == IBond.Order.SINGLE)
                sb.append("S").append(" ");
            else if (bond.getOrder() == IBond.Order.DOUBLE)
                sb.append("D").append(" ");
            else if (bond.getOrder() == IBond.Order.TRIPLE)
                sb.append("T").append(" ");
            else if (bond.getOrder() == IBond.Order.QUADRUPLE)
                sb.append("Q").append(" ");

            if (bond.getFlag(CDKConstants.ISAROMATIC))
                sb.append("Y").append(" ");
            else
                sb.append("N").append(" ");
        }
        return sb.toString();
    }


    /**
     *  Loops over a compound table specified by input arguments.<BR>
     *  Fetch the primary key and the mol file from the compound table, and stores
     *  a fingerprint for each compound into the orchem fingerprint table.<BR>
     *  Loops over <b>all</b> rows in input compound table.
     *
     * @throws Exception
     */
    public static void load() throws Exception {
        load(null, null);
    }
    /*
    public static void main(String[] args) throws Exception {
        LoadCDKFingerprints l  = new LoadCDKFingerprints();
        l.load("1","50");
        // begin delete orchem_fingprint_subsearch; delete orchem_fingprint_simsearch; delete orchem_log; commit; end;
    }
    */
}


