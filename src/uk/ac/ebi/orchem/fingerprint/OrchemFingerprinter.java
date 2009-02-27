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

package uk.ac.ebi.orchem.fingerprint;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.ringsearch.SSSRFinder;

import uk.ac.ebi.orchem.fingerprint.bitpos.BitPosApi;
import uk.ac.ebi.orchem.fingerprint.bitpos.Neighbour;


/**
 * Creates a fingerprint for OrChem, tuned for substructure and
 * similarity searching in (drug) compounds databases
 * 
 * @author markr@ebi.ac.uk
 *
 */
public class OrchemFingerprinter implements IFingerprinter {

    public final static int FINGERPRINT_SIZE=576;

    public int getSize() {
        return FINGERPRINT_SIZE;
    }

    public BitSet getFingerprint(IAtomContainer molecule) {

        BitSet fingerprint;
        fingerprint = new BitSet(FINGERPRINT_SIZE); //TODO

        longCarbonTrails(molecule,fingerprint);

        elementCounting(molecule,fingerprint);
        atomPairs(molecule,fingerprint);

        neighbours(molecule,fingerprint);

        IRingSet ringSet = new SSSRFinder(molecule).findSSSR();
        rings(ringSet,fingerprint);

        List<IRingSet> rslist = RingPartitioner.partitionRings(ringSet);
        ringSets(rslist,fingerprint);

        smartsPatterns(molecule,fingerprint);
        return fingerprint;
    }


    /**
     * Sets fingerprint bits related to occurence of elements.<BR>
     * For example, set a bit to true if there are more than 20 carbon atoms in the molecule.
     * What bit to set when is determined by input data from {@link uk.ac.ebi.orchem.fingerprint.bitpos.BitPositions}
     * @param molecule
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
                Integer bitPos = BitPosApi.bp.elemCntBits.get(mapKey);
                if (bitPos != null) {
                    fingerprint.set(bitPos, true);
                }
            }
        }
    }


    /**
     * Sets fingerprint bits for atom pairs of interest<BR>
     * For example, set a bit to true if there is a O-O atom pair.
     * @param molecule
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
            if (BitPosApi.bp.atomPairBits.containsKey(symbol1 + "-" + symbol2))
                fingerprint.set(BitPosApi.bp.atomPairBits.get(symbol1 + "-" + symbol2), true);
            else if (BitPosApi.bp.atomPairBits.containsKey(symbol2 + "-" + symbol1))
                fingerprint.set(BitPosApi.bp.atomPairBits.get(symbol2 + "-" + symbol1), true);
        }
    }


    /**
     * Sets fingerprint bits for patterns of neigbouring atoms.<BR>
     * Uses {@link Neighbour } beans to store patterns of neighbouring atoms.<BR>
     * Bond order and aromaticity can be taken into account or ignored, this
     * is determined by the pattern.
     *
     * @param molecule
     */
    private void neighbours(IAtomContainer molecule,BitSet fingerprint) {

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
                        atomPlusNeighbours.add(new Neighbour(subBond.getAtom(1).getSymbol(), subBond.getOrder(), subBond.getFlag(CDKConstants.ISAROMATIC)));
                    else if (subBond.getAtom(1).equals(centralAtom))
                        atomPlusNeighbours.add(new Neighbour(subBond.getAtom(0).getSymbol(), subBond.getOrder(), subBond.getFlag(CDKConstants.ISAROMATIC)));
                }

                /*
                   Now loop over all patterns, and see if the current atom plus neighbours matches any.
                   If so, then set the corresponding fingerprint bit number (given by the pattern) to true.
                */
                Iterator<Integer> patternItr = BitPosApi.bp.neighbourBits.keySet().iterator();
                List<Neighbour> neighbourPatternList = null;
                while (patternItr.hasNext()) {
                    Integer bit = patternItr.next();
                    neighbourPatternList = BitPosApi.bp.neighbourBits.get(bit);
                    if (neighbourPatternList.size() <= atomPlusNeighbours.size() && neighbourPatternList.get(0).getSymbol().equals(atomPlusNeighbours.get(0).getSymbol())) {
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
                                fingerprint.set(bit, true);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets fingerprint bits related to single rings: size, aromaticity, atoms in the rings
     * @param ringSet
     */
    private void rings(IRingSet ringSet,BitSet fingerprint) {
        Iterator<IAtomContainer> ringIterator = ringSet.atomContainers().iterator();
        Map<String, Integer> ringProps = new HashMap<String, Integer>();

        /* Loop over all the rings and build up a map with ring aspects */
        while (ringIterator.hasNext()) {

            IAtomContainer ring = ringIterator.next();

            int ringSize = ring.getAtomCount();
            boolean hasNitrogen = false;
            boolean hasHeteroAtom = false;
            boolean hasOnlyCarbon = true;

            Iterator<IAtom> itr = ring.atoms().iterator();
            while (itr.hasNext()) {
                IAtom atom = itr.next();
                if (atom.getSymbol().equals("N")) {
                    hasNitrogen = true;
                } else if (!atom.getSymbol().equals("C")) {
                    hasHeteroAtom = true;
                }
                if (hasNitrogen && hasHeteroAtom)
                    break;
            }

            if (hasNitrogen || hasHeteroAtom)
                hasOnlyCarbon = false;

            String aromStr = BitPosApi.bp.ringPrefixNonArom;
            Iterator<IBond> bondItr = ring.bonds().iterator();
            while (bondItr.hasNext()) {
                IBond bond = bondItr.next();
                if (bond.getFlag(CDKConstants.ISAROMATIC)) {
                    aromStr = BitPosApi.bp.ringPrefixArom;
                    break;
                }
            }

            addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + BitPosApi.bp.ringPrefixAny);
            addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + aromStr);
            if (hasNitrogen)
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + aromStr + BitPosApi.bp.ringPrefixNitro);
            if (hasHeteroAtom)
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + aromStr + BitPosApi.bp.ringPrefixHetero);
            if (hasOnlyCarbon)
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + aromStr + BitPosApi.bp.ringPrefixCarbonOnly);

        }

        Iterator<String> itr = ringProps.keySet().iterator();
        while (itr.hasNext()) {
            String ringProperties = itr.next();
            int countPropsOccurence = ringProps.get(ringProperties);

            for (int i = 1; i <= countPropsOccurence; i++) {
                if (BitPosApi.bp.ringBits.containsKey(ringProperties + i)) {
                    int bitPos = BitPosApi.bp.ringBits.get(ringProperties + i);
                    fingerprint.set(bitPos, true);
                }
            }
        }
    }

    /**
     * Helper method for {@link #rings(IRingSet,BitSet)}
     * @param m Map
     * @param s Key for Map
     */
    private void addRingProps(Map<String, Integer> m, String s) {
        if (m.containsKey(s)) {
            int cnt = m.get(s) + 1;
            m.put(s, cnt);
        } else
            m.put(s, 1);
    }


    /**
     * Set fingerprint bits related to ring set properties: size, members and connectivity.
     * Only rings sets with two or more atomcontainers ("multi") are considered (the single
     * ring sets are taken care of by the rings() method.
     *
     * @param ringsetList
     */
    private void ringSets(List<IRingSet> ringsetList,BitSet fingerprint) {

        /* Map tracking how many of what size multi ring sets we find */
        Map<Integer, Integer> ringsetCountPerSet = new HashMap<Integer, Integer>();

        int ringSetCount = 0;
        int maxConnectedCount = 0;
        int maxHexRingInSetCount = 0;
        int maxPentRingInSetCount = 0;

        /* Loop over all the ringsets*/
        for (IRingSet irs : ringsetList) {
            int numberOfRingsInSet = irs.getAtomContainerCount();
            if (numberOfRingsInSet > 1) {
                /* Increase overall counter, keeping track how many multi ring set are found */
                ringSetCount++;

                /* Track number of rings per set */
                Integer cnt = ringsetCountPerSet.get(numberOfRingsInSet);
                if (cnt != null) {
                    ringsetCountPerSet.put(numberOfRingsInSet, ++cnt);
                } else {
                    ringsetCountPerSet.put(numberOfRingsInSet, 1);
                }

                int hexRingInSetCount = 0;
                int pentRingInSetCount = 0;
                /* Loop over each ring in the ringset */
                Iterator<IAtomContainer> ringsetMembers = irs.atomContainers().iterator();
                while (ringsetMembers.hasNext()) {

                    IRing ring = (IRing)ringsetMembers.next();
                    /* Track number of hex and pent rings per set */
                    if (ring.getAtomCount() == 6)
                        hexRingInSetCount++;
                    if (ring.getAtomCount() == 5)
                        pentRingInSetCount++;

                    int cntNeighb = 0;
                    /* Loop over the ringset's neighbours (connected) */
                    IRingSet connectedRings = irs.getConnectedRings(ring);
                    Iterator<IAtomContainer> connRingMembers = connectedRings.atomContainers().iterator();
                    while (connRingMembers.hasNext()) {
                        cntNeighb++;
                        IRing neighbourRing = (IRing)connRingMembers.next();
                        int ringSize1 = ring.getAtomCount();
                        int ringSize2 = neighbourRing.getAtomCount();
                        if (ringSize1 <= ringSize2) {

                            /* Set bits for ring pairs glued together.
                             * Example:
                             *   a five ring and a six ring are glued (=share atoms/bonds).
                             *   The bit for ring pair 5_6 is set accordingly.
                             * */
                            Integer bitPos = BitPosApi.bp.ringSetBits.get(BitPosApi.bp.ringsetPairPrefix + ringSize1 + "_" + ringSize2);
                            if (bitPos != null) {
                                fingerprint.set(bitPos, true);
                            }
                        }
                    }
                    if (cntNeighb > maxConnectedCount)
                        maxConnectedCount = cntNeighb;
                    if (hexRingInSetCount > maxHexRingInSetCount)
                        maxHexRingInSetCount = hexRingInSetCount;
                    if (pentRingInSetCount > maxPentRingInSetCount)
                        maxPentRingInSetCount = pentRingInSetCount;
                }

                /* Set bit overall ringset count */
                flipRingSetBits(1, ringSetCount, BitPosApi.bp.ringsetCountTotalPrefix,fingerprint);

                /* Set rings related to cluttering - maximum amount of connected rings for any ring in the set */
                flipRingSetBits(2, maxConnectedCount, BitPosApi.bp.ringsetCountConnectedPrefix,fingerprint);

                /* Set rings related to occurence of hex and pent rings */
                flipRingSetBits(3, maxHexRingInSetCount, BitPosApi.bp.ringsetCountHexPrefix,fingerprint);
                flipRingSetBits(2, maxPentRingInSetCount, BitPosApi.bp.ringsetCountPentPrefix,fingerprint);

                /* Set bits that indicate the number of rings in the multiple ring sets */
                Set<Integer> keys = ringsetCountPerSet.keySet();
                Iterator<Integer> setCntItr = keys.iterator();
                while (setCntItr.hasNext()) {
                    Integer ringSetSize = setCntItr.next();
                    flipRingSetBits(2, ringSetSize, BitPosApi.bp.ringsetCountPerSet,fingerprint);
                }
            }
        }

    }

    /**
     * Helper method for {@link #ringSets(List,BitSet) }
     * @param start
     * @param maxCount
     * @param prefix
     */
    private void flipRingSetBits(int start, int maxCount, String prefix,BitSet fingerprint) {
        for (int cnt = start; cnt <= maxCount; cnt++) {
            String mapKey = prefix + cnt;
            if (BitPosApi.bp.ringSetBits.containsKey(mapKey)) {
                fingerprint.set(BitPosApi.bp.ringSetBits.get(mapKey), true);
            }
        }
    }

    /**
     * TODO
     * @param molecule
     */
    private void smartsPatterns(IAtomContainer molecule,BitSet fingerprint) {
        /* Build up result list */
        Map<String, String> results = new HashMap<String, String>();
        for (int atomPos = 0; atomPos < molecule.getAtomCount(); atomPos++) {
            List<IAtom> atoms = new ArrayList<IAtom>();
            IAtom at = molecule.getAtom(atomPos);
            atoms.add(at);
            traverse(atoms, at.getSymbol(), molecule, results);
        }

        /* Loop over result list and set bits where result matches a smarts pattern */
        Iterator iterator = results.keySet().iterator();
        while (iterator.hasNext()) {
            String pattern = (String)iterator.next();
            if (BitPosApi.bp.smartsPatternBits.containsKey(pattern)) {
                fingerprint.set(BitPosApi.bp.smartsPatternBits.get(pattern), true);
            }
        }

    }

    /**
     * .................
     * @param atomList
     * @param smarts
     * @param molecule
     * @param results
     */
    private void traverse(List<IAtom> atomList, String smarts, IAtomContainer molecule, Map<String, String> results) {

        IAtom lastAtomInList = atomList.get(atomList.size() - 1);

        for (Iterator<IBond> bonds = molecule.bonds().iterator(); bonds.hasNext(); ) {
            IBond bond = bonds.next();
            Iterator atoms = bond.atoms().iterator();
            IAtom at1 = (IAtom)atoms.next();
            IAtom at2 = (IAtom)atoms.next();

            IAtom nextAtom = null;
            if (at1.equals(lastAtomInList) && !atomList.contains(at2)) {
                nextAtom = at2;
            } else if (at2.equals(lastAtomInList) && !atomList.contains(at1)) {
                nextAtom = at1;
            }

            if (nextAtom != null) {
                String nextSmart = null;
                atomList.add(nextAtom);
                if (bond.getFlag(CDKConstants.ISAROMATIC))
                    nextSmart = smarts + ":" + nextAtom.getSymbol();
                else if (bond.getOrder() == IBond.Order.SINGLE) {
                    nextSmart = smarts + "-" + nextAtom.getSymbol();
                } else if (bond.getOrder() == IBond.Order.DOUBLE) {
                    nextSmart = smarts + "=" + nextAtom.getSymbol();
                } else if (bond.getOrder() == IBond.Order.TRIPLE) {
                    nextSmart = smarts + "#" + nextAtom.getSymbol();
                }

                if (atomList.size() >= 4) {
                    results.put(nextSmart, nextSmart);
                }

                if (atomList.size() != 5) // TODO MAX depth -> doc and make constant
                    traverse(atomList, nextSmart, molecule, results);

                atomList.remove(atomList.size() - 1);
            }
        }
    }


    //TODO
    // set up for different lengths (cascade down)
    // set for with or without double boings

    private void longCarbonTrails (IAtomContainer molecule,BitSet fingerprint) {

        List<IBond> ccBonds = new ArrayList<IBond>();
        for (Iterator<IBond> iterator = molecule.bonds().iterator(); iterator.hasNext(); ) {
            IBond b = iterator.next();
            if (b.getAtom(0).getSymbol().equals("C") && b.getAtom(1).getSymbol().equals("C") && !b.getFlag(CDKConstants.ISAROMATIC))  {
                ccBonds.add(b);
            }
        }
        int series=0;
        for (int i = 0; i < ccBonds.size(); i++)  {
            List<Integer> taken= new ArrayList<Integer>();
            taken.add(i);
            int len =carbonSeriesCounter (ccBonds,taken, 2);
            if (len>series)  {
                    series=len;
            }
        }
        if (series>17)  {
            System.out.println("..........  longest ->" +series);
        }
        
    }

    private int carbonSeriesCounter (List<IBond> ccBonds, List<Integer> bondsTakenIdxList, int length) {
        int lastBond = bondsTakenIdxList.get(bondsTakenIdxList.size()-1);
        IAtom at0 = ccBonds.get(lastBond).getAtom(0);
        IAtom at1 = ccBonds.get(lastBond).getAtom(0);
        int ret=length;

        for (Integer i = 0; i < ccBonds.size(); i++) {
            IBond b = ccBonds.get(i);
            if (!bondsTakenIdxList.contains(i) && (b.getAtom(0).equals(at0) || b.getAtom(0).equals(at1)|| b.getAtom(1).equals(at0) ||b.getAtom(1).equals(at1)))  {
                bondsTakenIdxList.add(i);
                int newLen= carbonSeriesCounter(ccBonds, bondsTakenIdxList,length+1);

                //System.out.println(newLen);
                //System.out.println(bondsTakenIdxList);

                ret = newLen > ret ? newLen : ret;
                bondsTakenIdxList.remove(bondsTakenIdxList.size()-1);
            }
        }        
        return ret;
    }


    public static void main(String[] args) {
        BitSet bs = new BitSet(575);
        bs.set(560,true);
        for (int i = 0; i < bs.size(); i++) {
            System.out.println(i+" "+bs.get(i));
        }

    }

}

