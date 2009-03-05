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
package uk.ac.ebi.orchem.fingerprint.bitpos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IBond;


/**
 * Holds maps with fingerprint information used to determine which bit number 
 * to set for which chemical aspect. Access to this class should be done through 
 * singleton {@link BitPosApi}<BR><BR>
 * 
 * Several maps are set up, each containing a pair (information,bit position).<BR>
 * For example, the map elementCntBits holds data related to element counts. It may have
 * an entry (C32,n) with n being some number - this data will be used by {@link
 * uk.ac.ebi.orchem.fingerprint.OrchemFingerprinter} to determine which bit position (n) to set
 * when a compound has 32 or more Carbon atoms.<BR>
 * And so on.. some maps are more elaborate than others but should have reasonably self-explaining
 * content.
 *
 * @author markr@ebi.ac.uk
 */
public class BitPositions {

    /* Map for fingerprinting element counts */
    public final Map<String, Integer> elemCntBits = new HashMap<String, Integer>();

    /* Map for atom pair fingerprinting */
    public final Map<String, Integer> atomPairBits = new HashMap<String, Integer>();

    /* Map for element neighbour fingerprinting */
    public final Map<Integer, List<Neighbour>> neighbourBits = new HashMap<Integer, List<Neighbour>>();

    /* Map for smarts pattern fingerprinting */
    public final Map<String, Integer> smartsPatternBits = new HashMap<String, Integer>();

    /* Map for fingerprinting ringsets with multiple (!) rings */
    public final Map<String, Integer> ringSetBits = new HashMap<String, Integer>();

    /* Map for fingerprinting ring aspects */
    public final Map<String, Integer> ringBits = new HashMap<String, Integer>();

    /* Map for fingerprinting carbon trails */
    public final Map<String, Integer> carbonTrails = new HashMap<String, Integer>();
    public final int MAX_CC_TRAIL_DEPTH=20;


    /* Reserved prefix strings for readable labeling of bit position dump */
    public final String ringsetCountTotalPrefix = new String("RsTot");
    public final String ringsetCountPerSet = new String("RsSet");
    public final String ringsetPairPrefix = new String("RsPair");
    public final String ringsetCountConnectedPrefix = new String("RsNeigh");
    public final String ringsetCountHexPrefix = new String("RsHex");
    public final String ringsetCountPentPrefix = new String("RsPent");
    public final String ringPrefixRing = new String("ring_");
    public final String ringPrefixAny = new String("any_");
    public final String ringPrefixArom = new String("arom_");
    public final String ringPrefixNonArom = new String("nonArom_");
    public final String ringPrefixNitro = new String("N_");
    public final String ringPrefixHetero = new String("Het_");
    public final String ringPrefixCarbonOnly = new String("CarbOnly_");

    public final String cTrailPrefix = new String("c-c_Trail");

    public final String hexRingAromNeighPrefix = new String("hexRingAromNeigh_");
    public final String hexRingNonAromNeighPrefix = new String("hexRingNonAromNeigh_");
    public final String hexRingMixNeighPrefix = new String("hexRingMixAromNeigh_");

    public final String ringNonaromDoubleBond = new String("ringDoubleBondNonArom_");


    /**
     * Constructor sets up the maps with fingerprinting data.
     */
    BitPositions () {
    // Commented out - bits that are very common
    //94%        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "1",        512   );
    //88%        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "1",        513   );
    //83%        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",        516   );
    //80%        neighbourBits.put(                                                                         144   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    //80%        neighbourBits.put(                                                                         145   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    //77%        neighbourBits.put(                                                                         146   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    //77%        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "2",        521   );


    neighbourBits.put(                                                                          0    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "2",         1    );
    elemCntBits.put("O3" ,                                                                      2    );
    elemCntBits.put("C20",                                                                      3    );
    ringSetBits.put(ringsetCountTotalPrefix+"1"     ,                                           4    );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "1",         5    );
    elemCntBits.put("N3" ,                                                                      6    );
    smartsPatternBits.put("C-C-N-C-C"   ,                                                       7    );
    neighbourBits.put(                                                                          8    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",         9    );
    smartsPatternBits.put("C:C-C-C"     ,                                                      10    );
    smartsPatternBits.put("C-C-C-C-C"   ,                                                      11    );
    neighbourBits.put(                                                                         12    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                         13    , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "3",        14    );
    smartsPatternBits.put("N-C-C-C-C"   ,                                                      15    );
    neighbourBits.put(                                                                         16    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));
    neighbourBits.put(                                                                         17    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O=C-C-C"     ,                                                      18    );
    neighbourBits.put(                                                                         19    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));
    neighbourBits.put(                                                                         20    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                ));
    neighbourBits.put(                                                                         21    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                                                ));
    neighbourBits.put(                                                                         22    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));
    smartsPatternBits.put("O=C-N-C-C"   ,                                                      23    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "1",        24    );
    neighbourBits.put(                                                                         25    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                                               ));
    neighbourBits.put(                                                                         26    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true)}                                                                               ));
    elemCntBits.put("C24",                                                                     27    );
    elemCntBits.put("N4" ,                                                                     28    );
    neighbourBits.put(                                                                         29    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                ));
    neighbourBits.put(                                                                         30    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                         31    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    smartsPatternBits.put("C-C-C-C:C"   ,                                                      32    );
    smartsPatternBits.put("O=C-C-C-C"   ,                                                      33    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "1",        34    );
    neighbourBits.put(                                                                         35    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("O-C-C-C-C"   ,                                                      36    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",        37    );
    smartsPatternBits.put("N-C-C:C"     ,                                                      38    );
    neighbourBits.put(                                                                         39    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                  ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "3",        40    );
    ringSetBits.put(ringsetPairPrefix+"5_6"         ,                                          41    );
    neighbourBits.put(                                                                         42    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",        43    );
    elemCntBits.put("O5" ,                                                                     44    );
    neighbourBits.put(                                                                         45    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("C-N-C:C"     ,                                                      46    );
    smartsPatternBits.put("C:C-O-C"     ,                                                      47    );
    smartsPatternBits.put("C-N-C-C:C"   ,                                                      48    );
    smartsPatternBits.put("N-C-C-N"     ,                                                      49    );
    elemCntBits.put("S1" ,                                                                     50    );
    smartsPatternBits.put("N-C-C-N-C"   ,                                                      51    );
    smartsPatternBits.put("O=C-C:C"     ,                                                      52    );
    smartsPatternBits.put("O-C-C-N"     ,                                                      53    );
    neighbourBits.put(                                                                         54    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true)}                                                                               ));
    neighbourBits.put(                                                                         55    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "1",        56    );
    ringSetBits.put(hexRingMixNeighPrefix+"1",                                                 57    );
    smartsPatternBits.put("N-C-C-C:C"   ,                                                      58    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",        59    );
    elemCntBits.put("N5" ,                                                                     60    );
    smartsPatternBits.put("O=C-C-N"     ,                                                      61    );
    elemCntBits.put("C28",                                                                     62    );
    smartsPatternBits.put("O-C-C-N-C"   ,                                                      63    );
    smartsPatternBits.put("C-C:N:C"     ,                                                      64    );
    smartsPatternBits.put("N:C:C-C"     ,                                                      65    );
    neighbourBits.put(                                                                         66    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                  ));
    smartsPatternBits.put("O=C-C-N-C"   ,                                                      67    );
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "4",        68    );
    smartsPatternBits.put("C-C=C-C"     ,                                                      69    );
    neighbourBits.put(                                                                         70    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,false)}                                                                              ));
    neighbourBits.put(                                                                         71    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));
    neighbourBits.put(                                                                         72    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                   ));
    smartsPatternBits.put("C-C-O-C-C"   ,                                                      73    );
    elemCntBits.put("Cl1",                                                                     74    );
    ringSetBits.put(ringsetCountPerSet+"3"          ,                                          75    );
    ringSetBits.put(ringsetCountConnectedPrefix+"2" ,                                          76    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3",        77    );
    smartsPatternBits.put("N:C:N:C"     ,                                                      78    );
    ringBits.put(ringNonaromDoubleBond+"1_1",                                                  79    );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "2",        80    );
    smartsPatternBits.put("C-C-C=C"     ,                                                      81    );
    neighbourBits.put(                                                                         82    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));
    smartsPatternBits.put("O-C-C:C"     ,                                                      83    );
    smartsPatternBits.put("O-C-C-C-N"   ,                                                      84    );
    smartsPatternBits.put("C-C-O-C:C"   ,                                                      85    );
    elemCntBits.put("F1" ,                                                                     86    );
    neighbourBits.put(                                                                         87    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true)}                                                                               ));
    neighbourBits.put(                                                                         88    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("Cl",null,false)}                                                                              ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",        89    );
    smartsPatternBits.put("O=C-C=C"     ,                                                      90    );
    smartsPatternBits.put("C-C:C:C-C"   ,                                                      91    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",        92    );
    elemCntBits.put("O7" ,                                                                     93    );
    smartsPatternBits.put("C-C-C:C-C"   ,                                                      94    );
    neighbourBits.put(                                                                         95    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O-C:C:C-C"   ,                                                      96    );
    elemCntBits.put("C32",                                                                     97    );
    smartsPatternBits.put("C-C-C=C-C"   ,                                                      98    );
    neighbourBits.put(                                                                         99    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                                               ));
    neighbourBits.put(                                                                        100    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("N-C:C-C"     ,                                                     101    );
    elemCntBits.put("N6" ,                                                                    102    );
    smartsPatternBits.put("O-C:C-C"     ,                                                     103    );
    smartsPatternBits.put("N:C:C:N"     ,                                                     104    );
    smartsPatternBits.put("O-C-C-O"     ,                                                     105    );
    smartsPatternBits.put("C:C-C:C"     ,                                                     106    );
    smartsPatternBits.put("N-C:N:C"     ,                                                     107    );
    smartsPatternBits.put("N-C-N-C"     ,                                                     108    );
    atomPairBits.put("N-N",                                                                   109    );
    atomPairBits.put("S-O",                                                                   110    );
    neighbourBits.put(                                                                        111    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    smartsPatternBits.put("N-C-C-O-C"   ,                                                     112    );
    smartsPatternBits.put("O=C-C=C-C"   ,                                                     113    );
    smartsPatternBits.put("N-C-C-C-N"   ,                                                     114    );
    smartsPatternBits.put("O-C-C-O-C"   ,                                                     115    );
    neighbourBits.put(                                                                        116    , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    neighbourBits.put(                                                                        117    , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                   ));
    neighbourBits.put(                                                                        118    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("S",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",       119    );
    neighbourBits.put(                                                                        120    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("C",null,true)}                                                                               ));
    neighbourBits.put(                                                                        121    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                        122    , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                        123    , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C-O-C-C:C"   ,                                                     124    );
    smartsPatternBits.put("C=C-C-C-C"   ,                                                     125    );
    smartsPatternBits.put("C:C-C=C"     ,                                                     126    );
    smartsPatternBits.put("C=C-C:C"     ,                                                     127    );
    smartsPatternBits.put("N-C-N-C-C"   ,                                                     128    );
    neighbourBits.put(                                                                        129    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                  ));
    neighbourBits.put(                                                                        130    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                        131    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "2",       132    );
    smartsPatternBits.put("C-C:C-N-C"   ,                                                     133    );
    smartsPatternBits.put("N-C-C=C"     ,                                                     134    );
    smartsPatternBits.put("O=C-C-C-N"   ,                                                     135    );
    smartsPatternBits.put("O-C-C-C-O"   ,                                                     136    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "4",       137    );
    smartsPatternBits.put("C-C:C-O-C"   ,                                                     138    );
    ringSetBits.put(ringsetCountHexPrefix+"3"       ,                                         139    );
    neighbourBits.put(                                                                        140    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                  ));
    smartsPatternBits.put("N:C-C:C"     ,                                                     141    );
    atomPairBits.put("N-O",                                                                   142    );
    smartsPatternBits.put("N:C:N-C"     ,                                                     143    );
    smartsPatternBits.put("C-N-C-N-C"   ,                                                     144    );
    neighbourBits.put(                                                                        145    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",       146    );
    neighbourBits.put(                                                                        147    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("F",null,false)}                                                                               ));
    smartsPatternBits.put("N-C:C:C-C"   ,                                                     148    );
    smartsPatternBits.put("N-C:C:C:N"   ,                                                     149    );
    atomPairBits.put("S-N",                                                                   150    );
    smartsPatternBits.put("N-C-O-C-C"   ,                                                     151    );
    smartsPatternBits.put("O-C-C=C"     ,                                                     152    );
    smartsPatternBits.put("C-C-S-C"     ,                                                     153    );
    smartsPatternBits.put("C:C-C-C:C"   ,                                                     154    );
    neighbourBits.put(                                                                        155    , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                        156    , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));
    neighbourBits.put(                                                                        157    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                  ));
    smartsPatternBits.put("O-C-C-C=O"   ,                                                     158    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "2",       159    );
    ringSetBits.put(ringsetCountPerSet+"4"          ,                                         160    );
    smartsPatternBits.put("N-C=N-C"     ,                                                     161    );
    smartsPatternBits.put("O-C:C-O"     ,                                                     162    );
    smartsPatternBits.put("O-C-C=O"     ,                                                     163    );
    neighbourBits.put(                                                                        164    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    smartsPatternBits.put("N-C-C:C-C"   ,                                                     165    );
    smartsPatternBits.put("O-C:C-C-C"   ,                                                     166    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",       167    );
    neighbourBits.put(                                                                        168    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));
    smartsPatternBits.put("O-C-C=C-C"   ,                                                     169    );
    smartsPatternBits.put("N=C-N-C"     ,                                                     170    );
    smartsPatternBits.put("O=C-C:C-C"   ,                                                     171    );
    smartsPatternBits.put("O=C-C-C:C"   ,                                                     172    );
    ringSetBits.put(ringsetCountTotalPrefix+"2"     ,                                         173    );
    smartsPatternBits.put("O-C:C-O-C"   ,                                                     174    );
    neighbourBits.put(                                                                        175    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("F",null,false)}                                                                              ));
    elemCntBits.put("F3" ,                                                                    176    );
    elemCntBits.put("Cl2",                                                                    177    );
    smartsPatternBits.put("N-C:C-C-C"   ,                                                     178    );
    neighbourBits.put(                                                                        179    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("F",null,false),new Neighbour("F",null,false)}                                                                              ));
    smartsPatternBits.put("N-S-C:C"     ,                                                     180    );
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "5",       181    );
    smartsPatternBits.put("O-C-C-C:C"   ,                                                     182    );
    elemCntBits.put("S2" ,                                                                    183    );
    smartsPatternBits.put("O-C-O-C"     ,                                                     184    );
    smartsPatternBits.put("C-C:C-C:C"   ,                                                     185    );
    ringBits.put(ringNonaromDoubleBond+"2_1",                                                 186    );
    smartsPatternBits.put("C-S-C-C-C"   ,                                                     187    );
    smartsPatternBits.put("N-C-N-C:C"   ,                                                     188    );
    ringSetBits.put(hexRingNonAromNeighPrefix+"1",                                            189    );
    smartsPatternBits.put("N-C:C:N"     ,                                                     190    );
    neighbourBits.put(                                                                        191    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    smartsPatternBits.put("C-O-C-C=C"   ,                                                     192    );
    smartsPatternBits.put("O=C-N-C=O"   ,                                                     193    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",       194    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",       195    );
    smartsPatternBits.put("N=C-N-C-C"   ,                                                     196    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",       197    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",       198    );
    smartsPatternBits.put("O=C-C:C-N"   ,                                                     199    );
    smartsPatternBits.put("O=C-C-O-C"   ,                                                     200    );
    smartsPatternBits.put("O-C:C:C-O"   ,                                                     201    );
    smartsPatternBits.put("C-S-C:C"     ,                                                     202    );
    neighbourBits.put(                                                                        203    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "1",       204    );
    ringSetBits.put(ringsetCountPentPrefix+"2"      ,                                         205    );
    smartsPatternBits.put("O=C-C:N"     ,                                                     206    );
    smartsPatternBits.put("S:C:C:C"     ,                                                     207    );
    ringSetBits.put(hexRingMixNeighPrefix+"2",                                                208    );
    elemCntBits.put("O12",                                                                    209    );
    smartsPatternBits.put("C-O-C-O-C"   ,                                                     210    );
    smartsPatternBits.put("O=C-C=C-N"   ,                                                     211    );
    smartsPatternBits.put("C:S:C-C"     ,                                                     212    );
    smartsPatternBits.put("O=C-N-C-N"   ,                                                     213    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "2",       214    );
    neighbourBits.put(                                                                        215    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("Cl-C:C:C-C"  ,                                                     216    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4",       217    );
    neighbourBits.put(                                                                        218    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));
    elemCntBits.put("Br1",                                                                    219    );
    smartsPatternBits.put("O-C-O-C-C"   ,                                                     220    );
    smartsPatternBits.put("O=C-C:C-O"   ,                                                     221    );
    neighbourBits.put(                                                                        222    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("N",null,false)}                                                                              ));
    neighbourBits.put(                                                                        223    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false)}                                                                                               ));
    smartsPatternBits.put("O-C-C-C=C"   ,                                                     224    );
    smartsPatternBits.put("N=C-C-C"     ,                                                     225    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",       226    );
    smartsPatternBits.put("C:C:N:N:C"   ,                                                     227    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "3",       228    );
    neighbourBits.put(                                                                        229    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                        230    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O-C=C-C"     ,                                                     231    );
    neighbourBits.put(                                                                        232    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                   ));
    neighbourBits.put(                                                                        233    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                  ));
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "3",       234    );
    neighbourBits.put(                                                                        235    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));
    neighbourBits.put(                                                                        236    , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));
    ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "1",       237    );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "1",       238    );
    smartsPatternBits.put("C=C-C=C"     ,                                                     239    );
    ringSetBits.put(ringsetPairPrefix+"6_7"         ,                                         240    );
    neighbourBits.put(                                                                        241    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                        242    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C-C=C-C=C"   ,                                                     243    );
    smartsPatternBits.put("O-C-C:C-C"   ,                                                     244    );
    neighbourBits.put(                                                                        245    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringSetBits.put(ringsetCountConnectedPrefix+"3" ,                                         246    );
    smartsPatternBits.put("Cl-C:C-C"    ,                                                     247    );
    smartsPatternBits.put("O=N-C:C"     ,                                                     248    );
    neighbourBits.put(                                                                        249    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,true)}                                                                              ));
    ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "1",       250    );
    smartsPatternBits.put("S:C:C:N"     ,                                                     251    );
    ringSetBits.put(ringsetPairPrefix+"5_5"         ,                                         252    );
    smartsPatternBits.put("N-N-C-C"     ,                                                     253    );
    smartsPatternBits.put("C:C-N-C:C"   ,                                                     254    );
    smartsPatternBits.put("C-C-S-C-C"   ,                                                     255    );
    elemCntBits.put("P1" ,                                                                    256    );
    ringSetBits.put(hexRingNonAromNeighPrefix+"2",                                            257    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",       258    );
    smartsPatternBits.put("O-C:C:C-N"   ,                                                     259    );
    smartsPatternBits.put("O-C:C-N"     ,                                                     260    );
    atomPairBits.put("P-O",                                                                   261    );
    ringSetBits.put(ringsetCountPerSet+"5"          ,                                         262    );
    neighbourBits.put(                                                                        263    , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    smartsPatternBits.put("C-O-C=C"     ,                                                     264    );
    neighbourBits.put(                                                                        265    , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));
    neighbourBits.put(                                                                        266    , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C:N-C:C"     ,                                                     267    );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",       268    );
    smartsPatternBits.put("O=C-N-N"     ,                                                     269    );
    ringSetBits.put(ringsetCountHexPrefix+"4"       ,                                         270    );
    smartsPatternBits.put("O=C-C-C=O"   ,                                                     271    );
    smartsPatternBits.put("N=C-C=C"     ,                                                     272    );
    smartsPatternBits.put("N-C-S-C"     ,                                                     273    );
    smartsPatternBits.put("O-N-C-C"     ,                                                     274    );
    ringSetBits.put(hexRingAromNeighPrefix+"1",                                               275    );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",       276    );
    ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "1",       277    );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "1",       278    );
    smartsPatternBits.put("S-C:C-C"     ,                                                     279    );
    smartsPatternBits.put("O=S-C-C"     ,                                                     280    );
    smartsPatternBits.put("C=N-N-C"     ,                                                     281    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3",       282    );
    ringBits.put(ringNonaromDoubleBond+"1_2",                                                 283    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",       284    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "5",       285    );
    smartsPatternBits.put("O-C:C:N"     ,                                                     286    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",       287    );
    smartsPatternBits.put("C-C=N-N-C"   ,                                                     288    );
    smartsPatternBits.put("N-C:C-N"     ,                                                     289    );
    carbonTrails.put(cTrailPrefix+"14",                                                       290    );
    smartsPatternBits.put("S-C:N:C"     ,                                                     291    );
    elemCntBits.put("O16",                                                                    292    );
    neighbourBits.put(                                                                        293    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false)}                                                                                               ));
    smartsPatternBits.put("N-C:C:C-N"   ,                                                     294    );
    neighbourBits.put(                                                                        295    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                        296    , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));
    neighbourBits.put(                                                                        297    , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "1",       298    );
    smartsPatternBits.put("N-C:N:N"     ,                                                     299    );
    smartsPatternBits.put("Cl-C:C-Cl"   ,                                                     300    );
    smartsPatternBits.put("O=C-O-C:C"   ,                                                     301    );
    smartsPatternBits.put("O-C-C:C-O"   ,                                                     302    );
    carbonTrails.put(cTrailPrefix+"15",                                                       303    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",       304    );
    neighbourBits.put(                                                                        305    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Cl",null,false)}                                                                             ));
    elemCntBits.put("I1",                                                                     306    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",       307    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "3",       308    );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",       309    );
    carbonTrails.put(cTrailPrefix+"16",                                                       310    );
    smartsPatternBits.put("N#C-C-C"     ,                                                     311    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "4",       312    );
    neighbourBits.put(                                                                        313    , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("N-N-C-N"     ,                                                     314    );
    ringSetBits.put(ringsetPairPrefix+"5_7"         ,                                         315    );
    smartsPatternBits.put("Br-C:C:C-C"  ,                                                     316    );
    neighbourBits.put(                                                                        317    , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}       ));
    ringSetBits.put(ringsetCountPerSet+"6"          ,                                         318    );
    neighbourBits.put(                                                                        319    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));
    smartsPatternBits.put("S-C:C-N"     ,                                                     320    );
    ringSetBits.put(ringsetCountConnectedPrefix+"4" ,                                         321    );
    smartsPatternBits.put("Cl-C:C-O"    ,                                                     322    );
    smartsPatternBits.put("S-C=N-C"     ,                                                     323    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5",       324    );
    neighbourBits.put(                                                                        325    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("S",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "4",       326    );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",       327    );
    ringSetBits.put(ringsetPairPrefix+"4_6"         ,                                         328    );
    smartsPatternBits.put("C-C-C#C"     ,                                                     329    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",       330    );
    elemCntBits.put("Na1",                                                                    331    );
    smartsPatternBits.put("N-N-C:C"     ,                                                     332    );
    atomPairBits.put("S-S",                                                                   333    );
    neighbourBits.put(                                                                        334    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("I",null,false)}                                                                               ));
    smartsPatternBits.put("Cl-C:C-O-C"  ,                                                     335    );
    smartsPatternBits.put("S=C-N-C"     ,                                                     336    );
    ringSetBits.put(ringsetCountPentPrefix+"3"      ,                                         337    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3",       338    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "3",       339    );
    carbonTrails.put(cTrailPrefix+"17",                                                       340    );
    ringSetBits.put(ringsetCountHexPrefix+"5"       ,                                         341    );
    neighbourBits.put(                                                                        342    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));
    elemCntBits.put("P2" ,                                                                    343    );
    smartsPatternBits.put("Cl-C:C-C=O"  ,                                                     344    );
    carbonTrails.put(cTrailPrefix+"18",                                                       345    );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",       346    );
    smartsPatternBits.put("N#C-C-C-C"   ,                                                     347    );
    elemCntBits.put("Br2",                                                                    348    );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",       349    );
    ringSetBits.put(hexRingAromNeighPrefix+"2",                                               350    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",       351    );
    ringSetBits.put(ringsetPairPrefix+"6_8"         ,                                         352    );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",       353    );
    smartsPatternBits.put("N#C-C=C"     ,                                                     354    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "5",       355    );
    neighbourBits.put(                                                                        356    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false)}                                                                             ));
    smartsPatternBits.put("S-C:C:C-N"   ,                                                     357    );
    smartsPatternBits.put("O-C-C=N"     ,                                                     358    );
    ringSetBits.put(ringsetPairPrefix+"3_5"         ,                                         359    );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",       360    );
    smartsPatternBits.put("Cl-C-C-N-C"  ,                                                     361    );
    smartsPatternBits.put("Cl-C-C-C"    ,                                                     362    );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",       363    );
    smartsPatternBits.put("Br-C:C-C"    ,                                                     364    );
    atomPairBits.put("O-O",                                                                   365    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3",       366    );
    ringSetBits.put(ringsetPairPrefix+"4_5"         ,                                         367    );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",       368    );
    ringBits.put(ringNonaromDoubleBond+"2_2",                                                 369    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4",       370    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3",       371    );
    ringSetBits.put(ringsetCountTotalPrefix+"3"     ,                                         372    );
    smartsPatternBits.put("O-S-C:C"     ,                                                     373    );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",       374    );
    ringSetBits.put(ringsetPairPrefix+"3_6"         ,                                         375    );
    ringSetBits.put(ringsetCountPerSet+"8"          ,                                         376    );
    smartsPatternBits.put("O=N-C:C-N"   ,                                                     377    );
    carbonTrails.put(cTrailPrefix+"19",                                                       378    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "4",       379    );
    smartsPatternBits.put("S-C:C-O"     ,                                                     380    );
    ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "2",       381    );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "2",       382    );
    smartsPatternBits.put("N-C:O:C"     ,                                                     383    );
    atomPairBits.put("P-N",                                                                   384    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3",       385    );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",       386    );
    ringSetBits.put(ringsetCountPentPrefix+"4"      ,                                         387    );
    smartsPatternBits.put("Cl-C-C=O"    ,                                                     388    );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "5",       389    );
    neighbourBits.put(                                                                        390    , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    carbonTrails.put(cTrailPrefix+"20",                                                       391    );
    smartsPatternBits.put("Cl-C-C-C-C"  ,                                                     392    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4",       393    );
    elemCntBits.put("B1" ,                                                                    394    );
    smartsPatternBits.put("S-C-S-C"     ,                                                     395    );
    elemCntBits.put("Si1",                                                                    396    );
    neighbourBits.put(                                                                        397    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "4",       398    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",       399    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4",       400    );
    elemCntBits.put("Pt1",                                                                    401    );
    neighbourBits.put(                                                                        402    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Si",null,false)}                                                                             ));
    ringSetBits.put(ringsetCountPerSet+"10"         ,                                         403    );
    ringSetBits.put(ringsetCountHexPrefix+"6"       ,                                         404    );
    smartsPatternBits.put("O=N-C:C-O"   ,                                                     405    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3",       406    );
    smartsPatternBits.put("Br-C-C-C"    ,                                                     407    );
    neighbourBits.put(                                                                        408    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("I",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5",       409    );
    atomPairBits.put("B-O",                                                                   410    );
    ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "2",       411    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3",       412    );
    neighbourBits.put(                                                                        413    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true),new Neighbour("Cl",null,false)}                                                 ));
    smartsPatternBits.put("O=S-C-N"     ,                                                     414    );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",       415    );
    neighbourBits.put(                                                                        416    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("N",null,true)}                                                                              ));
    ringBits.put(ringNonaromDoubleBond+"1_3",                                                 417    );
    smartsPatternBits.put("Br-C-C=O"    ,                                                     418    );
    smartsPatternBits.put("Cl-C-C-O"    ,                                                     419    );
    atomPairBits.put("Si-O",                                                                  420    );
    ringSetBits.put(ringsetPairPrefix+"6_12"        ,                                         421    );
    neighbourBits.put(                                                                        422    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Cl",null,false),new Neighbour("Cl",null,false)}                                                                            ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4",       423    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "5",       424    );
    smartsPatternBits.put("S-S-C:C"     ,                                                     425    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3",       426    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",       427    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "5",       428    );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",       429    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3",       430    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5",       431    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5",       432    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4",       433    );
    elemCntBits.put("K1",                                                                     434    );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "2",       435    );
    ringSetBits.put(ringsetPairPrefix+"5_8"         ,                                         436    );
    ringSetBits.put(ringsetCountTotalPrefix+"4"     ,                                         437    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4",       438    );
    elemCntBits.put("Se1",                                                                    439    );
    atomPairBits.put("Cl-O",                                                                  440    );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",       441    );
    ringSetBits.put(ringsetPairPrefix+"6_15"        ,                                         442    );
    ringSetBits.put(ringsetPairPrefix+"3_3"         ,                                         443    );
    ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "2",       444    );
    atomPairBits.put("F-S",                                                                   445    );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "2",       446    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4",       447    );
    atomPairBits.put("P-S",                                                                   448    );
    elemCntBits.put("Li1",                                                                    449    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4",       450    );
    elemCntBits.put("Tc1",                                                                    451    );
    elemCntBits.put("Au1",                                                                    452    );
    neighbourBits.put(                                                                        453    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,true)}                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5",       454    );
    elemCntBits.put("Fe1",                                                                    455    );
    atomPairBits.put("B-N",                                                                   456    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5",       457    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3",       458    );
    smartsPatternBits.put("Br-C-C-C:C"  ,                                                     459    );
    elemCntBits.put("Cu1",                                                                    460    );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5",       461    );
    neighbourBits.put(                                                                        462    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5",       463    );
    atomPairBits.put("B-F",                                                                   464    );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "2",       465    );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",       466    );
    smartsPatternBits.put("Cl-C-C-Cl"   ,                                                     467    );
    ringSetBits.put(ringsetPairPrefix+"3_7"         ,                                         468    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3",       469    );
    ringBits.put(ringNonaromDoubleBond+"2_3",                                                 470    );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",       471    );
    elemCntBits.put("Ru1",                                                                    472    );
    elemCntBits.put("Mn1",                                                                    473    );
    elemCntBits.put("Zn1",                                                                    474    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4",       475    );
    ringSetBits.put(ringsetPairPrefix+"4_7"         ,                                         476    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4",       477    );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5",       478    );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5",       479    );
    ringSetBits.put(ringsetCountPerSet+"30"         ,                                         480    );
    atomPairBits.put("P-F",                                                                   481    );
    neighbourBits.put(                                                                        482    , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("P",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4",       483    );
    elemCntBits.put("Re1",                                                                    484    );
    elemCntBits.put("Sn1",                                                                    485    );
    elemCntBits.put("As1",                                                                    486    );
    elemCntBits.put("Te1",                                                                    487    );
    atomPairBits.put("P-Cl",                                                                  488    );
    elemCntBits.put("Co1",                                                                    489    );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",       490    );
    elemCntBits.put("Bi1",                                                                    491    );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",       492    );
    ringSetBits.put(ringsetPairPrefix+"3_4"         ,                                         493    );
    elemCntBits.put("Ni1",                                                                    494    );
    elemCntBits.put("Pd1",                                                                    495    );
    neighbourBits.put(                                                                        496    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false)}                                                  ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5",       497    );
    elemCntBits.put("Sb1",                                                                    498    );
    neighbourBits.put(                                                                        499    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}      ));
    elemCntBits.put("Ca1",                                                                    500    );
    elemCntBits.put("V1" ,                                                                    501    );
    elemCntBits.put("Cr1",                                                                    502    );
    elemCntBits.put("Hg1",                                                                    503    );
    atomPairBits.put("Cl-N",                                                                  504    );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",       505    );
    elemCntBits.put("Ge1",                                                                    506    );
    elemCntBits.put("In1",                                                                    507    );
    elemCntBits.put("Mg1",                                                                    508    );
    elemCntBits.put("Gd1",                                                                    509    );
    elemCntBits.put("Mo1",                                                                    510    );
    elemCntBits.put("Y1" ,                                                                    511    );
    ringSetBits.put(ringsetPairPrefix+"4_8"         ,                                         512    );
    elemCntBits.put("Ag1",                                                                    513    );
    elemCntBits.put("Al1",                                                                    514    );
    elemCntBits.put("Os1",                                                                    515    );
    elemCntBits.put("Ga1",                                                                    516    );
    elemCntBits.put("Rh1",                                                                    517    );
    elemCntBits.put("Eu1",                                                                    518    );
    elemCntBits.put("W1" ,                                                                    519    );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5",       520    );
    elemCntBits.put("La1",                                                                    521    );
    elemCntBits.put("Nb1",                                                                    522    );
    elemCntBits.put("Pb1",                                                                    523    );
    elemCntBits.put("Ti1",                                                                    524    );
    elemCntBits.put("Tl1",                                                                    525    );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",       526    );
    elemCntBits.put("Ac1",                                                                    527    );
    elemCntBits.put("Am1",                                                                    528    );
    elemCntBits.put("Ar1",                                                                    529    );
    elemCntBits.put("At1",                                                                    530    );
    elemCntBits.put("Ba1",                                                                    531    );
    elemCntBits.put("Be1",                                                                    532    );
    elemCntBits.put("Bh1",                                                                    533    );
    elemCntBits.put("Bk1",                                                                    534    );
    elemCntBits.put("Cd1",                                                                    535    );
    elemCntBits.put("Ce1",                                                                    536    );
    elemCntBits.put("Cf1",                                                                    537    );
    elemCntBits.put("Cm1",                                                                    538    );
    elemCntBits.put("Cs1",                                                                    539    );
    elemCntBits.put("Db1",                                                                    540    );
    elemCntBits.put("Ds1",                                                                    541    );
    elemCntBits.put("Dy1",                                                                    542    );
    elemCntBits.put("Er1",                                                                    543    );
    elemCntBits.put("Es1",                                                                    544    );
    elemCntBits.put("Fm1",                                                                    545    );
    elemCntBits.put("Fr1",                                                                    546    );
    elemCntBits.put("He1",                                                                    547    );
    elemCntBits.put("Hf1",                                                                    548    );
    elemCntBits.put("Ho1",                                                                    549    );
    elemCntBits.put("Hs1",                                                                    550    );
    elemCntBits.put("Ir1",                                                                    551    );
    elemCntBits.put("Kr1",                                                                    552    );
    elemCntBits.put("Lr1",                                                                    553    );
    elemCntBits.put("Lu1",                                                                    554    );
    elemCntBits.put("Md1",                                                                    555    );
    elemCntBits.put("Mt1",                                                                    556    );
    elemCntBits.put("Nd1",                                                                    557    );
    elemCntBits.put("Ne1",                                                                    558    );
    elemCntBits.put("No1",                                                                    559    );
    elemCntBits.put("Np1",                                                                    560    );
    elemCntBits.put("Pa1",                                                                    561    );
    elemCntBits.put("Pm1",                                                                    562    );
    elemCntBits.put("Po1",                                                                    563    );
    elemCntBits.put("Pr1",                                                                    564    );
    elemCntBits.put("Pu1",                                                                    565    );
    elemCntBits.put("Ra1",                                                                    566    );
    elemCntBits.put("Rb1",                                                                    567    );
    elemCntBits.put("Rf1",                                                                    568    );
    elemCntBits.put("Rg1",                                                                    569    );
    elemCntBits.put("Rn1",                                                                    570    );
    elemCntBits.put("Sc1",                                                                    571    );
    elemCntBits.put("Sg1",                                                                    572    );
    elemCntBits.put("Sm1",                                                                    573    );
    elemCntBits.put("Sr1",                                                                    574    );
    elemCntBits.put("Ta1",                                                                    575    );
    elemCntBits.put("Tb1",                                                                    576    );
    elemCntBits.put("Th1",                                                                    577    );
    elemCntBits.put("Tm1",                                                                    578    );
    elemCntBits.put("U1" ,                                                                    579    );
    elemCntBits.put("Uub1",                                                                   580    );
    elemCntBits.put("Xe1",                                                                    581    );
    elemCntBits.put("Yb1",                                                                    582    );
    elemCntBits.put("Zr1",                                                                    583    );
    }

    /**
     * Private helper method used by constructor for bit sets on ring aspects.
     * @param ringsize size of the ring
     * @param bitPos current bitset position
     * @param count ring count aspect
     * @return bitPos new value
     */
    /*
    private int setupRings(int ringsize, int bitPos, int count) {
        ringBits.put(ringsize + ringPrefixRing + ringPrefixAny + count, bitPos++);
        if (ringsize > 4) { // aromatic rings of size 3 and 4 do not occur
            ringBits.put(ringsize + ringPrefixRing + ringPrefixArom + count, bitPos++);
            ringBits.put(ringsize + ringPrefixRing + ringPrefixArom + ringPrefixNitro + count, bitPos++);
            ringBits.put(ringsize + ringPrefixRing + ringPrefixArom + ringPrefixHetero + count, bitPos++);
            ringBits.put(ringsize + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly + count, bitPos++);
        }
        ringBits.put(ringsize + ringPrefixRing + ringPrefixNonArom + count, bitPos++);
        ringBits.put(ringsize + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro + count, bitPos++);
        ringBits.put(ringsize + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero + count, bitPos++);
        ringBits.put(ringsize + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + count, bitPos++);
        return bitPos;
    }
    */
}
