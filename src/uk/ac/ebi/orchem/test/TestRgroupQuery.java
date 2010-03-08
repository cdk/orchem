/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2010  Mark Rijnbeek
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

import junit.framework.Assert;

import org.openscience.cdk.exception.CDKException;

import uk.ac.ebi.orchem.bean.OrChemCompound;

/**
 * Junit test for OrChem substructure searching with R-groups. <P>
 */
public class TestRgroupQuery extends AbstractOrchemTest {

    /**
     * Tests an r-group query.
     * @param rgFile Symyx RGFile
     * @param strictStereo Y/N indicates whether matching should take stereo-isomerism into account
     * @param expectedResults expected number of results for the Orchem test set database
     */
    public void performQuery(String rgFile, String strictStereo,
                             int expectedResults) throws SQLException,
                                                         CDKException,
                                                         ClassNotFoundException {

        List<OrChemCompound> fprintSearchResults =
            dbApi.substructureSearch(rgFile, "MOL", conn, strictStereo, null);
        int numberOfResults = fprintSearchResults.size();
        System.out.println("results # : " + numberOfResults);
        Collections.sort(fprintSearchResults);
        for (OrChemCompound oc : fprintSearchResults) {
            System.out.print(oc.getId() + " ");
        }
        Assert.assertEquals("Expected number of results for rgroupQuery ",
                            numberOfResults, expectedResults);
    }

    /**
     * Test r-group query, restH false
     * @throws Exception
     */
    public void testRgroupQuery1() throws Exception {
        performQuery(RGROUP_QUERY_1, "N", 6);
    }

    /**
     * Test r-group query, restH true -> fewer results
     * @throws Exception
     */
    public void testRgroupQuery1_restH() throws Exception {
        String restHquery = RGROUP_QUERY_1;
        restHquery=restHquery.replaceFirst("M  LOG  1   1   0   0","M  LOG  1   1   0   1");
        System.out.println(restHquery);
        performQuery(restHquery, "N", 4);
    }

    private final String RGROUP_QUERY_1 = "$MDL  REV  1 0118101730\n" +
        "$MOL\n" +
        "$HDR\n" +
        "  RGroup query unit test: simple query test.\n" +
        "  Marvin  01181017302D          \n" +
        "\n" +
        "$END HDR\n" +
        "$CTAB\n" +
        "  7  7  0  0  0  0            999 V2000\n" +
        "    4.5003    0.0208    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    4.5003   -0.8043    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    5.2124   -1.2126    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    5.9244   -0.8043    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    5.9244    0.0208    0.0000 P   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    5.2124    0.4375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    6.9938    1.3152    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "  5  7  1  0  0  0  0\n" +
        "  1  2  1  0  0  0  0\n" +
        "  1  6  1  0  0  0  0\n" +
        "  2  3  1  0  0  0  0\n" +
        "  3  4  1  0  0  0  0\n" +
        "  4  5  1  0  0  0  0\n" +
        "  5  6  1  0  0  0  0\n" +
        "M  LOG  1   1   0   0   0,1-3\n" +
        "M  RGP  1   7   1\n" +
        "M  END\n" +
        "$END CTAB\n" +
        "$RGP\n" +
        "  1\n" +
        "$CTAB\n" +
        "  2  1  0  0  0  0            999 V2000\n" +
        "    3.5545   -5.0670    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    4.2689   -4.6544    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "  1  2  1  0  0  0  0\n" +
        "M  APO  1   2   1\n" +
        "M  END\n" +
        "$END CTAB\n" +
        "$CTAB\n" +
        "  3  2  0  0  0  0            999 V2000\n" +
        "    1.4874   -4.0503    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    2.2019   -3.6378    0.0000 Br  0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "    1.4874   -4.8753    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n" +
        "  1  2  1  0  0  0  0\n" +
        "  1  3  1  0  0  0  0\n" +
        "M  APO  1   3   1\n" +
        "M  END\n" +
        "$END CTAB\n" +
        "$CTAB\n"+
        "  2  1  0  0  0  0            999 V2000\n"+
        "    1.4874   -4.8753    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"+
        "    1.7009   -4.0784    0.0000 Br  0  0  0  0  0  0  0  0  0  0  0  0\n"+
        "  1  2  1  0  0  0  0\n"+
        "M  APO  1   1   1\n"+
        "M  END\n"+
        "$END CTAB\n" +
        "$END RGP\n" +
        "$END MOL\n";
}

