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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;


/**
 * Subgraph isomorphism resolver.
 * An example usage is:
 * <pre>
 * SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 * IAtomContainer target = sp.parseSmiles("C1CCCCC1C(=O)CCOCN");
 * IAtomContainer query1 = sp.parseSmiles("C1CCCCC1");
 * IAtomContainer query2 = sp.parseSmiles("CCOCN");
 * SubgraphIsomorphism checker = new SubgraphIsomorphism(target, query1);
 *
 * if (checker.matchSingle()) {
 *   // matched
 * }
 * </pre>
 *
 * @author      rajarshi/markr
 *
 */
public class SubgraphIsomorphism {

    private IAtomContainer target = null;
    private IAtomContainer query = null;
    private VF2State vf2 = null;
    List<NodePair[]> mappings = new ArrayList<NodePair[]>();

    /**
     * Constructs class to verify if query is a subgraph of target.
     *
     * @param  target  target molecule. Must not be an IQueryAtomContainer.
     * @param  query  query molecule. May be an IQueryAtomContainer.
     * @throws org.openscience.cdk.exception.CDKException if an invalid target molecule is specified
     */
    public SubgraphIsomorphism(IAtomContainer target, IAtomContainer query) throws CDKException {
        this.target = target;
        this.query = query;        
    }

    /**
     * Get the detected mappings.
     * It only makes sense to call this method after performing a matching.
     * @return the list of mappings
     */
    public List<NodePair[]> getMappings() {
        return mappings;
    }


    private void initialize(IAtomContainer target, IAtomContainer query) {
        this.vf2 = new VF2State(target, query);
    }

    /**
     * Get the target molecule.
     * @return the target molecule
     */
    public IAtomContainer getTarget() {
        return target;
    }

    /**
     * Set the target molecule.
     * @param target the target IAtomContainer
     */
    public void setTarget(IAtomContainer target)  {
        this.target = target;
    }

    /**
     * Get the query molecule.
     * @return the query molecule
     */
    public IAtomContainer getQuery() {
        return query;
    }

    /**
     * Set the query molecule.
     * @param query the target IAtomContainer
     */
    public void setQuery(IAtomContainer query) {
        this.query = query;
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
        if (target == null || query == null) throw new CDKException("Must set target and query molecules");
        initialize(target, query);
        mappings.clear();
        return singleMatch();
    }

    /**
     * See  {@link #matchSingle()}
     * @return
     * @throws CloneNotSupportedException
     */
    private boolean singleMatch() throws CloneNotSupportedException {
        if (vf2.getTargetMolecule().getAtomCount() < vf2.getQueryMolecule().getAtomCount())
            return false;

        if (vf2.isGoal()) {
            NodePair[] pairs = vf2.getCoreSet();
            mappings.add(pairs);
            return true;
        }

        if (vf2.isDead())
            return false;

        Integer queryNodeIdx = null;
        Integer targetNodeIdx = null;
        boolean found = false;
        NodePair nodePair;
        while (!found && (nodePair = vf2.nextPair(queryNodeIdx, targetNodeIdx)) != null) {

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
