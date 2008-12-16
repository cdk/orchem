/* $Revision$ 
 * $Author$ 
 * $Date$
 * 
 * 2008  Rajarshi Guha 
 *       Contact: cdk-devel@lists.sourceforge.net
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
 
 */
package org.openscience.cdk.isomorphism;

/**
 * Pair of two integers refering to the Atom positions
 * in atom lists of two IAtomContainers being compared.
 * @author      rajarshi
 * @cdk.keyword isomorphism
 * @cdk.module standard
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
