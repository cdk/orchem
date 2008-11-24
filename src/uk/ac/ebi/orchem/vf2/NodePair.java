package uk.ac.ebi.orchem.vf2;

import org.openscience.cdk.interfaces.IAtom;


public class NodePair {
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
}
