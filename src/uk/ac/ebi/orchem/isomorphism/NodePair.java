/*
 *  $Author$
 *  $Date$
 *  $$
 *
 *  Copyright (C) 2009  OrChem project
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
package uk.ac.ebi.orchem.isomorphism;

/**
 * 
 * Pair of two integers refering to the Atom positions
 * in atom lists of two IAtomContainers being compared.
 * @author      rajarshi
 *
*/
public class NodePair implements Comparable{
    Integer x;
    Integer y;

    public NodePair(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getQueryNode() {
        return x;
    }

    public Integer getTargetNode() {
        return y;
    }

    public int compareTo(Object o) {
        if (o instanceof NodePair) 
            return this.x.compareTo(((NodePair)o).x);
        else
            return 0;
    }
}
