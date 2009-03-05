/*  
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  OrChem project
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *
 */
package uk.ac.ebi.orchem.search;

import java.io.ObjectInputStream;

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

import oracle.sql.CLOB;

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

import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.db.OrChemParameters;
import uk.ac.ebi.orchem.shared.AtomsBondsCounter;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
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
        //OracleConnection conn = (OracleConnection)new StarliteConnection().getDbConnection();

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
            Map atomAndBondCounts = AtomsBondsCounter.atomAndBondCount(qAtCom);
            debug("QueryAtomContainer was made",debugging);

            /* Build up the where clause for the query using the fingerprint one bits */
            String whereCondition = "";
            StringBuffer builtCondition = new StringBuffer();
            
            //int bitPos=0;
            int fpSize=FingerPrinterAgent.FP.getFpSize();

            for (int i = 0; i < fpSize; i++) {
                if ((fingerprint.get(i) )){   
                    builtCondition.append(" and bit" + i + "='1'");
                }
            }

            /* Why hint? To prevent a hash join when too few distinctive bits have been set.
             * For example, when queried only for an aromatic hex ring */
            String hint="/*+ USE_NL(s,c) USE_NL(s,o) */ ";

            whereCondition += builtCondition.toString();
            whereCondition = whereCondition.trim();
            debug("Prefilter where condition built ",debugging);
            debug(whereCondition,debugging);

            stmPreFilter = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            String preFilterQuery = 
                " select   " +  hint +
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
                "  from " +
                "   orchem_fingprint_subsearch s" +
                "  ,orchem_compounds o," + 
                   compoundTableName +" c "+
                "  where " +
                "      s.id = c. " +compoundTablePrimaryKey+
                "  and s.id=o.id "; // AND .. whereCondition will be concatenated

            timestamp =System.currentTimeMillis();
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
            String molfile=null;

            while (res.next() && compounds.size() < topN) {
                timestamp =System.currentTimeMillis();
                try {
                    loopCount++;

                    /*****************************************
                     * Quick filter                          *
                     *****************************************/
                    if (res.getInt("single_bond_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.SINGLE_BOND_COUNT) ||
                        res.getInt("double_bond_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.DOUBLE_BOND_COUNT) ||
                        res.getInt("triple_bond_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.TRIPLE_BOND_COUNT) ||
                        res.getInt("aromatic_bond_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.AROMATIC_BOND_COUNT ) ||
                        res.getInt("saturated_bond_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.SATURATED_COUNT ) ||
                        res.getInt("s_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.S_COUNT) ||
                        res.getInt("o_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.O_COUNT) ||
                        res.getInt("n_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.N_COUNT) ||
                        res.getInt("f_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.F_COUNT) ||
                        res.getInt("cl_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.CL_COUNT) ||
                        res.getInt("br_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.BR_COUNT) ||
                        res.getInt("i_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.I_COUNT) ||
                        res.getInt("c_count") < (Integer)atomAndBondCounts.get(AtomsBondsCounter.C_COUNT)) {
        
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
                            molfile=res.getString(compoundTableMolfileColumn); 
                            databaseMolecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                        }
                        mlTime += (System.currentTimeMillis() - timestamp);
                        timestamp = System.currentTimeMillis();

                        /* Test if the match made is truly a substructure using VF2 algorithm*/
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
        IAtomContainer queryMolecule = MoleculeCreator.getNNMolecule(mdlReader, mol);
        OracleConnection conn = (OracleConnection)new OracleDriver().defaultConnection();
        PreparedStatement psTEMP = conn.prepareStatement("insert into testmdl values (orchem_sequence_log.nextval,?)");
        CLOB clobAll = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
        clobAll.open(CLOB.MODE_READWRITE);
        clobAll.setString(5, mol);
        psTEMP.setClob(1,clobAll);
        psTEMP.executeUpdate();
        clobAll.close();
        clobAll.freeTemporary();
        psTEMP.close();
        conn.commit();

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


}
