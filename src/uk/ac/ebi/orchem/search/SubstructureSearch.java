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

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;
import uk.ac.ebi.orchem.vf2.IsomorphismSort;
import uk.ac.ebi.orchem.vf2.SubgraphIsomorphism;

/**
 * Substructure search between a query molecule and the database molecules.<BR>
 * This class is loaded in the database and executed as a java stored procedure, hence the
 * properietary return type oracle.sql.ARRAY and such.
 * 
 * @author markr@ebi.ac.uk
 */

public class SubstructureSearch {

    
    /**
     * Performs a substructure search for a query molecule, using a fingerprint based
     * pre-filter query to find likely candidates, and then graph isomorphism to identify
     * and verify true substructures.
     *
     * @param mol MDL Mol file
     * @param topN top N results after which to stop searching
     * @param debugYN set to Y to see debugging on sql prompt. 
     * 
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     * @throws Exception
     */
    public static ARRAY search(String mol, Integer topN, String debugYN ) throws Exception {
        boolean debugging=false;

        if (debugYN.toLowerCase().equals("y")) 
            debugging=true;

        debug("started",debugging);
        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();
        debug("got connection",debugging);

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
            debug("creating CDK Molecule",debugging);

            Molecule queryMolecule = Utils.getMolecule(mdlReader, mol);
            debug("got Molecule",debugging);
            
            // sort the atoms of the query molecule
            IAtom[] sortedAtoms = (IsomorphismSort.atomsByFrequency(queryMolecule));
            debug(sortedAtoms.length+" sorted atoms",debugging);

            QueryAtomContainer qAtCom = QueryAtomContainerCreator.createBasicQueryContainer(queryMolecule);
            debug("QueryAtomContainer made",debugging);

            Molecule molForFP = Utils.getMolecule(mdlReader, mol); // double molecule = workaround for cdk1.0.4 TODO remove at one point
            BitSet fingerprint = FingerPrinterAgent.FP.getFingerPrinter512().getFingerprint(molForFP);
            Map atomAndBondCounts = Utils.atomAndBondCount(molForFP);

            String whereCondition = "";
            StringBuffer builtCondition = new StringBuffer();
            for (int i = 0; i < fingerprint.size(); i++) {
                if (fingerprint.get(i))
                    builtCondition.append(" and bit" + (i + 1) + "='1'");
            }

            whereCondition += builtCondition.toString();
            whereCondition = whereCondition.trim();
            debug("prefilter where condition built ",debugging);

            long now = System.currentTimeMillis();
            stmPreFilter = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            String preFilterQuery = 
                "  select /*+ INDEX_COMBINE (t) */ " +  
                "  id " + 
                ", single_bond_count " + 
                ", double_bond_count " +
                ", triple_bond_count " + 
                ", aromatic_bond_count " + 
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
            res = stmPreFilter.executeQuery(preFilterQuery + whereCondition);
            debug("pre-filter query took milliseconds: " + (System.currentTimeMillis() - now),debugging);


           /**********************************************************************
            * Graph validation section                                           *
            *                                                                    *
            **********************************************************************/
            Clob molFileClob = null;
            List compounds = new ArrayList();
            long uitTime = 0;
            int compTested = 0;
            String lookupQuery =
                " select c."   + compoundTablePkColumn +  ", " 
                + "        c." + compoundTableMolfileColumn + ", " 
                + "        c." + compoundTableFormulaColumn + 
                " from     "   + compoundTableName + " c " + 
                " where  c."   + compoundTablePkColumn + "=?";
            pstmLookup = conn.prepareStatement(lookupQuery);

            while (res.next() && compounds.size() < topN) {

                try {
                    pstmLookup.setString(1, res.getString("id"));
                    ResultSet resCompound = pstmLookup.executeQuery();

                    if (resCompound.next()) {

                        if (res.getInt("single_bond_count") < (Integer)atomAndBondCounts.get(Utils.SINGLE_BOND_COUNT) ||
                            res.getInt("double_bond_count") < (Integer)atomAndBondCounts.get(Utils.DOUBLE_BOND_COUNT) ||
                            res.getInt("triple_bond_count") < (Integer)atomAndBondCounts.get(Utils.TRIPLE_BOND_COUNT) ||
                            res.getInt("aromatic_bond_count") < (Integer)atomAndBondCounts.get(Utils.AROM_BOND_COUNT) ||
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
                            molFileClob = resCompound.getClob(compoundTableMolfileColumn);
                            int clobLen = new Long(molFileClob.length()).intValue();
                            String molfile = (molFileClob.getSubString(1, clobLen));

                            if (molfile != null) {
                                /* Create molecule */
                                Molecule databaseMolecule = Utils.getMolecule(mdlReader, molfile);

                                /* Test of the match made is truly a substructure */
                                now = System.currentTimeMillis();

                                //SubgraphIsomorphism s = new SubgraphIsomorphism(databaseMolecule, QueryAtomContainerCreator.createBasicQueryContainer(queryMolecule));
                                SubgraphIsomorphism s = new SubgraphIsomorphism(databaseMolecule, qAtCom);

                                if (s.matchSingle()) {
                                    OrChemCompound c = new OrChemCompound();
                                    c.setId(resCompound.getString(compoundTablePkColumn));
                                    c.setFormula(resCompound.getString(compoundTableFormulaColumn));
                                    c.setMolFileClob(molFileClob);
                                    compounds.add(c);
                                }
                                uitTime += (System.currentTimeMillis() - now);
                                compTested++;
                            }
                        }
                    }
                    resCompound.close();
                } catch (Exception e) {
                    System.out.println("CDK Error - " + res.getString("molregno") + ": " + e.getMessage());
                }
            }
            debug("CDK UniversalIsomorphismTester took milliseconds: " + uitTime,debugging);
            debug("amount of compounds through UIT: " + compTested,debugging);

            OrChemCompound[] output = new OrChemCompound[compounds.size()];
            for (int i = 0; i < compounds.size(); i++) {
                output[i] = (OrChemCompound)(compounds.get(i));
            }
            ArrayDescriptor arrayDescriptor = ArrayDescriptor.createDescriptor("ORCHEM_COMPOUND_LIST", conn);

            debug("ended",debugging);
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
     * Overload for {@link #search(String,int,String) search}to enable a Clob
     * to be passed into the search.
     *
     * @param molfileClob
     * @param topN top N results after which to stop searching
     * @return array of {@link uk.ac.ebi.orchem.bean.OrChemCompound compounds}
     *
     * @throws Exception
     */
    public static oracle.sql.ARRAY search(Clob molfileClob, Integer topN, String debugYN) throws Exception {
        int clobLen = new Long(molfileClob.length()).intValue();
        String molfile = (molfileClob.getSubString(1, clobLen));
        return search(molfile, topN, debugYN);
    }

    /**
     * Print debug massage to system output. To see this output in Oracle SQL*Plus 
     * use 'set severout on' and 'exec dbms_java.set_output(50000)'
     * 
     * @param debugMessage
     * @param debug
     */
    private static void debug (String debugMessage, boolean debug) {
        if (debug)  {
            System.out.println(new java.util.Date()+" debug: "+debugMessage);
        }
    }

}
