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

package uk.ac.ebi.orchem.fingerprint;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.StringTokenizer;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.fingerprint.IFingerprinter;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRing;
import org.openscience.cdk.interfaces.IRingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
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


    public final static int FINGERPRINT_SIZE=BitPosApi.bp.getFingerprintSize();
    private int hashModulo = BitPosApi.bp.HASH_OFFSET;
    IRingSet ringSet;
    List<IRingSet> rslist;

    public int getSize() {
        return FINGERPRINT_SIZE;
    }


    public BitSet getFingerprint(IAtomContainer molecule) throws CDKException {
        BitSet fingerprint;
        fingerprint = new BitSet(FINGERPRINT_SIZE); 
        /* Set a dummy default bit. This prevents compounds with 0 bits set, which in turn makes the similarity search unhappy*/
        fingerprint.set(0);

        carbonTrails(molecule,fingerprint);

        elementCounting(molecule,fingerprint);
        atomPairs(molecule,fingerprint);

        neighbours(molecule,fingerprint);

        //long start = System.currentTimeMillis();
        try {
            AllRingsFinder ringFinder =  new AllRingsFinder();
            ringFinder.setTimeout(2500);
            // fingerprinter prints up to 6; from 7 onwards bucky balls and such become quite expensive!
            ringSet = ringFinder.findAllRingsInIsolatedRingSystem(molecule,6); 
            //System.out.println("ring detection took ms : "+(System.currentTimeMillis()-start));
        } catch (Exception e) {
            long startExc = System.currentTimeMillis();
            ringSet = new SSSRFinder(molecule).findRelevantRings();
            System.out.println("warning timeout ring detection, exception handled in "+(System.currentTimeMillis()-startExc));
        }

        rings(ringSet,fingerprint);
        rslist = RingPartitioner.partitionRings(ringSet);
        ringSets(rslist,fingerprint);
        ringSetLayout(rslist,fingerprint);
        
        smilesPatterns(molecule,fingerprint);
        return fingerprint;

    }


    /**
     * Sets fingerprint bits related to occurence of elements.<BR>
     * For example, set a bit to true if there are more than 20 carbon atoms in the molecule.
     * What bit to set when is determined by input data from {@link uk.ac.ebi.orchem.fingerprint.bitpos.BitPositions}
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

            // if the element is 'rare' then it will not be in the
            // map of counts for elements. Instead we set an 'other element' bit
            if (!BitPosApi.bp.elemFingerprinted.containsKey(elemSymbol) 
                && !elemSymbol.equals("R")
                && !elemSymbol.equals("H")
            )  
            {
                Integer bitPos = BitPosApi.bp.elemCntBits.get(BitPosApi.bp.otherElem);
                fingerprint.set(bitPos, true);
            }
            else {
                for (int cnt = 1; cnt <= counted; cnt++) {
                    String mapKey = elemSymbol + cnt;
                    Integer bitPos = BitPosApi.bp.elemCntBits.get(mapKey);
                    if (bitPos != null) {
                        fingerprint.set(bitPos, true);
                    }
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
     * @param fingerprint
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
                    if (subBond.getOrder()!=null)  // SMARTS query atom container case
                        if (subBond.getAtom(0).equals(centralAtom))
                            atomPlusNeighbours.add(
                               new Neighbour(subBond.getAtom(1).getSymbol(), subBond.getOrder(), subBond.getFlag(CDKConstants.ISAROMATIC)));
                        else if (subBond.getAtom(1).equals(centralAtom))
                            atomPlusNeighbours.add(
                               new Neighbour(subBond.getAtom(0).getSymbol(), subBond.getOrder(), subBond.getFlag(CDKConstants.ISAROMATIC)));
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

                                    if (neighbPatt.getSymbol().equals(neighb.getSymbol())) {
                                        if (neighbPatt.getBondOrder() == null || neighbPatt.getBondOrder() == neighb.getBondOrder()) {
                                            if (neighbPatt.getAromatic() == null || neighbPatt.getAromatic().equals(neighb.getAromatic())) {
                                                patternAtomMapped = true;
                                                mappedAtomIndexes.add(neighIdx);
                                                if (patIdx == neighbourPatternList.size() - 1) {
                                                    allMapped = true;
                                                }
                                                break;
                                            }
                                        }
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
     * @param fingerprint
     */
    private void rings(IRingSet ringSet,BitSet fingerprint) {
        Iterator<IAtomContainer> ringIterator = ringSet.atomContainers().iterator();
        Map<String, Integer> ringProps = new HashMap<String, Integer>();

        /* Loop over all the rings and build up a map with ring aspects */
        while (ringIterator.hasNext()) {

            IAtomContainer ring = ringIterator.next();
            int ringSize = ring.getAtomCount();

            /* Fingerprint uncommon ring sizes */
            if (ringSize!=5 && ringSize!=6)  {
                String mapKey = BitPosApi.bp.ringSize+ringSize;
                Integer bitPos = BitPosApi.bp.ringSizes.get(mapKey);
                if (bitPos != null) {
                  fingerprint.set(bitPos, true);
                }
            }

            boolean hasNitrogen = false;
            boolean hasHeteroAtom = false;
            boolean hasOnlyCarbon = true;
            Iterator<IAtom> itr = ring.atoms().iterator();
            while (itr.hasNext()) {
                IAtom atom = itr.next();
                
                if (atom.getSymbol().equals("N")) {
                    hasNitrogen = true;
                } else if (!atom.getSymbol().equals("C")&&
                           !atom.getSymbol().equals("R")) {
                    hasHeteroAtom = true;
                }
                if (hasNitrogen && hasHeteroAtom)
                    break;
            }
            int dc= countDoubleBondsInRing(ring);
            if (dc==1)  
                addRingProps(ringProps, BitPosApi.bp.ringNonaromDoubleBond+"1_");
            else if (dc==2)  
                addRingProps(ringProps, BitPosApi.bp.ringNonaromDoubleBond+"2_");

            if (hasNitrogen || hasHeteroAtom)
                hasOnlyCarbon = false;

            String aromStr = BitPosApi.bp.ringPrefixNonArom;
            Iterator<IBond> bondItr = ring.bonds().iterator();
            while (bondItr.hasNext()) {
                IBond bond = bondItr.next();
                if (bond.getOrder()!=null && bond.getFlag(CDKConstants.ISAROMATIC)) {
                    aromStr = BitPosApi.bp.ringPrefixArom;
                    break;
                }
                if (bond.getOrder()==null) {
                    aromStr="unreliable"; // can happen for QueryAtomContainter for SMARTS
                    break;
                }
            }

            addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + BitPosApi.bp.ringPrefixAny);
            addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + aromStr);
            if (hasNitrogen) {
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + aromStr + BitPosApi.bp.ringPrefixNitro);
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + BitPosApi.bp.ringPrefixNitro);
            }

            if (hasHeteroAtom) {
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + aromStr + BitPosApi.bp.ringPrefixHetero);
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + BitPosApi.bp.ringPrefixHetero);

            }
            if (hasOnlyCarbon) {
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + aromStr + BitPosApi.bp.ringPrefixCarbonOnly);
                addRingProps(ringProps, ringSize + BitPosApi.bp.ringPrefixRing + BitPosApi.bp.ringPrefixCarbonOnly);
            }

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
     * Helper method for {@link #rings(IRingSet ringSet,BitSet) }
     * @param ring
     * @return number of double bond counts (non aromatic)
     */
    private int countDoubleBondsInRing (IAtomContainer ring) {
        int dblBondCount=0;
        Iterator<IBond> bondItr = ring.bonds().iterator();
        while (bondItr.hasNext()) {
            IBond bond = bondItr.next();
            if (!bond.getFlag(CDKConstants.ISAROMATIC) && bond.getOrder()==IBond.Order.DOUBLE) {
                dblBondCount++;
            }
        }
        return dblBondCount;
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
     * @param fingerprint
     */
    private void ringSets(List<IRingSet> ringsetList,BitSet fingerprint) {

        /* Map tracking how many of what size multi ring sets we find */
        Map<Integer, Integer> ringsetCountPerSet = new HashMap<Integer, Integer>();

        int ringSetCount = 0;
        int maxConnectedCount = 0;
        int maxHexRingInSetCount = 0;
        int maxPentRingInSetCount = 0;
        int hexNonAromNeighbourCount=0;
        int hexAromNeighbourCount=0;
        int hexMixedAromNeighbourCount=0;

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
                    Boolean ringIsAromatic=isAromaticRing(ring);

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
                        if (ringSize1==6 && ringSize2==6 )  {
                            Boolean neighbourRingIsAromatic=isAromaticRing(neighbourRing);
                            if (neighbourRingIsAromatic!=null && ringIsAromatic!=null) {
                                if (ringIsAromatic && neighbourRingIsAromatic ) 
                                    hexMixedAromNeighbourCount++;
                                else if (!ringIsAromatic && !neighbourRingIsAromatic ) 
                                    hexNonAromNeighbourCount++;
                                else 
                                    hexAromNeighbourCount++;
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
                doRingSetBits(1, ringSetCount, BitPosApi.bp.ringsetCountTotalPrefix,fingerprint);

                /* Set rings related to cluttering - maximum amount of connected rings for any ring in the set */
                doRingSetBits(2, maxConnectedCount, BitPosApi.bp.ringsetCountConnectedPrefix,fingerprint);

                /* Set rings related to occurence of hex and pent rings */
                doRingSetBits(3, maxHexRingInSetCount, BitPosApi.bp.ringsetCountHexPrefix,fingerprint);
                doRingSetBits(2, maxPentRingInSetCount, BitPosApi.bp.ringsetCountPentPrefix,fingerprint);

                /* Set bits that indicate the number of rings in the multiple ring sets */
                Set<Integer> keys = ringsetCountPerSet.keySet();
                Iterator<Integer> setCntItr = keys.iterator();
                while (setCntItr.hasNext()) {
                    Integer ringSetSize = setCntItr.next();
                    doRingSetBits(2, ringSetSize, BitPosApi.bp.ringsetCountPerSet,fingerprint);
                }
            }
        }

        /* Set bits related to aromatic/non aromatic hex ring connections */
        doRingSetBits(1, hexNonAromNeighbourCount/2 ,  BitPosApi.bp.hexRingNonAromNeighPrefix,fingerprint);
        doRingSetBits(1, hexAromNeighbourCount/2 ,     BitPosApi.bp.hexRingAromNeighPrefix,fingerprint);
        doRingSetBits(1, hexMixedAromNeighbourCount/2, BitPosApi.bp.hexRingMixNeighPrefix,fingerprint);

    }

    /**
     * Helper method for {@link #ringSets(List,BitSet) }
     * @param ring
     * @return true if ring is aromatic, false if not aromatic and Null if
     * can't decide (SMARTS case)
     */
    private Boolean isAromaticRing (IAtomContainer ring) {

        // Only when all the bond orders have a value set, 
        // we'll dare say something about aromaticity
        for (IBond bond : ring.bonds() )  {
            if (bond.getOrder()==null)  {
              return null;
            }
        }

        for (IBond bond : ring.bonds() )  {
            if (bond.getFlag(CDKConstants.ISAROMATIC)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method for {@link #ringSets(List,BitSet) }
     * @param start
     * @param maxCount
     * @param prefix
     * @param fingerprint
     */
    private void doRingSetBits(int start, int maxCount, String prefix,BitSet fingerprint) {
        for (int cnt = start; cnt <= maxCount; cnt++) {
            String mapKey = prefix + cnt;
            if (BitPosApi.bp.ringSetBits.containsKey(mapKey)) {
                fingerprint.set(BitPosApi.bp.ringSetBits.get(mapKey), true);
            }
        }
    }
    

    /**
     * Set fingerprint bits related for certain SMILES patterns
     * @param molecule
     * @param fingerprint
     */
    private void smilesPatterns(IAtomContainer molecule,BitSet fingerprint) {
        /* Build up result list */
        Map<String, String> results = new HashMap<String, String>();
        Map<String,String> threeSomes = new HashMap<String,String>();

        for (int atomPos = 0; atomPos < molecule.getAtomCount(); atomPos++) {
            IAtom at = molecule.getAtom(atomPos);
            if (!at.getSymbol().equals("R")) {
                List<IAtom> atoms = new ArrayList<IAtom>();
                atoms.add(at);
                smilesTraversal(atoms, at.getSymbol(), molecule, results, fingerprint, threeSomes);
            }
        }

        /* Loop over result list and set bits where result matches a SMILES pattern */
        Iterator iterator = results.keySet().iterator();
        while (iterator.hasNext()) {
            String pattern = (String)iterator.next();
            if (BitPosApi.bp.smilesPatternBits.containsKey(pattern)) {
                fingerprint.set(BitPosApi.bp.smilesPatternBits.get(pattern), true);
            }
        }

    }

    /**
     * Recursive helper function for {@link OrchemFingerprinter#smilesPatterns(org.openscience.cdk.interfaces.IAtomContainer,java.util.BitSet)}to traverse
     * through the atom container graph, building up potentially interesting SMILES patterns.
     *
     * @param atomList
     * @param smiles
     * @param molecule
     * @param results
     */
    private void smilesTraversal(List<IAtom> atomList, String smiles, 
                                 IAtomContainer molecule, Map<String, 
                                 String> results, BitSet fingerprint, 
                                 Map<String,String> threeSomes) {

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

            if (nextAtom != null && nextAtom.getSymbol()!=null && !nextAtom.getSymbol().equals("R")) {
                String nextSMILES = null;
                if (bond.getOrder()==null) // can be for SMARTS queries
                    nextSMILES=null;
                else if (bond.getFlag(CDKConstants.ISAROMATIC))
                    nextSMILES = smiles + ":" + nextAtom.getSymbol();
                else if (bond.getOrder() == IBond.Order.SINGLE) {
                    nextSMILES = smiles + "-" + nextAtom.getSymbol();
                } else if (bond.getOrder() == IBond.Order.DOUBLE) {
                    nextSMILES = smiles + "=" + nextAtom.getSymbol();
                } else if (bond.getOrder() == IBond.Order.TRIPLE) {
                    nextSMILES = smiles + "#" + nextAtom.getSymbol();
                }

                if(nextSMILES!=null ) {
                    atomList.add(nextAtom);
                    if (atomList.size() >= 4) {
                        results.put(nextSMILES, nextSMILES);
                    }
    
                    if (atomList.size() == 3 ) {
                        hashThreeSome(nextSMILES,fingerprint, threeSomes);
                    }
    
                    if (atomList.size() != 6) // TODO MAX depth -> doc and make constant
                        smilesTraversal(atomList, nextSMILES, molecule, results, fingerprint, threeSomes);
                    atomList.remove(atomList.size() - 1);
                }
            }
        }
    }


    /**
     * Set fingerprint bits for longer series of non aromatic carbon molecules. These help to identify
     * compounds with long 'tails' such as molregno 140 in StarLite, although not uniquely.
     *
     * @param molecule
     * @param fingerprint
     */
    private void carbonTrails(IAtomContainer molecule, BitSet fingerprint) {

        List<IBond> ccBonds = new ArrayList<IBond>();
        for (Iterator<IBond> iterator = molecule.bonds().iterator(); iterator.hasNext(); ) {
            IBond b = iterator.next();
            if (b.getAtom(0).getSymbol().equals("C") && 
                b.getAtom(1).getSymbol().equals("C") &&
                b.getOrder()!=null &&
                !b.getFlag(CDKConstants.ISAROMATIC) && 
                b.getOrder() == IBond.Order.SINGLE) {
                ccBonds.add(b);
            }
        } 

        //create a map of all candidate C atoms
        Map<IAtom, IAtom> uniqueAtoms = new HashMap<IAtom, IAtom>();
        for (int idxBond = 0; idxBond < ccBonds.size(); idxBond++) {
            for (Iterator<IAtom> iterator = ccBonds.get(idxBond).atoms().iterator(); iterator.hasNext(); ) {
                IAtom at = iterator.next();
                uniqueAtoms.put(at, at);
            }
        }

        int series = 0;
        Iterator iterator = uniqueAtoms.keySet().iterator();
        while (iterator.hasNext()) {
            IAtom atom = (IAtom)iterator.next();
            List<IAtom> atomPath = new ArrayList<IAtom>();
            atomPath.add(atom);
            List<Integer> takenBondsListIdx = new ArrayList<Integer>();
            int len = carbonSeriesCounter(ccBonds, takenBondsListIdx, atomPath, 1);
            if (len > series) {
                series = len;
                if (series >= BitPosApi.bp.MAX_CC_TRAIL_DEPTH )  {
                    break;
                }
            }
        }

        for (int len = 12; len <= series; len++) {
            String key = BitPosApi.bp.cTrailPrefix + len;
            if (BitPosApi.bp.carbonTrails.containsKey(key)) {
                fingerprint.set(BitPosApi.bp.carbonTrails.get(key), true);
            }
        }

    }

    /**
     * Recursive C-trail path exploration, helper method for {@link #carbonTrails(IAtomContainer, BitSet)}
     * 
     * @param ccBonds
     * @param takenBondsListIdx
     * @param atomsPath
     * @param length
     * @return
     */
    private int carbonSeriesCounter(List<IBond> ccBonds, List<Integer> takenBondsListIdx, List<IAtom> atomsPath, int length) {

        int ret = length;
        if (length < BitPosApi.bp.MAX_CC_TRAIL_DEPTH) {
            IAtom lastAtomInPath = atomsPath.get(atomsPath.size() - 1);

            for (Integer i = 0; i < ccBonds.size(); i++) {
                IBond b = ccBonds.get(i);
                if (!takenBondsListIdx.contains(i)) {
                    IAtom nextAtom = null;
                    if (b.getAtom(0).equals(lastAtomInPath))
                        nextAtom = b.getAtom(1);
                    else if (b.getAtom(1).equals(lastAtomInPath))
                        nextAtom = b.getAtom(0);

                    if (nextAtom != null && !atomsPath.contains(nextAtom)) {
                        takenBondsListIdx.add(i);
                        atomsPath.add(nextAtom);

                        int newLen = carbonSeriesCounter(ccBonds, takenBondsListIdx, atomsPath, length + 1);

                        ret = newLen > ret ? newLen : ret;
                        takenBondsListIdx.remove(takenBondsListIdx.size() - 1);
                        atomsPath.remove(nextAtom);
                    }
                }
            }
        }
        return ret;
    }


    static List<String> commonThreeSomes  = new ArrayList<String>();
    static {        
        commonThreeSomes.add("C-C-C");
        commonThreeSomes.add("C:C:C");
        commonThreeSomes.add("C-C:C");
        commonThreeSomes.add("C-C-N");
        commonThreeSomes.add("C-C-O");
        commonThreeSomes.add("C-C=O");
        commonThreeSomes.add("C-N-C");
        commonThreeSomes.add("C-O-C");
        commonThreeSomes.add("N:C:N");
        commonThreeSomes.add("N-C=O");
        commonThreeSomes.add("C:N:C");
        commonThreeSomes.add("C:C-N");
        commonThreeSomes.add("C:C-O");
        commonThreeSomes.add("O-C=O");
        commonThreeSomes.add("C:C:N");
        commonThreeSomes.add("N-C-N");
    }    
    /**
     * The 'hashed' part of the OrChem fingerprint. Each combination of 3 atoms
     * is hashed to a value modulo a value defined in the class with the bit positions.
     *
     * @param threeAtomString something like "O-N=O"
     * @param fingerprint 
     * @param threeSomes map of three-atom series already processed. Map is to quickly check and avoid duplicate work here.
     */
    private void hashThreeSome (String threeAtomString, BitSet fingerprint, Map threeSomes) {

        if (!threeSomes.containsKey(threeAtomString)) {
            //System.out.println(threeAtomString);
            // determine reverse string, so for example "C:C-Br" -> "Br-C:C"
            StringTokenizer s = new StringTokenizer(threeAtomString, ":#-=", true);
            StringBuilder sb = new StringBuilder();
            while (s.hasMoreElements()) {
                String str = s.nextToken();
                sb.insert(0, str);
            }
            String reversedThreeAtomSmiles = sb.toString();

            /* Pick between the two equivalent Strings the one with the lowest
               hash code. So either "C:C-Br" or "Br-C:C", not both. They're the same,
               so hashing both would be redundant */
            String choice = null;
            if (threeAtomString.hashCode() < reversedThreeAtomSmiles.hashCode())
                choice = threeAtomString;
            else
                choice = reversedThreeAtomSmiles;

            // already done this one ? check again, possible reversed now
            if (!threeSomes.containsKey(choice)) {
                threeSomes.put(choice, null);

                /* Now check not to hash the very common threesomes. Like "C:C:C".
                 * These would flood the fingerprint for particular hash positions. */
                if (!commonThreeSomes.contains(choice)) {
                    int hc = ((choice.hashCode()) % hashModulo) + 1;
                    if (hc < 0) {
                        hc *= -1;
                    }
                    fingerprint.set(hc);
                }
            }
        }
    }
    
    /**
     * Method to capture how carbon-only ring clusters are laid out (ring sizes 5 or 6).
     * We look for clusters of three attached rings and fingerprint certain properties.
     * <br> 
     * This helps to distinguish rare layouts from more common layouts. For example,
     * a problem case was to find a straight row of three connected single bond hex-rings (unit test ID 34) 
     * That layout is very rare but looks to be deceptively common. 
     * Its rareness will be picked up better by the cluster descriptor below.
     *
     * @param rslist
     * @param fingerprint
     */
    private void ringSetLayout (List<IRingSet> rslist,BitSet fingerprint) {
        for (IRingSet rs : rslist) {
            List<IAtomContainer> atcList = new ArrayList<IAtomContainer>();
            Iterator<IAtomContainer> itr = rs.atomContainers().iterator();
            
            while (itr.hasNext()) {
                atcList.add(itr.next());
            }
            if (atcList.size() >= 3) {
                int ringCnt = atcList.size();
                for (int i = 0; i < ringCnt; i++) {
                    for (int j = i + 1; j < ringCnt; j++) {
                        for (int k = j + 1; k < ringCnt; k++) {
                            if (i != j && i != k && j != k) {

                                IAtomContainer atc1 = atcList.get(i);
                                int atc1Size = atc1.getAtomCount();
                                IAtomContainer atc2 = atcList.get(j);
                                int atc2Size = atc2.getAtomCount();
                                IAtomContainer atc3 = atcList.get(k);
                                int atc3Size = atc3.getAtomCount();
           
                                /* We only care about pent and hex carbon rings. 
                                 * Other ring sizes are less rare to begin with */
                                if ((atc1Size == 5 || atc1Size == 6) && (atc2Size == 5 || atc2Size == 6) &&
                                    (atc3Size == 5 || atc3Size == 6) && carbonOnly(atc1) && carbonOnly(atc2) && carbonOnly(atc3)) {

                                    boolean threeRingCluster = false;
                                    if (haveSharedAtoms(atc1, atc2)) {
                                        if (haveSharedAtoms(atc1, atc3) || haveSharedAtoms(atc2, atc3))
                                            threeRingCluster = true;
                                    } else if (haveSharedAtoms(atc1, atc3) && haveSharedAtoms(atc2, atc3))
                                        threeRingCluster = true;

                                    //Three ring cluster means three rings (pent/hex) somehow connected to each other
                                    if (threeRingCluster) {

                                        /*
                                         * Now build a 4 character cluster descriptor
                                         * Example descriptors: "5DLM", or "6SHH"
                                         *
                                         * Pos  Values   Meaning
                                         * ---  ------   ----------------------------------------------------
                                         * 1    6,5    : Ring triplet contains strictly 6 rings or otherwise includes (up to 3) 5 rings
                                         * 2    S,D    : Triplet bond nature : S single bonds only, D double bonds (possibly aromatic) also present
                                         * 3    L,H    : Connectivity, H=3 (means some atom participates in all 3 rings)
                                         * 2    L,M,H  : Shared bonds between triplet: Low=0,1,2 Med=3,4 High=5..n
                                         */
                                        String fiveSix = "6";
                                        String singleDouble = "S";
                                        String connectivity ="L";
                                        String bondOverlap = "L";

                                        if (atc1Size == 5 || atc2Size == 5 || atc3Size == 5) {
                                            fiveSix = "5";
                                        }

                                        Map<IAtom, Integer> sharedAtoms = new HashMap<IAtom, Integer>();
                                        collectSharedAtoms(atc1, atc2, sharedAtoms);
                                        collectSharedAtoms(atc1, atc3, sharedAtoms);
                                        collectSharedAtoms(atc2, atc3, sharedAtoms);

                                        Set<IBond> sharedBonds = new HashSet<IBond>();
                                        
                                        Boolean dblBonds=null;
                                        dblBonds = detectSharedAndDoubleBonds(atc1, sharedAtoms, sharedBonds);
                                        if(dblBonds!=null) {
                                            if (dblBonds)
                                                singleDouble = "D";

                                            dblBonds = detectSharedAndDoubleBonds(atc2, sharedAtoms, sharedBonds);
                                            if(dblBonds!=null) {
                                                if (dblBonds)
                                                    singleDouble = "D";
                                                
                                                dblBonds = detectSharedAndDoubleBonds(atc3, sharedAtoms, sharedBonds);

                                                if(dblBonds!=null) {
                                                    if (dblBonds)
                                                        singleDouble = "D";

                                                    Iterator<Integer> iterator = sharedAtoms.values().iterator();
                                                    while (iterator.hasNext()) {
                                                        if (iterator.next() == 3) {
                                                            connectivity = "H";
                                                            break;
                                                        }
                                                    }
                                                    if (sharedBonds.size() >= 5)
                                                        bondOverlap = "H";
                                                    else if (sharedBonds.size() >= 3)
                                                        bondOverlap = "M";
                                                    
                                                    int bitPos = BitPosApi.bp.ringLayout.get(fiveSix + singleDouble + connectivity +bondOverlap);
                                                    //System.out.println(fiveSix + singleDouble + connectivity +bondOverlap);
                                                    fingerprint.set(bitPos, true);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * Helper method for {@link #ringSetLayout (List,BitSet)}
     * @param atcA
     * @return true if all atoms in the container are cabrones
     */
    private boolean carbonOnly(IAtomContainer atcA) {
        for (int i = 0; i < atcA.getAtomCount(); i++) {
            if (!atcA.getAtom(i).getSymbol().equals("C")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Helper method for {@link #ringSetLayout (List,BitSet)}
     * @param atcA
     * @param atcB
     * @return true if the two containers (rings) are connected ("glued") by atoms 
     */
    private boolean haveSharedAtoms(IAtomContainer atcA, IAtomContainer atcB) {
        for (int i = 0; i < atcA.getAtomCount(); i++) {
            if (atcB.contains(atcA.getAtom(i))) {
                return true;
            }
        }
        return false;
    }


    /**
     * Helper method for {@link #ringSetLayout (List,BitSet)}<br>
     * Find all the atoms shared between two atoms containers (rings)
     * @param atcA
     * @param atcB
     * @param sharedAtoms
     */
    private void collectSharedAtoms(IAtomContainer atcA, IAtomContainer atcB, Map<IAtom, Integer> sharedAtoms) {
        for (int i = 0; i < atcA.getAtomCount(); i++) {
            IAtom atA = atcA.getAtom(i);
            if (atcB.contains(atA))
                if (!sharedAtoms.containsKey(atA)) {
                    sharedAtoms.put(atA, 1);
                } else {
                    sharedAtoms.put(atA, sharedAtoms.get(atA) + 1);
                }
        }
    }

    /**
     * Method to find bonds between atoms that are shared by rings in a cluster.
     * Also picks up if there's a double bond in the ring
     * @param atc
     * @param sharedAtoms
     * @param sharedBonds
     * @return true if there's a double bond somewhere in the atom container
     */
    private Boolean detectSharedAndDoubleBonds (IAtomContainer atc, Map sharedAtoms, Set sharedBonds) {
        Boolean doubleBondsFound=false;
        Iterator<IBond> bonds = atc.bonds().iterator();
        while (bonds.hasNext())  {
            IBond bond = bonds.next();
            if (bond.getOrder()==null)
                return null; // break out and give up
            if (bond.getOrder()==IBond.Order.DOUBLE || bond.getFlag(CDKConstants.ISAROMATIC)==true) 
                doubleBondsFound=true;
            if ( sharedAtoms.containsKey(bond.getAtom(0))&& sharedAtoms.containsKey(bond.getAtom(1)))  {
                    sharedBonds.add(bond);
            }
        }
        return doubleBondsFound;
    }

}

