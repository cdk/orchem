package uk.ac.ebi.orchem.isomorphism;

/* $Revision$
 * $Author$
 * $Date$
 *
 * Copyright (C) 2001
 *   Dipartimento di Informatica e Sistemistica,
 *   Universita degli studi di Napoli ``Federico II'
 *   <http://amalfi.dis.unina.it>
 *
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


import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;


/**
 * Definition of an abstract class representing a state of the
 * matching process between two atom containers, a query and a target.
 * @author      markr/rajarshi
 */
public abstract class State {

    /**
     * Tracks recursion depth, for debugging purpose
     */
    static int recursionDepth = 0;

    /**
     * Getter for query atom container
     */
    abstract IAtomContainer getQueryContainer();

    /**
     * Getter for target atom container
     */
    abstract IAtomContainer getTargetContainer();

    /**
     * Indicator if the match should take stereo bond types into account or not.
     */
    boolean strictStereoIsomorphism;

    /**
     * Backup for the explicit hydrogen counts in a query (as we strip off the hydrogens (nodes) for VF2 matching
     */
    int[] queryExplHydrogenCountBackup;

    /**
     * Checks whether the two nodes can be added to the state.
     *
     * @param node1 The query node
     * @param node2 The target node
     * @return true if (node1, node2) can be added to the current state
     */
    abstract boolean isFeasiblePair(Integer node1, Integer node2);


    /**
     * Returns true if a bond exists between the atoms on the positions provided
     * in the provided container.
     * 
     * @param container atom container
     * @param idx1 index of first atom
     * @param idx2 index of second atom
     * @return
     */
    boolean bondExists(IAtomContainer container, int idx1,
                                 int idx2) {
        IAtom atom1 = container.getAtom(idx1);
        IAtom atom2 = container.getAtom(idx2);
        return container.getBond(atom1, atom2) != null;
    }


    /**
     * Checks if a given bond in the query matches a given bond in the target.<BR>
     * Note the "pseudo" arguments - these indicate if atoms in query and
     * target repsectively are 'pseudo' atoms. We want that information, but
     * do not want to test pseudo atoms using the expensive 'instanceof', hence
     * the reason for having this readily available as flags in an array.
     *
     * @param query query atom container
     * @param target target atom container
     * @param qIdx1 index for query, first atom in bond
     * @param qIdx2 index for query, second atom in bond
     * @param tIdx1 index for target, first atom in bond
     * @param tIdx2 index for target, second atom in bond
     * @param pseudoQ positional indicator for query flagging up pseudo atoms
     * @param pseudoT positional indicator for target flagging up pseudo atoms
     * @return true if the bonds match.
     */
    boolean bondMatches(IAtomContainer query, IAtomContainer target,
                                  int qIdx1, int qIdx2, int tIdx1,
                                  int tIdx2, boolean[] pseudoQ,
                                  boolean[] pseudoT) {

        IBond qBond =
            query.getBond(query.getAtom(qIdx1), query.getAtom(qIdx2));
        IBond tbond =
            target.getBond(target.getAtom(tIdx1), target.getAtom(tIdx2));

        // If the aromaticity flags for the two bonds differ, stop.
        boolean queryAromaticFlag = qBond.getFlag(CDKConstants.ISAROMATIC);
        if (queryAromaticFlag != tbond.getFlag(CDKConstants.ISAROMATIC))
            return false;

        // If the strict on stereo isometry and stereo indicators differ, no match
        if (strictStereoIsomorphism && qBond.getStereo() != null &&
            tbond.getStereo() != null &&
            // The E Z stereo types are not useful here .. they can get set for Benzene for example. 
            // Chose to ignore.
            qBond.getStereo() != IBond.Stereo.E_OR_Z && qBond.getStereo() != IBond.Stereo.E_Z_BY_COORDINATES &&
            tbond.getStereo() != IBond.Stereo.E_OR_Z && tbond.getStereo() != IBond.Stereo.E_Z_BY_COORDINATES &&
            qBond.getStereo() != tbond.getStereo()) {
            return false;
        }

        // Next we check bond order, but only if bonds were not aromatic. This is because
        // bond order may be 1 or 2 which does not indicate aromaticity. Also we only
        // check one bond since at this point, both bonds will have the same value of the
        // aromaticity flag
        if (!queryAromaticFlag) {
            if (qBond.getOrder() != tbond.getOrder())
                return false;
        }

        //Now do atom symbol matching.. tricky: take pesudo atom flags into account
        if (matches(query, target, qIdx1, tIdx1, pseudoQ, pseudoT) &&
            matches(query, target, qIdx2, tIdx2, pseudoQ, pseudoT))
            return true;

        if (matches(query, target, qIdx1, tIdx2, pseudoQ, pseudoT) &&
            matches(query, target, qIdx2, tIdx1, pseudoQ, pseudoT))
            return true;

        return false;
    }

    /**
     * Helper method for bond matching, taking pseudo atoms to a certain extent 
     * into account.
     *
     * @param atc1 atom container 1
     * @param atc2 atom container 2
     * @param idx1 atom index for atom container 1
     * @param idx2 atom index for atom container 2
     * @param pseudoAtc1 pseudo atom flags for atom container 1
     * @param pseudoAtc2 pseudo atom flags for atom container 2
     * @return true if atoms atc1(idx1) and atc2(idx2) match by symbol.
     */
     private boolean matches(IAtomContainer atc1, IAtomContainer atc2, int idx1,
                            int idx2, boolean[] pseudoAtc1, boolean[] pseudoAtc2) {
        IAtom qAtom = atc1.getAtom(idx1);
        IAtom tAtom = atc2.getAtom(idx2);

        //Normal match
        if (qAtom.getSymbol().equals(tAtom.getSymbol()))
            return true;

        //Pseudo atoms
        //"A" is any atom, "Q" is a heteroatom (i.e. any atom except C or H)
        if (pseudoAtc1[idx1]) {
            if (pseudoAtc2[idx2]) {
                return true;
            } else {
                PseudoAtom pseudo = (PseudoAtom)qAtom;
                if (pseudo.getLabel().equals("Q")) {
                    if (tAtom.getSymbol().equals("C") ||
                        tAtom.getSymbol().equals("H")) {
                        return false;
                    } else {
                        return true;
                    }

                } else {
                    return true;
                }
            }
        }

        if (pseudoAtc2[idx2]) {
            if (pseudoAtc1[idx1]) {
                return true;
            } else {
                PseudoAtom pseudo = (PseudoAtom)tAtom;
                if (pseudo.getLabel().equals("Q")) {
                    if (qAtom.getSymbol().equals("C") ||
                        qAtom.getSymbol().equals("H")) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }

        //Default:
        return false;
    }

   /**
    * Indicates if the goal has been reached, that is if query has been mapped
    * to the target.
    * @return true if all nodes of the query graph r mapped to the target graph.
    */
    abstract boolean isGoal();

    /**
     * Can we do any more matches?.
     * @return true if no more matches can be obtained, otherwise false
     */
    abstract boolean isDead();

}
