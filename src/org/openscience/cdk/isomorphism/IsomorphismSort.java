/* $Revision$
 * $Author$
 * $Date$
 *
 *  Copyright (C) 2001-2008  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.isomorphism;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.*;


/**
 * Facilitates sort before isomorphic comparison
 * @author      markr
 * @cdk.keyword isomorphism
 * @cdk.module standard
 *
 */
public class IsomorphismSort {
    static class AtomCountComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            Map.Entry e1 = (Map.Entry)o1;
            Map.Entry e2 = (Map.Entry)o2;
            return ((Integer)e1.getValue()).compareTo((Integer)e2.getValue());
        }
    }

    /**
     * Returns a sorted array of Atoms for a given input IAtomContainer,
     * with the sort based on the frequency of atom symbols.<BR>
     * For example input C47H65N11O6 will produce an atom array with the "O"
     * atoms first, then "N", then "C" and lastly "H".<BR>
     * This type of sort can benefit algorithms like VF2, sorting the query
     * container before a subgraph match.<BR>
     *
     * @param iac input IAtomContainer
     * @return a sorted array of atoms for that IAtomContainer
     */
    public static IAtom[] atomsByFrequency(IAtomContainer iac) {

        /* Create a Map with (key,value) being (atom symbol, overall count) */
        Map map = new TreeMap();

       Iterator atomIterator = iac.atoms().iterator();//1.1.2
        while (atomIterator.hasNext()) {
            IAtom a = (IAtom)atomIterator.next();
            Integer count = (Integer)(map.get(a.getSymbol()));
            if (count == null) {
                map.put(a.getSymbol(), new Integer(1));
            } else
                map.put(a.getSymbol(), ++count);
        }

        /* Create a List from the Set returned by entrySet() and use Collections.sort() 
         * and a custom Comparator to sort the Map.Entries in this List. */
        Iterator it = map.entrySet().iterator();
        List<Map.Entry> atomCountList = new ArrayList<Map.Entry>();
        while (it.hasNext()) {
            atomCountList.add((Map.Entry)it.next());
        }
        Collections.sort(atomCountList, new AtomCountComparator());

        /* Create an output atom array based on the sorted list */
        IAtom[] iAtomArray = new IAtom[iac.getAtomCount()];
        int iacSortedIdx = 0;
        for (Map.Entry e: atomCountList)
            for (int i = 0; i < iac.getAtomCount(); i++) {
                if (iac.getAtom(i).getSymbol().equals(e.getKey()))
                    iAtomArray[iacSortedIdx++] = iac.getAtom(i);
            }

        return iAtomArray;
    }

}
