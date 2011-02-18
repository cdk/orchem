/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Mark Rijnbeek
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
package uk.ac.ebi.orchem.test;

import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import uk.ac.ebi.orchem.bean.OrChemCompound;
import uk.ac.ebi.orchem.isomorphism.SubgraphIsomorphism;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.shared.WrappedAtomContainer;


/**
 * Junit test for OrChem substructure searching<P>
 *
 * Contains a long list of test methods, each for a "query compound" with the
 * number in the method is the database id (primary key) of that compound in
 * table ORCHEM_COMPOUND_SAMPLE.<P>
 * You can add more query compounds to this table by adding them to
 * the queries mol file in the unit test directory. They will get loaded by {@link LoadCompounds}.<P>
 * Target compounds are loaded from a second file with an id offset of 1000.<P>
 * Tests are done with query compounds against the target compounds <B>plus</B> the query compounds, to
 * test if the substructure search at least establishes that a query compound is a substructure of itself.
 *
 */

public class TestSubstructureSearch extends AbstractOrchemTest {

    /**
     * Tests how a substructure search using a pre-filter plus fingerprint relates to
     * a full non filtered search. These two should give exactly the same result - if 
     * not, then the fingerprinter is incorrectly filtering (false negatives)
     * 
     * @param dbId database id of query molecule in database test set
     * @param strictStereo Y or N to indicate exact stereoisomeric matching
     * @param idList limited list of IDs in database to consider; leave null if you want
     * @throws Exception
     */
    private void fingerprintVersusFullScan(int dbId, String strictStereo, List<Integer> idList) throws Exception {

        PreparedStatement pStmt = conn.prepareStatement("select id, smiles_or_mol molfile from orchem_compound_sample where id=?");
        pStmt.setInt(1,dbId);
        ResultSet res = pStmt.executeQuery();
        Clob molFileClob = null;
        String mdl = null;

        if (res.next()) {
            System.out.println("\n______________________________________________");
            System.out.println("db id is "+res.getInt("id"));
            
            molFileClob = res.getClob("molfile");
            int clobLen = new Long(molFileClob.length()).intValue();
            mdl = (molFileClob.getSubString(1, clobLen));

            /* part 1: do a substructure search using the fingerprinter */            
            System.out.println("Fingerprint substructure search:");
            List<OrChemCompound> fprintSearchResults = dbApi.substructureSearch(mdl, "MOL", conn, strictStereo,"N", idList);
            System.out.println("results # : "+fprintSearchResults.size());
            Collections.sort(fprintSearchResults);
            for (OrChemCompound oc : fprintSearchResults) {
                System.out.print(oc.getId() + " ");
            }

            /* part 2: find all substructures by doing a full scan on the data set */            
            System.out.println("\nFull isomorphism test : ");
            List<Integer> fullScanResults = fullScan(targetMolecules, strictStereo, idList, mdl);
            Collections.sort(fullScanResults);
            System.out.println("results # : "+fullScanResults.size());

            for (Integer id : fullScanResults) {
                System.out.print(id + " ");
            }
            System.out.println();

            assertEquals("Fingerprint search and full scan must have same results size list ",fprintSearchResults.size(),fullScanResults.size());
            assertTrue  ("Fingerprint search and full scan must have same result id's in list ",equalIds(fprintSearchResults,fullScanResults));
        }
        res.close();
        pStmt.close();
    }


    /**
     * Performs a complete substructure search between a query compound 
     * and target compounds. This can be used to find all substructures and 
     * validate this against the prefilter result from a fingerprinter.
     * 
     * @param targetMolecules
     * @param strictStereo
     * @param idList
     * @param mdl
     * @return
     */
    private List<Integer> fullScan
    (List<WrappedAtomContainer> targetMolecules, String strictStereo, List<Integer> idList, String mdl)
    throws SQLException, CDKException, CloneNotSupportedException {
        List<Integer> result = new ArrayList<Integer>();

        MDLV2000Reader mdlReader = new MDLV2000Reader();
        IAtomContainer queryMolecule = MoleculeCreator.getNNMolecule(mdlReader,mdl,false);
        
        MoleculeCreator.backupExplicitHcount(queryMolecule);
        queryMolecule=AtomContainerManipulator.removeHydrogens(queryMolecule);
        int[] explHydrogenCountBackup = MoleculeCreator.createExplHydrogenArray(queryMolecule);

        for (WrappedAtomContainer target : targetMolecules) {
            if (idList==null || idList.contains(target.getDbId()+"")) {
                SubgraphIsomorphism s = new SubgraphIsomorphism
                    (target.getAtomContainer(), queryMolecule, strictStereo, explHydrogenCountBackup);
                if (s.matchSingle()) {
                    result.add(target.getDbId());
                }
            }
        }
        return result;
    }

    /**
     * Helper method to assert if lists have same database Ids in them
     * @param compList
     * @param ids
     * @return 
     */
    private boolean equalIds (List<OrChemCompound> compList, List<Integer> ids) {
        for(OrChemCompound c : compList) {
            if (!ids.contains(new Integer(c.getId())))  {
                 return false;   
            }
        }
        return true;
    }


    /**
     * Substructure search testing with SMILES input.
     * @param SMILES textInput substructure
     * @param strictStereo Y or N to indicate exact stereoisomeric matching
     * @param idList limited list of IDs in database to consider; leave null if you
     * @param expectedCount expected number of results to hit in the test set
     * @throws Exception
     */
    private void smilesTest(String SMILES, String strictStereo, List<Integer> idList, int expectedCount) throws Exception {

        System.out.println("\n______________________________________________");
        System.out.println("testing SMILES "+SMILES);

        System.out.println("SMILES based substructure search:");
        List<OrChemCompound> fprintSearchResults = dbApi.substructureSearch(SMILES, "SMILES", conn, strictStereo,"N", idList);
        System.out.println("results # : "+fprintSearchResults.size());
        Collections.sort(fprintSearchResults);
        for (OrChemCompound oc : fprintSearchResults) {
            System.out.print(oc.getId() + " ");
        }
        System.out.println();
        assertEquals("Expected # of results ",fprintSearchResults.size(),expectedCount);
    }

    /*
     * Start of Junit test methods 
     */
     
    public void testCompoundID_1() throws Exception {
        fingerprintVersusFullScan(1,"N",null);
    }
    public void testCompoundID_2() throws Exception {
        fingerprintVersusFullScan(2,"N",null);
    }
    
    public void testCompoundID_3() throws Exception {
        fingerprintVersusFullScan(3,"N",null);
    }

    public void testCompoundID_4() throws Exception {
        fingerprintVersusFullScan(4,"N",null);
    }

    public void testCompoundID_5() throws Exception {
        fingerprintVersusFullScan(5,"N",null);
    }

    public void testCompoundID_6() throws Exception {
        fingerprintVersusFullScan(6,"N",null);
    }

    public void testCompoundID_7() throws Exception {
        fingerprintVersusFullScan(7,"N",null);
    }
    
    public void testCompoundID_8() throws Exception {
        fingerprintVersusFullScan(8,"N",null);
    }

    public void testCompoundID_9() throws Exception {
        fingerprintVersusFullScan(9,"N",null);
    }

    public void testCompoundID_10() throws Exception {
        fingerprintVersusFullScan(10,"N",null);
    }

    public void testCompoundID_11() throws Exception {
        fingerprintVersusFullScan(11,"N",null);
    }

    public void testCompoundID_12() throws Exception {
        fingerprintVersusFullScan(12,"N",null);
    }

    public void testCompoundID_13() throws Exception {
        fingerprintVersusFullScan(13,"N",null);
    }

    public void testCompoundID_14() throws Exception {
        fingerprintVersusFullScan(14,"N",null);
    }

    public void testCompoundID_15() throws Exception {
        fingerprintVersusFullScan(15,"N",null);
    }

    public void testCompoundID_16() throws Exception {
        fingerprintVersusFullScan(16,"N",null);
    }

    public void testCompoundID_17() throws Exception {
        fingerprintVersusFullScan(17,"N",null);
    }

    public void testCompoundID_18() throws Exception {
        fingerprintVersusFullScan(18,"N",null);
    }

    public void testCompoundID_19() throws Exception {
        fingerprintVersusFullScan(19,"N",null);
    }

    public void testCompoundID_20() throws Exception {
        fingerprintVersusFullScan(20,"N",null); 
    }

    public void testCompoundID_21() throws Exception {
        fingerprintVersusFullScan(21,"N",null);
    }

    public void testCompoundID_22() throws Exception {
        fingerprintVersusFullScan(22,"N",null);
    }
    public void testCompoundID_23() throws Exception {
        fingerprintVersusFullScan(23,"N",null);
    }

    public void testCompoundID_24() throws Exception {
        fingerprintVersusFullScan(24,"N",null);
    }

    public void testCompoundID_25() throws Exception {
        fingerprintVersusFullScan(25,"N",null);
    }

    public void testCompoundID_26() throws Exception {
        fingerprintVersusFullScan(26,"N",null);
    }

    public void testCompoundID_27() throws Exception {
        fingerprintVersusFullScan(27,"N",null);
    }

    public void testCompoundID_28() throws Exception {
        fingerprintVersusFullScan(28,"N",null);
    }

    public void testCompoundID_29() throws Exception {
        fingerprintVersusFullScan(29,"N",null);
    }

    public void testCompoundID_30() throws Exception {
        fingerprintVersusFullScan(30,"N",null);
    }
    public void testCompoundID_31() throws Exception {
        fingerprintVersusFullScan(31,"N",null);
    }

    public void testCompoundID_32() throws Exception {
        fingerprintVersusFullScan(32,"N",null);
    }
    public void testCompoundID_33() throws Exception {
        fingerprintVersusFullScan(33,"N",null);
    }
    public void testCompoundID_34() throws Exception {
        fingerprintVersusFullScan(34,"N",null);
    }
    public void testCompoundID_35() throws Exception {
        fingerprintVersusFullScan(35,"N",null);
    }
    public void testCompoundID_36() throws Exception {
        fingerprintVersusFullScan(36,"N",null);
    }
    public void testCompoundID_37() throws Exception {
        fingerprintVersusFullScan(37,"N",null);
    }
    public void testCompoundID_38() throws Exception {
        fingerprintVersusFullScan(38,"N",null);
    }
    public void testCompoundID_39() throws Exception {
        fingerprintVersusFullScan(39,"N",null);
    }
    public void testCompoundID_40() throws Exception {
        fingerprintVersusFullScan(40,"N",null);
    }

    public void testCompoundID_41() throws Exception {
        fingerprintVersusFullScan(41,"N",null);
    }
    public void testCompoundID_42() throws Exception {
        fingerprintVersusFullScan(42,"N",null);
    }
    public void testCompoundID_43() throws Exception {
        fingerprintVersusFullScan(43,"N",null);
    }

    public void testCompoundID_43_strict() throws Exception {
        fingerprintVersusFullScan(43,"Y",null);
    }

    public void testCompoundID_44() throws Exception {
        fingerprintVersusFullScan(44,"N",null);
    }

    public void testCompoundID_45() throws Exception {
        fingerprintVersusFullScan(45,"N",null);
    }

    public void testCompoundID_46() throws Exception {
        fingerprintVersusFullScan(46,"N",null);
    }

    public void testCompoundID_47() throws Exception {
        fingerprintVersusFullScan(47,"N",null);
    }

    public void testCompoundID_48() throws Exception {
        fingerprintVersusFullScan(48,"N",null);
    }

    public void testCompoundID_1_idList() throws Exception {
        System.out.println("testCompoundID_1_idList()");
        List<Integer> idList = new ArrayList<Integer>();
        idList.add(10);
        idList.add(11);
        idList.add(12);
        idList.add(13);
        idList.add(14);
        idList.add(27);
        fingerprintVersusFullScan(43,"Y",idList);
    }

    public void testBenzeneSMILES() throws Exception {
        System.out.println("testBenzeneSMILES() - limited ID list");
        List<Integer> idList = new ArrayList<Integer>();
        idList.add(10);
        idList.add(11);
        idList.add(12);
        idList.add(13);
        idList.add(14);
        idList.add(27);
        smilesTest("c1ccccc1", "Y", idList, 3);
    }

    /**
     * A test to make sure a particular bug is now fixed.
     * The bug was that the fingerprint for the given SMILES did not match
     * the target molecule labeled "SMILES bug test 1" in the unit test data set.
     * The bug was rooted in a ringLayout issue.
     * 
     * @throws Exception
     */
    public void testSMILESBug1() throws Exception {
        System.out.println("test SMILES Bug 1");
        smilesTest("C1CC2CCC3C(CCc4ccccc34)C2C1", "N", null, 2);
        
    }

    public void testSMILESExplHydrogens() throws Exception {
        System.out.println("test SMILES Explicit hydrogens missing");
        smilesTest("CP", "N", null, 6);
        smilesTest("CP[H]", "N", null, 2);
   }



    /**
     * Tests searching for a compound itself, using exact="Y".
     * This requires the Inchi function to work, as the Inchi is used to verify the result(s)
     * @throws Exception
     */
    public void testExactSearch() throws Exception {
        PreparedStatement pStmt = conn.prepareStatement
            ("select id, smiles_or_mol molfile from orchem_compound_sample " +
            " where id between 1 and 40"); // 20 and 21 have stereo on hydrogens.. 
        PreparedStatement inchiStmt = conn.prepareStatement
            ("select id, orchem_convert.molfiletoinchi(smiles_or_mol) as inchi " +
            " from orchem_compound_sample where id = ?");

        ResultSet res = pStmt.executeQuery();
        System.out.println("\n______________________________________________");
        while (res.next()) {
            String id = res.getString("id");

            //Create inchi for query
            inchiStmt.setString(1, id);
            ResultSet resInchi = inchiStmt.executeQuery();
            String inChiQuery="";
            if (resInchi.next()) {
                inChiQuery = resInchi.getString("inchi");
            }
            resInchi.close();

            System.out.print("\ndb id is "+id);
            String mdl = res.getString("molfile");
            System.out.print(" exact search (via substructure search) :");
            List<OrChemCompound> fprintSearchResults = dbApi.substructureSearch(mdl, "MOL", conn, "Y","Y", null);            
            
            for (OrChemCompound result : fprintSearchResults ) {
                if (result.getId().equals(id)) {
                    System.out.print("found self, id "+id);
                }
                else {
                    System.out.print("found id "+result.getId()+" ... check InChI's ");
                    inchiStmt.setString(1, result.getId());
                    resInchi = inchiStmt.executeQuery();
                    String inChiDbcompound="";
                    if (resInchi.next()) {
                        inChiDbcompound = resInchi.getString("inchi");
                    }
                    resInchi.close();
                    System.out.print("\n"+inChiQuery);
                    System.out.print("\n"+inChiDbcompound);
                    assertEquals("Inchi should match ",inChiQuery, inChiDbcompound);
                }
                System.out.println("");
            }
        }
        res.close();
        pStmt.close();
    }




}


