package uk.ac.ebi.orchem.temp;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;


/**
 * TODO 
 * TEMP CLASS : switch to CDK once this class is available
 *
 * Facilitates sort before isomorphic comparison
 * @author      markr
 * @cdk.keyword isomorphism
 * @cdk.module standard
 *
 */
public class IsomorphismSort {


    /**
     * Returns a sorted array of Atoms for a given input IAtomContainer,
     * with the sort based on:
     * <UL>
     *   <LI>the frequency of atom symbols </LI>
     *   <LI>the connectiviyy of the atoms </LI>
     * </UL>
     * <p>
     * This type of sort benefits algorithms like VF2, sorting the query
     * container before a subgraph match. The algorithm will then start with 
     * atoms that are quite likely hardest to match<BR>
     *
     * @param iac input IAtomContainer
     * @return a sorted array of atoms for that IAtomContainer
     */
    public static IAtom[] atomsByFrequency(IAtomContainer iac) {

       // Create a map with (key,value) being (atom symbol, overall count) 
       Map<String,Integer> elementCounts = new TreeMap<String,Integer>();
       Iterator<IAtom> atomIterator = iac.atoms().iterator();
       while (atomIterator.hasNext()) {
            IAtom a = atomIterator.next();
            Integer count = (elementCounts.get(a.getSymbol()));
            if (count == null) {
                elementCounts.put(a.getSymbol(), new Integer(1));
            } else
                elementCounts.put(a.getSymbol(), ++count);
        }

        // Create a map with (key,value) being (IAtom, number of bond IAtom occurs in)
        Map<IAtom,Integer> bondParticipationCount = new HashMap<IAtom, Integer>();
        Iterator<IBond> bondIterator = iac.bonds().iterator();
        while (bondIterator.hasNext())  {
            IBond b = bondIterator.next();
            Iterator<IAtom> atomsInBondIterator = b.atoms().iterator();
            while (atomsInBondIterator.hasNext())  {
                IAtom at =atomsInBondIterator.next();
                if (!bondParticipationCount.containsKey(at))  
                     bondParticipationCount.put(at,1);
                else
                bondParticipationCount.put(at,bondParticipationCount.get(at)+1);
            }
        }
        // Mind the atoms not in any bond (like for chembl compound 295740)
        for (IAtom atom : iac.atoms()) {
            if (!bondParticipationCount.containsKey(atom)) {
                bondParticipationCount.put(atom, 0);
            }
        }


        // we now have to maps that will be used to sort the incoming atom container
        List<AtomForIsomorphismSort> atomList = new ArrayList<AtomForIsomorphismSort>();
        Iterator<IAtom> itr = bondParticipationCount.keySet().iterator();
        while (itr.hasNext()) {
            IAtom a = itr.next();
            AtomForIsomorphismSort afis = new AtomForIsomorphismSort(a, elementCounts.get(a.getSymbol()),bondParticipationCount.get(a));
            atomList.add(afis);
        }

        Collections.sort(atomList, new AtomForIsomorphismSortComparator());

        // Create an output atom array based on the sorted list 
        IAtom[] iAtomArray = new IAtom[iac.getAtomCount()];
        int iacSortedIdx = 0;
        for (AtomForIsomorphismSort afis: atomList) {
            iAtomArray[iacSortedIdx] = afis.iatom;
            iacSortedIdx++;
        }
        return iAtomArray;
    }

    /**
     * Beans helper class for sorting
     */
    private static class AtomForIsomorphismSort {
        IAtom iatom;
        Integer overallElementCount;
        Integer atomBondParticipationCount;
        public AtomForIsomorphismSort (IAtom _iatom, int _overallElementCount,  int _atomBondParticipationCount ) {
            iatom = _iatom;
            overallElementCount = _overallElementCount;
            atomBondParticipationCount = _atomBondParticipationCount;
        }
    }

    /**
     * Comparator for the sort by overall element count, atom connectivity
     */
    private static class AtomForIsomorphismSortComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            AtomForIsomorphismSort e1 = (AtomForIsomorphismSort)o1;
            AtomForIsomorphismSort e2 = (AtomForIsomorphismSort)o2;
            if (!e1.iatom.getSymbol().equals(e2.iatom.getSymbol()))  {
                return (e1.overallElementCount).compareTo(e2.overallElementCount);
            }
            else
                return (e2.atomBondParticipationCount).compareTo(e1.atomBondParticipationCount);
        }
    }

}
