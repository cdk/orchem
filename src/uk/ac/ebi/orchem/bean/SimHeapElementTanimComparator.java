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
package uk.ac.ebi.orchem.bean;


import java.util.Comparator;

/**
 * Comparator for comparing two {@link uk.ac.ebi.orchem.bean.SimHeapElement}objects
 *
 * @author markr@ebi.ac.uk
 */
public class SimHeapElementTanimComparator implements Comparator {

    public int compare(Object o1, Object o2) {

        if (o1 instanceof SimHeapElement && o2 instanceof SimHeapElement) {
            SimHeapElement b1 = (SimHeapElement)o1;
            SimHeapElement b2 = (SimHeapElement)o2;

            if (b1.getTanimotoCoeff() != null && b2.getTanimotoCoeff() != null)
                return b1.getTanimotoCoeff().compareTo(b2.getTanimotoCoeff());
            else
                return 0;
        } else
            return 0;
    }
}
