package uk.ac.ebi.orchem.vf2;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.matchers.IQueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.smiles.SmilesParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SubgraphIsomorphism {

    List<NodePair[]> mappings = new ArrayList<NodePair[]>();

    private State state;

    public SubgraphIsomorphism(State graphMatcher) {
        this.state = graphMatcher;
    }

    public SubgraphIsomorphism(IAtomContainer target, IQueryAtomContainer query) {
        //this.state = new UllmanSubgraphIsomorphism(query, target);
        this.state = new VF2SubgraphIsomorphism(target, query, false);
        
    }

    /**
     * Check whether the query is a subgraph of the target graph.
     * <p/>
     * This method will return after it finds the first match. So if you want
     * to find <b>all</b> subgraph mappings of the query on to the target use
     * {@link #matchAll()}.
     * <p/>
     * While this method is intended just for checking whether a match occurs or not
     * you can get the first match that was found via {@link #getMatches()}
     *
     * @return true if the query is a subgraph of the target
     * @throws CloneNotSupportedException if there is an internal error
     * @see #matchAll()
     * @see #getMatches()
     */
    public boolean matchSingle() throws CloneNotSupportedException {
        mappings.clear();
        return matches(state);
    }

    /**
     * Check whether the query is a subgraph of the target graph.
     * <p/>
     * This method will identify all the unique mappings of the query
     * graph on to the target graph. The result is a List of {@link net.guha.apps.matching.NodePair}
     * arrays. The length of the list equals the number of unique matches.
     * <p/>
     * Each NodePair array will indicate the association between the query atom and the target atom
     * that was found, for all atoms in the query graph.
     * <p/>
     * Note that this method is much slower than {@link #matchSingle()}.
     *
     * @return A List of NodePair arrays indicating the matches found
     * @throws CloneNotSupportedException if there is an internal error
     * @see net.guha.apps.matching.NodePair
     * @see #matchSingle()
     * @see #getMatches()
     */
    public boolean matchAll() throws CloneNotSupportedException {
        mappings.clear();
        allMatches(state);
        return mappings.size() > 0;
    }

    /**
     * Get the unique matche.
     *
     * @return A list of mappings from query atoms to target atoms
     * @see net.guha.apps.matching.NodePair
     */
    public List<NodePair[]> getMatches() {
        return mappings;
    }

    private boolean matches(State state) throws CloneNotSupportedException {
        if (state.getTargetMolecule().getAtomCount() < state.getQueryMolecule().getAtomCount()) return false;

        if (state.isGoal()) {
            NodePair[] pairs = state.getCoreSet();
            mappings.add(pairs);
            return true;
        }

        if (state.isDead()) return false;

        Integer n1 = null;
        Integer n2 = null;
        boolean found = false;
        NodePair nodePair;
        while (!found && (nodePair = state.nextPair(n1, n2)) != null) {

            n1 = nodePair.getQueryNode();
            n2 = nodePair.getTargetNode();

            //char[] chars = new char[(state.recursionDepth*5)];
            //Arrays.fill(chars, ' ');
            //System.out.println(String.valueOf(chars)+n1+","+n2);            

            if (state.isFeasiblePair(n1, n2)) {
                State cloneState = (State) state.clone();
                cloneState.addPair(n1, n2);
                cloneState.recursionDepth++;
                found = matches(cloneState); //<< recursive call   !
                cloneState.recursionDepth--;
            }
        }

        return found;

    }

    private boolean allMatches(State state) throws CloneNotSupportedException {
        if (state.getTargetMolecule().getAtomCount() < state.getQueryMolecule().getAtomCount()) return false;

        if (state.isGoal()) {
            NodePair[] pairs = state.getCoreSet();
            if (!contains(mappings, pairs)) mappings.add(pairs);
//            mappings.add(pairs);
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

            if (state.isFeasiblePair(n1, n2)) {
                State cloneState = (State) state.clone();
                cloneState.addPair(n1, n2);
                if (allMatches(cloneState)) {
                    cloneState.backtrack();
                    return true;
                } else {
                    cloneState.backtrack();
                }
            }
        }
        return false;
    }

    /**
     * Check the current list of matches to see if the current match was already found.
     * <p/>
     * Note that since the algorithm gives us <i>atom mappings</i>, we simply look to
     * see if the collection of target atom ID's are present somewhere in the list
     * of matches.
     *
     * @param m Current list of matches
     * @param p Match to check for
     * @return true if the current match was seen previously
     */
    private boolean contains(List<NodePair[]> m, NodePair[] p) {
        for (NodePair[] pairs : m) {

            if (pairs.length != p.length) continue;

            int[] curr = new int[pairs.length];
            int[] newp = new int[p.length];

            for (int i = 0; i < p.length; i++) {
                curr[i] = pairs[i].getTargetNode();
                newp[i] = p[i].getTargetNode();
            }
            Arrays.sort(curr);
            Arrays.sort(newp);

            boolean allThere = true;
            for (int i = 0; i < p.length; i++) {
                if (curr[i] != newp[i]) {
                    allThere = false;
                    break;
                }
            }
            if (allThere) return true;
        }
        return false;
    }

    public static void main(String[] args) throws CDKException, CloneNotSupportedException, InterruptedException {


        SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IAtomContainer target;
        QueryAtomContainer query;

//        target = sp.parseSmiles("C1CC1");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCC"), true);

//        target = sp.parseSmiles("C1CCC1");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCC1"), true);

        target = sp.parseSmiles("C1CC1CC1CC1");
        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCC"), true);

//        target = sp.parseSmiles("C12C3C4C1C5C2C3C45");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCCCCC"), true);

//        target = sp.parseSmiles("C1CCCCC1C(C)(C)(C)");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CC(C)(C)(C)"), true);
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCCCC1C(C)(C)(C)"), true);

//        target = sp.parseSmiles("C1CCCCC1C2CCCCC2");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCCCC1C2CCCCC2"), true);

//        target = sp.parseSmiles("CCCCCCCCC1CCCC(C1)CCCCCCCC");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCCCC1"), true);

//        target = sp.parseSmiles("C1(C(C(C(C(C1(F)F)(F)F)(F)F)(F)F)(F)F)(C(C(F)(F)F)(F)F)F");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCCCC1"), true);

//        target = sp.parseSmiles("C1CCC2(CC1)OCCO2");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("C1CCCC1(CC)(CC)"), true);

//        target = sp.parseSmiles("CCCC");
//        query = QueryAtomContainerCreator.createAnyAtomAnyBondContainer(sp.parseSmiles("CCC"), true);

        SubgraphIsomorphism checker = new SubgraphIsomorphism(target, query);

//        long start, end, elapsed;
//
//        start = System.currentTimeMillis();
//        for (int i = 0; i < 1000; i++) checker.matchSingle();
//        end = System.currentTimeMillis();
//        elapsed = end - start;
//        System.out.println("Ullman elapsed = " + elapsed);
//
//        start = System.currentTimeMillis();
//        for (int i = 0; i < 1000; i++) UniversalIsomorphismTester.isSubgraph(target, query);
//        end = System.currentTimeMillis();
//        elapsed = end - start;
//        System.out.println("UIT elapsed = " + elapsed);

        System.out.println("");
        System.out.println("UIT.isSubgraph(target, query) = " + UniversalIsomorphismTester.isSubgraph(target, query));
        System.out.println("checker.matches() = " + checker.matchAll() + ", nmatch = " + checker.mappings.size());

        System.out.println();
        for (NodePair[] pairs : checker.mappings) {
            for (NodePair pair : pairs) {
                System.out.print(" (" + pair.getQueryNode() + "," + pair.getTargetNode() + ") ");
            }
            System.out.println("");
        }
    }
}
