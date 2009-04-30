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

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.StringTokenizer;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.nonotify.NNMolecule;

/**
 * This class can be used to quickly materialize half-wit molecules during substructure searching, rather
 * than creating them from scratch from .mol or .cml files every time (=WAY more expensive run-time)<BR>
 * The other alternative, to store these compounds as proper serialized Java classes
 * also turned out to be seriously expensive with regards to time and storage.<P><P>
 * 
 */
public class OrchemMoleculeBuilder {

    /**
     * Builds a molecule using the atoms and bonds stored as string data for each compound in the database.
     * See more on these strings at {@link uk.ac.ebi.orchem.load.LoadCDKFingerprints#atomsAsString} and 
     * {@link uk.ac.ebi.orchem.load.LoadCDKFingerprints#bondsAsString} and 
     * @return molecule
     */
    public static NNMolecule getBasicAtomContainer(String atomString, String bondString) {

        NNMolecule mol = new NNMolecule();

        StringTokenizer s = new StringTokenizer(atomString);
        int atomPos = 0;
        Map<Integer, IAtom> atomMap = new HashMap<Integer, IAtom>();
        List<IBond> bondList = new ArrayList<IBond>();

        while (s.hasMoreElements()) {
            IAtom at = new Atom();
            at.setSymbol(s.nextToken());
            atomMap.put(atomPos++, at);
        }

        s = new StringTokenizer(bondString);
        while (s.hasMoreElements()) {

            IAtom atA = atomMap.get(new Integer(s.nextToken()));
            IAtom atB = atomMap.get(new Integer(s.nextToken()));
            char bondOrder = s.nextToken().charAt(0);
            char aromaticYN = s.nextToken().charAt(0);

            IBond b = new Bond();
            b.setAtom(atA, 0);
            b.setAtom(atB, 1);
            switch (bondOrder) {
            case 'S':
                b.setOrder(IBond.Order.SINGLE);
                break;
            case 'D':
                b.setOrder(IBond.Order.DOUBLE);
                break;
            case 'T':
                b.setOrder(IBond.Order.TRIPLE);
                break;
            case 'Q':
                b.setOrder(IBond.Order.QUADRUPLE);
                break;
            }

            switch (aromaticYN) {
            case 'Y':
                b.setFlag(CDKConstants.ISAROMATIC, true);
                break;
            case 'N':
                b.setFlag(CDKConstants.ISAROMATIC, false);
                break;
            }

            bondList.add(b);
        }
        
        
        IAtom[] atoms = new IAtom[atomMap.size()];
        int pos = 0;
        Iterator iterator = atomMap.values().iterator();
        while (iterator.hasNext()) {
            IAtom iAtom = (IAtom)iterator.next();
            atoms[pos++] = iAtom;
        }

        IBond[] bonds = bondList.toArray(new IBond[0]);
        mol.setAtoms(atoms);
        mol.setBonds(bonds);

        return mol;
    }

}
