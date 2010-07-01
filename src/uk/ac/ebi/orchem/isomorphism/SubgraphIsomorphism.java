/* $Revision$
 * $Author$
 * $Date$
 *
 * Copyright (C) 2001
 *   Dipartimento di Informatica e Sistemistica,
 *   Universita degli studi di Napoli ``Federico II'
 *   <http://amalfi.dis.unina.it>
 *
 * 2008  Rajarshi Guha/Mark Rijnbeek
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

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;


/**
 * Subgraph isomorphism resolver.
 * @author      rajarshi/markr
 *
 */
public class SubgraphIsomorphism {

    private VF2State vf2 = null;
    List<NodePair[]> mappings = new ArrayList<NodePair[]>();

    /**
     * Constructs class to verify if query is a subgraph of target.
     *
     * @param  target  target molecule. 
     * @param  query  query molecule. 
     * @param  strictStereoIsomorphismYN indicates if matching should be strict with regards to stereo isometry
     * @param queryExplHydrogenCountBackup bakcup array with original explicit hydrogen counts of the (hydrogen stripped) query
     * @throws CDKException
     */
    public SubgraphIsomorphism(IAtomContainer target, IAtomContainer query, String strictStereoIsomorphismYN, int[] queryExplHydrogenCountBackup) throws CDKException {
        this.vf2 = new VF2State(target, query, strictStereoIsomorphismYN,queryExplHydrogenCountBackup);
    }

    /**
     * Overloaded constructor<br>
     * Constructs class to verify if query is a subgraph of target.
     *
     * @param  target  target molecule. 
     * @param  query  query molecule. 
     * @param queryExplHydrogenCountBackup bakcup array with original explicit hydrogen counts of the (hydrogen stripped) query
     * @throws CDKException
     */
    /**
     * 
     */
    public SubgraphIsomorphism(IAtomContainer target, IAtomContainer query,int[] queryExplHydrogenCountBackup) throws CDKException {
        this.vf2 = new VF2State(target, query, "N", queryExplHydrogenCountBackup );
    }


    /**
     * Get the detected mappings.
     * It only makes sense to call this method after performing a matching.
     * @return the list of mappings
     */
    public List<NodePair[]> getMappings() {
        return mappings;
    }



    /**
     * Check whether the query is a subgraph of the target graph.
     * This method will return after it finds the <b>first match</b>. 
     *
     * @return true if the query is a subgraph of the target
     * @throws CloneNotSupportedException if there is an internal error
     * @throws org.openscience.cdk.exception.CDKException if target or query molecules have not been set
     */
    public boolean matchSingle() throws CloneNotSupportedException, CDKException {
        mappings.clear();
        return singleMatch();
    }

    /**
     * See  {@link #matchSingle()}
     * @return
     * @throws CloneNotSupportedException
     */
    private boolean singleMatch() throws CloneNotSupportedException {
        if (vf2.getTargetContainer().getAtomCount() <
            vf2.getQueryContainer().getAtomCount())
            return false;

        if (vf2.isGoal()) {
            NodePair[] pairs = vf2.getCoreSet();

            /*
            The goal has been reached, so the query has been mapped to target, 
            therfore query is a substructure of the target.

            However, if this was an R-group query, the result could still be 
            rejected.If the RestH property is true for some atom with an R-group
            linked, then the R-group may only be substituted with a member
            of the Rgroup or with H..
            This can be verified:
                - find any atom in the query with RestH flagged
                - find the atom mapped to it in the target container
                - see if the target has more (non hydrogen) bonds than the query. 
                  if so,discard it.
            */

            IAtomContainer query = vf2.getQueryContainer();
            IAtomContainer target = vf2.getTargetContainer();

            for (int i = 0; i < query.getAtomCount(); i++) {
                IAtom queryAtom = query.getAtom(i);
                if (queryAtom.getProperty(CDKConstants.REST_H) != null &&
                    queryAtom.getProperty(CDKConstants.REST_H).equals(true)) {
                    for (NodePair pair : pairs) {
                        if (pair.getQueryNode() == i) {
                            IAtom targetAtom =
                                target.getAtom(pair.getTargetNode());

                            int qConnectivityCount=0, tConnectivityCount = 0;
                            for (IBond queryBond : query.bonds()) {
                                if (queryBond.contains(queryAtom) &&
                                    !queryBond.getConnectedAtom(queryAtom).getSymbol().equals("H"))
                                    qConnectivityCount++;
                            }
                            for (IBond targetBond : target.bonds()) {
                                if (targetBond.contains(targetAtom) &&
                                    !targetBond.getConnectedAtom(targetAtom).getSymbol().equals("H"))
                                    tConnectivityCount++;
                            }
                            if (tConnectivityCount>qConnectivityCount) {
                                //System.out.println("Rejecting based on RestH property");    
                                return false;
                            }
                        }
                    }
                }
            }
            mappings.add(pairs);
            return true;
        }

        if (vf2.isDead())
            return false;

        Integer queryNodeIdx = null;
        Integer targetNodeIdx = null;
        boolean found = false;
        NodePair nodePair;
        while (!found &&
               (nodePair = vf2.nextPair(queryNodeIdx, targetNodeIdx)) !=
               null) {

            queryNodeIdx = nodePair.getQueryNode();
            targetNodeIdx = nodePair.getTargetNode();

            // can be used to indent debugging, ignore
            //char[] chars = new char[(vf2.recursionDepth * 5)];
            //Arrays.fill(chars, ' ');

            if (vf2.isFeasiblePair(queryNodeIdx, targetNodeIdx)) {
                VF2UndoBean undo = new VF2UndoBean();
                vf2.addPair(queryNodeIdx, targetNodeIdx, undo);
                vf2.recursionDepth++;
                found = singleMatch(); // <- warning, recursive call
                vf2.recursionDepth--;
                vf2.undoAddPair(queryNodeIdx, targetNodeIdx, undo);
            }
        }
        return found;
    }


}
