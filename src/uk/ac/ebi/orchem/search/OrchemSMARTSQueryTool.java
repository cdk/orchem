package uk.ac.ebi.orchem.search;

/* $Revision$ $Author$ $Date$
 *
 * Copyright (C) 2007  Rajarshi Guha <rajarshi@users.sourceforge.net>
 *
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.CDKHueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.smarts.HydrogenAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.LogicalOperatorAtom;
import org.openscience.cdk.isomorphism.matchers.smarts.RecursiveSmartsAtom;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.smiles.smarts.parser.SMARTSParser;
import org.openscience.cdk.smiles.smarts.parser.TokenMgrError;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;


/**
 * Stripped version of {@link org.openscience.cdk.smiles.smarts.SMARTSQueryTool}
 * <BR>
 * Limit costly allRingsFinder in initialization.
 */

public class OrchemSMARTSQueryTool {
/*
    private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(OrchemSMARTSQueryTool.class);
    private String smarts;
    private IAtomContainer atomContainer = null;
    private QueryAtomContainer query = null;

    private List<List<Integer>> matchingAtoms = null;

    public OrchemSMARTSQueryTool(String smarts) throws CDKException {
        this.smarts = smarts;
        try {
            initializeQuery();
        } catch (TokenMgrError error) {
            throw new CDKException("Error parsing SMARTS", error);
        }
    }

    public String getSmarts() {
        return smarts;
    }

    public void setSmarts(String smarts) throws CDKException {
        this.smarts = smarts;
        initializeQuery();
    }


    public boolean matches(IAtomContainer atomContainer) throws CDKException {
        return matches(atomContainer, false);
    }

    public boolean matches(IAtomContainer atomContainer, boolean forceInitialization) throws CDKException {

        if (this.atomContainer == atomContainer) {
            if (forceInitialization)
                initializeMolecule();
        } else {
            this.atomContainer = atomContainer;
            initializeMolecule();
        }

        // First calculate the recursive smarts
        initializeRecursiveSmarts(this.atomContainer);

        // lets see if we have a single atom query
        if (query.getAtomCount() == 1) {
            // lets get the query atom
            IQueryAtom queryAtom = (IQueryAtom)query.getAtom(0);

            matchingAtoms = new ArrayList<List<Integer>>();
            for (IAtom atom : this.atomContainer.atoms()) {
                if (queryAtom.matches(atom)) {
                    List<Integer> tmp = new ArrayList<Integer>();
                    tmp.add(this.atomContainer.getAtomNumber(atom));
                    matchingAtoms.add(tmp);
                }
            }
        } else {
            List bondMapping = UniversalIsomorphismTester.getSubgraphMaps(this.atomContainer, query);
            matchingAtoms = getAtomMappings(bondMapping, this.atomContainer);
        }

        return matchingAtoms.size() != 0;
    }

    public int countMatches() {
        return matchingAtoms.size();
    }

    public List<List<Integer>> getMatchingAtoms() {
        return matchingAtoms;
    }

    public List<List<Integer>> getUniqueMatchingAtoms() {
        List<List<Integer>> ret = new ArrayList<List<Integer>>();
        for (List<Integer> atomMapping : matchingAtoms) {
            Collections.sort(atomMapping);

            // see if this sequence of atom indices is present
            // in the return container
            boolean present = false;
            for (List<Integer> r : ret) {
                if (r.size() != atomMapping.size())
                    continue;
                Collections.sort(r);
                boolean matches = true;
                for (int i = 0; i < atomMapping.size(); i++) {
                    int index1 = atomMapping.get(i);
                    int index2 = r.get(i);
                    if (index1 != index2) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    present = true;
                    break;
                }
            }
            if (!present)
                ret.add(atomMapping);
        }
        return ret;
    }

    private void initializeMolecule() throws CDKException {
        // Code copied from 
        // org.openscience.cdk.qsar.descriptors.atomic.AtomValenceDescriptor;
        Map<String, Integer> valencesTable = new HashMap<String, Integer>();
        valencesTable.put("H", 1);
        valencesTable.put("Li", 1);
        valencesTable.put("Be", 2);
        valencesTable.put("B", 3);
        valencesTable.put("C", 4);
        valencesTable.put("N", 5);
        valencesTable.put("O", 6);
        valencesTable.put("F", 7);
        valencesTable.put("Na", 1);
        valencesTable.put("Mg", 2);
        valencesTable.put("Al", 3);
        valencesTable.put("Si", 4);
        valencesTable.put("P", 5);
        valencesTable.put("S", 6);
        valencesTable.put("Cl", 7);
        valencesTable.put("K", 1);
        valencesTable.put("Ca", 2);
        valencesTable.put("Ga", 3);
        valencesTable.put("Ge", 4);
        valencesTable.put("As", 5);
        valencesTable.put("Se", 6);
        valencesTable.put("Br", 7);
        valencesTable.put("Rb", 1);
        valencesTable.put("Sr", 2);
        valencesTable.put("In", 3);
        valencesTable.put("Sn", 4);
        valencesTable.put("Sb", 5);
        valencesTable.put("Te", 6);
        valencesTable.put("I", 7);
        valencesTable.put("Cs", 1);
        valencesTable.put("Ba", 2);
        valencesTable.put("Tl", 3);
        valencesTable.put("Pb", 4);
        valencesTable.put("Bi", 5);
        valencesTable.put("Po", 6);
        valencesTable.put("At", 7);
        valencesTable.put("Fr", 1);
        valencesTable.put("Ra", 2);
        valencesTable.put("Cu", 2);
        valencesTable.put("Mn", 2);
        valencesTable.put("Co", 2);

        SSSRFinder finder = new SSSRFinder(atomContainer);
        IRingSet allRings;
        allRings = finder.findRelevantRings();
        IRingSet sssr = finder.findSSSR();

        for (IAtom atom : atomContainer.atoms()) {

            // add a property to each ring atom that will be an array of
            // Integers, indicating what size ring the given atom belongs to
            // Add SSSR ring counts
            if (allRings.contains(atom)) { // it's in a ring
                atom.setFlag(CDKConstants.ISINRING, true);
                // lets find which ring sets it is a part of
                List<Integer> ringsizes = new ArrayList<Integer>();
                IRingSet currentRings = allRings.getRings(atom);
                int min = 0;
                for (int i = 0; i < currentRings.getAtomContainerCount(); i++) {
                    int size = currentRings.getAtomContainer(i).getAtomCount();
                    if (min > size) min = size;
                    ringsizes.add(size);
                }
                atom.setProperty(CDKConstants.RING_SIZES, ringsizes);
                atom.setProperty(CDKConstants.SMALLEST_RINGS, sssr.getRings(atom));
            } else {
                atom.setFlag(CDKConstants.ISINRING, false);
            }

            // determine how many rings bonds each atom is a part of
            int hCount;
            if (atom.getHydrogenCount() == CDKConstants.UNSET) hCount = 0;
            else hCount = atom.getHydrogenCount();

            List<IAtom> connectedAtoms = atomContainer.getConnectedAtomsList(atom);
            int total = hCount + connectedAtoms.size();
            for (IAtom connectedAtom : connectedAtoms) {
                if (connectedAtom.getSymbol().equals("H")) {
                    hCount++;
                }
            }
            atom.setProperty(CDKConstants.TOTAL_CONNECTIONS, total);
            atom.setProperty(CDKConstants.TOTAL_H_COUNT, hCount);

            if (valencesTable.get(atom.getSymbol()) != null) {
                int formalCharge = atom.getFormalCharge() == CDKConstants.UNSET ? 0 : atom.getFormalCharge();
                atom.setValency(valencesTable.get(atom.getSymbol()) - formalCharge);
            }
        }

        for (IBond bond : atomContainer.bonds()) {
            if (allRings.getRings(bond).getAtomContainerCount() > 0) {
                bond.setFlag(CDKConstants.ISINRING, true);
            }
        }

        for (IAtom atom : atomContainer.atoms()) {
            List<IAtom> connectedAtoms = atomContainer.getConnectedAtomsList(atom);

            int counter = 0;
            IAtom any;
            for (IAtom connectedAtom : connectedAtoms) {
                any = connectedAtom;
                if (any.getFlag(CDKConstants.ISINRING)) {
                    counter++;
                }
            }
            atom.setProperty(CDKConstants.RING_CONNECTIONS, counter);
        }

        // check for aromaticity
        try {
            AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(atomContainer);
            CDKHueckelAromaticityDetector.detectAromaticity(atomContainer);
        } catch (CDKException e) {
            logger.debug(e.toString());
            throw new CDKException(e.toString(), e);
        }
    }

    private void initializeRecursiveSmarts(IAtomContainer atomContainer) throws CDKException {
        for (IAtom atom : query.atoms()) {
            initializeRecursiveSmartsAtom(atom, atomContainer);
        }
    }

    private void initializeRecursiveSmartsAtom(IAtom atom, IAtomContainer atomContainer) throws CDKException {
        if (atom instanceof LogicalOperatorAtom) {
            initializeRecursiveSmartsAtom(((LogicalOperatorAtom)atom).getLeft(), atomContainer);
            if (((LogicalOperatorAtom)atom).getRight() != null) {
                initializeRecursiveSmartsAtom(((LogicalOperatorAtom)atom).getRight(), atomContainer);
            }
        } else if (atom instanceof RecursiveSmartsAtom) {
            ((RecursiveSmartsAtom)atom).setAtomContainer(atomContainer);
        } else if (atom instanceof HydrogenAtom) {
            ((HydrogenAtom)atom).setAtomContainer(atomContainer);
        }
    }

    private void initializeQuery() throws CDKException {
        matchingAtoms = null;
        query = SMARTSParser.parse(smarts);
    }


    private List<List<Integer>> getAtomMappings(List bondMapping, IAtomContainer atomContainer) {
        List<List<Integer>> atomMapping = new ArrayList<List<Integer>>();

        // loop over each mapping
        for (Object aBondMapping : bondMapping) {
            List list = (List)aBondMapping;

            List<Integer> tmp = new ArrayList<Integer>();
            IAtom atom1 = null;
            IAtom atom2 = null;
            // loop over this mapping
            for (Object aList : list) {
                RMap map = (RMap)aList;
                int bondID = map.getId1();

                // get the atoms in this bond
                IBond bond = atomContainer.getBond(bondID);
                atom1 = bond.getAtom(0);
                atom2 = bond.getAtom(1);

                Integer idx1 = atomContainer.getAtomNumber(atom1);
                Integer idx2 = atomContainer.getAtomNumber(atom2);

                if (!tmp.contains(idx1))
                    tmp.add(idx1);
                if (!tmp.contains(idx2))
                    tmp.add(idx2);
            }
            if (tmp.size() > 0)
                atomMapping.add(tmp);

            // If there is only one bond, check if it matches both ways.
            if (list.size() == 1 && atom1.getAtomicNumber() == atom2.getAtomicNumber()) {
                List<Integer> tmp2 = new ArrayList<Integer>();
                tmp2.add(tmp.get(0));
                tmp2.add(tmp.get(1));
                atomMapping.add(tmp2);
            }
        }


        return atomMapping;
    }
*/
}
