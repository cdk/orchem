package uk.ac.ebi.orchem.vf2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


/**
 * Sort before doing isomorphic comparisons.
 * @author markr@ebi.ac.uk
 *
 */
public class IsomorphismSort {

    /**
     * Returns a sorted array of Atoms for a given input IAtomContainer,
     * with the sort based on the frequency of atom symbols.<BR>
     * For example input C47H65N11O6 will produce an atom array with the "O" 
     * atoms first, then "N", then "C" and lastly "H".<BR>
     * This type of sort can benefit algorithms like VF2, sorting the query
     * container before starting graph comparison.
     *
     * @param iac input IAtomContainer
     * @return a sorted array of atoms
     */
    public static IAtom[] atomsByFrequency(IAtomContainer iac) {

        System.out.println("atomsByFrequency started");
        /* Create a Map with (key,value) being (atom symbol, overall count) */
        Map map = new HashMap();
        Iterator atomIterator = iac.atoms();
        while (atomIterator.hasNext()) {
            IAtom a = (IAtom)atomIterator.next();
            Integer count = (Integer)map.get(a.getSymbol());
            if (count == null) {
                map.put(a.getSymbol(), new Integer(1));
            } else
                map.put(a.getSymbol(), ++count);
        }

        System.out.println("atomsByFrequency 2");

        /* Create a Set that sorts the Map on overall count (increasing ) */
        Set set = new TreeSet(new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Map.Entry e1 = (Map.Entry)o1;
                        Map.Entry e2 = (Map.Entry)o2;
                        Integer int1 = (Integer)e1.getValue();
                        Integer int2 = (Integer)e2.getValue();
                        return int1.compareTo(int2);
                    }
                });
        set.addAll(map.entrySet());


        System.out.println("atomsByFrequency 3");

        /* Create an output atom array based on the sorted Set */
        IAtom[] iAtomArray = new IAtom[iac.getAtomCount()];
        int iacSortedIdx = 0;
        for (Iterator iter = set.iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry)iter.next();
            System.out.println("atomsByFrequency .. ");

            for (int i = 0; i < iac.getAtomCount(); i++) {
                if (iac.getAtom(i).getSymbol().equals(entry.getKey()))
                    iAtomArray[iacSortedIdx++] = iac.getAtom(i);
            }
        }
        System.out.println("done, returning iAtomArray "+iAtomArray.length);

        return iAtomArray;
    }
}
