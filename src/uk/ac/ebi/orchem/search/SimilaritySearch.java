package uk.ac.ebi.orchem.search;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.apache.commons.collections.buffer.PriorityBuffer;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;

import org.openscience.cdk.smiles.SmilesParser;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.bean.OrChemCompoundTanimComparator;
import uk.ac.ebi.orchem.bean.SimHeapElement;
import uk.ac.ebi.orchem.bean.SimHeapElementTanimComparator;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * Similarity search between a query molecule and the database molecules.<BR>
 * This class is loaded in the database and executed as a java stored procedure, hence the
 * proprietary things like "oracle.sql.ARRAY" and defaultConnection.
 * 
 * @author markr@ebi.ac.uk, algorithm credits to S.Joshua Swamidass and Pierre Baldi
 *
 */
public class SimilaritySearch {

    private static SmilesParser sp= new SmilesParser(DefaultChemObjectBuilder.getInstance());

    /**
     * Mama's little helper: array to quickly assess how many bits are set to one for an int between 0 and 255.
     * So on position 15 you find 4, because 15 in bit notation is 001111=4 bits. And so on.
     */
    private static final int BIT_COUNT[] =
    { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4,
      2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6,
      4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
      2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5,
      3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
      4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8 };


    /**
     * Performs a similarity search between a query molecule and the orchem fingerprint table.
     *
     * @param queryFp fingerprint of the query molecule
     * @param _cutOff tanimoto score below which to stop searching
     * @param _topN top N results after which to stop searching
     * @param debugYN
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     * @throws Exception
     */
    private static oracle.sql.ARRAY  search(BitSet queryFp, Float _cutOff, Integer _topN, String debugYN) throws Exception { 

        /*
         * 
        The comment block below describes the search algorithm.
        From:
         "Bounds and Algorithms for Fast Exact Searches of Chemical Fingerprints in Linear and Sub-Linear Time"
          S.Joshua Swamidass and Pierre Baldi
          http://dx.doi.org/10.1021/ci600358f

         Top K Hits
         ----------
         We can search for the top K hits by starting from the maximum (where A=B), and exploring discrete possible
         values of B right and left of the maximum.

         More precisely, for binary fingerprints, we first
         index the molecules in the database by their fingerprint "bit count"
         to enable efficient referencing
         of a particular bit count bin.

         Next, with respect to a particular query, we calculate the bound
         on the similarity for every bit count in the database.

         Then we sort these bit counts by their associated bound and iterate over the
         molecules in the database, in order of decreasing bound.

         As we iterate, we calculate the similarity between the query and the database molecule and use
         a heap to efficiently track the top hits. The algorithm terminates when
         "the lowest similarity value in the heap is greater than the bound associated with the current database bin"

         Algorithm 1 Top K Search
         Require: database of fingerprints binned by bit count Bs
         Ensure: hits contains top K hits which satisfy SIMILARITY( ) > T

         1:  hits <- MINHEAP()
         2:  bounds <- LIST()
         3:  for all B in database do //iterate over bins
         4:    tuple <- TUPLE(BOUND(A,B),B)
         5:    LISTAPPEND(bounds, tuple)
         6:  end for
         7:  QUICKSORT(bounds) //NOTE: the length of bounds is constant
         8:  for all bound, B in bounds do //iterate in order of decreasing bound
         9:    if bound < T then
         10:      break //threshold stopping condition
         11:   end if
         12:   if K ≤ HEAPSIZE(hits) and bound < MINSIMILARITY(hits) then
         13:     break //top-K stopping condition
         14:   end if
         15:   for all in database[B] do
         16:     S=SIMILARITY( )
         17:     tuple <- TUPLE(S, )
         18:     if S ≤ T then
         19:        continue //ignore this and continue to next
         20:     else if LENGTH(hits)< K then
         21:        HEAPPUSH(hits, tuple)
         22:     else if S > MINSIMILARITY(hits) then
         23:       HEAPPOPMIN(hits)
         24:       HEAPPUSH(hits,tuple)
         25:     end if
         26:   end for
         27: end for
         28: return hits
         */

        boolean debugging=false;
        if (debugYN.toLowerCase().equals("y")) 
            debugging=true;

        debug("started",debugging);

       /**********************************************************************
        * Similarity search algorithm section                                *
        *                                                                    *
        **********************************************************************/
        Comparator heapComparator = new SimHeapElementTanimComparator();
        PriorityBuffer heap = null;
        OracleConnection conn = null;
        PreparedStatement pstmtFp=null;
        PreparedStatement pstmLookup=null;
        
        String query = " select bit_count, id, fp from orchem_fingprint_simsearch where  bit_count = ? ";
        float cutOff= _cutOff.floatValue();
        int topN = _topN.intValue();

        try {
            conn = (OracleConnection)new OracleDriver().defaultConnection();
            String compoundTableName = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_TABLE, conn);
            String compoundTablePkColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_PK, conn);
            String compoundTableMolfileColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_MOL, conn);
            String compoundTableFormulaColumn = OrChemParameters.getParameterValue(OrChemParameters.COMPOUND_FORMULA, conn);

            conn.setDefaultRowPrefetch(100);

            float queryBitCount = queryFp.cardinality();
            byte[] queryBytes = Utils.toByteArray(queryFp, 1024);
    
            float lowBucketNum = queryBitCount - 1;
            float highBucketNum = queryBitCount + 1;
            float currBucketNum = queryBitCount;
    
            pstmtFp = conn.prepareStatement(query);
            ResultSet resFp = null;
            boolean done = false;
            byte[] compBytes = null;
            float bitsInCommon = 0;
            float tanimotoCoeff = 0f;
            heap = new PriorityBuffer(true, heapComparator);
            int bucksSearched=0;
            int loopCount=0;
    
            while (!done) {
                loopCount++;
                pstmtFp.setFloat(1, currBucketNum);
                bucksSearched++;
                resFp = pstmtFp.executeQuery();
    
                float bound = 0f;
                if (currBucketNum < queryBitCount)
                    bound = currBucketNum / queryBitCount;
                else
                    bound = queryBitCount/currBucketNum;

                /* Algorithm step 9..11
                   Here we can break out because the tanimoto score is becoming to low */
                if (bound < cutOff) {
                    debug("bound < cutOff, done",debugging);
                    done = true;
                }
                //
    
                if (!done) {
                    //Algorithm 15-26
                    while (resFp.next()) { 
    
                        bitsInCommon = 0;
                        compBytes = resFp.getBytes("fp");
                        for (int i = 0; i < compBytes.length && i < queryBytes.length; i++) {
                            int bAnd = compBytes[i] & queryBytes[i];
                            if (bAnd < 0) {
                                bAnd += 128;
                                bitsInCommon += BIT_COUNT[bAnd] + 1;
                            } else {
                                bitsInCommon += BIT_COUNT[bAnd];
                            }
                        }
                        tanimotoCoeff = bitsInCommon / (queryBitCount + currBucketNum - bitsInCommon);
                        if (tanimotoCoeff >= cutOff) {
                            SimHeapElement elm = new SimHeapElement();
                            elm.setID(resFp.getString("id"));
                            elm.setTanimotoCoeff(new Float(tanimotoCoeff));
    
                            if (heap.size() < topN) {
                                heap.add(elm);
                                
                            } else if (tanimotoCoeff > ((SimHeapElement)(heap.get())).getTanimotoCoeff().floatValue()) {
                                heap.remove();
                                heap.add(elm);
    
                            }
                        }
                    }
                    resFp.close();
                    /* Algorithm 12-14:
                     * When top N hits is reached, and the lowest score of the
                     * hits is greater than the current bucket bound, stop.
                     * If not, the next bucket may contain a better score, so go on.
                     */
                    if (heap.size() >= topN && ((SimHeapElement)(heap.get())).getTanimotoCoeff().floatValue() > bound) {
                        done = true;
                        debug("topN reached, done",debugging);

                    } else {
                        // calculate new currBucket
                        float up = queryBitCount/ highBucketNum;
                        float down = lowBucketNum/queryBitCount;
    
                        if (up > down) {
                            currBucketNum = highBucketNum;
                            highBucketNum++;
                        } else {
                            currBucketNum = lowBucketNum;
                            lowBucketNum--;
                        }
    
                        if (lowBucketNum < 1 && highBucketNum > 1024)
                            done = true;
                    }
                }
            }
            debug("searched bit_count buckets: "+loopCount,debugging);
    

           /********************************************************************
            * Search completed.                                                *
            *                                                                  *
            * Next section is just looking up the compounds by ID and          *
            * returning the results, sorted by Tanimoto coefficient            *
            *                                                                  *
            *******************************************************************/
            String lookupCompoundQuery = 
            " select " +
                  compoundTableMolfileColumn+
            " ,"+ compoundTableFormulaColumn+
            " from " +
            " " +compoundTableName+
            " where " +
            " "+compoundTablePkColumn+
            " =?";

            pstmLookup = conn.prepareStatement(lookupCompoundQuery);
            List compounds = new ArrayList();
    
            while (heap.size() != 0) {
                SimHeapElement bElm = (SimHeapElement)heap.remove();
                pstmLookup.setString(1, bElm.getID());
                ResultSet resLookup = pstmLookup.executeQuery();
                if (resLookup.next()) {
                    OrChemCompound c = new OrChemCompound();
                    c.setId(bElm.getID());
                    c.setFormula(resLookup.getString(compoundTableFormulaColumn));
                    c.setMolFileClob(resLookup.getClob(compoundTableMolfileColumn));
                    c.setScore(bElm.getTanimotoCoeff().floatValue());
                    compounds.add(c);
                }
                resLookup.close();
            }
            pstmLookup.close();
            Collections.sort(compounds, new OrChemCompoundTanimComparator());

            OrChemCompound[] output = new OrChemCompound[compounds.size()];
            for (int i = 0; i < compounds.size(); i++) {
                output[i] = (OrChemCompound) (compounds.get(i));
            }
            ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor("ORCHEM_COMPOUND_LIST", conn);
            debug("ended",debugging);
            return new ARRAY(arrayDescriptor, conn, output);
        }
        catch (Exception ex ) {
            ex.printStackTrace();
            throw (ex);
        }
        finally {
            if (pstmLookup!=null) 
                pstmLookup.close();
            if (pstmtFp!=null) 
                pstmtFp.close();
            if (conn != null)
                conn.close();
        }

    }

    /**
     * Similarity search with molfile Clob as input arg
     * @param molfileClob
     * @param cutOff
     * @param topN
     * @param debugYN
     * @return
     * @throws Exception
     */

    public static oracle.sql.ARRAY  molSearch(Clob molfileClob, Float cutOff, Integer topN,String debugYN) throws Exception {
        MDLV2000Reader mdlReader = new MDLV2000Reader();
        int clobLen = new Long(molfileClob.length()).intValue();
        String molfile = (molfileClob.getSubString(1, clobLen));
        Molecule molecule = Utils.getMolecule(mdlReader, molfile);
        BitSet fp = FingerPrinterAgent.FP.getFingerPrinter1024().getFingerprint(molecule);
        return search(fp, cutOff, topN, debugYN);
    }


    /**
     * Similarity search with molfile String as input arg
     * @param molfile
     * @param cutOff
     * @param topN
     * @param debugYN
     * @return
     * @throws Exception
     */
    public static oracle.sql.ARRAY molSearch(String molfile, Float cutOff, Integer topN, String debugYN) throws Exception {
        MDLV2000Reader mdlReader = new MDLV2000Reader();
        Molecule molecule = Utils.getMolecule(mdlReader, molfile);
        BitSet fp = FingerPrinterAgent.FP.getFingerPrinter1024().getFingerprint(molecule);
        return search(fp, cutOff, topN, debugYN);
    }

    /**
     * Similarity search by simplified molecular input line entry specification
     *
     * @param smiles string
     * @param topN top N results after which to stop searching
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     *
     * @throws Exception
     */
    public static oracle.sql.ARRAY smilesSearch(String smiles, Float cutOff, Integer topN, String debugYN) throws Exception {
        IAtomContainer molecule = sp.parseSmiles(smiles);
        BitSet fp = FingerPrinterAgent.FP.getFingerPrinter1024().getFingerprint(molecule);
        return search(fp, cutOff, topN, debugYN);
    }


    /**
     * Print debug massage to system output. To see this output in Oracle SQL*Plus
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
