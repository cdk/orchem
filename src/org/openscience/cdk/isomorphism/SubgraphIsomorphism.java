package org.openscience.cdk.isomorphism;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Subgraph isomorphism resolver
 * 
 */
public class SubgraphIsomorphism {

    List<NodePair[]> mappings = new ArrayList<NodePair[]>();

    private State state;

    public SubgraphIsomorphism(State graphMatcher) {
        this.state = graphMatcher;
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
     */
    public boolean matchSingle() throws CloneNotSupportedException {
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
            
            //if (n1==2 && n2==11)  {
            //        System.out.println("BREAK");
            //}


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
     */
    public boolean matchAll() throws CloneNotSupportedException {
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
            System.out.println(String.valueOf(chars) + n1 + "," + n2);

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
