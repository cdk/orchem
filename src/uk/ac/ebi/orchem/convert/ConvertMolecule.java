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

package uk.ac.ebi.orchem.convert;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import java.io.Writer;

import java.sql.SQLException;


import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;
import oracle.sql.CLOB;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 * To be used as java stored procedure, 
 */

public class ConvertMolecule {
    /**
     * Convert Molfile to Smiles 
     * 
     * @author federico_paoli
     *
     * @param Molfile
     * @return CLOB
     * @throws Exception
     */

    public static CLOB MolfileToSmiles(CLOB Molfile) throws Exception {
        CLOB psmiles=null;
        OracleConnection conn = null;
        conn = (OracleConnection)new OracleDriver().defaultConnection();
        try {
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            // Convert CLOB to String
            /*int clobLen = new Long(Molfile.length()).intValue();
             String molfile = (Molfile.getSubString(1, clobLen));*/
            StringBuffer molfile=new StringBuffer();
            Reader clobReader = Molfile.getCharacterStream( );
            char[] buffer = new char[ Molfile.getBufferSize( ) ];
            int read = 0;
            int bufflen = buffer.length;
            while( (read = clobReader.read(buffer,0,bufflen)) > 0 ) {
              molfile.append(new String( buffer, 0, read ));
            }
            clobReader.close( );
            if (molfile != null) {
                    // Generate SMILES from molfile
                    NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile.toString());
                    SmilesGenerator sg = new SmilesGenerator();
                    fixCarbonHCount(molecule);
                    String smiles = sg.createSMILES(molecule);
                    // Convert String to CLOB
                    psmiles=CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
                    psmiles.open(CLOB.MODE_READWRITE);
                    psmiles.setString(1, smiles);
                    psmiles.close();

             } else {
               psmiles=null;
             }
  
        } catch (Exception e) {
            psmiles=null;
            System.out.println(Utils.getErrorString(e));
      }
        finally {
          conn.close();
        }
     return psmiles;
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
