package uk.ac.ebi.orchem.vf2;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import java.util.List;

public interface IState {
    public IAtomContainer getTargetMolecule();

    public IAtomContainer getQueryMolecule();

    /**
     * Checks whether the two nodes can be added to the state.
     *
     * @param node1 The query node
     * @param node2 The target node
     * @return true if (node1, node2) can be added to the current state
     */
    boolean isFeasiblePair(Integer node1, Integer node2);

    public void addPair(Integer node1, Integer node2);

    public boolean isGoal();

    /**
     * Can we do any more matches?.
     *
     * @return true if no more matches can be obtained, otherwise false
     */
    public boolean isDead();

    public int getCoreLength();

    public NodePair[] getCoreSet();

    public Object clone() throws CloneNotSupportedException;

    public void backtrack();

    public NodePair nextPair(Integer prev_n1, Integer prev_n2);
}
