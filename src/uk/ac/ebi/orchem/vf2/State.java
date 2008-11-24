package uk.ac.ebi.orchem.vf2;


import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

public abstract class State {

    public static int recursionDepth=0;

    protected abstract IAtomContainer getTargetMolecule();



    protected abstract IAtomContainer getQueryMolecule();

    /**
     * Checks whether the two nodes can be added to the state.
     *
     * @param node1 The query node
     * @param node2 The target node
     * @return true if (node1, node2) can be added to the current state
     */
    abstract boolean isFeasiblePair(Integer node1, Integer node2);

    protected abstract void addPair(Integer node1, Integer node2);

    protected abstract boolean isGoal();

    /**
     * Can we do any more matches?.
     *
     * @return true if no more matches can be obtained, otherwise false
     */
    protected abstract boolean isDead();

    protected abstract int getCoreLength();

    protected abstract NodePair[] getCoreSet();

    protected abstract Object clone() throws CloneNotSupportedException;

    protected abstract void backtrack();

    protected abstract NodePair nextPair(Integer prev_n1, Integer prev_n2);

    protected boolean bondExists(IAtomContainer container, int idx1, int idx2) {
        IAtom atom1 = container.getAtom(idx1);
        IAtom atom2 = container.getAtom(idx2);
        return container.getBond(atom1, atom2) != null;
    }

    protected boolean bondMatches(IQueryAtomContainer g1, IAtomContainer g2, int i, int k, int j, int l) {
        IQueryBond qbond = (IQueryBond) g1.getBond(g1.getAtom(i), g1.getAtom(k));
        IBond tbond = g2.getBond(g2.getAtom(j), g2.getAtom(l));
        return qbond.matches(tbond);
    }
}
