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

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Subgraph isomorphism resolver.
 *
 * This class is the entry point to the different isomorphism algorithms. Currently
 * Ullman and VF2 are supported. The user need not know the internals of these algorithms
 * and should merely specify which one to use. VF2 is generally recommended.
 *
 * An example usage is
 *
 * <pre>
 * SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
 * IAtomContainer target = sp.parseSmiles("C1CCCCC1C(=O)CCOCN");
 * IAtomContainer query1 = sp.parseSmiles("C1CCCCC1");
 * IAtomContainer query2 = sp.parseSmiles("CCOCN");
 * SubgraphIsomorphism checker = new SubgraphIsomorphism(target, query1, Algorithm.VF2);
 *
 * // just check whether there is a match or not
 * if (checker.matchSingle()) {
 *   // matched
 * }
 *
 * // test for a match and get all matches
 * if (checker.matchAll()) {
 *   int nmatch = checker.getMappings().size(); // 12
 * }
 *
 * // look for another substructure in the target
 * checker.setQuery(query2);
 * if (checker.matchAll()) {
 *   int nmatch = checker.getMappings().size(); // 1
 * }
 * </pre>
 *
 * @author      rajarshi/markr
 * @cdk.keyword isomorphism
 * @cdk.license MIT-like
 * @cdk.module standard
 * @see org.openscience.cdk.isomorphism.VF2SubgraphIsomorphism
 * @see org.openscience.cdk.isomorphism.UllmanSubgraphIsomorphism
 *
 */

public class SubgraphIsomorphism {

    private IAtomContainer target = null;
    private IAtomContainer query = null;
    private Algorithm algorithm;

    List<NodePair[]> mappings = new ArrayList<NodePair[]>();
    private State state = null;

    /**
     * Choice of algorithm types for subgraph isomorphism
     */
    public enum Algorithm {
        VF2, ULLMAN
    }

    /**
     * Constructs class to verify if query is a subgraph of target.
     *
     *
     * @param  target  target molecule. Must not be an IQueryAtomContainer.
     * @param  query  query molecule. May be an IQueryAtomContainer.
     * @param algorithm  {@link org.openscience.cdk.isomorphism.SubgraphIsomorphism.Algorithm}
     * @throws org.openscience.cdk.exception.CDKException if an invalid target molecule is specified
     */
    public SubgraphIsomorphism(IAtomContainer target, IAtomContainer query, Algorithm algorithm) throws CDKException {
        if (target instanceof IQueryAtomContainer) throw new CDKException("Target molecule cannot be IQueryAtomContainer");
        this.target = target;
        this.query = query;        
        if (algorithm != Algorithm.VF2 && algorithm != Algorithm.ULLMAN) throw new RuntimeException("Algorithm undefined");
        this.algorithm = algorithm;
    }

    /**
     * Constructs class to verify if query is a subgraph of target.
     * <p/>
     * Picks default algorithm (VF2).
     *
     * @param query query molecule. Must not be an IQueryAtomContainer
     * @param target target molecule. May be an IQueryAtomContainer
     * @throws org.openscience.cdk.exception.CDKException
     *          if an invalid target molecule is specified
     */
    public SubgraphIsomorphism(IAtomContainer target, IAtomContainer query) throws CDKException {
        this(target, query, Algorithm.VF2);
    }

    /**
     * Get the detected mappings.
     *
     * It only makes sense to call this method after performing a matching.
     *
     * @return the list of mappings
     * @see #matchSingle()
     * @see #matchAll()
     */
    public List<NodePair[]> getMappings() {
        return mappings;
    }

    /**
     * Constructs class to verify if query is a subgraph of target.
     *
     * Note that this only specifies which matching algorithm to use. Before performing a match
     * you must set the target and query molecules.
     *
     * @param algorithm The {@link org.openscience.cdk.isomorphism.SubgraphIsomorphism.Algorithm}
     * @see #setTarget(org.openscience.cdk.interfaces.IAtomContainer)
     * @see #setQuery(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public SubgraphIsomorphism(Algorithm algorithm) {
        if (algorithm != Algorithm.VF2 && algorithm != Algorithm.ULLMAN) throw new RuntimeException("Algorithm undefined");
        this.algorithm = algorithm;
    }


    private void initialize(IAtomContainer target, IAtomContainer query, Algorithm algorithm) {
        if (algorithm == Algorithm.VF2) {
            this.state = new VF2SubgraphIsomorphism(target, query);
        } else if (algorithm == Algorithm.ULLMAN) {
            this.state = new UllmanSubgraphIsomorphism(target, query);
        }
    }

    /**
     * Get the target molecule.
     *
     * @return the target molecule
     * @see #setTarget(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public IAtomContainer getTarget() {
        return target;
    }

    /**
     * Set the target molecule.
     *
     * Must not be an IQueryAtomContainer
     *
     * @param target the target IAtomContainer
     * @throws org.openscience.cdk.exception.CDKException if specified molecule is an IQueryAtomContainer
     * @see #getTarget()
     */
    public void setTarget(IAtomContainer target) throws CDKException {
        if (target instanceof IQueryAtomContainer) throw new CDKException("Target cannot be IQueryAtomContainer");
        this.target = target;
    }

    /**
     * Get the query molecule.
     *
     * @return the query molecule
     * @see #setQuery(org.openscience.cdk.interfaces.IAtomContainer)
     */
    public IAtomContainer getQuery() {
        return query;
    }

    /**
     * Set the query molecule.
     *
     * May be an IQueryAtomContainer
     *
     * @param query the target IAtomContainer
     * @see #getQuery()
     */
    public void setQuery(IAtomContainer query) {
        this.query = query;
    }

    /**
     * Check whether the query is a subgraph of the target graph.
     * <BR>
     * This method will return after it finds the first match. So if you want
     * to find <b>all</b> subgraph mappings of the query on to the target use
     * {@link #matchAll()}.
     * <BR>
     * While this method is intended just for checking whether a match occurs or not
     * you can get the first match that was found via {@link #matchAll()}
     *
     * @return true if the query is a subgraph of the target
     * @throws CloneNotSupportedException if there is an internal error
     * @see #matchAll()
     * @throws org.openscience.cdk.exception.CDKException if target or query molecules have not been set
     */
    public boolean matchSingle() throws CloneNotSupportedException, CDKException {
        if (target == null || query == null) throw new CDKException("Must set target and query molecules");
        initialize(target, query, algorithm);
        
        mappings.clear();
        return singleMatch(state);
    }

    /**
     * See  {@link #matchSingle()}
     * @param state
     * @return
     * @throws CloneNotSupportedException
     */
    private boolean singleMatch(State state) throws CloneNotSupportedException {
        if (state.getTargetMolecule().getAtomCount() < state.getQueryMolecule().getAtomCount())
            return false;

        if (state.isGoal()) {
            NodePair[] pairs = state.getCoreSet();
            mappings.add(pairs);
            return true;
        }

        if (state.isDead())
            return false;

        Integer n1 = null;
        Integer n2 = null;
        boolean found = false;
        NodePair nodePair;
        while (!found && (nodePair = state.nextPair(n1, n2)) != null) {

            n1 = nodePair.getQueryNode();
            n2 = nodePair.getTargetNode();

            char[] chars = new char[(state.recursionDepth * 5)];
            Arrays.fill(chars, ' ');

            //DEBUG :
            //System.out.println(String.valueOf(chars)+n1+","+n2+"   "+
            //state.getTargetMolecule().getAtom(n2).getSymbol()+","+
            //state.getQueryMolecule().getAtom(n1).getSymbol());

            if (state.isFeasiblePair(n1, n2)) {
                State cloneState = (State)state.clone();
                cloneState.addPair(n1, n2);
                cloneState.recursionDepth++;
                found = singleMatch(cloneState);
                cloneState.recursionDepth--;
            }
        }
        return found;
    }


    /**
     * Check whether the query is a subgraph of the target graph.
     * <BR>
     * This method will identify all the unique mappings of the query
     * graph on to the target graph. The result is a List of {@link org.openscience.cdk.isomorphism.NodePair}
     * arrays. The length of the list equals the number of unique matches.
     * <BR>
     * Each NodePair array will indicate the association between the query atom and the target atom
     * that was found, for all atoms in the query graph.
     * <BR>
     * Note that this method is much slower than {@link #matchSingle()}.
     *
     * @return A List of NodePair arrays indicating the matches found
     * @throws CloneNotSupportedException if there is an internal error
     * @see org.openscience.cdk.isomorphism.NodePair
     * @see #matchSingle()
     * @throws org.openscience.cdk.exception.CDKException if target or query have not been set
     */
    public boolean matchAll() throws CloneNotSupportedException, CDKException {
        if (target == null || query == null) throw new CDKException("Must set target and query molecules");
        initialize(target, query, algorithm);

        mappings.clear();
        allMatches(state);
        return mappings.size() > 0;
    }


    /**
     * See {@link #matchAll()
     * @param state
     * @return
     * @throws CloneNotSupportedException
     */
    private boolean allMatches(State state) throws CloneNotSupportedException {
        if (state.getTargetMolecule().getAtomCount() < state.getQueryMolecule().getAtomCount())
            return false;

        if (state.isGoal()) {
            NodePair[] pairs = state.getCoreSet();
            if (!contains(mappings, pairs)) {
                mappings.add(pairs);
            }
            return false;
        }

        if (state.isDead())
            return false;

        Integer n1 = null;
        Integer n2 = null;
        NodePair nodePair;
        while ((nodePair = state.nextPair(n1, n2)) != null) {
            n1 = nodePair.getQueryNode();
            n2 = nodePair.getTargetNode();

            char[] chars = new char[(state.recursionDepth * 5)];
            Arrays.fill(chars, ' ');
            //DEBUG
            //System.out.println(String.valueOf(chars) + n1 + "," + n2);

            if (state.isFeasiblePair(n1, n2)) {
                State cloneState = (State)state.clone();
                cloneState.addPair(n1, n2);
                cloneState.recursionDepth++;

                if (allMatches(cloneState)) {
                    cloneState.backtrack();
                    cloneState.recursionDepth--;
                    return true;
                } else {
                    cloneState.backtrack();
                    cloneState.recursionDepth--;
                }

            }
        }
        return false;
    }


    /**
     * Check the current list of matches to see if the current match was already found.
     * <BR>
     * Note that since the algorithm gives us <i>atom mappings</i>, we simply look to
     * see if the collection of target atom ID's are present somewhere in the list
     * of matches.
     *
     * @param m Current list of matches
     * @param p Match to check for
     * @return true if the current match was seen previously
     */
    private boolean contains(List<NodePair[]> m, NodePair[] p) {
        for (NodePair[] pairs: m) {
            Arrays.sort(pairs);
            Arrays.sort(p);
            boolean allThere = true;
            for (int i = 0; i < p.length; i++) {
                if (p[i].x != pairs[i].x || p[i].y != pairs[i].y) {
                    allThere = false;
                    break;
                }
            }
            if (allThere)
                return true;
        }
        return false;
    }
}
