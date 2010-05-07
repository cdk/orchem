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

import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import uk.ac.ebi.orchem.bean.OrChemCompound;


/**
 * Junit test for OrChem substructure searching with R-groups. <P>
 */
public class TestRgroupQuery extends AbstractOrchemTest {

    /**
     * Tests RGroup queries.
     * @param rgFile Symyx RGFile
     * @param strictStereo Y/N indicates whether matching should take stereo-isomerism into account
     * @param expectedResults expected number of results for the Orchem test set database
     */
    public void performQuery(String rgFile, String strictStereo, int expectedResults) throws Exception {
        List<OrChemCompound> fprintSearchResults = dbApi.substructureSearch(rgFile, "MOL", conn, strictStereo, null);
        int numberOfResults = fprintSearchResults.size();
        System.out.println("\nresults # : " + numberOfResults);
        Collections.sort(fprintSearchResults);
        for (OrChemCompound oc : fprintSearchResults) {
            System.out.print(oc.getId() + " ");
        }
        Assert.assertEquals("Expected number of results for rgroupQuery ", numberOfResults, expectedResults);
    }

    /**
     * Test r-group query 1, restH false
     * @throws Exception
     */
    public void testRgroupQuery1() throws Exception {
        performQuery(RGROUP_QUERY_1, "N", 6);
    }

    /**
     * Test r-group query 1, restH true -> fewer results
     * @throws Exception
     */
    public void testRgroupQuery1_restH() throws Exception {
        String restHquery = RGROUP_QUERY_1;
        restHquery =restHquery.replaceFirst(
            "M  LOG  1   1   0   0", 
            "M  LOG  1   1   0   1");
        performQuery(restHquery, "N", 4);
    }


    /**
     * Test r-group query 2
     * @throws Exception
     */
    public void testRgroupQuery2() throws Exception {
        performQuery(RGROUP_QUERY_2, "N", 55);
    }

    /**
     * Test r-group query 2 - wih modified occurrence: 0,1 -> 1
     * @throws Exception
     */
    public void testRgroupQuery2_occR1_1() throws Exception {
        String modifiedQuery=RGROUP_QUERY_2.replace(
            "M  LOG  1   1   0   0   0,1",
            "M  LOG  1   1   0   0   1");
        performQuery(modifiedQuery, "N", 29);
    }


    /**
     * Test r-group query 2 - wih modified occurrences
     * @throws Exception
     */
    public void testRgroupQuery2_occR1_1_occR2_1() throws Exception {
        String modifiedQuery=RGROUP_QUERY_2.replace(
            "M  LOG  1   1   0   0   0,1",
            "M  LOG  1   1   0   0   1");
        modifiedQuery=modifiedQuery.replace(
            "M  LOG  1   2   0   0   0,1",
            "M  LOG  1   2   0   0   1");
        performQuery(modifiedQuery, "N", 26);
    }

    /**
     * Test r-group query 2 - wih modified occurrences and stereo sensitive
     * @throws Exception
     */
    public void testRgroupQuery2_occR1_1_occR2_1_stereo() throws Exception {
        String modifiedQuery=RGROUP_QUERY_2.replace(
            "M  LOG  1   1   0   0   0,1",
            "M  LOG  1   1   0   0   1");
        modifiedQuery=modifiedQuery.replace(
            "M  LOG  1   2   0   0   0,1",
            "M  LOG  1   2   0   0   1");
        performQuery(modifiedQuery, "Y", 15);
    }

    /**
     * Test r-group query 3
     * @throws Exception
     */
    public void testRgroupQuery3() throws Exception {
        performQuery(RGROUP_QUERY_3, "N", 7);
    }


    /**
     * Test r-group query 4
     * @throws Exception
     */
    public void testRgroupQuery4() throws Exception {
        performQuery(RGROUP_QUERY_4, "N", 70);
    }

    /**
     * Test r-group query 4, restH trie
     * @throws Exception
     */
    public void testRgroupQuery4_restH() throws Exception {
        String modifiedQuery=RGROUP_QUERY_4.replace(
              "M  LOG  1   4   0   0   0,>0",
              "M  LOG  1   4   0   1   0,>0");
        performQuery(modifiedQuery, "N", 4);
    }


    /**
     * Test r-group query 4, restH trie
     * @throws Exception
     */
    public void testRgroupQuery4_R1zero() throws Exception {
        String modifiedQuery=RGROUP_QUERY_4.replace(
              "0   0   >0",
              "0   0   0,>0");
        performQuery(modifiedQuery, "N", 87);
    }



    final String RGROUP_QUERY_1 = "$MDL  REV  1 0118101730\n" +
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



    final String RGROUP_QUERY_2 = 
    "$MDL  REV  1   0412101541\n"+
    "$MOL\n"+
    "$HDR\n"+
    "  Rgroup query file (RGFile)\n"+
    "  CDK    04121015412D\n"+
    "\n"+
    "$END HDR\n"+
    "$CTAB\n"+
    "  7  7  0  0  0  0  0  0  0  0999 V2000\n"+
    "    0.4525    1.2216    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    1.6661    0.3400    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    1.2025   -1.0866    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -0.2975   -1.0866    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -0.7610    0.3400    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    0.4525    2.7216    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    2.7267    1.4007    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  1  0  0  0  0 \n"+
    "  2  3  1  0  0  0  0 \n"+
    "  3  4  1  0  0  0  0 \n"+
    "  4  5  1  0  0  0  0 \n"+
    "  5  1  1  0  0  0  0 \n"+
    "  1  6  1  1  0  0  0 \n"+
    "  2  7  1  0  0  0  0 \n"+
    "M  RGP  2   6   1   7   2\n"+
    "M  LOG  1   1   0   0   0,1\n"+
    "M  LOG  1   2   0   0   0,1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$RGP\n"+
    "   1\n"+
    "$CTAB\n"+
    "  1  0  0  0  0  0  0  0  0  0999 V2000\n"+
    "   -0.7610   -3.0866    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "M  APO  1  1  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$CTAB\n"+
    "  1  0  0  0  0  0  0  0  0  0999 V2000\n"+
    "    1.2390   -3.0866    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "M  APO  1  1  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$CTAB\n"+
    "  2  1  0  0  0  0  0  0  0  0999 V2000\n"+
    "    3.2390   -4.1473    0.0000 P   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    4.2996   -3.0866    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  1  0  0  0  0 \n"+
    "M  APO  1  1  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$END RGP\n"+
    "$RGP\n"+
    "   2\n"+
    "$CTAB\n"+
    "  1  0  0  0  0  0  0  0  0  0999 V2000\n"+
    "   -0.7610   -6.1473    0.0000 Cl  0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "M  APO  1  1  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$CTAB\n"+
    "  1  0  0  0  0  0  0  0  0  0999 V2000\n"+
    "    1.2390   -6.1473    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "M  APO  1  1  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$END RGP\n"+
    "$END MOL\n";


    final String RGROUP_QUERY_3 = 
    "$MDL  REV  1   0412101715\n"+
    "$MOL\n"+
    "$HDR\n"+
    "  Rgroup query file (RGFile)\n"+
    "  CDK    04121017152D\n"+
    "\n"+
    "$END HDR\n"+
    "$CTAB\n"+
    "  7  7  0  0  0  0  0  0  0  0999 V2000\n"+
    "    3.6359    1.5749    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    4.9349    0.8249    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    4.9349   -0.6751    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    3.6359   -1.4251    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    2.3368   -0.6751    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    2.3368    0.8249    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    1.0378   -1.4251    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  1  0  0  0  0 \n"+
    "  2  3  1  0  0  0  0 \n"+
    "  3  4  1  0  0  0  0 \n"+
    "  4  5  1  0  0  0  0 \n"+
    "  5  6  1  0  0  0  0 \n"+
    "  6  1  1  0  0  0  0 \n"+
    "  5  7  1  0  0  0  0 \n"+
    "M  RGP  1   7   1\n"+
    "M  LOG  1   1   0   0   >0\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$RGP\n"+
    "   1\n"+
    "$CTAB\n"+
    "  6  6  0  0  0  0  0  0  0  0999 V2000\n"+
    "    3.0448   -3.7057    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    4.3438   -4.4557    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    4.3438   -5.9557    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    3.0448   -6.7057    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    1.7457   -5.9557    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    1.7457   -4.4557    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  2  0  0  0  0 \n"+
    "  2  3  1  0  0  0  0 \n"+
    "  3  4  2  0  0  0  0 \n"+
    "  4  5  1  0  0  0  0 \n"+
    "  5  6  2  0  0  0  0 \n"+
    "  6  1  1  0  0  0  0 \n"+
    "M  APO  1  1  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$END RGP\n"+
    "$END MOL\n";

    final String RGROUP_QUERY_4 = 
    "$MDL  REV  1   0413101426\n"+
    "$MOL\n"+
    "$HDR\n"+
    "  Rgroup query file (RGFile)\n"+
    "  CDK    04131014262D\n"+
    "\n"+
    "$END HDR\n"+
    "$CTAB\n"+
    "  8  8  0  0  0  0  0  0  0  0999 V2000\n"+
    "    1.0992    1.8384    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    2.3982    1.0884    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    2.3982   -0.4116    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    1.0992   -1.1616    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -0.1999   -0.4116    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -0.1999    1.0884    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    3.6972    1.8384    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -1.6999   -0.4116    0.0000 R#  0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  1  0  0  0  0 \n"+
    "  2  3  1  0  0  0  0 \n"+
    "  3  4  1  0  0  0  0 \n"+
    "  4  5  1  0  0  0  0 \n"+
    "  5  6  1  0  0  0  0 \n"+
    "  6  1  1  0  0  0  0 \n"+
    "  2  7  1  0  0  0  0 \n"+
    "  5  8  1  0  0  0  0 \n"+
    "M  RGP  2   7   4   8   1\n"+
    "M  LOG  1   1   0   0   >0\n"+
    "M  LOG  1   4   0   0   0,>0\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$RGP\n"+
    "   1\n"+
    "$CTAB\n"+
    "  6  6  0  0  0  0  0  0  0  0999 V2000\n"+
    "   -0.4008   -3.1616    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    0.8982   -3.9116    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    0.8982   -5.4116    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -0.4008   -6.1616    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -1.6999   -5.4116    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -1.6999   -3.9116    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  2  0  0  0  0 \n"+
    "  2  3  1  0  0  0  0 \n"+
    "  3  4  2  0  0  0  0 \n"+
    "  4  5  1  0  0  0  0 \n"+
    "  5  6  2  0  0  0  0 \n"+
    "  6  1  1  0  0  0  0 \n"+
    "M  APO  1  1  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$CTAB\n"+
    "  7  7  0  0  0  0  0  0  0  0999 V2000\n"+
    "    4.1972   -3.4722    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    5.4963   -4.2222    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    5.4963   -5.7222    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    4.1972   -6.4722    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    2.8982   -5.7222    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    2.8982   -4.2222    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    6.5569   -3.1616    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  2  0  0  0  0 \n"+
    "  2  3  1  0  0  0  0 \n"+
    "  3  4  2  0  0  0  0 \n"+
    "  4  5  1  0  0  0  0 \n"+
    "  5  6  2  0  0  0  0 \n"+
    "  6  1  1  0  0  0  0 \n"+
    "  2  7  1  0  0  0  0 \n"+
    "M  APO  1  7  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$CTAB\n"+
    "  6  6  0  0  0  0  0  0  0  0999 V2000\n"+
    "    9.8650   -3.1616    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   11.1730   -3.9168    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   11.1730   -5.4272    0.0000 N   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    9.8650   -6.1824    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    8.5569   -5.4272    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "    8.5569   -3.9168    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  2  0  0  0  0 \n"+
    "  2  3  1  0  0  0  0 \n"+
    "  3  4  2  0  0  0  0 \n"+
    "  4  5  1  0  0  0  0 \n"+
    "  5  6  2  0  0  0  0 \n"+
    "  6  1  1  0  0  0  0 \n"+
    "M  APO  1  2  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$END RGP\n"+
    "$RGP\n"+
    "   4\n"+
    "$CTAB\n"+
    "  3  2  0  0  0  0  0  0  0  0999 V2000\n"+
    "   -1.6521   -9.9722    0.0000 S   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -1.6521   -8.4722    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "   -1.6999  -11.5250    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n"+
    "  1  2  2  0  0  0  0 \n"+
    "  1  3  2  0  0  0  0 \n"+
    "M  APO  1  1  1\n"+
    "M  END\n"+
    "$END CTAB\n"+
    "$END RGP\n"+
    "$END MOL\n";

}


