/* $Revision$
 * $Author$
 * $Date$
 *
 *
 * Copyright (C) 2001
 *   Dipartimento di Informatica e Sistemistica,
 *   Universita degli studi di Napoli ``Federico II'
 *   <http://amalfi.dis.unina.it>
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

package uk.ac.ebi.orchem.isomorphism;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.tools.CDKHydrogenAdder;


/**
 * Translation of C++ VF2 algprithm from VF lib.<BR>
 * http://amalfi.dis.unina.it/graph/db/vflib-2.0/doc/vflib.html<BR>
 * Further simplicfication of CDK VF version.
 *
 */
public class VF2State extends State {

    private IAtomContainer queryAtomContainer; 
    int queryAtomCount;
    Integer[] core_query;
    Integer[] in_query;
    Integer[] out_query;
    int query_both_len, query_in_len, query_out_len;
    boolean[] pseudoQueryAtomIndicator;

    private IAtomContainer targetAtomContainer; //g2
    int targetAtomCount;
    Integer[] core_target;
    Integer[] in_target;
    Integer[] out_target;
    int targ_both_len, targ_in_len, targ_out_len;
    boolean[] pseudoTargetAtomIndicator;

    int core_len, orig_core_len;
    Integer[] order;

    static Integer share_count = 1;


    /**
     * Constructor sets up a new VF2 State
     * @param target
     * @param query
     */
    public VF2State(IAtomContainer target, IAtomContainer query, String strictStereoIsomrph, int[] _explHydrogenCountBackup) {
        
        strictStereoIsomorphism = strictStereoIsomrph.equals("Y") ? true: false;
        queryExplHydrogenCountBackup= _explHydrogenCountBackup;
        queryAtomContainer = query;
        targetAtomContainer = target;
        queryAtomCount = queryAtomContainer.getAtomCount();
        targetAtomCount = targetAtomContainer.getAtomCount();

        pseudoQueryAtomIndicator = new boolean[queryAtomCount];
        for (int i = 0; i < queryAtomCount; i++) {
            if (queryAtomContainer.getAtom(i) instanceof PseudoAtom)
                pseudoQueryAtomIndicator[i]=true;
            else
                pseudoQueryAtomIndicator[i]=false;
        }

        pseudoTargetAtomIndicator = new boolean[targetAtomCount];
        for (int i = 0; i < targetAtomCount; i++) {
            if (targetAtomContainer.getAtom(i) instanceof PseudoAtom)
                pseudoTargetAtomIndicator[i]=true;
            else
                pseudoTargetAtomIndicator[i]=false;
        }

        core_len = 0;
        orig_core_len = 0;
        query_both_len = 0;
        query_in_len = 0;
        targ_out_len = 0;
        targ_both_len = 0;
        targ_in_len = 0;
        targ_out_len = 0;
        core_query = new Integer[queryAtomCount];
        core_target = new Integer[targetAtomCount];
        in_query = new Integer[queryAtomCount];
        in_target = new Integer[targetAtomCount];
        out_query = new Integer[queryAtomCount];
        out_target = new Integer[targetAtomCount];

        for (int i = 0; i < queryAtomCount; i++) {
            core_query[i] = null;
            in_query[i] = 0;
            out_query[i] = 0;
        }

        for (int i = 0; i < targetAtomCount; i++) {
            core_target[i] = null;
            in_target[i] = 0;
            out_target[i] = 0;
        }
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
        IBond bond = container.getConnectedBondsList(atom).get(whichOne);
        return container.getAtomNumber(bond.getConnectedAtom(atom));
    }

    /**
     * Try and make a mapping
     * @param queryIdx
     * @param targetIdx
     * @return a node pair if the pair defined by the provided indexes looks a valid option, null otherwise
     */
     NodePair nextPair(Integer queryIdx, Integer targetIdx) {
        if (queryIdx == null)
            queryIdx = 0;
        if (targetIdx == null)
            targetIdx = 0;
        else
            targetIdx++;

        if (query_both_len > core_len && targ_both_len > core_len) {
            while (queryIdx < queryAtomCount &&
                   (core_query[queryIdx] != null || out_query[queryIdx] == 0 || in_query[queryIdx] == 0)) {
                queryIdx++;
                targetIdx = 0;
            }
        } else if (query_out_len > core_len && targ_out_len > core_len) {
            while (queryIdx < queryAtomCount && (core_query[queryIdx] != null || out_query[queryIdx] == 0)) {
                queryIdx++;
                targetIdx = 0;
            }
        } else if (query_in_len > core_len && targ_in_len > core_len) {
            while (queryIdx < queryAtomCount && (core_query[queryIdx] != null || in_query[queryIdx] == 0)) {
                queryIdx++;
                targetIdx = 0;
            }
        } else if (queryIdx == 0 && order != null) {
            int i = 0;
            queryIdx = order[i];
            while (i < queryAtomCount && core_query[queryIdx] != null)
                i++;
            if (i == queryAtomCount)
                queryIdx = queryAtomCount;
        } else {
            while (queryIdx < queryAtomCount && core_query[queryIdx] != null) {
                queryIdx++;
                targetIdx = 0;
            }
        }

        if (query_both_len > core_len && targ_both_len > core_len) {
            while (targetIdx < targetAtomCount &&
                   (core_target[targetIdx] != null || out_target[targetIdx] == 0 || in_target[targetIdx] == 0)) {
                targetIdx++;
            }
        } else if (query_out_len > core_len && targ_out_len > core_len) {
            while (targetIdx < targetAtomCount && (core_target[targetIdx] != null || out_target[targetIdx] == 0)) {
                targetIdx++;
            }
        } else if (query_in_len > core_len && targ_in_len > core_len) {
            while (targetIdx < targetAtomCount && (core_target[targetIdx] != null || in_target[targetIdx] == 0)) {
                targetIdx++;
            }
        } else {
            while (targetIdx < targetAtomCount && core_target[targetIdx] != null) {
                targetIdx++;
            }
        }

        if (queryIdx < queryAtomCount && targetIdx < targetAtomCount)
            return new NodePair(queryIdx, targetIdx);

        return null;
    }



    /**
     * Checks whether the two nodes can be added to the state.
     *
     * @param queryNodeIdx The query node
     * @param targetNodeIdx The target node
     * @return true if (node1, node2) can be added to the current state
     */
    boolean isFeasiblePair(Integer queryNodeIdx, Integer targetNodeIdx) {

        IAtom queryAtom = queryAtomContainer.getAtom(queryNodeIdx);
        IAtom targetAtom = targetAtomContainer.getAtom(targetNodeIdx);

        // first of all compare symbols (elements), they should match 
        if (!queryAtom.getSymbol().equals(targetAtom.getSymbol()))
            return false;


        // Take query's explicit hydrogens into account
        if (queryExplHydrogenCountBackup[queryNodeIdx]!=0 && 
            !(pseudoQueryAtomIndicator[queryNodeIdx]) &&
            !(pseudoTargetAtomIndicator[targetNodeIdx])
        ) {
            int queryExplHcount = queryExplHydrogenCountBackup[queryNodeIdx];
            if (queryExplHcount>targetAtom.getImplicitHydrogenCount()) {
                //System.out.println("!! "+ queryExplHcount + " "+targetAtom.getImplicitHydrogenCount());
                return false;
            }
        }

        int termoutQuery = 0, terminQuery = 0, newQuery = 0;
        int termoutTarget = 0, terminTarget = 0, newTarget = 0;

        int i, other1, other2;

        // Check the 'out' edges of query
        for (i = 0; i < queryAtomContainer.getConnectedBondsCount(queryNodeIdx); i++) {
            other1 = getOtherAtomOfBond(queryAtomContainer, queryNodeIdx, i);

            if (core_query[other1] != null) {
                other2 = core_query[other1];
                if (!bondExists(targetAtomContainer, targetNodeIdx, other2) ||
                    !bondMatches(queryAtomContainer, targetAtomContainer, queryNodeIdx, other1, targetNodeIdx, other2,pseudoQueryAtomIndicator, pseudoTargetAtomIndicator))
                    return false;
            } else {
                if (in_query[other1] != 0)
                    terminQuery++;
                if (out_query[other1] != 0)
                    termoutQuery++;
                if (in_query[other1] == 0 && out_query[other1] == 0)
                    newQuery++;
            }
        }

        // Check the 'in' edges of query
        for (i = 0; i < queryAtomContainer.getConnectedBondsCount(queryNodeIdx); i++) {
            other1 = getOtherAtomOfBond(queryAtomContainer, queryNodeIdx, i);
            if (core_query[other1] != null) {
                other2 = core_query[other1];
                if (!bondExists(targetAtomContainer, other2, targetNodeIdx) ||
                    !bondMatches(queryAtomContainer, targetAtomContainer, queryNodeIdx, other1, other2, targetNodeIdx,pseudoQueryAtomIndicator, pseudoTargetAtomIndicator))
                    return false;
            } else {
                if (in_query[other1] != 0)
                    terminQuery++;
                if (out_query[other1] != 0)
                    termoutQuery++;
                if (in_query[other1] == 0 && out_query[other1] == 0)
                    newQuery++;
            }
        }

        // Check the 'out' edges of target
        for (i = 0; i < targetAtomContainer.getConnectedBondsCount(targetNodeIdx); i++) {
            other2 = getOtherAtomOfBond(targetAtomContainer, targetNodeIdx, i);
            if (core_target[other2] != null) { /* Do nothing */
            } else {
                if (in_target[other2] != 0)
                    terminTarget++;
                if (out_target[other2] != 0)
                    termoutTarget++;
                if (in_target[other2] == 0 && out_target[other2] == 0)
                    newTarget++;
            }
        }

        // Check the 'in' edges of target
        for (i = 0; i < targetAtomContainer.getConnectedBondsCount(targetNodeIdx); i++) {
            other2 = getOtherAtomOfBond(targetAtomContainer, targetNodeIdx, i);
            if (core_target[other2] != null) { /* Do nothing */
            } else {
                if (in_target[other2] != 0)
                    terminTarget++;
                if (out_target[other2] != 0)
                    termoutTarget++;
                if (in_target[other2] == 0 && out_target[other2] == 0)
                    newTarget++;
            }
        }
        return terminQuery <= terminTarget && termoutQuery <= termoutTarget &&
            (terminQuery + termoutQuery + newQuery) <= (terminTarget + termoutTarget + newTarget);
    }


    /**
     * Undoes the operations performed by {@link #addPair}
     * <br>
     * This restores the state so that another addPair operation can be tried.
     * It prevents cloning states as part of the VF2 recursion (the alternative
     * is to clone the state, call singleMatch with the clonde and when done, discard the clone. 
     * This involves shitloads of cloning which is expensive albeit more elegant 
     * than the undo version here)
     * 
     * @param queryNodeIdx
     * @param targetNodeIdx
     * @param undo
     */
     void undoAddPair(Integer queryNodeIdx, Integer targetNodeIdx, VF2UndoBean undo) {
        core_len--;
        if (undo.undo_in_query_at_queryNodeIdx) {
            in_query[queryNodeIdx] = 0;
        }
        if (undo.undo_out_query_at_queryNodeIdx) {
            out_query[queryNodeIdx] = 0;
        }
        if (undo.undo_in_target_at_targetNodeIdx) {
            in_target[targetNodeIdx] = 0;
        }
        if (undo.undo_out_target_at_targetNodeIdx) {
            out_target[targetNodeIdx] = 0;
        }

        query_in_len = query_in_len - undo.undo_query_in_len;
        query_out_len = query_out_len - undo.undo_query_out_len;
        query_both_len = query_both_len - undo.undo_query_both_len;

        targ_in_len = targ_in_len - undo.undo_targ_in_len;
        targ_out_len = targ_out_len - undo.undo_targ_out_len;
        targ_both_len = targ_both_len - undo.undo_targ_both_len;

        core_query[queryNodeIdx] = undo.undo_core_query_at_queryNodeIdx;
        core_target[targetNodeIdx] = undo.undo_core_target_at_targetNodeIdx;

        for (int idx : undo.in_query_to_zero) {
            in_query[idx] = 0;
        }
        for (int idx : undo.out_query_to_zero) {
            out_query[idx] = 0;
        }
        for (int idx : undo.in_target_to_zero) {
            in_target[idx] = 0;
        }
        for (int idx : undo.out_target_to_zero) {
            out_target[idx] = 0;
        }
    }


    /**
     * Adds a pair of likely candidates to the state. Can be undone by 
     * the twin operation {@link #undoAddPair}
     * @param queryNodeIdx
     * @param targetNodeIdx
     */
     void addPair(Integer queryNodeIdx, Integer targetNodeIdx, VF2UndoBean undo) {
        assert (queryNodeIdx < queryAtomCount);
        assert (targetNodeIdx < targetAtomCount);
        assert (core_len < queryAtomCount);
        assert (core_len < targetAtomCount);

        core_len++;

        if (in_query[queryNodeIdx] == 0) {
            in_query[queryNodeIdx] = core_len;
            undo.undo_in_query_at_queryNodeIdx = true;
            query_in_len++;
            undo.undo_query_in_len++;
            if (out_query[queryNodeIdx] != 0) {
                query_both_len++;
                undo.undo_query_both_len++;
            }
        }

        if (out_query[queryNodeIdx] == 0) {
            out_query[queryNodeIdx] = core_len;
            undo.undo_out_query_at_queryNodeIdx = true;
            query_out_len++;
            undo.undo_query_out_len++;
            if (in_query[queryNodeIdx] != 0) {
                query_both_len++;
                undo.undo_query_both_len++;
            }
        }

        if (in_target[targetNodeIdx] == 0) {
            in_target[targetNodeIdx] = core_len;
            undo.undo_in_target_at_targetNodeIdx=true;
            targ_in_len++;
            undo.undo_targ_in_len++;
            if (out_target[targetNodeIdx] != 0) {
                targ_both_len++;
                undo.undo_targ_both_len++;
            }
        }

        if (out_target[targetNodeIdx] == 0) {
            out_target[targetNodeIdx] = core_len;
            undo.undo_out_target_at_targetNodeIdx=true;
            targ_out_len++;
            undo.undo_targ_out_len++;
            if (in_target[targetNodeIdx] != 0) {
                targ_both_len++;
                undo.undo_targ_both_len++;
            }
        }

        undo.undo_core_query_at_queryNodeIdx=core_query[queryNodeIdx];
        undo.undo_core_target_at_targetNodeIdx=core_query[queryNodeIdx];
        core_query[queryNodeIdx] = targetNodeIdx;
        core_target[targetNodeIdx] = queryNodeIdx;

        int i, other;

        for (i = 0; i < queryAtomContainer.getConnectedBondsCount(queryNodeIdx); i++) {
            other = getOtherAtomOfBond(queryAtomContainer, queryNodeIdx, i);
            if (in_query[other] == 0) {
                undo.in_query_to_zero.add(other);
                in_query[other] = core_len;
                query_in_len++;
                undo.undo_query_in_len++;
                if (out_query[other] != 0) {
                    query_both_len++;
                    undo.undo_query_both_len++;
                }
            }
        }

        for (i = 0; i < queryAtomContainer.getConnectedBondsCount(queryNodeIdx); i++) {
            other = getOtherAtomOfBond(queryAtomContainer, queryNodeIdx, i);
            if (out_query[other] == 0) {
                undo.out_query_to_zero.add(other);
                out_query[other] = core_len;
                query_out_len++;
                undo.undo_query_out_len++;
                if (in_query[other] != 0) {
                    query_both_len++;
                    undo.undo_query_both_len++;
                }
            }
        }

        for (i = 0; i < targetAtomContainer.getConnectedBondsCount(targetNodeIdx); i++) {
            other = getOtherAtomOfBond(targetAtomContainer, targetNodeIdx, i);
            if (in_target[other] == 0) {
                undo.in_target_to_zero.add (other);
                in_target[other] = core_len;
                targ_in_len++;
                undo.undo_targ_in_len++;
                if (out_target[other] != 0) {
                    targ_both_len++;
                    undo.undo_targ_both_len++;
                }
            }
        }

        for (i = 0; i < targetAtomContainer.getConnectedBondsCount(targetNodeIdx); i++) {
            other = getOtherAtomOfBond(targetAtomContainer, targetNodeIdx, i);
            if (out_target[other] == 0) {
                out_target[other] = core_len;
                undo.out_target_to_zero.add(other);
                targ_out_len++;
                undo.undo_targ_out_len++;
                if (in_target[other] != 0) {
                    targ_both_len++;
                    undo.undo_targ_both_len++;
                }
            }
        }

    }

    /**
     * Are we done?
     * @return true if all nodes of the query graph r mapped to the target graph.
     */
     boolean isGoal() {
        return core_len == queryAtomCount;
    }

    /**
     * Can we do any more matches?.
     * @return true if no more matches can be obtained, otherwise false
     */
     boolean isDead() {
        return queryAtomCount > targetAtomCount || query_both_len > targ_both_len || query_out_len > targ_out_len ||
            query_in_len > targ_in_len;
    }


    /**
     * Getter
     * @return the query
     */
     IAtomContainer getQueryContainer() {
        return queryAtomContainer;
    }

    /**
     * Getter
     * @return the target
     */
     IAtomContainer getTargetContainer() {
        return targetAtomContainer;
    }

    /**
     * Getter
     * @return
     */
     int getCoreLength() {
        return core_len;
    }

    /**
     * Getter
     * @return
     */
     NodePair[] getCoreSet() {
        List<NodePair> pairList = new ArrayList<NodePair>();
        for (int i = 0; i < queryAtomCount; i++) {
            if (core_query[i] != null) {
                pairList.add(new NodePair(i, core_query[i]));
            }
        }
        return pairList.toArray(new NodePair[] { });
    }

}
