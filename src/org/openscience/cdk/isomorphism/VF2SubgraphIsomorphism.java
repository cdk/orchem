/* $Revision$ 
 * $Author$ 
 * $Date$
 *
 * Copyright (C) 2001  
 *   Dipartimento di Informatica e Sistemistica,
 *   Universita degli studi di Napoli ``Federico II'
 *   <http://amalfi.dis.unina.it>
 * 
 * 2008  Rajarshi Guha 
 *       Contact: cdk-devel@lists.sourceforge.net
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 *  1. The above copyright notice and this permission notice shall be included in all copies or substantial
 *      portions of the Software, together with the associated disclaimers.
 *  2. Any modification to the standard distribution of the Software shall be mentioned in a prominent notice
 *      in the documentation provided with the modified distribution, stating clearly how, when and by
 *      whom the Software has been modified.
 *  3. Either the modified distribution shall contain the entire sourcecode of the standard distribution of the 
 *      Software, or the documentation shall provide instructions on where the source code of the standard
 *      distribution of the Software can be obtained.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

package org.openscience.cdk.isomorphism;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryAtom;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import java.util.ArrayList;
import java.util.List;


/**
 * Verbatim translation of C++ VF2 algprithm from VF lib.<BR>
 * http://amalfi.dis.unina.it/graph/db/vflib-2.0/doc/vflib.html<BR>
 *
 * @author      rajarshi
 * @cdk.keyword isomorphism
 * @cdk.license MIT-like
 * @cdk.module standard
 *
 */
public class VF2SubgraphIsomorphism extends State {


    int core_len, orig_core_len;
    Integer added_node1;
    int t1both_len, t2both_len, t1in_len, t1out_len, t2in_len, t2out_len;

    // begin fix - made attributes non-static
    Integer[] core_1;
    Integer[] core_2;
    Integer[] in_1;
    Integer[] in_2;
    Integer[] out_1;
    Integer[] out_2;
    // end fix

    Integer[] order;

    private IAtomContainer g2;
    private IAtomContainer g1;

    int n1, n2;
    public static Integer share_count =1; 

    protected Object clone() throws CloneNotSupportedException {
        VF2SubgraphIsomorphism ret = new VF2SubgraphIsomorphism();
        ret.g1 = g1;
        ret.g2 = g2;
        ret.n1 = n1;

        // begin fix
        ret.n2 = n2; 
        // end fix

        ret.order = order;
        ret.core_len = ret.orig_core_len = core_len;
        ret.t1in_len = t1in_len;
        ret.t1out_len = t1out_len;
        ret.t1both_len = t1both_len;
        ret.t2in_len = t2in_len;
        ret.t2out_len = t2out_len;
        ret.t2both_len = t2both_len;
        ret.added_node1 = null;

        ret.core_1 = (Integer[])core_1.clone();
        ret.core_2 = (Integer[])core_2.clone();
        ret.in_1 = (Integer[])in_1.clone();
        ret.in_2 = (Integer[])in_2.clone();
        ret.out_1 = (Integer[])out_1.clone();
        ret.out_2 = (Integer[])out_2.clone();


        ++share_count;
        return ret;
    }

    private VF2SubgraphIsomorphism() {
    }

    public VF2SubgraphIsomorphism(IAtomContainer target, IAtomContainer query) {
        g1 = query;
        g2 = target;

        n1 = g1.getAtomCount();
        n2 = g2.getAtomCount(); 

        core_len = 0;
        orig_core_len = 0;
        t1both_len = t1in_len = t2out_len = 0;
        t2both_len = t2in_len = t2out_len = 0;
        added_node1 = null;

        core_1 = new Integer[n1];
        core_2 = new Integer[n2];
        in_1 = new Integer[n1];
        in_2 = new Integer[n2];
        out_1 = new Integer[n1];
        out_2 = new Integer[n2];

        for (int i = 0; i < n1; i++) {
            core_1[i] = null;
            in_1[i] = 0;
            out_1[i] = 0;
        }

        for (int i = 0; i < n2; i++) {
            core_2[i] = null;
            in_2[i] = 0;
            out_2[i] = 0;
        }

    }

    protected IAtomContainer getTargetMolecule() {
        return g2;
    }

    protected IAtomContainer getQueryMolecule() {
        return g1;
    }

    /**
     * Get the atom attached to the specified atom by the specified bond.
     * <p/>
     * The method assumes 2-centered bonds
     *
     * @param container The molecules
     * @param whichAtom Which atom (index)
     * @param whichOne  Which of the bonds connected to the above atom
     * @return The atom at the other end of this bond
     */
    private Integer getOtherAtomOfBond(IQueryAtomContainer container, int whichAtom, int whichOne) {
        IAtom atom = container.getAtom(whichAtom);
        IBond bond = (IBond)container.getConnectedBondsList(atom).get(whichOne);
        return container.getAtomNumber(bond.getConnectedAtom(atom));
    }

    /**
     * Get the atom attached to the specified atom by the specified bond.
     * <p/>
     * The method assumes 2-centered bonds
     *
     * @param container The molecules
     * @param whichAtom Which atom (index)
     * @param whichOne  Which of the bonds connected to the above atom
     * @return The atom at the other end of this bond
     */
    private Integer getOtherAtomOfBond(IAtomContainer container, int whichAtom, int whichOne) {
        IAtom atom = container.getAtom(whichAtom);
        IBond bond = (IBond)container.getConnectedBondsList(atom).get(whichOne);
        return container.getAtomNumber(bond.getConnectedAtom(atom));
    }

    /**
     * Checks whether the two nodes can be added to the state.
     *
     * @param node1 The query node
     * @param node2 The target node
     * @return true if (node1, node2) can be added to the current state
     */
    boolean isFeasiblePair(Integer node1, Integer node2) {
    
        assert node1 < n1;
        assert node2 < n2;
        assert core_1[node1] == null;
        assert core_2[node2] == null;

        IAtom atomG1 = g1.getAtom(node1);
        IAtom g2Atom = g2.getAtom(node2);

        if (atomG1 instanceof IQueryAtom) {
            IQueryAtom g1Atom = (IQueryAtom) atomG1;
            if (!g1Atom.matches(g2Atom))
                return false;
        } else {
           if (!atomG1.getSymbol().equals(g2Atom.getSymbol())) return false;
        }

        int i, other1, other2;
        int termout1 = 0, termout2 = 0, termin1 = 0, termin2 = 0, new1 = 0, new2 = 0;

        // Check the 'out' edges of node1
        for (i = 0; i < g1.getConnectedBondsCount(node1); i++) {
            other1 = getOtherAtomOfBond(g1, node1, i);

            if (core_1[other1] != null) {
                other2 = core_1[other1];
                if (!bondExists(g2, node2, other2) || !bondMatches(g1, g2, node1, other1, node2, other2))
                    return false;
            } else {
                if (in_1[other1] != 0)
                    termin1++;
                if (out_1[other1] != 0)
                    termout1++;
                if (in_1[other1] == 0 && out_1[other1] == 0) 
                    new1++;
            }
        }

        // Check the 'in' edges of node1
        for (i = 0; i < g1.getConnectedBondsCount(node1); i++) {
            other1 = getOtherAtomOfBond(g1, node1, i);
            if (core_1[other1] != null) {
                other2 = core_1[other1];
                if (!bondExists(g2, other2, node2) || !bondMatches(g1, g2, node1, other1, other2, node2))
                    return false;
            } else {
                if (in_1[other1] != 0)
                    termin1++;
                if (out_1[other1] != 0)
                    termout1++;
                if (in_1[other1] == 0 && out_1[other1] == 0)
                    new1++;
            }
        }

        // Check the 'out' edges of node2
        for (i = 0; i < g2.getConnectedBondsCount(node2); i++) {
            other2 = getOtherAtomOfBond(g2, node2, i);
            if (core_2[other2] != null) { /* Do nothing */
            } else {
                if (in_2[other2] != 0)
                    termin2++;
                if (out_2[other2] != 0)
                    termout2++;
                if (in_2[other2] == 0 && out_2[other2] == 0)
                    new2++;
            }
        }

        // Check the 'in' edges of node2
        for (i = 0; i < g2.getConnectedBondsCount(node2); i++) {
            other2 = getOtherAtomOfBond(g2, node2, i);
            if (core_2[other2] != null) { /* Do nothing */
            } else {
                if (in_2[other2] != 0)
                    termin2++;
                if (out_2[other2] != 0)
                    termout2++;
                if (in_2[other2] == 0 && out_2[other2] == 0)
                    new2++;
            }
        }
        return termin1 <= termin2 && termout1 <= termout2 && (termin1 + termout1 + new1) <= (termin2 + termout2 + new2);
    }

    protected void addPair(Integer node1, Integer node2) {
        assert (node1 < n1);
        assert (node2 < n2);
        assert (core_len < n1);
        assert (core_len < n2);

        core_len++;
        added_node1 = node1;

        if (in_1[node1] == 0) {
            in_1[node1] = core_len;
            t1in_len++;
            if (out_1[node1] != 0)
                t1both_len++;
        }
        if (out_1[node1] == 0) {
            out_1[node1] = core_len;
            t1out_len++;
            if (in_1[node1] != 0)
                t1both_len++;
        }

        if (in_2[node2] == 0) {
            in_2[node2] = core_len;
            t2in_len++;
            if (out_2[node2] != 0)
                t2both_len++;
        }
        if (out_2[node2] == 0) {
            out_2[node2] = core_len;
            t2out_len++;
            if (in_2[node2] != 0)
                t2both_len++;
        }

        core_1[node1] = node2;
        core_2[node2] = node1;

        int i, other;
        for (i = 0; i < g1.getConnectedBondsCount(node1); i++) {
            other = getOtherAtomOfBond(g1, node1, i);
            if (in_1[other] == 0) {
                in_1[other] = core_len;
                t1in_len++;
                if (out_1[other] != 0)
                    t1both_len++;
            }
        }

        for (i = 0; i < g1.getConnectedBondsCount(node1); i++) {
            other = getOtherAtomOfBond(g1, node1, i);
            if (out_1[other] == 0) {
                out_1[other] = core_len;
                t1out_len++;
                if (in_1[other] != 0)
                    t1both_len++;
            }
        }

        for (i = 0; i < g2.getConnectedBondsCount(node2); i++) {
            other = getOtherAtomOfBond(g2, node2, i);
            if (in_2[other] == 0) {
                in_2[other] = core_len;
                t2in_len++;
                if (out_2[other] != 0)
                    t2both_len++;
            }
        }

        for (i = 0; i < g2.getConnectedBondsCount(node2); i++) {
            other = getOtherAtomOfBond(g2, node2, i);
            if (out_2[other] == 0) {
                out_2[other] = core_len;
                t2out_len++;
                if (in_2[other] != 0)
                    t2both_len++;
            }
        }

    }

    protected boolean isGoal() {
        return core_len == n1;
    }

    /**
     * Can we do any more matches?.
     *
     * @return true if no more matches can be obtained, otherwise false
     */
    protected boolean isDead() {
        return n1 > n2 || t1both_len > t2both_len || t1out_len > t2out_len || t1in_len > t2in_len;
    }

    protected int getCoreLength() {
        return core_len;
    }

    protected NodePair[] getCoreSet() {
        List<NodePair> pairList = new ArrayList<NodePair>();
        for (int i = 0; i < n1; i++) {
            if (core_1[i] != null) {
                pairList.add(new NodePair(i, core_1[i]));
            }
        }
        return pairList.toArray(new NodePair[] { });

    }

    protected void backtrack() {
        assert (core_len - orig_core_len <= 1);
        assert (added_node1 != null);

        if (orig_core_len < core_len) {
            int i, node2;

            if (in_1[added_node1] == core_len)
                in_1[added_node1] = 0;
            for (i = 0; i < g1.getConnectedBondsCount(added_node1); i++) {
                int other = getOtherAtomOfBond(g1, added_node1, i);
                if (in_1[other] == core_len)
                    in_1[other] = 0;
            }

            if (out_1[added_node1] == core_len)
                out_1[added_node1] = 0;
            for (i = 0; i < g1.getConnectedBondsCount(added_node1); i++) {
                int other = getOtherAtomOfBond(g1, added_node1, i);
                if (out_1[other] == core_len)
                    out_1[other] = 0;
            }

            node2 = core_1[added_node1];

            if (in_2[node2] == core_len)
                in_2[node2] = 0;
            for (i = 0; i < g2.getConnectedBondsCount(node2); i++) {
                int other = getOtherAtomOfBond(g2, node2, i);
                if (in_2[other] == core_len)
                    in_2[other] = 0;
            }

            if (out_2[node2] == core_len)
                out_2[node2] = 0;
            for (i = 0; i < g2.getConnectedBondsCount(node2); i++) {
                int other = getOtherAtomOfBond(g2, node2, i);
                if (out_2[other] == core_len)
                    out_2[other] = 0;
            }

            core_1[added_node1] = null;
            core_2[node2] = null;

            core_len = orig_core_len;
            added_node1 = null;
        }
    }

    protected NodePair nextPair(Integer prev_n1, Integer prev_n2) {
        if (prev_n1 == null)
            prev_n1 = 0;
        if (prev_n2 == null)
            prev_n2 = 0;
        else
            prev_n2++;

        if (t1both_len > core_len && t2both_len > core_len) {
            while (prev_n1 < n1 && (core_1[prev_n1] != null || out_1[prev_n1] == 0 || in_1[prev_n1] == 0)) {
                prev_n1++;
                prev_n2 = 0;
            }
        } else if (t1out_len > core_len && t2out_len > core_len) {
            while (prev_n1 < n1 && (core_1[prev_n1] != null || out_1[prev_n1] == 0)) {
                prev_n1++;
                prev_n2 = 0;
            }
        } else if (t1in_len > core_len && t2in_len > core_len) {
            while (prev_n1 < n1 && (core_1[prev_n1] != null || in_1[prev_n1] == 0)) {
                prev_n1++;
                prev_n2 = 0;
            }
        } else if (prev_n1 == 0 && order != null) {
            int i = 0;
            while (i < n1 && core_1[prev_n1 = order[i]] != null)
                i++;
            if (i == n1)
                prev_n1 = n1;
        } else {
            while (prev_n1 < n1 && core_1[prev_n1] != null) {
                prev_n1++;
                prev_n2 = 0;
            }
        }

        if (t1both_len > core_len && t2both_len > core_len) {
            while (prev_n2 < n2 && (core_2[prev_n2] != null || out_2[prev_n2] == 0 || in_2[prev_n2] == 0)) {
                prev_n2++;
            }
        } else if (t1out_len > core_len && t2out_len > core_len) {
            while (prev_n2 < n2 && (core_2[prev_n2] != null || out_2[prev_n2] == 0)) {
                prev_n2++;
            }
        } else if (t1in_len > core_len && t2in_len > core_len) {
            while (prev_n2 < n2 && (core_2[prev_n2] != null || in_2[prev_n2] == 0)) {
                prev_n2++;
            }
        } else {
            while (prev_n2 < n2 && core_2[prev_n2] != null) {
                prev_n2++;
            }
        }

        if (prev_n1 < n1 && prev_n2 < n2)
            return new NodePair(prev_n1, prev_n2);

        return null;
    }

}
