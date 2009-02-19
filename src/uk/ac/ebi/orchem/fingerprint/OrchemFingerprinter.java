package uk.ac.ebi.orchem.fingerprint;

import java.io.IOException;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Set;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;
import org.openscience.cdk.tools.CDKValencyChecker;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;


/**
 *
 * Creates a fingerprint that is (supposed to be) tweaked for Chembl-like
 * databases with drug compounds.
 *
 * TODO!! I think we should throw in the rarer elements. These are often part of wonky mols
 * 322326, 154147 and such. Also give some more bits of course
 *
 */
public class OrchemFingerprinter implements IFingerprinter {

    private BitSet fingerprint;

    public BitSet getFingerprint(IAtomContainer molecule) {
        fingerprint = new BitSet(512); //...
        //elementCounting(molecule, fingerprint);
        //atomPairs(molecule,fingerprint);
        //neighbours(molecule,fingerprint);
        rings(molecule, fingerprint);

        return fingerprint;
    }

    public int getSize() {
        return fingerprint.size();
    }


    /**
     * Sets fingerprint bits related to occurence of elements.<BR>
     * For example, set a bit to true if there are more than 20 carbon atoms in the molecule.
     * What bit to set when is determined by input data from {@link OrchemFpBits}
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

            for (int cnt = 1; cnt <= counted; cnt++) {
                String mapKey = elemSymbol + cnt;
                //System.out.println(mapKey);
                Integer bitPos = OrchemFpBits.elemCntBits.get(mapKey);
                if (bitPos != null) {
                    fingerprint.set(bitPos, true);
                    //System.out.println(">>>>>>>"+bitPos);
                }
            }
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
                                        (neighbPatt.getBondOrder() == null ||
                                         neighbPatt.getBondOrder() == neighb.getBondOrder()) &&
                                        (neighbPatt.getAromatic() == null ||
                                         neighbPatt.getAromatic() == neighb.getAromatic())) {
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
                                fingerprint.set(bit, true);
                            }
                        }
                    }
                }
            }
        }
    }


    private void rings(IAtomContainer container, BitSet fingerprint) {

        IRingSet ringSet = new SSSRFinder(container).findSSSR();
        List<IRingSet> rslist = RingPartitioner.partitionRings(ringSet);


        /*
            Bit Position  Bit Substructure
            >= 1 any ring size 3

            >= 1 carbon-only ring size 3
            >= 1 nitrogen-containing ring size 3
            >= 1 heteroatom-containing ring size 3

            >= 1  carbon-only ring size 3 > with non ar double bond?
            >= 1  nitrogen-containing ring size 3 > with non ar double bond?
            >= 1  heteroatom-containing ring size 3 > with non ar double bond?

            ............

            ..

            =>1 ringset with multiple rings
            =>2 ringset with multiple rings
            =>3 ringset with multiple rings
            =>4 ringset with multiple rings
            =>5 ringset with multiple rings


           ....

            =>1 ringset pair 3,3 .. does that ever occur?
            =>1 ringset pair 3,4
            =>1 ringset pair 3,5
            =>1 ringset pair 3,6 starl 36763
            =>1 ringset pair 3,8

            =>1 ringset pair 4,4
            =>1 ringset pair 4,5
            =>1 ringset pair 4,6
            =>1 ringset pair 4,8

            =>1 ringset pair 5,5
            =>1 ringset pair 5,6
            =>1 ringset pair 5,8

            =>2 ringset pair 5,6 starlite 375179
            =>2 ringset pair 5,8 starlite 861

            =>1 ringset pair 6,6 .. or too common???
            =>1 ringset pair 6,8 ..  starlite 158895

            =>3 ringset hexring count
            =>4 ringset hexring count
            =>5 ringset hexring count

            =>2 ringset pentring count
            =>3 ringset pentring count

        */

        /* Counter for ringsets with multiple rings, not single ring (uninteresting) sets   */
        int multipleRingSetCount = 0;
        /* Map tracking how many of what size multi ring sets we find */
        Map<Integer, Integer> ringsetSizes = new HashMap<Integer, Integer>();

        for (IRingSet irs : rslist) {
            int numberOfRingsInSet = irs.getAtomContainerCount();
            if (numberOfRingsInSet > 1) {
                /* Increase overall counter */
                multipleRingSetCount++;
                /* Update map with current set sizes found */
                Integer cnt = ringsetSizes.get(numberOfRingsInSet);
                if (cnt != null) {
                    ringsetSizes.put(numberOfRingsInSet, ++cnt);

                } else {
                    ringsetSizes.put(numberOfRingsInSet, 1);
                }
            }

            Iterator<IAtomContainer> itr =  irs.atomContainers().iterator();
            while (itr.hasNext())  {
                IRing ring = (IRing)itr.next();
                IRingSet rs2 = irs.getConnectedRings(ring);
                Iterator<IAtomContainer> it2 = rs2.atomContainers().iterator();
                while (it2.hasNext())  {
                    IRing neighbourRing = (IRing)it2.next();
                    System.out.println(ring.getAtomCount()+"_"+neighbourRing.getAtomCount());
                    //TODO find useful values here, run on a large range of things
                }
                

            }

        }

        /* Set bits for compounds with one or more ringsets with multiple rings */
        for (int cnt = 1; cnt <= multipleRingSetCount; cnt++) {
            String mapKey = OrchemFpBits.ringsetCountTotalPrefix + "" + cnt;
            if (OrchemFpBits.ringSetBits.containsKey(mapKey)) {
                fingerprint.set(OrchemFpBits.ringSetBits.get(mapKey), true);
            }
        }

        /* Set bits that indicate the number of rings in the multiple ring sets */
        Set<Integer> keys = ringsetSizes.keySet();
        Iterator<Integer> i = keys.iterator();
        while (i.hasNext()) {
            Integer numberOfRingsInSet = i.next();
            Integer counted = ringsetSizes.get(numberOfRingsInSet);

            for (int cnt = 1; cnt <= numberOfRingsInSet; cnt++) {
                String mapKey = OrchemFpBits.ringsetCountBySizePrefix + cnt;
                Integer bitPos = OrchemFpBits.ringSetBits.get(mapKey);
                if (bitPos != null) {
                    fingerprint.set(bitPos, true);
                }
            }
        }


    }

    private void setRingFingerprints(IAtomContainer ring) {
        Iterator<IAtom> itr = ring.atoms().iterator();
        while (itr.hasNext()) {
            IAtom atom = itr.next();
            if (atom.getSymbol().equals("N")) {
                System.out.println("Nitrogen in ring");
            } else if (!atom.getSymbol().equals("C")) {
                System.out.println("Hetero atom in ring");
            }
            //TODO break out (optimize)
        }
        //TODO -> conclude carbon only

        Iterator<IBond> bondItr = ring.bonds().iterator();
        while (bondItr.hasNext()) {
            IBond bond = bondItr.next();
            if (bond.getFlag(CDKConstants.ISAROMATIC)) {
                System.out.println("Aromatic RING");
                //TODO break out
            }
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

