package uk.ac.ebi.orchem.fingerprint;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;


/**
 *
 * Creates a fingerprint that is (supposed to be) tweaked for Chembl-like
 * databases with drug compounds.
 *
 */
public class OrchemFingerprinter implements IFingerprinter {

    private BitSet fingerprint;

    public BitSet getFingerprint(IAtomContainer molecule) {
        fingerprint = new BitSet(512);
        //elementCounting(molecule, fingerprint);
        //atomPairs(molecule,fingerprint);
        //neighbours(molecule,fingerprint);
        rings(molecule,fingerprint);

        return fingerprint;
    }

    public int getSize() {
        return fingerprint.size();
    }


    /**
     * Sets fingerprint bits related to occurence of elements.<BR>
     * For example, set a bit to true if there are more than 20 carbon atoms in the molecule.
     * @param molecule
     * @param fingerprint
     */
    private void elementCounting(IAtomContainer molecule, BitSet fingerprint) {

        String elemSymbol = null;
        Integer elemSymbCount = 0;
        Map<String, Integer> elemCounts = new HashMap<String, Integer>();

        /* Step 1: build map with counts per element. Map: key=>symbol, value=>total count */
        Iterator it = molecule.atoms().iterator();
        while (it.hasNext()) {
            elemSymbol = ((IAtom)it.next()).getSymbol();
            elemSymbCount = elemCounts.get(elemSymbol);
            if (elemSymbCount != null)
                elemCounts.put(elemSymbol, ++elemSymbCount);
            else
                elemCounts.put(elemSymbol, 1);
        }

        /* Step 2: finger print based on element count */
        it = elemCounts.keySet().iterator();
        while (it.hasNext()) {
            elemSymbol = (String)it.next();
            int counted = elemCounts.get(elemSymbol);

            /* Behold, lots of hard coded stuff. Perhaps one day I'll think of a pretty design pattern instead */
            if (elemSymbol.equals("C")) {
                if (counted >= 20)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("C20"), true);
                if (counted >= 24)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("C24"), true);
                if (counted >= 28)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("C28"), true);
                if (counted >= 32)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("C32"), true);
            } else if (elemSymbol.equals("Cl")) {
                if (counted >= 1)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("Cl1"), true);
                if (counted >= 2)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("Cl2"), true);
            } else if (elemSymbol.equals("F")) {
                if (counted >= 1)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("F1"), true);
                if (counted >= 3)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("F3"), true);
            } else if (elemSymbol.equals("N")) {
                if (counted >= 3)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("N3"), true);
                if (counted >= 4)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("N4"), true);
                if (counted >= 5)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("N5"), true);
                if (counted >= 6)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("N6"), true);
            } else if (elemSymbol.equals("O")) {
                if (counted >= 3)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("O3"), true);
                if (counted >= 5)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("O5"), true);
                if (counted >= 7)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("O7"), true);
            } else if (elemSymbol.equals("S")) {
                if (counted >= 1)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("S1"), true);
                if (counted >= 2)
                    fingerprint.set(OrchemFpBits.elemCntBits.get("S2"), true);
            } else if (elemSymbol.equals("B"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("B1"), true);
            else if (elemSymbol.equals("Cu"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("Cu1"), true);
            else if (elemSymbol.equals("Fe"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("Fe1"), true);
            else if (elemSymbol.equals("I"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("I1"), true);
            else if (elemSymbol.equals("K"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("K1"), true);
            else if (elemSymbol.equals("Li"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("Li1"), true);
            else if (elemSymbol.equals("Na"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("Na1"), true);
            else if (elemSymbol.equals("P"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("P1"), true);
            else if (elemSymbol.equals("Pt"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("Pt1"), true);
            else if (elemSymbol.equals("Se"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("Se1"), true);
            else if (elemSymbol.equals("Si"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("Si1"), true);
            else if (elemSymbol.equals("Tc"))
                fingerprint.set(OrchemFpBits.elemCntBits.get("Tc1"), true);
        }
    }


    /**
     * Sets fingerprint bits for atom pairs of interest<BR>
     * For example, set a bit to true if there is a O-O atom pair.
     * @param molecule
     * @param fingerprint
     */
    private void atomPairs(IAtomContainer molecule, BitSet fingerprint) {
        /* Loop over all bonds in the molecule */
        Iterator<IBond> bondIterator = molecule.bonds().iterator();
        while (bondIterator.hasNext()) {
            IBond bond = bondIterator.next();
            String symbol1 = bond.getAtom(0).getSymbol();
            String symbol2 = bond.getAtom(1).getSymbol();

            /* Test if the atoms in the bond occur as an atom pair we want to fingerprint.
             * Try both ways.. */
            if (OrchemFpBits.atomPairBits.containsKey(symbol1 + "-" + symbol2))
                fingerprint.set(OrchemFpBits.atomPairBits.get(symbol1 + "-" + symbol2), true);
            else if (OrchemFpBits.atomPairBits.containsKey(symbol2 + "-" + symbol1))
                fingerprint.set(OrchemFpBits.atomPairBits.get(symbol2 + "-" + symbol1), true);
        }
    }


    /**
     * Sets fingerprint bits for patterns of neigbouring atoms.<BR>
     * Uses {@link Neighbour } beans to store patterns of neighbouring atoms.<BR>
     * Bond order and aromaticity can be taken into account or ignored, this
     * is determined by the pattern.
     * 
     * @param molecule
     * @param fingerprint
     */
    private void neighbours(IAtomContainer molecule, BitSet fingerprint) {

        /* Loop over all atoms and see if it neighbours match a neighbourhood pattern that gets a bit set */
        Iterator<IAtom> atomIterator = molecule.atoms().iterator();
        while (atomIterator.hasNext()) {

            IAtom centralAtom = atomIterator.next();
            List<Neighbour> atomPlusNeighbours = new ArrayList<Neighbour>();

            /* Only the elements listed in the next match are used in neighbourhood patterns */
            if (centralAtom.getSymbol().matches("(C|N|O|P|S)")) {

                /* Put the central atom in as element zero in the list (not really a neighbour) */
                atomPlusNeighbours.add(new Neighbour(centralAtom.getSymbol(), null, false));
                Iterator<IBond> bondSubIterator = molecule.bonds().iterator();
                /* Put all neighbours in the list, plus the bond order and aromaticity  */
                while (bondSubIterator.hasNext()) {
                    IBond subBond = bondSubIterator.next();
                    if (subBond.getAtom(0).equals(centralAtom))
                        atomPlusNeighbours.add(new Neighbour(subBond.getAtom(1).getSymbol(), subBond.getOrder(),
                                                        subBond.getFlag(CDKConstants.ISAROMATIC)));
                    else if (subBond.getAtom(1).equals(centralAtom))
                        atomPlusNeighbours.add(new Neighbour(subBond.getAtom(0).getSymbol(), subBond.getOrder(),
                                                        subBond.getFlag(CDKConstants.ISAROMATIC)));
                }

                /* 
                   Now loop over all patterns, and see if the current atom plus neighbours matches any.
                   If so, then set the corresponding fingerprint bit number (given by the pattern) to true. 
                */
                Iterator<Integer> patternItr = OrchemFpBits.neighbourBits.keySet().iterator();
                List<Neighbour> neighbourPatternList = null;
                while (patternItr.hasNext()) {
                    Integer bit = patternItr.next();
                    neighbourPatternList = OrchemFpBits.neighbourBits.get(bit);
                    if (neighbourPatternList.size() <= atomPlusNeighbours.size() &&
                        neighbourPatternList.get(0).getSymbol().equals(atomPlusNeighbours.get(0).getSymbol())) {
                        List<Integer> mappedAtomIndexes = new ArrayList<Integer>();

                        /* boolean to indicate that all atoms in the pattern have been mapped (meaning: 100% match -> set bit) */
                        boolean allMapped = false;
                        
                        /* nested loop over pattern atoms and then the current neighbour atoms to try and make a match */
                        for (int patIdx = 1; patIdx < neighbourPatternList.size(); patIdx++) {
                            boolean patternAtomMapped = false;
                            Neighbour neighbPatt = neighbourPatternList.get(patIdx);

                            for (int neighIdx = 1; neighIdx < atomPlusNeighbours.size(); neighIdx++) {
                                if (!mappedAtomIndexes.contains(neighIdx)) {
                                    Neighbour neighb = atomPlusNeighbours.get(neighIdx);
                                    if (neighbPatt.getSymbol().equals(neighb.getSymbol()) &&
                                        (neighbPatt.getBondOrder() == null || neighbPatt.getBondOrder() == neighb.getBondOrder()) &&
                                        (neighbPatt.getAromatic() == null || neighbPatt.getAromatic() == neighb.getAromatic())) {
                                        patternAtomMapped = true;
                                        mappedAtomIndexes.add(neighIdx);
                                        if (patIdx == neighbourPatternList.size() - 1) {
                                            allMapped = true;
                                        }
                                        break;
                                    }
                                }
                            }
                            if (!patternAtomMapped)
                                break;
                            if (allMapped) {
                               fingerprint.set(bit,true);
                            }
                        }
                    }
                }
            }
        }
    }




    private void rings(IAtomContainer container, BitSet fingerprint) {

        double weight =
            MolecularFormulaManipulator.getTotalNaturalAbundance(MolecularFormulaManipulator.getMolecularFormula(container));
        for (int i = 1; i < 11; i++) {
            //if (weight > (100 * i))
            //bitSet.set(size-26+i); // 26 := RESERVED_BITS+1
        }
        IRingSet ringSet = new SSSRFinder(container).findSSSR();
        List<IRingSet> rslist = RingPartitioner.partitionRings(ringSet);

        /*
            Bit Position  Bit Substructure
            115 >= 1 any ring size 3
            116 >= 1 saturated or aromatic carbon-only ring size 3
            117 >= 1 saturated or aromatic nitrogen-containing ring size 3
            118 >= 1 saturated or aromatic heteroatom-containing ring size 3
            119 >= 1 unsaturated non-aromatic carbon-only ring size 3
            120 >= 1 unsaturated non-aromatic nitrogen-containing ring size 3
            121 >= 1 unsaturated non-aromatic heteroatom-containing ring size 3
            122 >= 2 any ring size 3
            123 >= 2 saturated or aromatic carbon-only ring size 3
            124 >= 2 saturated or aromatic nitrogen-containing ring size 3
            125 >= 2 saturated or aromatic heteroatom-containing ring size 3
            126 >= 2 unsaturated non-aromatic carbon-only ring size 3
            127 >= 2 unsaturated non-aromatic nitrogen-containing ring size 3
            128 >= 2 unsaturated non-aromatic heteroatom-containing ring size 3
                    */

        Iterator<IAtomContainer> iatcItr = ringSet.atomContainers().iterator();
        while (iatcItr.hasNext()) {
            IAtomContainer ring = iatcItr.next();
            System.out.println("Ring of size " + ring.getAtomCount());
        }

        for (int i = 0; i < 7; i++) {
            //if (ringSet.getAtomContainerCount() > i)
            //bitSet.set(size-15+i); // 15 := RESERVED_BITS+1+10 mass bits
        }
        int maximumringsystemsize = 0;
        for (int i = 0; i < rslist.size(); i++) {
            if (((IRingSet)rslist.get(i)).getAtomContainerCount() > maximumringsystemsize)
                maximumringsystemsize = ((IRingSet)rslist.get(i)).getAtomContainerCount();
        }
        for (int i = 0; i < maximumringsystemsize && i < 9; i++) {
            //bitSet.set(size-8+i-3);
        }

    }


    /*
    private void neighbours(IAtomContainer molecule, BitSet fingerprint) {
        Map<String, String> results = new HashMap<String, String>();
        for (int atomPos = 0; atomPos < molecule.getAtomCount(); atomPos++) {
            List<SubGraphElement> currSubGraph = new ArrayList<SubGraphElement>();
            currSubGraph.add(new SubGraphElement(molecule.getAtom(atomPos), IBond.Order.SINGLE, false));
            traverse(currSubGraph, molecule, results);
        }
        Iterator iterator = results.keySet().iterator();
        while (iterator.hasNext()) {
            String pattern = (String)iterator.next();
            if (OrchemFpBits.atomPairBits.containsKey(pattern))
                fingerprint.set(OrchemFpBits.atomPairBits.get(pattern), true);
        }
    }


    private void traverse(List<SubGraphElement> currSubGraph, IAtomContainer molecule, Map<String, String> results) {
        IAtom lastAtomInList = currSubGraph.get(currSubGraph.size() - 1).at;
        for (Iterator bonds = molecule.bonds().iterator(); bonds.hasNext(); ) {
            Bond bond = (Bond)bonds.next();
            Iterator atoms = bond.atoms().iterator();
            IAtom at1 = (IAtom)atoms.next();
            IAtom at2 = (IAtom)atoms.next();
            IAtom nextCandidate = null;

            if (at1.equals(lastAtomInList) && !currSubGraph.contains(at2)) {
                nextCandidate = at2;
            } else if (at2.equals(lastAtomInList) && !currSubGraph.contains(at1)) {
                nextCandidate = at1;
            }

            if (nextCandidate != null) {
                currSubGraph.add(new SubGraphElement(nextCandidate, bond.getOrder(), false));

                StringBuilder strb = new StringBuilder();
                StringBuilder strb2 = new StringBuilder();

                if (currSubGraph.size() >= 2) {
                    //"-", "=", and "#"
                    for (SubGraphElement s : currSubGraph) {
                        strb.append("_" + s.at.getSymbol());
                        //TODO aromatic and double/triple bonds !
                        if (s.bondOrder == IBond.Order.SINGLE)
                            strb2.append("-" + s.at.getSymbol());
                        else if (s.bondOrder == IBond.Order.DOUBLE)
                            strb2.append("=" + s.at.getSymbol());
                        else if (s.bondOrder == IBond.Order.TRIPLE)
                            strb2.append("#" + s.at.getSymbol());

                    }
                    String subgraph = strb.toString();
                    subgraph = subgraph.substring(1, subgraph.length());
                    results.put(subgraph, subgraph);

                    subgraph = strb2.toString();
                    subgraph = subgraph.substring(1, subgraph.length());
                    results.put(subgraph, subgraph);
                }

                if (currSubGraph.size() != 5) // TODO MAX depth -> doc and make constant
                    traverse(currSubGraph, molecule, results);

                currSubGraph.remove(currSubGraph.size() - 1);
            }
        }
    }


    public static void main(String[] args) {
        String test = "Cl";
        System.out.println(test.matches("(C|Cl|P)"));


    }

*/

}

