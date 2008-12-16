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


import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.isomorphism.matchers.IQueryBond;

/**
 * Definition of an abstract class representing a state of the
 * matching process between two IAtomContainers
 * @author      rajarshi
 * @cdk.keyword isomorphism
 * @cdk.license MIT-like
 * @cdk.module standard
 */
public abstract class State {

    /**
     * int to track recursion depth, for debugging purpose
     */
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

    // here we check if the bond is a query bond (i.e. from a IQueryAtomContainer). If so,
    // we coerce to IQueryBond and procede with the match.
    //
    // If it's not a query bond we then do a manual match considering atom equality and bond
    // order, I think that's correct.
    protected boolean bondMatches(IAtomContainer g1, IAtomContainer g2, int i, int k, int j, int l) {
        IBond aBond = g1.getBond(g1.getAtom(i), g1.getAtom(k));
        IBond tbond = g2.getBond(g2.getAtom(j), g2.getAtom(l));
        if (aBond instanceof IQueryBond) {
            IQueryBond qbond = (IQueryBond) aBond;
            return qbond.matches(tbond);
        } else {
            boolean aFlag = aBond.getFlag(CDKConstants.ISAROMATIC);

            // if the aromaticity flags for the two bonds differ, no chance
            if (aFlag != tbond.getFlag(CDKConstants.ISAROMATIC)) return false;

            // next we check bond order, but only if bonds were not aromatic. This is because
            // bond order may be 1 or 2 which does not indicate aromaticity. Also we only
            // check one bond since at this point, both bonds will have the same value of the
            // aromaticity flag
            if (!aFlag) {
                if (aBond.getOrder() != tbond.getOrder()) return false;
            }
            if (g1.getAtom(i).getSymbol().equals(g2.getAtom(j).getSymbol()) &&
                    g1.getAtom(k).getSymbol().equals(g2.getAtom(l).getSymbol())) return true;
            if (g1.getAtom(i).getSymbol().equals(g2.getAtom(l).getSymbol()) &&
                    g1.getAtom(k).getSymbol().equals(g2.getAtom(j).getSymbol())) return true;
            return false;
        }
    }
}
