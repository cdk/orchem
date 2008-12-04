package uk.ac.ebi.orchem.search;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.IsomorphismSort;
import org.openscience.cdk.isomorphism.SubgraphIsomorphism;
import org.openscience.cdk.isomorphism.VF2SubgraphIsomorphism;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;

import org.openscience.cdk.smiles.SmilesParser;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * Substructure search between a query molecule and the database molecules.<BR>
 * This class is loaded in the database and executed as a java stored procedure, hence the
 * proprietary things like "oracle.sql.ARRAY" and defaultConnection.
 *  
 *
 * @author markr@ebi.ac.uk
 */

public class SubstructureSearch {
    
    private static SmilesParser sp= new SmilesParser(DefaultChemObjectBuilder.getInstance());

    /**
     * Performs a substructure search for a query molecule, using a fingerprint based
     * pre-filter query to find likely candidates, and then graph isomorphism to identify
     * and verify true substructures.
     *
     * @param queryMolecule
     * @param tEMP_HACK TODO remove ..
     * @param topN top N results after which to stop searching
     * @param debugYN set to Y to see debugging on sql prompt. 
     * 
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     * @throws Exception
     */

    private static ARRAY search(IAtomContainer queryMolecule, IAtomContainer tEMP_HACK, Integer topN, String debugYN ) throws Exception {
        long start= System.currentTimeMillis();
        int clobCount=0;
        int loopCount=0;
        int compTested = 0;

        /* time stamps to debug elapse time through various phases */
        long timestamp=0;
        long uitTime = 0;
        long mlTime=0;
        long prefilterTime=0;
        long queryExTime=0;
        long clobTime=0;


        boolean debugging=false;
        if (debugYN.toLowerCase().equals("y")) 
            debugging=true;

        debug("Start",debugging);
        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();
        debug("Got connection",debugging);

        conn.setDefaultRowPrefetch(50);
        conn.setReadOnly(true);
        conn.setAutoCommit (false);
        Statement stmPreFilter = null;
        PreparedStatement pstmLookup = null;
        ResultSet res = null;
        try {

            String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
            String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
            String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);
            String compoundTableFormulaColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_FORMULA, conn);

           /**********************************************************************
            * Pre-filter query section                                           *
            *                                                                    *
            **********************************************************************/
            MDLV2000Reader mdlReader = new MDLV2000Reader();

            //debug("Creating CDK query molecule",debugging);
            //Molecule queryMolecule = Utils.getMolecule(mdlReader, mol);
            //debug("Got query molecule",debugging);
            
            // sort the atoms of the query molecule
            IAtom[] sortedAtoms = (IsomorphismSort.atomsByFrequency(queryMolecule));
            debug(sortedAtoms.length+" sorted atoms",debugging);
            queryMolecule.setAtoms(sortedAtoms);

            QueryAtomContainer qAtCom = QueryAtomContainerCreator.createBasicQueryContainer(queryMolecule);
            debug("QueryAtomContainer made",debugging);

            //Molecule molForFP = Utils.getMolecule(mdlReader, mol); // double molecule = workaround for cdk1.0.4 TODO remove at one point
            BitSet fingerprint = FingerPrinterAgent.FP.getFingerPrinter512().getFingerprint(tEMP_HACK);
            Map atomAndBondCounts = Utils.atomAndBondCount(tEMP_HACK);


            /* Build up the where clause for the query using the fingerprint one bits */
            String whereCondition = "";
            StringBuffer builtCondition = new StringBuffer();
            for (int i = 0; i < fingerprint.size(); i++) {
                if (fingerprint.get(i))
                    builtCondition.append(" and bit" + (i + 1) + "='1'");
            }

            whereCondition += builtCondition.toString();
            whereCondition = whereCondition.trim();
            debug("Prefilter where condition built ",debugging);
            //debug(whereCondition,debugging);

            stmPreFilter = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            String preFilterQuery = 
                "  select /*+ INDEX_COMBINE (t) FIRST_ROWS */ " +  
                "  id " + 
                ", single_bond_count " + 
                ", double_bond_count " +
                ", triple_bond_count " + 
                ", s_count " + 
                ", o_count " + 
                ", n_count " +
                ", f_count " + 
                ", cl_count " + 
                ", br_count " + 
                ", i_count " + 
                ", c_count " +
                "  from orchem_fingprint_subsearch t " + 
                "  where  1=1 ";

            timestamp =System.currentTimeMillis();
            res = stmPreFilter.executeQuery(preFilterQuery + whereCondition);
            debug("Pre-filter query took (ms) : " + (System.currentTimeMillis() - timestamp),debugging);
            prefilterTime=System.currentTimeMillis() - start;
            
           /**********************************************************************
            * Graph validation section                                           *
            *                                                                    *
            **********************************************************************/
            Clob molFileClob = null;
            List compounds = new ArrayList();

            String lookupQuery =
                  " select c." + compoundTablePkColumn      + "," 
                + "        c." + compoundTableMolfileColumn + "," 
                + "        c." + compoundTableFormulaColumn + ","
                + " orchem.get_clob_as_char(c." + compoundTableMolfileColumn + ") mol_as_char"
                + " from   "   + compoundTableName          + " c " 
                + " where  c." + compoundTablePkColumn      + "=?";
            pstmLookup = conn.prepareStatement(lookupQuery);
            Molecule databaseMolecule=null;

            debug("start loop over pre-filter results", debugging);
            while (res.next() && compounds.size() < topN) {
                timestamp =System.currentTimeMillis();
                try {
                    loopCount++;
                    pstmLookup.setString(1, res.getString("id"));
                    ResultSet resCompound = pstmLookup.executeQuery();

                    queryExTime+=(System.currentTimeMillis() - timestamp);
                    timestamp = System.currentTimeMillis();

                    if (resCompound.next()) {

                        if (
                             res.getInt("single_bond_count") < (Integer)atomAndBondCounts.get(Utils.SINGLE_BOND_COUNT) ||
                             res.getInt("double_bond_count") < (Integer)atomAndBondCounts.get(Utils.DOUBLE_BOND_COUNT) ||
                             res.getInt("triple_bond_count") < (Integer)atomAndBondCounts.get(Utils.TRIPLE_BOND_COUNT) ||

                             res.getInt("s_count") < (Integer)atomAndBondCounts.get(Utils.S_COUNT) ||
                             res.getInt("o_count") < (Integer)atomAndBondCounts.get(Utils.O_COUNT) ||
                             res.getInt("n_count") < (Integer)atomAndBondCounts.get(Utils.N_COUNT) ||
                             res.getInt("f_count") < (Integer)atomAndBondCounts.get(Utils.F_COUNT) ||
                             res.getInt("cl_count") < (Integer)atomAndBondCounts.get(Utils.CL_COUNT) ||
                             res.getInt("br_count") < (Integer)atomAndBondCounts.get(Utils.BR_COUNT) ||
                             res.getInt("i_count") < (Integer)atomAndBondCounts.get(Utils.I_COUNT) ||
                             res.getInt("c_count") < (Integer)atomAndBondCounts.get(Utils.C_COUNT)) {

                            // DO NOTHING - quick scan eliminates the candidate based on attributes

                        } else {

                            //OLD
                            //molFileClob = resCompound.getClob(compoundTableMolfileColumn);
                            //int clobLen = new Long(molFileClob.length()).intValue();
                            //String molfile = (molFileClob.getSubString(1, clobLen));

                            /* Bit of a hack to limit explicit Clob retrievals (slow) as much as pissable */
                            String molfile = resCompound.getString("mol_as_char");
                            if (molfile == null) {
                                clobCount++;
                                molFileClob = resCompound.getClob(compoundTableMolfileColumn);
                                int clobLen = new Long(molFileClob.length()).intValue();
                                molfile = (molFileClob.getSubString(1, clobLen));
                            }


                            clobTime += (System.currentTimeMillis() - timestamp);
                            timestamp = System.currentTimeMillis();

                            /* Create molecule */
                            databaseMolecule = Utils.getNNMolecule(mdlReader, molfile);
                            mlTime += (System.currentTimeMillis() - timestamp);
                            timestamp = System.currentTimeMillis();

                            /* Test of the match made is truly a substructure */
                            SubgraphIsomorphism s =
                                new SubgraphIsomorphism(new VF2SubgraphIsomorphism(databaseMolecule, qAtCom));

                            if (s.matchSingle()) {
                                OrChemCompound c = new OrChemCompound();
                                c.setId(resCompound.getString(compoundTablePkColumn));
                                c.setFormula(resCompound.getString(compoundTableFormulaColumn));
                                c.setMolFileClob(resCompound.getClob(compoundTableMolfileColumn));
                                compounds.add(c);
                            }
                            uitTime += (System.currentTimeMillis() - timestamp);
                            compTested++;
                        }
                    }
                    resCompound.close();
                } catch (Exception e) {
                    debug ("CDK Error - " + res.getString("molregno") + ": " + e.getMessage(),debugging);
                }

            }

            OrChemCompound[] output = new OrChemCompound[compounds.size()];
            for (int i = 0; i < compounds.size(); i++) {
                output[i] = (OrChemCompound)(compounds.get(i));
            }
            ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor("ORCHEM_COMPOUND_LIST", conn);
            long overallTime = System.currentTimeMillis()-start;

            debug("Graph isomorphism tests took (ms)      :  " + uitTime, debugging);
            debug("Creating CDK molecules took (ms)       :  " + mlTime, debugging);
            debug("Compound lookup time (ms)              :  " + queryExTime, debugging);
            debug("Clob retrieval time (ms)               :  " + clobTime, debugging);
            debug("Overall time (ms)"+overallTime+" ("+(prefilterTime+queryExTime+clobTime+uitTime+mlTime)+")", debugging);
            debug("______",debugging);
            debug("Amount of compounds looped             : #" + loopCount, debugging);
            debug("Amount of compounds tested isomorphism : #" + compTested, debugging);
            debug("Amount of Clobs made                   : #" + clobCount, debugging);
            debug("______________________",debugging);
            debug("End", debugging);

            return new ARRAY(arrayDescriptor, conn, output);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw (ex);
        } finally {
            if (res != null)
                res.close();

            if (pstmLookup != null)
                pstmLookup.close();

            if (stmPreFilter != null)
                res.close();

            if (conn != null)
                conn.close();
        }
    }

    /**
     * Substructure search by Molfile
     * TODO remove 1.0.4 hack
     *
     * @param mol
     * @param topN top N results after which to stop searching
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     *
     * @throws Exception
     */
    public static oracle.sql.ARRAY molSearch(String mol, Integer topN, String debugYN) throws Exception {
        MDLV2000Reader mdlReader = new MDLV2000Reader();
        IAtomContainer queryMolecule = Utils.getMolecule(mdlReader, mol);
        IAtomContainer HACK = Utils.getMolecule(mdlReader, mol);
        return search(queryMolecule, HACK, topN, debugYN);
    }

    /**
     * Overload for {@link #molSearch(String,int,String) search}to enable a Clob
     * to be passed into the search.
     */
    public static oracle.sql.ARRAY molSearch(Clob molfileClob, Integer topN, String debugYN) throws Exception {
        int clobLen = new Long(molfileClob.length()).intValue();
        String molfile = (molfileClob.getSubString(1, clobLen));
        return molSearch(molfile, topN, debugYN);
    }

    /**
     * Substructure search by Smiles
     * TODO remove 1.0.4 hack
     *
     * @param smiles
     * @param topN top N results after which to stop searching
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     *
     * @throws Exception
     */
    public static oracle.sql.ARRAY smilesSearch(String smiles, Integer topN, String debugYN) throws Exception {
        IAtomContainer queryMolecule = sp.parseSmiles(smiles);
        IAtomContainer HACK= sp.parseSmiles(smiles);
        return search(queryMolecule, HACK, topN, debugYN);
    }

    /**
     * Print debug message to system output. To see this output in Oracle SQL*Plus
     * use 'set severout on' and 'exec dbms_java.set_output(50000)'
     *
     * @param debugMessage
     * @param debug
     */
    private static void debug(String debugMessage, boolean debug) {
        if (debug) {
            System.out.println(new java.util.Date() + " debug: " + debugMessage);
        }
    }

}
