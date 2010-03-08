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

package uk.ac.ebi.orchem.test;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.BitSet;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * To be used as java stored procedure, verifies the OrChem setup by a user
 */

public class VerifyOrchem {
    /**
     * Displays user feedback on creating a compound and a fingerprint and
     * creating a Smiles String
     * @param primaryKey
     */
    public static Clob verify(String primaryKey) throws Exception {

        StringBuffer out = new StringBuffer();
        
        try {
            OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();

            MDLV2000Reader mdlReader = new MDLV2000Reader();
            IFingerprinter fingerPrinter = FingerPrinterAgent.FP.getFingerPrinter();
            BitSet fpBitset;

            String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
            String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
            String compoundTableMolfileColumn =
                OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);

            String compoundQuery =
                " select " + 
                "    " + compoundTablePkColumn + 
                "   ," + compoundTableMolfileColumn + 
                " from   " + compoundTableName + " c " + 
                " where  " + compoundTablePkColumn + "=?";


            PreparedStatement stmtQueryCompounds = conn.prepareStatement(compoundQuery);
            stmtQueryCompounds.setString(1,primaryKey);
            
            ResultSet res = stmtQueryCompounds.executeQuery();

            if (res.next()) {
                out.append("\nOKAY: compound found with primary key "+primaryKey);

                String molfile = res.getString(compoundTableMolfileColumn);

                out.append("\nDEBUG: molfile is \n"+molfile);

                if (molfile != null) {
                    NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                    out.append("\nOKAY: CDK molecule created");
                    fpBitset = fingerPrinter.getFingerprint(molecule);
                    out.append("\nOKAY: Fingerprint made");

                    SmilesGenerator sg = new SmilesGenerator();
                    fixCarbonHCount(molecule);
                    String smiles = sg.createSMILES(molecule);
                    out.append("\nOKAY: Smiles generated " + smiles);

                    try {
                        PreparedStatement pStmt =
                            conn.prepareStatement("select orchem_convert.molfiletoinchi(?) inchi from dual");
                        pStmt.setString(1, molfile);
                        res = pStmt.executeQuery();
                        Clob inchiClob = null;
                        int clobLen = 0;
                        String inChi = null;
                        if (res.next()) {
                            inchiClob = res.getClob("inchi");
                            clobLen = new Long(inchiClob.length()).intValue();
                            inChi = (inchiClob.getSubString(1, clobLen));
                        }
                        out.append("\nOKAY: InChi created is " + inChi);
                    } catch (SQLException e) {
                        out.append("\ncould not generate InChi "+e.getMessage());
                    }

                } else {
                    out.append("\n\nERROR-> Molfile is null");
                }

            } else {
                out.append("\nERROR-> No compound found with primary key = " + primaryKey + "! ");
            }
            res.close();
            stmtQueryCompounds.close();

        } catch (Exception e) {
            out.append("\nERROR");
            out.append(Utils.getErrorString(e));

        }
        return Utils.StringToClob(out.toString());

    }


    private static void fixCarbonHCount(Molecule mol) {
        double bondCount = 0;
        org.openscience.cdk.interfaces.IAtom atom;
        for (int f = 0; f < mol.getAtomCount(); f++) {
            atom = mol.getAtom(f);
            bondCount = mol.getBondOrderSum(atom);
            int correction = (int)bondCount - (atom.getCharge() != null ? atom.getCharge().intValue() : 0);
            if (atom.getSymbol().equals("C")) {
                atom.setHydrogenCount(4 - correction);
            } else if (atom.getSymbol().equals("N")) {
                atom.setHydrogenCount(3 - correction);
            }
        }
    }

}
