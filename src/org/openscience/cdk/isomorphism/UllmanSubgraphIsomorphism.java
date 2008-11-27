package org.openscience.cdk.isomorphism;


import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Verbatim translation of C++ Ullmann algorithm from VF lib.<BR>
 * http://amalfi.dis.unina.it/graph/db/vflib-2.0/doc/vflib.html<BR>
 * 
 */

public class UllmanSubgraphIsomorphism extends State {
    private int coreLen;
    private Integer[] core1;
    private Integer[] core2;

    private IAtomContainer g2;
    private IQueryAtomContainer g1;

    int n1, n2;
    byte[][] M;



    public UllmanSubgraphIsomorphism(IAtomContainer target,IQueryAtomContainer query) {

        this.g2 = target;
        this.g1 = query;

        n1 = g1.getAtomCount();
        n2 = g2.getAtomCount();

        coreLen = 0;

        core1 = new Integer[n1];
        core2 = new Integer[n2];
        M = new byte[n1][n2];

        for (int i = 0; i < n1; i++) core1[i] = null;
        for (int i = 0; i < n2; i++) core2[i] = null;
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                IQueryAtom g1Atom = (IQueryAtom) g1.getAtom(i);
                IAtom g2Atom = g2.getAtom(j);

                if (g1.getConnectedBondsCount(g1Atom) <= g2.getConnectedBondsCount(g2Atom)
                        && g1Atom.matches(g2Atom)) {
                    
                    M[i][j] = 1;
                } else M[i][j] = 0;
            }
        }

    }

    /**
     * Only used for the clone operation.
     */
    private UllmanSubgraphIsomorphism() {
    }

    public IAtomContainer getTargetMolecule() {
        return g2;
    }

    public IAtomContainer getQueryMolecule() {
        return g1;
    }

    /**
     * Checks whether the two nodes can be added to the state.
     *
     * @param node1 The query node
     * @param node2 The target node
     * @return true if (node1, node2) can be added to the current state
     */
    public boolean isFeasiblePair(Integer node1, Integer node2) {
        assert node1 < n1;
        assert node2 < n2;

        return M[node1][node2] != 0;
    }

    public void addPair(Integer node1, Integer node2) {
        assert node1 < n1;
        assert node2 < n2;
        assert coreLen < n1;
        assert coreLen < n2;

        core1[node1] = node2;
        core2[node2] = node1;

        coreLen++;

        for (int k = coreLen; k < n1; k++)
            M[k][node2] = 0;

        refine();
    }

    public boolean isGoal() {
        return coreLen == n1;
    }

    public boolean isDead() {
        if (n1 > n2) return true;
        for (int i = coreLen; i < n1; i++) {
            boolean carryOn = false;
            for (int j = 0; j < n2; j++) {
                if (M[i][j] != 0) {
                    carryOn = true;
                    break;
                }
            }
            if (!carryOn) return true;
        }
        return false;
    }

    public int getCoreLength() {
        return coreLen;
    }

    public NodePair[] getCoreSet() {
        List<NodePair> pairList = new ArrayList<NodePair>();
        for (int i = 0; i < n1; i++) {
            if (core1[i] != null) {
                pairList.add(new NodePair(i, core1[i]));
            }
        }
        return pairList.toArray(new NodePair[] {});
    }

    public Object clone() throws CloneNotSupportedException {
        UllmanSubgraphIsomorphism cloned = new UllmanSubgraphIsomorphism();
        cloned.g1 = g1;
        cloned.g2 = g2;
        cloned.n1 = n1;
        cloned.n2 = n2;
        cloned.coreLen = coreLen;
        cloned.core1 = new Integer[n1];
        cloned.core2 = new Integer[n2];
        cloned.M = new byte[n1][n2];
        System.arraycopy(core1, 0, cloned.core1, 0, cloned.n1);
        System.arraycopy(core2, 0, cloned.core2, 0, cloned.n2);
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                cloned.M[i][j] = M[i][j];
            }
        }
        return cloned;
    }

    public void backtrack() {

    }

    /**
     * Get the next pair of nodes to look at, based on the previous pair of nodes.
     *
     * @param prev_n1 The previous node from the query graph
     * @param prev_n2 The previous node from the target graph
     * @return null if there are no more pairs to return, otherwise a {@link NodePair}
     *         object containing the next pair of node to be tried.
     */
    public NodePair nextPair(Integer prev_n1, Integer prev_n2) {
        if (prev_n1 == null) {
            prev_n1 = coreLen;
            prev_n2 = 0;
        } else if (prev_n2 == null) prev_n2 = 0;
        else prev_n2++;

        if (prev_n2 >= n2) {
            prev_n1++;
            prev_n2 = 0;
        }
 
        if (prev_n1 != coreLen) return null;
        while (prev_n2 < n2 && M[prev_n1][prev_n2] == 0)
            prev_n2++;
        if (prev_n2 < n2) {
            return new NodePair(prev_n1, prev_n2);
        } else return null;
    }

    private void refine() {
        for (int i = coreLen; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                if (M[i][j] != 0) {
                
                    boolean edge_ik, edge_jl;
                    for (int k = coreLen - 1; k < coreLen; k++) {

                        Integer l = core1[k];
                        assert l != null;

                        edge_ik = bondExists(g1, i, k);
                        edge_jl = bondExists(g2, j, l);

                        if (edge_ik && !edge_jl ) {
                            M[i][j] = 0;
                            break;
                        } else if (edge_ik && !bondMatches(g1, g2, i, k, j, l)) {
                            M[i][j] = 0;
                            break;
                        } else if (edge_ik && !bondMatches(g1, g2, k, i, l, j)) {
                            M[i][j] = 0;
                            break;
                        }
                    }
                }
            }
        }
    }



}
