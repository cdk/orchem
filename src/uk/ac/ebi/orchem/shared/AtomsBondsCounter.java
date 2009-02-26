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
package uk.ac.ebi.orchem.shared;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.SaturationChecker;

/**
 * Class to counting (particular) elements and bond orders in a given atom container
 * 
 * @author markr@ebi.ac.uk
 */
public class AtomsBondsCounter {

    public static final String SINGLE_BOND_COUNT   = "sinbc";
    public static final String DOUBLE_BOND_COUNT   = "doubc";
    public static final String TRIPLE_BOND_COUNT   = "tribc";
    public static final String AROMATIC_BOND_COUNT = "aromcc";
    public static final String S_COUNT = "sc";
    public static final String O_COUNT = "oc";
    public static final String N_COUNT = "nc";
    public static final String F_COUNT = "fc";
    public static final String CL_COUNT = "clc";
    public static final String BR_COUNT = "brc";
    public static final String I_COUNT = "ic";
    public static final String C_COUNT = "cc";
    public static final String P_COUNT = "pc";
    public static final String SATURATED_COUNT = "satc";


    /**
     * Given an atom container, calculates atoms and bonds and
     * stores these in a result map. Used for substructure searching, to
     * get a quick filter when comparing two molecules.
     *
     * @param iac
     * @return map with various counts for bonds and atoms. See static xxx_COUNT values for map content.
     */
    public static Map atomAndBondCount(IAtomContainer iac) {

        Map result = new HashMap();

        //get atom and bond counts
        Integer molSingleBondCount = 0;
        Integer molDoubleBondCount = 0;
        Integer molTripleBondCount = 0;
        Integer molAromBondCount = 0;

        Integer molSCount = 0;
        Integer molOCount = 0;
        Integer molNCount = 0;
        Integer molFCount = 0;
        Integer molClCount = 0;
        Integer molBrCount = 0;
        Integer molICount = 0;
        Integer molCCount = 0;
        Integer molPCount = 0;
        Integer satCount = 0;

        IBond bond;
        IAtom atom;
        for (int i = 0; i < iac.getBondCount(); i++) {
            bond = iac.getBond(i);

            if (bond.getFlag(CDKConstants.ISAROMATIC)) molAromBondCount ++;
            else if (bond.getOrder() ==  Bond.Order.SINGLE )  
                molSingleBondCount++;
            else if (bond.getOrder() == Bond.Order.DOUBLE) 
                molDoubleBondCount++;
            else if (bond.getOrder() == Bond.Order.TRIPLE) 
                molTripleBondCount++;
        }

        SaturationChecker satCheck;
        try {
            satCheck = new SaturationChecker();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException(e));
        }

        for (int i = 0; i < iac.getAtomCount(); i++) {
            atom = iac.getAtom(i);
            if (atom.getSymbol().equals("S"))
                molSCount++;
            else if (atom.getSymbol().equals("N"))
                molNCount++;
            else if (atom.getSymbol().equals("O"))
                molOCount++;
            else if (atom.getSymbol().equals("F"))
                molFCount++;
            else if (atom.getSymbol().equals("Cl"))
                molClCount++;
            else if (atom.getSymbol().equals("Br"))
                molBrCount++;
            else if (atom.getSymbol().equals("I"))
                molICount++;
            else if (atom.getSymbol().equals("C"))
                molCCount++;
            else if (atom.getSymbol().equals("P"))
                molPCount++;

            try {
                if (satCount != 999999)
                    if (satCheck.isSaturated(atom, iac)) {
                        satCount++;
                    }
            } catch (CDKException e) {
                e.printStackTrace();
                satCount = 999999;
            }

        }

        result.put(SINGLE_BOND_COUNT, molSingleBondCount);
        result.put(DOUBLE_BOND_COUNT, molDoubleBondCount);
        result.put(TRIPLE_BOND_COUNT, molTripleBondCount);
        result.put(AROMATIC_BOND_COUNT, molAromBondCount);

        result.put(S_COUNT, molSCount);
        result.put(O_COUNT, molOCount);
        result.put(N_COUNT, molNCount);
        result.put(F_COUNT, molFCount);
        result.put(CL_COUNT, molClCount);
        result.put(BR_COUNT, molBrCount);
        result.put(I_COUNT, molICount);
        result.put(C_COUNT, molCCount);
        result.put(P_COUNT, molPCount);

        result.put(SATURATED_COUNT, satCount);
        
        return result;
    }

}
