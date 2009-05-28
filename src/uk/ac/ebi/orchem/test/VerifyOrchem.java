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

package uk.ac.ebi.orchem.test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
    public static void verify(String primaryKey) throws Exception {

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
                System.out.println("OKAY: compound found with primary key "+primaryKey);


                String molfile = res.getString(compoundTableMolfileColumn);

                if (molfile != null) {
                    NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                    System.out.println("OKAY: CDK molecule created");
                    fpBitset = fingerPrinter.getFingerprint(molecule);
                    System.out.println("OKAY: Fingerprint made");

                    SmilesGenerator sg = new SmilesGenerator();
                    fixCarbonHCount(molecule);
                    String smiles = sg.createSMILES(molecule);
                    System.out.println("OKAY: Smiles generated " + smiles);

                } else {
                    System.out.println("ERROR-> Molfile is null");
                }

            } else {
                System.out.println("ERROR-> No compound found with primary key = " + primaryKey + "! ");
            }
            res.close();
            stmtQueryCompounds.close();

        } catch (Exception e) {
            System.out.println("ERROR");
            System.out.println(Utils.getErrorString(e));
        }


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
