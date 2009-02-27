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

    /* Reserved strings used to build up ring aspect identifiers   */
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


    neighbourBits.put(                                                                                  0   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "2",                 1   );
    elemCntBits.put("O3" ,                                                                              2   );
    elemCntBits.put("C20",                                                                              3   );
    ringSetBits.put(ringsetCountTotalPrefix+"1"     ,                                                   4   );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "1",                 5   );
    elemCntBits.put("N3" ,                                                                              6   );
    smartsPatternBits.put("C-C-N-C-C"   ,                                                               7   );
    neighbourBits.put(                                                                                  8   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",                 9   );
    smartsPatternBits.put("C:C-C-C"     ,                                                              10   );
    smartsPatternBits.put("C-C-C-C-C"   ,                                                              11   );
    neighbourBits.put(                                                                                 12   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                                 13   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "3",                14   );
    smartsPatternBits.put("N-C-C-C-C"   ,                                                              15   );
    neighbourBits.put(                                                                                 16   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));
    neighbourBits.put(                                                                                 17   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O=C-C-C"     ,                                                              18   );
    neighbourBits.put(                                                                                 19   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));
    neighbourBits.put(                                                                                 20   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                ));
    neighbourBits.put(                                                                                 21   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                                                ));
    neighbourBits.put(                                                                                 22   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));
    smartsPatternBits.put("O=C-N-C-C"   ,                                                              23   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "1",                24   );
    neighbourBits.put(                                                                                 25   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                                               ));
    neighbourBits.put(                                                                                 26   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true)}                                                                               ));
    elemCntBits.put("C24",                                                                             27   );
    elemCntBits.put("N4" ,                                                                             28   );
    neighbourBits.put(                                                                                 29   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                ));
    neighbourBits.put(                                                                                 30   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                                 31   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    smartsPatternBits.put("C-C-C-C:C"   ,                                                              32   );
    smartsPatternBits.put("O=C-C-C-C"   ,                                                              33   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "1",                34   );
    neighbourBits.put(                                                                                 35   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("O-C-C-C-C"   ,                                                              36   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",                37   );
    smartsPatternBits.put("N-C-C:C"     ,                                                              38   );
    neighbourBits.put(                                                                                 39   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                  ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "3",                40   );
    ringSetBits.put(ringsetPairPrefix+"5_6"         ,                                                  41   );
    neighbourBits.put(                                                                                 42   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",                43   );
    elemCntBits.put("O5" ,                                                                             44   );
    neighbourBits.put(                                                                                 45   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("C-N-C:C"     ,                                                              46   );
    smartsPatternBits.put("C:C-O-C"     ,                                                              47   );
    smartsPatternBits.put("C-N-C-C:C"   ,                                                              48   );
    smartsPatternBits.put("N-C-C-N"     ,                                                              49   );
    elemCntBits.put("S1" ,                                                                             50   );
    smartsPatternBits.put("N-C-C-N-C"   ,                                                              51   );
    smartsPatternBits.put("O=C-C:C"     ,                                                              52   );
    smartsPatternBits.put("O-C-C-N"     ,                                                              53   );
    neighbourBits.put(                                                                                 54   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true)}                                                                               ));
    neighbourBits.put(                                                                                 55   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "1",                56   );
    smartsPatternBits.put("N-C-C-C:C"   ,                                                              57   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",                58   );
    elemCntBits.put("N5" ,                                                                             59   );
    smartsPatternBits.put("O=C-C-N"     ,                                                              60   );
    elemCntBits.put("C28",                                                                             61   );
    smartsPatternBits.put("O-C-C-N-C"   ,                                                              62   );
    smartsPatternBits.put("C-C:N:C"     ,                                                              63   );
    smartsPatternBits.put("N:C:C-C"     ,                                                              64   );
    neighbourBits.put(                                                                                 65   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                  ));
    smartsPatternBits.put("O=C-C-N-C"   ,                                                              66   );
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "4",                67   );
    smartsPatternBits.put("C-C=C-C"     ,                                                              68   );
    neighbourBits.put(                                                                                 69   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,false)}                                                                              ));
    neighbourBits.put(                                                                                 70   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));
    neighbourBits.put(                                                                                 71   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                   ));
    smartsPatternBits.put("C-C-O-C-C"   ,                                                              72   );
    elemCntBits.put("Cl1",                                                                             73   );
    ringSetBits.put(ringsetCountPerSet+"3"          ,                                                  74   );
    ringSetBits.put(ringsetCountConnectedPrefix+"2" ,                                                  75   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3",                76   );
    smartsPatternBits.put("N:C:N:C"     ,                                                              77   );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "2",                78   );
    smartsPatternBits.put("C-C-C=C"     ,                                                              79   );
    neighbourBits.put(                                                                                 80   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));
    smartsPatternBits.put("O-C-C:C"     ,                                                              81   );
    smartsPatternBits.put("O-C-C-C-N"   ,                                                              82   );
    smartsPatternBits.put("C-C-O-C:C"   ,                                                              83   );
    elemCntBits.put("F1" ,                                                                             84   );
    neighbourBits.put(                                                                                 85   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true)}                                                                               ));
    neighbourBits.put(                                                                                 86   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("Cl",null,false)}                                                                              ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",                87   );
    smartsPatternBits.put("O=C-C=C"     ,                                                              88   );
    smartsPatternBits.put("C-C:C:C-C"   ,                                                              89   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",                90   );
    elemCntBits.put("O7" ,                                                                             91   );
    smartsPatternBits.put("C-C-C:C-C"   ,                                                              92   );
    neighbourBits.put(                                                                                 93   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O-C:C:C-C"   ,                                                              94   );
    elemCntBits.put("C32",                                                                             95   );
    smartsPatternBits.put("C-C-C=C-C"   ,                                                              96   );
    neighbourBits.put(                                                                                 97   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                                               ));
    neighbourBits.put(                                                                                 98   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("N-C:C-C"     ,                                                              99   );
    elemCntBits.put("N6" ,                                                                            100   );
    smartsPatternBits.put("O-C:C-C"     ,                                                             101   );
    smartsPatternBits.put("N:C:C:N"     ,                                                             102   );
    smartsPatternBits.put("O-C-C-O"     ,                                                             103   );
    smartsPatternBits.put("C:C-C:C"     ,                                                             104   );
    smartsPatternBits.put("N-C:N:C"     ,                                                             105   );
    smartsPatternBits.put("N-C-N-C"     ,                                                             106   );
    atomPairBits.put("N-N",                                                                           107   );
    atomPairBits.put("S-O",                                                                           108   );
    neighbourBits.put(                                                                                109   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    smartsPatternBits.put("N-C-C-O-C"   ,                                                             110   );
    smartsPatternBits.put("O=C-C=C-C"   ,                                                             111   );
    smartsPatternBits.put("N-C-C-C-N"   ,                                                             112   );
    smartsPatternBits.put("O-C-C-O-C"   ,                                                             113   );
    neighbourBits.put(                                                                                114   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    neighbourBits.put(                                                                                115   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                   ));
    neighbourBits.put(                                                                                116   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("S",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",               117   );
    neighbourBits.put(                                                                                118   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("C",null,true)}                                                                               ));
    neighbourBits.put(                                                                                119   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                                120   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                                121   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C-O-C-C:C"   ,                                                             122   );
    smartsPatternBits.put("C=C-C-C-C"   ,                                                             123   );
    smartsPatternBits.put("C:C-C=C"     ,                                                             124   );
    smartsPatternBits.put("C=C-C:C"     ,                                                             125   );
    smartsPatternBits.put("N-C-N-C-C"   ,                                                             126   );
    neighbourBits.put(                                                                                127   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                  ));
    neighbourBits.put(                                                                                128   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                                129   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "2",               130   );
    smartsPatternBits.put("C-C:C-N-C"   ,                                                             131   );
    smartsPatternBits.put("N-C-C=C"     ,                                                             132   );
    smartsPatternBits.put("O=C-C-C-N"   ,                                                             133   );
    smartsPatternBits.put("O-C-C-C-O"   ,                                                             134   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "4",               135   );
    smartsPatternBits.put("C-C:C-O-C"   ,                                                             136   );
    ringSetBits.put(ringsetCountHexPrefix+"3"       ,                                                 137   );
    neighbourBits.put(                                                                                138   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                  ));
    smartsPatternBits.put("N:C-C:C"     ,                                                             139   );
    atomPairBits.put("N-O",                                                                           140   );
    smartsPatternBits.put("N:C:N-C"     ,                                                             141   );
    smartsPatternBits.put("C-N-C-N-C"   ,                                                             142   );
    neighbourBits.put(                                                                                143   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",               144   );
    neighbourBits.put(                                                                                145   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("F",null,false)}                                                                               ));
    smartsPatternBits.put("N-C:C:C-C"   ,                                                             146   );
    smartsPatternBits.put("N-C:C:C:N"   ,                                                             147   );
    atomPairBits.put("S-N",                                                                           148   );
    smartsPatternBits.put("N-C-O-C-C"   ,                                                             149   );
    smartsPatternBits.put("O-C-C=C"     ,                                                             150   );
    smartsPatternBits.put("C-C-S-C"     ,                                                             151   );
    smartsPatternBits.put("C:C-C-C:C"   ,                                                             152   );
    neighbourBits.put(                                                                                153   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                                154   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));
    neighbourBits.put(                                                                                155   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                  ));
    smartsPatternBits.put("O-C-C-C=O"   ,                                                             156   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "2",               157   );
    ringSetBits.put(ringsetCountPerSet+"4"          ,                                                 158   );
    smartsPatternBits.put("N-C=N-C"     ,                                                             159   );
    smartsPatternBits.put("O-C:C-O"     ,                                                             160   );
    smartsPatternBits.put("O-C-C=O"     ,                                                             161   );
    neighbourBits.put(                                                                                162   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    smartsPatternBits.put("N-C-C:C-C"   ,                                                             163   );
    smartsPatternBits.put("O-C:C-C-C"   ,                                                             164   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",               165   );
    neighbourBits.put(                                                                                166   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));
    smartsPatternBits.put("O-C-C=C-C"   ,                                                             167   );
    smartsPatternBits.put("N=C-N-C"     ,                                                             168   );
    smartsPatternBits.put("O=C-C:C-C"   ,                                                             169   );
    smartsPatternBits.put("O=C-C-C:C"   ,                                                             170   );
    ringSetBits.put(ringsetCountTotalPrefix+"2"     ,                                                 171   );
    smartsPatternBits.put("O-C:C-O-C"   ,                                                             172   );
    neighbourBits.put(                                                                                173   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("F",null,false)}                                                                              ));
    elemCntBits.put("F3" ,                                                                            174   );
    elemCntBits.put("Cl2",                                                                            175   );
    smartsPatternBits.put("N-C:C-C-C"   ,                                                             176   );
    neighbourBits.put(                                                                                177   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("F",null,false),new Neighbour("F",null,false)}                                                                              ));
    smartsPatternBits.put("N-S-C:C"     ,                                                             178   );
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "5",               179   );
    smartsPatternBits.put("O-C-C-C:C"   ,                                                             180   );
    elemCntBits.put("S2" ,                                                                            181   );
    smartsPatternBits.put("O-C-O-C"     ,                                                             182   );
    smartsPatternBits.put("C-C:C-C:C"   ,                                                             183   );
    smartsPatternBits.put("C-S-C-C-C"   ,                                                             184   );
    smartsPatternBits.put("N-C-N-C:C"   ,                                                             185   );
    smartsPatternBits.put("N-C:C:N"     ,                                                             186   );
    neighbourBits.put(                                                                                187   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    smartsPatternBits.put("C-O-C-C=C"   ,                                                             188   );
    smartsPatternBits.put("O=C-N-C=O"   ,                                                             189   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",               190   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",               191   );
    smartsPatternBits.put("N=C-N-C-C"   ,                                                             192   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",               193   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",               194   );
    smartsPatternBits.put("O=C-C:C-N"   ,                                                             195   );
    smartsPatternBits.put("O=C-C-O-C"   ,                                                             196   );
    smartsPatternBits.put("O-C:C:C-O"   ,                                                             197   );
    smartsPatternBits.put("C-S-C:C"     ,                                                             198   );
    neighbourBits.put(                                                                                199   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "1",               200   );
    ringSetBits.put(ringsetCountPentPrefix+"2"      ,                                                 201   );
    smartsPatternBits.put("O=C-C:N"     ,                                                             202   );
    smartsPatternBits.put("S:C:C:C"     ,                                                             203   );
    elemCntBits.put("O12",                                                                            204   );
    smartsPatternBits.put("C-O-C-O-C"   ,                                                             205   );
    smartsPatternBits.put("O=C-C=C-N"   ,                                                             206   );
    smartsPatternBits.put("C:S:C-C"     ,                                                             207   );
    smartsPatternBits.put("O=C-N-C-N"   ,                                                             208   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "2",               209   );
    neighbourBits.put(                                                                                210   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("Cl-C:C:C-C"  ,                                                             211   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4",               212   );
    neighbourBits.put(                                                                                213   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));
    elemCntBits.put("Br1",                                                                            214   );
    smartsPatternBits.put("O-C-O-C-C"   ,                                                             215   );
    smartsPatternBits.put("O=C-C:C-O"   ,                                                             216   );
    neighbourBits.put(                                                                                217   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("N",null,false)}                                                                              ));
    neighbourBits.put(                                                                                218   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false)}                                                                                               ));
    smartsPatternBits.put("O-C-C-C=C"   ,                                                             219   );
    smartsPatternBits.put("N=C-C-C"     ,                                                             220   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",               221   );
    smartsPatternBits.put("C:C:N:N:C"   ,                                                             222   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "3",               223   );
    neighbourBits.put(                                                                                224   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                                225   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O-C=C-C"     ,                                                             226   );
    neighbourBits.put(                                                                                227   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                   ));
    neighbourBits.put(                                                                                228   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                  ));
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "3",               229   );
    neighbourBits.put(                                                                                230   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));
    neighbourBits.put(                                                                                231   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));
    ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "1",               232   );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "1",               233   );
    smartsPatternBits.put("C=C-C=C"     ,                                                             234   );
    ringSetBits.put(ringsetPairPrefix+"6_7"         ,                                                 235   );
    neighbourBits.put(                                                                                236   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                                237   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C-C=C-C=C"   ,                                                             238   );
    smartsPatternBits.put("O-C-C:C-C"   ,                                                             239   );
    neighbourBits.put(                                                                                240   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringSetBits.put(ringsetCountConnectedPrefix+"3" ,                                                 241   );
    smartsPatternBits.put("Cl-C:C-C"    ,                                                             242   );
    smartsPatternBits.put("O=N-C:C"     ,                                                             243   );
    neighbourBits.put(                                                                                244   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,true)}                                                                              ));
    ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "1",               245   );
    smartsPatternBits.put("S:C:C:N"     ,                                                             246   );
    ringSetBits.put(ringsetPairPrefix+"5_5"         ,                                                 247   );
    smartsPatternBits.put("N-N-C-C"     ,                                                             248   );
    smartsPatternBits.put("C:C-N-C:C"   ,                                                             249   );
    smartsPatternBits.put("C-C-S-C-C"   ,                                                             250   );
    elemCntBits.put("P1" ,                                                                            251   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",               252   );
    smartsPatternBits.put("O-C:C:C-N"   ,                                                             253   );
    smartsPatternBits.put("O-C:C-N"     ,                                                             254   );
    atomPairBits.put("P-O",                                                                           255   );
    ringSetBits.put(ringsetCountPerSet+"5"          ,                                                 256   );
    neighbourBits.put(                                                                                257   , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    smartsPatternBits.put("C-O-C=C"     ,                                                             258   );
    neighbourBits.put(                                                                                259   , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));
    neighbourBits.put(                                                                                260   , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C:N-C:C"     ,                                                             261   );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",               262   );
    smartsPatternBits.put("O=C-N-N"     ,                                                             263   );
    ringSetBits.put(ringsetCountHexPrefix+"4"       ,                                                 264   );
    smartsPatternBits.put("O=C-C-C=O"   ,                                                             265   );
    smartsPatternBits.put("N=C-C=C"     ,                                                             266   );
    smartsPatternBits.put("N-C-S-C"     ,                                                             267   );
    smartsPatternBits.put("O-N-C-C"     ,                                                             268   );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",               269   );
    ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "1",               270   );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "1",               271   );
    smartsPatternBits.put("S-C:C-C"     ,                                                             272   );
    smartsPatternBits.put("O=S-C-C"     ,                                                             273   );
    smartsPatternBits.put("C=N-N-C"     ,                                                             274   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3",               275   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",               276   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "5",               277   );
    smartsPatternBits.put("O-C:C:N"     ,                                                             278   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",               279   );
    smartsPatternBits.put("C-C=N-N-C"   ,                                                             280   );
    smartsPatternBits.put("N-C:C-N"     ,                                                             281   );
    smartsPatternBits.put("S-C:N:C"     ,                                                             282   );
    elemCntBits.put("O16",                                                                            283   );
    neighbourBits.put(                                                                                284   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false)}                                                                                               ));
    smartsPatternBits.put("N-C:C:C-N"   ,                                                             285   );
    neighbourBits.put(                                                                                286   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                                287   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));
    neighbourBits.put(                                                                                288   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "1",               289   );
    smartsPatternBits.put("N-C:N:N"     ,                                                             290   );
    smartsPatternBits.put("Cl-C:C-Cl"   ,                                                             291   );
    smartsPatternBits.put("O=C-O-C:C"   ,                                                             292   );
    smartsPatternBits.put("O-C-C:C-O"   ,                                                             293   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",               294   );
    neighbourBits.put(                                                                                295   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Cl",null,false)}                                                                             ));
    elemCntBits.put("I1",                                                                             296   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",               297   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "3",               298   );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",               299   );
    smartsPatternBits.put("N#C-C-C"     ,                                                             300   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "4",               301   );
    neighbourBits.put(                                                                                302   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("N-N-C-N"     ,                                                             303   );
    ringSetBits.put(ringsetPairPrefix+"5_7"         ,                                                 304   );
    smartsPatternBits.put("Br-C:C:C-C"  ,                                                             305   );
    neighbourBits.put(                                                                                306   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}       ));
    ringSetBits.put(ringsetCountPerSet+"6"          ,                                                 307   );
    neighbourBits.put(                                                                                308   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));
    smartsPatternBits.put("S-C:C-N"     ,                                                             309   );
    ringSetBits.put(ringsetCountConnectedPrefix+"4" ,                                                 310   );
    smartsPatternBits.put("Cl-C:C-O"    ,                                                             311   );
    smartsPatternBits.put("S-C=N-C"     ,                                                             312   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5",               313   );
    neighbourBits.put(                                                                                314   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("S",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "4",               315   );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",               316   );
    ringSetBits.put(ringsetPairPrefix+"4_6"         ,                                                 317   );
    smartsPatternBits.put("C-C-C#C"     ,                                                             318   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",               319   );
    elemCntBits.put("Na1",                                                                            320   );
    smartsPatternBits.put("N-N-C:C"     ,                                                             321   );
    atomPairBits.put("S-S",                                                                           322   );
    neighbourBits.put(                                                                                323   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("I",null,false)}                                                                               ));
    smartsPatternBits.put("Cl-C:C-O-C"  ,                                                             324   );
    smartsPatternBits.put("S=C-N-C"     ,                                                             325   );
    ringSetBits.put(ringsetCountPentPrefix+"3"      ,                                                 326   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3",               327   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "3",               328   );
    ringSetBits.put(ringsetCountHexPrefix+"5"       ,                                                 329   );
    neighbourBits.put(                                                                                330   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));
    elemCntBits.put("P2" ,                                                                            331   );
    smartsPatternBits.put("Cl-C:C-C=O"  ,                                                             332   );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",               333   );
    smartsPatternBits.put("N#C-C-C-C"   ,                                                             334   );
    elemCntBits.put("Br2",                                                                            335   );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",               336   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",               337   );
    ringSetBits.put(ringsetPairPrefix+"6_8"         ,                                                 338   );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",               339   );
    smartsPatternBits.put("N#C-C=C"     ,                                                             340   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "5",               341   );
    neighbourBits.put(                                                                                342   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false)}                                                                             ));
    smartsPatternBits.put("S-C:C:C-N"   ,                                                             343   );
    smartsPatternBits.put("O-C-C=N"     ,                                                             344   );
    ringSetBits.put(ringsetPairPrefix+"3_5"         ,                                                 345   );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",               346   );
    smartsPatternBits.put("Cl-C-C-N-C"  ,                                                             347   );
    smartsPatternBits.put("Cl-C-C-C"    ,                                                             348   );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",               349   );
    smartsPatternBits.put("Br-C:C-C"    ,                                                             350   );
    atomPairBits.put("O-O",                                                                           351   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3",               352   );
    ringSetBits.put(ringsetPairPrefix+"4_5"         ,                                                 353   );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",               354   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4",               355   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3",               356   );
    ringSetBits.put(ringsetCountTotalPrefix+"3"     ,                                                 357   );
    smartsPatternBits.put("O-S-C:C"     ,                                                             358   );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",               359   );
    ringSetBits.put(ringsetPairPrefix+"3_6"         ,                                                 360   );
    ringSetBits.put(ringsetCountPerSet+"8"          ,                                                 361   );
    smartsPatternBits.put("O=N-C:C-N"   ,                                                             362   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "4",               363   );
    smartsPatternBits.put("S-C:C-O"     ,                                                             364   );
    ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "2",               365   );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "2",               366   );
    smartsPatternBits.put("N-C:O:C"     ,                                                             367   );
    atomPairBits.put("P-N",                                                                           368   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3",               369   );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",               370   );
    ringSetBits.put(ringsetCountPentPrefix+"4"      ,                                                 371   );
    smartsPatternBits.put("Cl-C-C=O"    ,                                                             372   );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "5",               373   );
    neighbourBits.put(                                                                                374   , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    smartsPatternBits.put("Cl-C-C-C-C"  ,                                                             375   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4",               376   );
    elemCntBits.put("B1" ,                                                                            377   );
    smartsPatternBits.put("S-C-S-C"     ,                                                             378   );
    elemCntBits.put("Si1",                                                                            379   );
    neighbourBits.put(                                                                                380   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "4",               381   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",               382   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4",               383   );
    elemCntBits.put("Pt1",                                                                            384   );
    neighbourBits.put(                                                                                385   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Si",null,false)}                                                                             ));
    ringSetBits.put(ringsetCountPerSet+"10"         ,                                                 386   );
    ringSetBits.put(ringsetCountHexPrefix+"6"       ,                                                 387   );
    smartsPatternBits.put("O=N-C:C-O"   ,                                                             388   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3",               389   );
    smartsPatternBits.put("Br-C-C-C"    ,                                                             390   );
    neighbourBits.put(                                                                                391   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("I",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5",               392   );
    atomPairBits.put("B-O",                                                                           393   );
    ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "2",               394   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3",               395   );
    neighbourBits.put(                                                                                396   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true),new Neighbour("Cl",null,false)}                                                 ));
    smartsPatternBits.put("O=S-C-N"     ,                                                             397   );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",               398   );
    neighbourBits.put(                                                                                399   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("N",null,true)}                                                                              ));
    smartsPatternBits.put("Br-C-C=O"    ,                                                             400   );
    smartsPatternBits.put("Cl-C-C-O"    ,                                                             401   );
    atomPairBits.put("Si-O",                                                                          402   );
    ringSetBits.put(ringsetPairPrefix+"6_12"        ,                                                 403   );
    neighbourBits.put(                                                                                404   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Cl",null,false),new Neighbour("Cl",null,false)}                                                                            ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4",               405   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "5",               406   );
    smartsPatternBits.put("S-S-C:C"     ,                                                             407   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3",               408   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",               409   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "5",               410   );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",               411   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3",               412   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5",               413   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5",               414   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4",               415   );
    elemCntBits.put("K1",                                                                             416   );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "2",               417   );
    ringSetBits.put(ringsetPairPrefix+"5_8"         ,                                                 418   );
    ringSetBits.put(ringsetCountTotalPrefix+"4"     ,                                                 419   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4",               420   );
    elemCntBits.put("Se1",                                                                            421   );
    atomPairBits.put("Cl-O",                                                                          422   );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",               423   );
    ringSetBits.put(ringsetPairPrefix+"6_15"        ,                                                 424   );
    ringSetBits.put(ringsetPairPrefix+"3_3"         ,                                                 425   );
    ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "2",               426   );
    atomPairBits.put("F-S",                                                                           427   );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "2",               428   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4",               429   );
    atomPairBits.put("P-S",                                                                           430   );
    elemCntBits.put("Li1",                                                                            431   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4",               432   );
    elemCntBits.put("Tc1",                                                                            433   );
    elemCntBits.put("Au1",                                                                            434   );
    neighbourBits.put(                                                                                435   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,true)}                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5",               436   );
    elemCntBits.put("Fe1",                                                                            437   );
    atomPairBits.put("B-N",                                                                           438   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5",               439   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3",               440   );
    smartsPatternBits.put("Br-C-C-C:C"  ,                                                             441   );
    elemCntBits.put("Cu1",                                                                            442   );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5",               443   );
    neighbourBits.put(                                                                                444   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5",               445   );
    atomPairBits.put("B-F",                                                                           446   );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "2",               447   );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",               448   );
    smartsPatternBits.put("Cl-C-C-Cl"   ,                                                             449   );
    ringSetBits.put(ringsetPairPrefix+"3_7"         ,                                                 450   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3",               451   );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",               452   );
    elemCntBits.put("Ru1",                                                                            453   );
    elemCntBits.put("Mn1",                                                                            454   );
    elemCntBits.put("Zn1",                                                                            455   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4",               456   );
    ringSetBits.put(ringsetPairPrefix+"4_7"         ,                                                 457   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4",               458   );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5",               459   );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5",               460   );
    ringSetBits.put(ringsetCountPerSet+"30"         ,                                                 461   );
    atomPairBits.put("P-F",                                                                           462   );
    neighbourBits.put(                                                                                463   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("P",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4",               464   );
    elemCntBits.put("Re1",                                                                            465   );
    elemCntBits.put("Sn1",                                                                            466   );
    elemCntBits.put("As1",                                                                            467   );
    elemCntBits.put("Te1",                                                                            468   );
    atomPairBits.put("P-Cl",                                                                          469   );
    elemCntBits.put("Co1",                                                                            470   );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",               471   );
    elemCntBits.put("Bi1",                                                                            472   );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",               473   );
    ringSetBits.put(ringsetPairPrefix+"3_4"         ,                                                 474   );
    elemCntBits.put("Ni1",                                                                            475   );
    elemCntBits.put("Pd1",                                                                            476   );
    neighbourBits.put(                                                                                477   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false)}                                                  ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5",               478   );
    elemCntBits.put("Sb1",                                                                            479   );
    neighbourBits.put(                                                                                480   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}      ));
    elemCntBits.put("Ca1",                                                                            481   );
    elemCntBits.put("V1" ,                                                                            482   );
    elemCntBits.put("Cr1",                                                                            483   );
    elemCntBits.put("Hg1",                                                                            484   );
    atomPairBits.put("Cl-N",                                                                          485   );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",               486   );
    elemCntBits.put("Ge1",                                                                            487   );
    elemCntBits.put("In1",                                                                            488   );
    elemCntBits.put("Mg1",                                                                            489   );
    elemCntBits.put("Gd1",                                                                            490   );
    elemCntBits.put("Mo1",                                                                            491   );
    elemCntBits.put("Y1" ,                                                                            492   );
    ringSetBits.put(ringsetPairPrefix+"4_8"         ,                                                 493   );
    elemCntBits.put("Ag1",                                                                            494   );
    elemCntBits.put("Al1",                                                                            495   );
    elemCntBits.put("Os1",                                                                            496   );
    elemCntBits.put("Ga1",                                                                            497   );
    elemCntBits.put("Rh1",                                                                            498   );
    elemCntBits.put("Eu1",                                                                            499   );
    elemCntBits.put("W1" ,                                                                            500   );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5",               501   );
    elemCntBits.put("La1",                                                                            502   );
    elemCntBits.put("Nb1",                                                                            503   );
    elemCntBits.put("Pb1",                                                                            504   );
    elemCntBits.put("Ti1",                                                                            505   );
    elemCntBits.put("Tl1",                                                                            506   );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",               507   );
    elemCntBits.put("Ac1",                                                                            508   );
    elemCntBits.put("Am1",                                                                            509   );
    elemCntBits.put("Ar1",                                                                            510   );
    elemCntBits.put("At1",                                                                            511   );
    elemCntBits.put("Ba1",                                                                            512   );
    elemCntBits.put("Be1",                                                                            513   );
    elemCntBits.put("Bh1",                                                                            514   );
    elemCntBits.put("Bk1",                                                                            515   );
    elemCntBits.put("Cd1",                                                                            516   );
    elemCntBits.put("Ce1",                                                                            517   );
    elemCntBits.put("Cf1",                                                                            518   );
    elemCntBits.put("Cm1",                                                                            519   );
    elemCntBits.put("Cs1",                                                                            520   );
    elemCntBits.put("Db1",                                                                            521   );
    elemCntBits.put("Ds1",                                                                            522   );
    elemCntBits.put("Dy1",                                                                            523   );
    elemCntBits.put("Er1",                                                                            524   );
    elemCntBits.put("Es1",                                                                            525   );
    elemCntBits.put("Fm1",                                                                            526   );
    elemCntBits.put("Fr1",                                                                            527   );
    elemCntBits.put("He1",                                                                            528   );
    elemCntBits.put("Hf1",                                                                            529   );
    elemCntBits.put("Ho1",                                                                            530   );
    elemCntBits.put("Hs1",                                                                            531   );
    elemCntBits.put("Ir1",                                                                            532   );
    elemCntBits.put("Kr1",                                                                            533   );
    elemCntBits.put("Lr1",                                                                            534   );
    elemCntBits.put("Lu1",                                                                            535   );
    elemCntBits.put("Md1",                                                                            536   );
    elemCntBits.put("Mt1",                                                                            537   );
    elemCntBits.put("Nd1",                                                                            538   );
    elemCntBits.put("Ne1",                                                                            539   );
    elemCntBits.put("No1",                                                                            540   );
    elemCntBits.put("Np1",                                                                            541   );
    elemCntBits.put("Pa1",                                                                            542   );
    elemCntBits.put("Pm1",                                                                            543   );
    elemCntBits.put("Po1",                                                                            544   );
    elemCntBits.put("Pr1",                                                                            545   );
    elemCntBits.put("Pu1",                                                                            546   );
    elemCntBits.put("Ra1",                                                                            547   );
    elemCntBits.put("Rb1",                                                                            548   );
    elemCntBits.put("Rf1",                                                                            549   );
    elemCntBits.put("Rg1",                                                                            550   );
    elemCntBits.put("Rn1",                                                                            551   );
    elemCntBits.put("Sc1",                                                                            552   );
    elemCntBits.put("Sg1",                                                                            553   );
    elemCntBits.put("Sm1",                                                                            554   );
    elemCntBits.put("Sr1",                                                                            555   );
    elemCntBits.put("Ta1",                                                                            556   );
    elemCntBits.put("Tb1",                                                                            557   );
    elemCntBits.put("Th1",                                                                            558   );
    elemCntBits.put("Tm1",                                                                            559   );
    elemCntBits.put("U1" ,                                                                            560   );
    elemCntBits.put("Uub1",                                                                           561   );
    elemCntBits.put("Xe1",                                                                            562   );
    elemCntBits.put("Yb1",                                                                            563   );
    elemCntBits.put("Zr1",                                                                            564   );

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
