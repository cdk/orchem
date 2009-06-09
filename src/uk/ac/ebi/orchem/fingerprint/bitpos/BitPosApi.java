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
package uk.ac.ebi.orchem.fingerprint.bitpos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Static singleton wrapper for {@link BitPositions}
 * http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
 * 
 * @author markr@ebi.ac.uk
 * 
 */
public class BitPosApi {
    public final static BitPositions bp = new BitPositions();

    public static void main(String[] args) {
        printFingerprintBitPositions();
    }

    /**
     * Convenience method to print out the bit position of each
     * chemical attribute in the OrChem fingerprint
     *
     */
    public static void printFingerprintBitPositions() {

        Map<Integer, String> all = new HashMap<Integer, String>();
        all.putAll(prepareContentDump(bp.elemCntBits));
        all.putAll(prepareContentDump(bp.atomPairBits));
        all.putAll(prepareContentDump(bp.ringSetBits));
        all.putAll(prepareContentDump(bp.ringBits));
        all.putAll(prepareContentDump(bp.smartsPatternBits));
        all.putAll(prepareContentDump(bp.carbonTrails));
        all.putAll(prepareContentDump(bp.ringLayout));

        for (Iterator<Integer> neighItr = bp.neighbourBits.keySet().iterator(); neighItr.hasNext(); ) {
            int bit = neighItr.next();
            List<Neighbour> nbList = bp.neighbourBits.get(bit);
            StringBuilder sb = new StringBuilder();
            for (Neighbour n : nbList)
                sb.append(n.toString());
            all.put(bit, sb.toString());
        }

        int i=1;
        while (i<1024) {
            if (all.containsKey(i)) {
                String condition = all.get(i);
                System.out.println("Bit  " + i + "  " + condition);
            }
            i++;

        }
        System.out.println(">> first 1024 bits printed");
    }

    /**
     * Helper method for {@link #printFingerprintBitPositions()  }
     * @param bitposMap map with chemical attributes and their bit positions
     * @return same map content, but with bit position as Key, and for Value a string representation of the attribute
     */
    private static Map<Integer, String> prepareContentDump(Map<String, Integer> bitposMap) {
        Map<Integer, String> ret = new HashMap<Integer, String>();
        Collection c = bitposMap.keySet();
        Iterator<String> it = c.iterator();
        while (it.hasNext()) {
            String o = it.next();
            Integer bit = bitposMap.get(o);
            ret.put(bit, o);
        }
        return ret;
    }


}
