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

import java.sql.SQLException;

import java.util.Collections;
import java.util.List;

import org.openscience.cdk.exception.CDKException;

import uk.ac.ebi.orchem.bean.OrChemCompound;


public class TestSMARTSSearch extends AbstractOrchemTest {

    private void smartsQuery(String SMARTS, int expectedResults) throws CDKException, SQLException, ClassNotFoundException {
        /* part 1: do a substructure search using the fingerprinter */            
        System.out.println("\nFingerprint SMARTS search "+SMARTS);
        List<OrChemCompound> fprintSearchResults = dbApi.smartsSearch(SMARTS, conn,9999999);
        System.out.println("results # : "+fprintSearchResults.size());
        Collections.sort(fprintSearchResults);
        for (OrChemCompound oc : fprintSearchResults) {
            System.out.print(oc.getId() + " ");
        }
        System.out.println("\n_______");
        assertEquals("Smarts search expected number of results ",expectedResults,fprintSearchResults.size());
    }

    /*
     * Actual Junit test methods 
     */

    public void testSMARTS_1() throws Exception {
        smartsQuery("N1CC[O,N]CC1",112);
    }

    public void testSMARTS_2() throws Exception {
        smartsQuery("c1c[nH]nn1",1);
    }

    public void testSMARTS_3() throws Exception {
        smartsQuery("NC(CS)C(O)=O",29);
    }

    public void testSMARTS_4() throws Exception {
        smartsQuery("[H][C@@]1(CCC(C)=CC1=O)C(C)=C",3);
    }

    public void testSMARTS_5() throws Exception {
        smartsQuery("N#Cc1ccc(cc1)C(=O)c2ccc(cc2C)N3N=CC(=O)[O,N]C3(=O)",1);
    }

    public void testSMARTS_6() throws Exception {
        smartsQuery("N1ANC(=O)C1",1);
    }

    public void testSMARTS_7() throws Exception {
        smartsQuery("n1ancc1",121);
    }

}
