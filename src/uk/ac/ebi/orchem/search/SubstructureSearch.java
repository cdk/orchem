package uk.ac.ebi.orchem.search;

import java.io.ObjectInputStream;

import java.sql.Clob;
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
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.IsomorphismSort;
import org.openscience.cdk.isomorphism.SubgraphIsomorphism;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.nonotify.NNMolecule;
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
    private static MDLV2000Reader mdlReader = new MDLV2000Reader();


    /**
     * TODO look into shit bits. long term we should get an orchem fingerprinter without andy folding or shit bits
     * 
     * Performs a substructure search for a query molecule, using a fingerprint based
     * pre-filter query to find likely candidates, and then graph isomorphism to identify
     * and verify true substructures.
     *
     * @param queryMolecule
     * @param topN top N results after which to stop searching
     * @param debugYN set to Y to see debugging on sql prompt. 
     * 
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     * @throws Exception
     */

    private static ARRAY search(IAtomContainer queryMolecule, Integer topN, String debugYN ) throws Exception {
        long start= System.currentTimeMillis();
        int loopCount=0;
        int ignoreCount=0;

        int compTested = 0;

        /* time stamps to debug elapse time through various phases */
        long timestamp=0;
        long uitTime = 0;
        long mlTime=0;
        long prefilterTime=0;
        long clobTime=0;

        boolean debugging=false;
        if (debugYN.toLowerCase().equals("y")) 
            debugging=true;

        debug("Start",debugging);
        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();

        debug("Got connection",debugging);

        String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
        String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);
        String compoundTablePrimaryKey = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);

        conn.setDefaultRowPrefetch(50);
        conn.setReadOnly(true);
        conn.setAutoCommit (false);
        Statement stmPreFilter = null;
        ResultSet res = null;
        try {


           /**********************************************************************
            * Pre-filter query section                                           *
            **********************************************************************/
           
            // sort the atoms of the query molecule
            IAtom[] sortedAtoms = (IsomorphismSort.atomsByFrequency(queryMolecule));
            debug(sortedAtoms.length+" sorted atoms",debugging);
            queryMolecule.setAtoms(sortedAtoms);

            QueryAtomContainer qAtCom = QueryAtomContainerCreator.createBasicQueryContainer(queryMolecule);
            BitSet fingerprint = FingerPrinterAgent.FP.getFingerPrinter().getFingerprint(qAtCom);
            Map atomAndBondCounts = Utils.atomAndBondCount(qAtCom);
            debug("QueryAtomContainer made",debugging);

            /* Build up the where clause for the query using the fingerprint one bits */
            /* Condensed fingerprint for substructure search */

            String whereCondition = "";
            StringBuffer builtCondition = new StringBuffer();
            
            int bitPos=0;
            int fpCondensedSize=FingerPrinterAgent.FP.getFpCondensedSize();
            for (int i = 0; i < fpCondensedSize; i++) { 
                if ( (fingerprint.get(i) || fingerprint.get(i+fpCondensedSize))  &&!isShitBit(i) ) {
                    bitPos=i+1;
                    builtCondition.append(" and bit" + (bitPos) + "='1'");
                }
            }

            whereCondition += builtCondition.toString();
            whereCondition = whereCondition.trim();
            debug("Prefilter where condition built ",debugging);
            debug(whereCondition,debugging);

            stmPreFilter = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String preFilterQuery = 

                //"  select /*+INDEX_COMBINE(s) USE_NL(s o) USE_NL(s c)*/ " +  

                "  select " +  // which hint .. if any ... pick a btree one perhaps
                "   s.id " + 
                " , o.single_bond_count " + 
                " , o.double_bond_count " +
                " , o.triple_bond_count " + 
                " , o.aromatic_bond_count " +
                " , o.saturated_bond_count " + 
                " , o.s_count " + 
                " , o.o_count " + 
                " , o.n_count " +
                " , o.f_count " + 
                " , o.cl_count " + 
                " , o.br_count " + 
                " , o.i_count " + 
                " , o.c_count " +
                " , o.cdk_molecule "+
                " , nvl(dbms_lob.getlength(o.cdk_molecule),0) blob_length" +
                " , c."+compoundTableMolfileColumn+
                " , dbms_lob.getlength(c." + compoundTableMolfileColumn+") clob_length "+
                "  from " +
                "   orchem_fingprint_subsearch s" +
                "  ,orchem_compounds o," + 
                   compoundTableName +" c "+
                "  where " +
                "      s.id = c. " +compoundTablePrimaryKey+
                "  and s.id=o.id "; // AND .. whereCondition will be concatenated

            timestamp =System.currentTimeMillis();
            System.out.println(preFilterQuery + whereCondition);
            res = stmPreFilter.executeQuery(preFilterQuery + whereCondition);
            debug("Pre-filter query took (ms) : " + (System.currentTimeMillis() - timestamp),debugging);
            prefilterTime=System.currentTimeMillis() - start;
            
           /**********************************************************************
            * Graph validation section                                           *
            *                                                                    *
            **********************************************************************/
            List compounds = new ArrayList();
            NNMolecule databaseMolecule=null;

            debug("start loop over pre-filter results", debugging);
            Clob molFileClob = null;
            String molfile=null;

            while (res.next() && compounds.size() < topN) {
                timestamp =System.currentTimeMillis();
                try {
                    loopCount++;

                    /*****************************************
                     * Quick filter                          *
                     *****************************************/
                    if (res.getInt("single_bond_count") < (Integer)atomAndBondCounts.get(Utils.SINGLE_BOND_COUNT) ||
                        res.getInt("double_bond_count") < (Integer)atomAndBondCounts.get(Utils.DOUBLE_BOND_COUNT) ||
                        res.getInt("triple_bond_count") < (Integer)atomAndBondCounts.get(Utils.TRIPLE_BOND_COUNT) ||
                        res.getInt("aromatic_bond_count") < (Integer)atomAndBondCounts.get(Utils.AROMATIC_BOND_COUNT ) ||
                        res.getInt("saturated_bond_count") < (Integer)atomAndBondCounts.get(Utils.SATURATED_COUNT ) ||
                        res.getInt("s_count") < (Integer)atomAndBondCounts.get(Utils.S_COUNT) ||
                        res.getInt("o_count") < (Integer)atomAndBondCounts.get(Utils.O_COUNT) ||
                        res.getInt("n_count") < (Integer)atomAndBondCounts.get(Utils.N_COUNT) ||
                        res.getInt("f_count") < (Integer)atomAndBondCounts.get(Utils.F_COUNT) ||
                        res.getInt("cl_count") < (Integer)atomAndBondCounts.get(Utils.CL_COUNT) ||
                        res.getInt("br_count") < (Integer)atomAndBondCounts.get(Utils.BR_COUNT) ||
                        res.getInt("i_count") < (Integer)atomAndBondCounts.get(Utils.I_COUNT) ||
                        res.getInt("c_count") < (Integer)atomAndBondCounts.get(Utils.C_COUNT)) {
        
                        /* DO NOTHING - quick scan eliminates the candidate based on attributes */
                        ignoreCount++;

                    } else {

                        /* Create CDK molecule (either in serialized form or through molfileclob->string->molecule */
                        if (res.getInt("blob_length")!=0 )  {
                            ObjectInputStream ois = new ObjectInputStream(res.getBlob("cdk_molecule").getBinaryStream());
                            databaseMolecule = (NNMolecule)ois.readObject();
                            ois.close();
                        }
                        else {
                            if (res.getInt("clob_length")>4000 )  {
                                
                                molFileClob = res.getClob(compoundTableMolfileColumn);
                                int clobLen = new Long(molFileClob.length()).intValue();
                                molfile = (molFileClob.getSubString(1, clobLen));
                            }
                            else
                                molfile=res.getString(compoundTableMolfileColumn); 
                            databaseMolecule = Utils.getNNMolecule(mdlReader, molfile);
                        }
                        mlTime += (System.currentTimeMillis() - timestamp);
                        timestamp = System.currentTimeMillis();

                        /* Test of the match made is truly a substructure using VF2 algorithm*/
                        SubgraphIsomorphism s =
                        new SubgraphIsomorphism(databaseMolecule, qAtCom,SubgraphIsomorphism.Algorithm.VF2);

                        if (s.matchSingle()) {
                            OrChemCompound c = new OrChemCompound();
                            c.setId(res.getString("id"));
                            c.setMolFileClob(res.getClob(compoundTableMolfileColumn));
                            compounds.add(c);
                        }
                        uitTime += (System.currentTimeMillis() - timestamp);
                        compTested++;
                    }
                } catch (Exception e) {
                    debug("CDK Error - " + res.getString(compoundTablePrimaryKey) + ": " + e.getMessage(), debugging);
                }
            }

            OrChemCompound[] output = new OrChemCompound[compounds.size()];
            for (int i = 0; i < compounds.size(); i++) {
                output[i] = (OrChemCompound)(compounds.get(i));
            }
            ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor("ORCHEM_COMPOUND_LIST", conn);
            long overallTime = System.currentTimeMillis()-start;

            debug("Results list size                      :  " + compounds.size(), debugging);
            debug("Graph isomorphism tests took (ms)      :  " + uitTime, debugging);
            debug("Molecule retrieval/creation took (ms)  :  " + mlTime, debugging);
            debug("Overall time (ms)"+overallTime+" ("+(prefilterTime+clobTime+uitTime+mlTime)+")", debugging);
            debug("______",debugging);
            debug("Amount of compounds looped             : #" + loopCount, debugging);
            debug("Amount of compounds tested isomorphism : #" + compTested, debugging);
            debug("Amount of candidates ignored           : #" + ignoreCount, debugging);
            debug("______________________",debugging);
            debug("End", debugging);

            return new ARRAY(arrayDescriptor, conn, output);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw (ex);
        } finally {
            if (res != null)
                res.close();

            if (stmPreFilter != null)
                res.close();

            if (conn != null)
                conn.close();
        }
    }

    /**
     * Substructure search by Molfile
     *
     * @param mol
     * @param topN top N results after which to stop searching
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     *
     * @throws Exception
     */
    public static oracle.sql.ARRAY molSearch(String mol, Integer topN, String debugYN) throws Exception {
        IAtomContainer queryMolecule = Utils.getNNMolecule(mdlReader, mol);
        return search(queryMolecule, topN, debugYN);
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
     *
     * @param smiles
     * @param topN top N results after which to stop searching
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     *
     * @throws Exception
     */
     public static oracle.sql.ARRAY smilesSearch(String smiles, Integer topN, String debugYN) throws Exception {
         IAtomContainer queryMolecule = sp.parseSmiles(smiles);
         return search(queryMolecule, topN, debugYN);
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


    //Try out to ignore overly common bits - should collect these more uhm cleverly 
    //TODO think if this should be really implemented
    private static List shitBits = new ArrayList();
    static {
        
        shitBits.add(30);
        shitBits.add(54);
        shitBits.add(188);
        shitBits.add(147);
        shitBits.add(201);
        shitBits.add(502);
        shitBits.add(498);
        shitBits.add(484);
        shitBits.add(438 );
        
    }
    private static boolean isShitBit(int bitNum) {

        if (shitBits.contains(bitNum))
            return true;
        else
            return false;
    }


}
