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

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
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
     * See more on these strings at {@link #atomsAsString} and {@link #bondsAsString} 
     * @return molecule
     */
    public static IMolecule getBasicAtomContainer(String atomString, String bondString) {

        NNMolecule mol = new NNMolecule();

        StringTokenizer s = new StringTokenizer(atomString);
        int atomPos = 0;
        Map<Integer, IAtom> atomMap = new HashMap<Integer, IAtom>();
        List<IBond> bondList = new ArrayList<IBond>();

        while (s.hasMoreElements()) {
            String token = s.nextToken();
            if (token.contains(":")) {
                PseudoAtom ps = new PseudoAtom();
                ps.setSymbol("R");
                ps.setLabel(token.substring(2));
                atomMap.put(atomPos++, ps);
            }
            else {
                IAtom at = new Atom();
                at.setSymbol(token);
                atomMap.put(atomPos++, at);
            }
        }

        s = new StringTokenizer(bondString);
        while (s.hasMoreElements()) {

            IAtom atA = atomMap.get(new Integer(s.nextToken()));
            IAtom atB = atomMap.get(new Integer(s.nextToken()));
            char bondOrder = s.nextToken().charAt(0);
            char aromaticYN = s.nextToken().charAt(0);
            int stereo = new Integer(s.nextToken());
            //System.out.println("stereo is "+stereo);


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


            for (IBond.Stereo enumStereo : IBond.Stereo.values()) {
                if (enumStereo.ordinal()==stereo)  {
                    b.setStereo(enumStereo);
                }
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

    /**
     * Method that iterates an atom array and puts the atom symbols in a String, space separated.<br>
     * The result could look for example like this:<br>
     * "N N C N C N C C C C C O C C C C C C"<P>
     * These strings are persisted in the db and can be used to quickly materialize a molecule
     * by {@link OrchemMoleculeBuilder}<br>
     * Pseudo atoms (like R#, Q and A) get special treatment.
     * 
     *
     * @param atoms
     * @return atom String listing the periodic element symbols in the atomcontainer
     * @throws SQLException
     */
    public static String atomsAsString(IAtom[] atoms) throws SQLException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < atoms.length; i++) {
            IAtom atom = atoms[i];
            if (atom instanceof org.openscience.cdk.PseudoAtom ) {
                sb.append(atom.getSymbol()).append(":").append(((PseudoAtom)atom).getLabel()).append(" ");
            }
            else {
                sb.append(atom.getSymbol()).append(" ");
            }
        }
        return sb.toString().trim();

    }

    /**
     * Method that iterates bonds in an atom container and puts the bond info in a String, space separated.<br>
     * The result could look for example like this:<br>
     * " 1 10 S N 0 0 2 S N 8 1 3 S Y 8 0 4 S N 0 5 14 S N 8"<br>
     * Read as follows: 1 10 S N 0 means atoms at atomArray[1] and atomArray[10] have
     * a single (S) bond that is not (N) aromatic and stereo is NONE (0) <br>
     * This repeats for each group of 5 tokens. Valid values for the third token are S|D|T|Q and
     * Y|N for the fourth token. The fifth token is the ordinal value of IBond.Stereo<P>
     *
     * These strings are persisted in the db and can be used to quickly materialize a molecule
     * by {@link OrchemMoleculeBuilder}
     *
     * @param atomArray the atom array used in {@link #atomsAsString}to create atom String info
     * @param atContainer
     * @return
     * @throws SQLException
     */
    public static String bondsAsString(IAtom[] atomArray, IAtomContainer atContainer) throws SQLException {

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
        
            sb.append (bond.getStereo().ordinal()).append(" ");

        }
        return sb.toString();
    }
}