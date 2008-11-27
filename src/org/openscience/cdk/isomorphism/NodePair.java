package org.openscience.cdk.isomorphism;


/**
 * Pair of two integers refering to the Atom positions
 * in atom lists of two IAtomContainers that are being
 * compared.
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
