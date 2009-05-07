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
import java.util.Iterator;


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
    public final Map<String, String> elemFingerprinted = new HashMap<String, String>();

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

    public final String otherElem="OtherElement";

    /**
     * Constructor sets up the maps with fingerprinting data.
     */
    BitPositions () {
    // Commented out - bits that are very common (may reconsider)
    //94%        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "1",        512   );
    //88%        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "1",        513   );
    //83%        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",        516   );
    //80%        neighbourBits.put(                                                                         144   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    //80%        neighbourBits.put(                                                                         145   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    //77%        neighbourBits.put(                                                                         146   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    //77%        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "2",        521   );

        /* Position 0 is reserved !*/
        neighbourBits.put(                                                                  1    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "2", 2    );
        elemCntBits.put("O3" ,                                                              3    );
        elemCntBits.put("C20",                                                              4    );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "1", 5    );
        ringSetBits.put(ringsetCountTotalPrefix+"1"     ,                                   6    );
        elemCntBits.put("N3" ,                                                              7    );
        smartsPatternBits.put("C-C-N-C-C"   ,                                               8    );
        neighbourBits.put(                                                                  9    , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2", 10   );
        smartsPatternBits.put("C:C-C-C"     ,                                               11   );
        smartsPatternBits.put("C-C-C-C-C"   ,                                               12   );
        neighbourBits.put(                                                                  13   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
        neighbourBits.put(                                                                  14   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "3", 15   );
        smartsPatternBits.put("N-C-C-C-C"   ,                                               16   );
        neighbourBits.put(                                                                  17   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));
        neighbourBits.put(                                                                  18   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
        smartsPatternBits.put("O=C-C-C"     ,                                               19   );
        neighbourBits.put(                                                                  20   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));
        neighbourBits.put(                                                                  21   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                ));
        neighbourBits.put(                                                                  22   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                                                ));
        neighbourBits.put(                                                                  23   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));
        smartsPatternBits.put("O=C-N-C-C"   ,                                               24   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "1", 25   );
        neighbourBits.put(                                                                  26   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                                               ));
        neighbourBits.put(                                                                  27   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true)}                                                                               ));
        elemCntBits.put("C24",                                                              28   );
        elemCntBits.put("N4" ,                                                              29   );
        neighbourBits.put(                                                                  30   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                ));
        neighbourBits.put(                                                                  31   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
        neighbourBits.put(                                                                  32   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
        smartsPatternBits.put("C-C-C-C:C"   ,                                               33   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "1", 34   );
        smartsPatternBits.put("O=C-C-C-C"   ,                                               35   );
        neighbourBits.put(                                                                  36   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
        smartsPatternBits.put("O-C-C-C-C"   ,                                               37   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1", 38   );
        smartsPatternBits.put("N-C-C:C"     ,                                               39   );
        neighbourBits.put(                                                                  40   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                  ));
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "3", 41   );
        ringSetBits.put(ringsetPairPrefix+"5_6"         ,                                   42   );
        smartsPatternBits.put("C-C-C-C-C-C"     ,                                           43   );
        neighbourBits.put(                                                                  44   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1", 45   );
        elemCntBits.put("O5" ,                                                              46   );
        neighbourBits.put(                                                                  47   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
        smartsPatternBits.put("C-N-C:C"     ,                                               48   );
        smartsPatternBits.put("C:C-O-C"     ,                                               49   );
        smartsPatternBits.put("C-N-C-C:C"   ,                                               50   );
        smartsPatternBits.put("N-C-C-N"     ,                                               51   );
        elemCntBits.put("S1" ,                                                              52   );
        smartsPatternBits.put("N-C-C-N-C"   ,                                               53   );
        smartsPatternBits.put("O=C-C:C"     ,                                               54   );
        smartsPatternBits.put("O-C-C-N"     ,                                               55   );
        neighbourBits.put(                                                                  56   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true)}                                                                               ));
        neighbourBits.put(                                                                  57   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false)}                                                                                               ));
        smartsPatternBits.put("C:C:C:C:N:C"     ,                                           58   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "1", 59   );
        smartsPatternBits.put("O=C-C-C-C-C"     ,                                           60   );
        smartsPatternBits.put("C:C:C:C-C=O"     ,                                           61   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1", 62   );
        ringSetBits.put(hexRingMixNeighPrefix+"1",                                          63   );
        smartsPatternBits.put("N-C-C-C:C"   ,                                               64   );
        smartsPatternBits.put("C-N-C-C-N-C"     ,                                           65   );
        elemCntBits.put("N5" ,                                                              66   );
        smartsPatternBits.put("O-C-C-C-C-C"     ,                                           67   );
        smartsPatternBits.put("O=C-C-N"     ,                                               68   );
        elemCntBits.put("C28",                                                              69   );
        smartsPatternBits.put("C:C:C-C-C-N"     ,                                           70   );
        smartsPatternBits.put("O-C-C-N-C"   ,                                               71   );
        smartsPatternBits.put("C:C:N:C:C:C"     ,                                           72   );
        smartsPatternBits.put("C-C:N:C"     ,                                               73   );
        smartsPatternBits.put("N:C:C-C"     ,                                               74   );
        smartsPatternBits.put("C-C-N-C-C:C"     ,                                           75   );
        smartsPatternBits.put("C-C-N-C:C:C"     ,                                           76   );
        neighbourBits.put(                                                                  77   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                  ));
        smartsPatternBits.put("C-N-C-C-C:C"     ,                                           78   );
        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "4", 79   );
        smartsPatternBits.put("O=C-C-N-C"   ,                                               80   );
        smartsPatternBits.put("C-C=C-C"     ,                                               81   );
        ringSetBits.put(ringsetCountPerSet+"3"          ,                                   82   );
        ringSetBits.put(ringsetCountConnectedPrefix+"2" ,                                   83   );
        smartsPatternBits.put("O-C:C:C:C-C"     ,                                           84   );
        neighbourBits.put(                                                                  85   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,false)}                                                                              ));
        neighbourBits.put(                                                                  86   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));
        smartsPatternBits.put("C-C-N-C-C-O"     ,                                           87   );
        neighbourBits.put(                                                                  88   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                   ));
        smartsPatternBits.put("C-C-O-C-C"   ,                                               89   );
        elemCntBits.put("Cl1",                                                              90   );
        smartsPatternBits.put("C-C-N-C-C=O"     ,                                           91   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3", 92   );
        smartsPatternBits.put("C:C-C-C-C-C"     ,                                           93   );
        smartsPatternBits.put("N:C:N:C"     ,                                               94   );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "2", 95   );
        smartsPatternBits.put("O=C-N-C-C-N"     ,                                           96   );
        ringBits.put(ringNonaromDoubleBond+"1_1",                                           97   );
        smartsPatternBits.put("C-O-C-C-C-C"     ,                                           98   );
        smartsPatternBits.put("C-C-C=C"     ,                                               99   );
        neighbourBits.put(                                                                  100  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));
        smartsPatternBits.put("O-C-C:C"     ,                                               101  );
        smartsPatternBits.put("O=C-N-C-C=O"     ,                                           102  );
        smartsPatternBits.put("O-C-C-C-N"   ,                                               103  );
        smartsPatternBits.put("C:C-C-C-C-N"     ,                                           104  );
        smartsPatternBits.put("O-C-C:C:C:C"     ,                                           105  );
        smartsPatternBits.put("C-C-O-C:C"   ,                                               106  );
        elemCntBits.put("F1" ,                                                              107  );
        neighbourBits.put(                                                                  108  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true)}                                                                               ));
        smartsPatternBits.put("C-C-O-C:C:C"     ,                                           109  );
        smartsPatternBits.put("C-C-C-O-C-C"     ,                                           110  );
        neighbourBits.put(                                                                  111  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("Cl",null,false)}                                                                              ));
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1", 112  );
        smartsPatternBits.put("O=C-C=C"     ,                                               113  );
        smartsPatternBits.put("C-C:C:C-C"   ,                                               114  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1", 115  );
        smartsPatternBits.put("O=C-N-C:C:C"     ,                                           116  );
        smartsPatternBits.put("O=C-C-C-C:C"     ,                                           117  );
        elemCntBits.put("O7" ,                                                              118  );
        smartsPatternBits.put("C-C-C:C-C"   ,                                               119  );
        neighbourBits.put(                                                                  120  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
        smartsPatternBits.put("O-C:C:C-C"   ,                                               121  );
        smartsPatternBits.put("C:C:C:C:C-Cl"    ,                                           122  );
        elemCntBits.put("C32",                                                              123  );
        smartsPatternBits.put("C-C-C=C-C"   ,                                               124  );
        neighbourBits.put(                                                                  125  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                                               ));
        neighbourBits.put(                                                                  126  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
        smartsPatternBits.put("C-N-C-C-C-O"     ,                                           127  );
        smartsPatternBits.put("N-C:C-C"     ,                                               128  );
        elemCntBits.put("N6" ,                                                              129  );
        smartsPatternBits.put("C-C:C:N:C:C"     ,                                           130  );
        smartsPatternBits.put("O-C:C-C"     ,                                               131  );
        smartsPatternBits.put("C-C:C:C:C-C"     ,                                           132  );
        smartsPatternBits.put("N:C:C:N"     ,                                               133  );
        smartsPatternBits.put("O-C-C-N-C=O"     ,                                           134  );
        smartsPatternBits.put("O-C-C-O"     ,                                               135  );
        smartsPatternBits.put("C:C-C:C"     ,                                               136  );
        smartsPatternBits.put("C:N:C:C:C-C"     ,                                           137  );
        smartsPatternBits.put("C:C-C:C:C:C"     ,                                           138  );
        smartsPatternBits.put("N:C:C:C:C-C"     ,                                           139  );
        smartsPatternBits.put("C:C:C:N:C-C"     ,                                           140  );
        smartsPatternBits.put("N-C:N:C"     ,                                               141  );
        smartsPatternBits.put("N-C-N-C"     ,                                               142  );
        atomPairBits.put("N-N",                                                             143  );
        atomPairBits.put("S-O",                                                             144  );
        neighbourBits.put(                                                                  145  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 146  );
        smartsPatternBits.put("N-C-C-O-C"   ,                                               147  );
        smartsPatternBits.put("O=C-C=C-C"   ,                                               148  );
        smartsPatternBits.put("N-C-C-C-N"   ,                                               149  );
        smartsPatternBits.put("O-C-C-O-C"   ,                                               150  );
        smartsPatternBits.put("C-C-C-N-C:C"     ,                                           151  );
        neighbourBits.put(                                                                  152  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
        smartsPatternBits.put("C-N-C-C-C-N"     ,                                           153  );
        neighbourBits.put(                                                                  154  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                   ));
        neighbourBits.put(                                                                  155  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("S",null,false)}                                                                              ));
        neighbourBits.put(                                                                  156  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("C",null,true)}                                                                               ));
        neighbourBits.put(                                                                  157  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
        neighbourBits.put(                                                                  158  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
        neighbourBits.put(                                                                  159  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
        smartsPatternBits.put("C-O-C-C:C"   ,                                               160  );
        smartsPatternBits.put("N-C:C:C:C-C"     ,                                           161  );
        smartsPatternBits.put("C=C-C-C-C"   ,                                               162  );
        smartsPatternBits.put("C:C:C-C:C:C"     ,                                           163  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "2", 164  );
        smartsPatternBits.put("C:C-C=C"     ,                                               165  );
        smartsPatternBits.put("C=C-C:C"     ,                                               166  );
        smartsPatternBits.put("N-C-N-C-C"   ,                                               167  );
        smartsPatternBits.put("C:C:C-C-O-C"     ,                                           168  );
        smartsPatternBits.put("O-C-C-C-C-N"     ,                                           169  );
        neighbourBits.put(                                                                  170  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                  ));
        smartsPatternBits.put("C-O-C:C:C-C"     ,                                           171  );
        neighbourBits.put(                                                                  172  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
        neighbourBits.put(                                                                  173  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
        smartsPatternBits.put("S-C:C:C:C:C"     ,                                           174  );
        smartsPatternBits.put("C-C:C-N-C"   ,                                               175  );
        smartsPatternBits.put("N-C-C=C"     ,                                               176  );
        smartsPatternBits.put("C-O-C-C-N-C"     ,                                           177  );
        smartsPatternBits.put("N:C:N:C:C:C"     ,                                           178  );
        smartsPatternBits.put("O=C-C-C-N"   ,                                               179  );
        ringSetBits.put(ringsetCountHexPrefix+"3"       ,                                   180  );
        smartsPatternBits.put("O-C-C-C-O"   ,                                               181  );
        smartsPatternBits.put("C:C:C:C-C=C"     ,                                           182  );
        smartsPatternBits.put("C:C:N:C:N:C"     ,                                           183  );
        smartsPatternBits.put("C-C-C:C:N:C"     ,                                           184  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "4", 185  );
        smartsPatternBits.put("C-C:C-O-C"   ,                                               186  );
        neighbourBits.put(                                                                  187  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                  ));
        smartsPatternBits.put("C:C-C-N-C=O"     ,                                           188  );
        smartsPatternBits.put("C:C:C-C=C-C"     ,                                           189  );
        smartsPatternBits.put("N:C-C:C"     ,                                               190  );
        smartsPatternBits.put("C-C-C-C=C-C"     ,                                           191  );
        smartsPatternBits.put("C:C-C-C-C-O"     ,                                           192  );
        atomPairBits.put("N-O",                                                             193  );
        smartsPatternBits.put("N:C:N-C"     ,                                               194  );
        smartsPatternBits.put("C-N-C-N-C"   ,                                               195  );
        neighbourBits.put(                                                                  196  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 197  );
        smartsPatternBits.put("O=C-C-C-N-C"     ,                                           198  );
        neighbourBits.put(                                                                  199  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("F",null,false)}                                                                               ));
        smartsPatternBits.put("N-C:C:C-C"   ,                                               200  );
        smartsPatternBits.put("N-C:C:C:N"   ,                                               201  );
        smartsPatternBits.put("F-C:C:C:C:C"     ,                                           202  );
        atomPairBits.put("S-N",                                                             203  );
        smartsPatternBits.put("C-C-O-C-C-O"     ,                                           204  );
        smartsPatternBits.put("C-C:C-C-C-C"     ,                                           205  );
        smartsPatternBits.put("O=C-C-C-C-N"     ,                                           206  );
        smartsPatternBits.put("C-C-C-C-C=C"     ,                                           207  );
        smartsPatternBits.put("N:C-C:C:C:C"     ,                                           208  );
        smartsPatternBits.put("C-O-C-C-C-O"     ,                                           209  );
        smartsPatternBits.put("N-C-O-C-C"   ,                                               210  );
        smartsPatternBits.put("O-C-C=C"     ,                                               211  );
        smartsPatternBits.put("C-C-S-C"     ,                                               212  );
        smartsPatternBits.put("C:C-C-C:C"   ,                                               213  );
        smartsPatternBits.put("C:C:C-C-C:C"     ,                                           214  );
        smartsPatternBits.put("C-C-C-O-C:C"     ,                                           215  );
        neighbourBits.put(                                                                  216  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));
        smartsPatternBits.put("C-C-C:C:C-O"     ,                                           217  );
        neighbourBits.put(                                                                  218  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));
        neighbourBits.put(                                                                  219  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                  ));
        smartsPatternBits.put("C-C:C:C-C-C"     ,                                           220  );
        smartsPatternBits.put("C-N-C-N-C-C"     ,                                           221  );
        smartsPatternBits.put("O-C-C-C=O"   ,                                               222  );
        smartsPatternBits.put("C:N:C:C:C:N"     ,                                           223  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "2", 224  );
        smartsPatternBits.put("O=S-C:C:C:C"     ,                                           225  );
        smartsPatternBits.put("C:N:C:C:C-N"     ,                                           226  );
        ringSetBits.put(ringsetCountPerSet+"4"          ,                                   227  );
        smartsPatternBits.put("C-O-C-C-C-N"     ,                                           228  );
        smartsPatternBits.put("C-C-C-N-C-N"     ,                                           229  );
        smartsPatternBits.put("N-C=N-C"     ,                                               230  );
        smartsPatternBits.put("O-C:C-O"     ,                                               231  );
        smartsPatternBits.put("N:C:C:C-C-C"     ,                                           232  );
        smartsPatternBits.put("C:N:C:C:N-C"     ,                                           233  );
        smartsPatternBits.put("C-N:C:N:C:C"     ,                                           234  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 235  );
        smartsPatternBits.put("O-C-C=O"     ,                                               236  );
        neighbourBits.put(                                                                  237  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
        smartsPatternBits.put("N-C-C:C-C"   ,                                               238  );
        smartsPatternBits.put("O-C:C-C-C"   ,                                               239  );
        smartsPatternBits.put("C-C-C-O-C=O"     ,                                           240  );
        smartsPatternBits.put("N:C:N:C:C-C"     ,                                           241  );
        neighbourBits.put(                                                                  242  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));
        smartsPatternBits.put("C-C:C-N-C-C"     ,                                           243  );
        smartsPatternBits.put("C:C-N-C-C-N"     ,                                           244  );
        smartsPatternBits.put("C-N-C-C=C-C"     ,                                           245  );
        smartsPatternBits.put("C-C-O-C-C-N"     ,                                           246  );
        smartsPatternBits.put("C:N:C:N:C-C"     ,                                           247  );
        smartsPatternBits.put("O-C-C-C-C=O"     ,                                           248  );
        smartsPatternBits.put("C-C-C-C:C:N"     ,                                           249  );
        smartsPatternBits.put("O-C-C=C-C"   ,                                               250  );
        smartsPatternBits.put("N=C-N-C"     ,                                               251  );
        smartsPatternBits.put("O=C-C:C-C"   ,                                               252  );
        smartsPatternBits.put("O=C-C-C:C"   ,                                               253  );
        smartsPatternBits.put("C:C:C-C:N:C"     ,                                           254  );
        smartsPatternBits.put("O-C:C-O-C"   ,                                               255  );
        smartsPatternBits.put("N-C:N:C:C:C"     ,                                           256  );
        ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "1", 257  );
        smartsPatternBits.put("C:C:C:N-C-C"     ,                                           258  );
        smartsPatternBits.put("C-N-C-C:C-C"     ,                                           259  );
        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "5", 260  );
        ringSetBits.put(ringsetCountTotalPrefix+"2"     ,                                   261  );
        neighbourBits.put(                                                                  262  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("F",null,false)}                                                                              ));
        smartsPatternBits.put("C-C-O-C-N-C"     ,                                           263  );
        smartsPatternBits.put("O=C-C-C:C:C"     ,                                           264  );
        smartsPatternBits.put("N-C:N:C:N:C"     ,                                           265  );
        elemCntBits.put("F3" ,                                                              266  );
        elemCntBits.put("Cl2",                                                              267  );
        smartsPatternBits.put("N-C:C-C-C"   ,                                               268  );
        neighbourBits.put(                                                                  269  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("F",null,false),new Neighbour("F",null,false)}                                                                              ));
        smartsPatternBits.put("C-N:C:C:C:C"     ,                                           270  );
        smartsPatternBits.put("C:C-N-C-C:C"     ,                                           271  );
        smartsPatternBits.put("N-S-C:C"     ,                                               272  );
        smartsPatternBits.put("O-C-C-C:C"   ,                                               273  );
        smartsPatternBits.put("C-C-N-C:N:C"     ,                                           274  );
        elemCntBits.put("S2" ,                                                              275  );
        smartsPatternBits.put("C:N:C:N-C-C"     ,                                           276  );
        smartsPatternBits.put("N-S-C:C:C:C"     ,                                           277  );
        ringSetBits.put(hexRingNonAromNeighPrefix+"1",                                      278  );
        smartsPatternBits.put("C-N-C:C:C-C"     ,                                           279  );
        smartsPatternBits.put("O-C-O-C"     ,                                               280  );
        smartsPatternBits.put("C-C:C-O-C-C"     ,                                           281  );
        smartsPatternBits.put("C:C:C-C-C-O"     ,                                           282  );
        smartsPatternBits.put("C-C=C-N-C-C"     ,                                           283  );
        smartsPatternBits.put("C-C:C-C:C"   ,                                               284  );
        smartsPatternBits.put("C-S-C-C-C"   ,                                               285  );
        smartsPatternBits.put("C-C:C-C:C:C"     ,                                           286  );
        smartsPatternBits.put("N-C-N-C:C"   ,                                               287  );
        smartsPatternBits.put("N:C:C:C-N-C"     ,                                           288  );
        smartsPatternBits.put("C:C:N:C-N-C"     ,                                           289  );
        smartsPatternBits.put("C-C-N:C:C:N"     ,                                           290  );
        ringBits.put(ringNonaromDoubleBond+"2_1",                                           291  );
        smartsPatternBits.put("C-C-O-C-C:C"     ,                                           292  );
        smartsPatternBits.put("C=C-C-N-C-C"     ,                                           293  );
        smartsPatternBits.put("N-C-C-C:C-C"     ,                                           294  );
        smartsPatternBits.put("O=C-N-C:C-C"     ,                                           295  );
        smartsPatternBits.put("C-C-C:C-O-C"     ,                                           296  );
        smartsPatternBits.put("N-C:C:N"     ,                                               297  );
        smartsPatternBits.put("O=C-C:C:C-C"     ,                                           298  );
        smartsPatternBits.put("Cl-C:C:C:C-C"    ,                                           299  );
        smartsPatternBits.put("C:C:N:C-C-C"     ,                                           300  );
        neighbourBits.put(                                                                  301  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
        smartsPatternBits.put("C-O-C-C=C"   ,                                               302  );
        smartsPatternBits.put("N:C:C:C:N-C"     ,                                           303  );
        smartsPatternBits.put("N-C-C-C:C:N"     ,                                           304  );
        smartsPatternBits.put("O=C-N-C=O"   ,                                               305  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2", 306  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 307  );
        smartsPatternBits.put("C-C-C-C:N:C"     ,                                           308  );
        smartsPatternBits.put("C:C:C-C-C=C"     ,                                           309  );
        smartsPatternBits.put("N=C-N-C-C"   ,                                               310  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2", 311  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1", 312  );
        ringSetBits.put(ringsetPairPrefix+"6_7"         ,                                   313  );
        smartsPatternBits.put("O=C-C:C-N"   ,                                               314  );
        smartsPatternBits.put("O=C-C-O-C"   ,                                               315  );
        smartsPatternBits.put("O-C:C:C-O"   ,                                               316  );
        smartsPatternBits.put("C-S-C:C"     ,                                               317  );
        neighbourBits.put(                                                                  318  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
        smartsPatternBits.put("O=C-C:N"     ,                                               319  );
        smartsPatternBits.put("S:C:C:C"     ,                                               320  );
        ringSetBits.put(hexRingMixNeighPrefix+"2",                                          321  );
        elemCntBits.put("O12",                                                              322  );
        smartsPatternBits.put("C-O-C-O-C"   ,                                               323  );
        smartsPatternBits.put("O=C-C=C-N"   ,                                               324  );
        ringSetBits.put(ringsetCountPentPrefix+"2"      ,                                   325  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "3", 326  );
        smartsPatternBits.put("C:S:C-C"     ,                                               327  );
        smartsPatternBits.put("O=C-N-C-N"   ,                                               328  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "2", 329  );
        neighbourBits.put(                                                                  330  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
        smartsPatternBits.put("Cl-C:C:C-C"  ,                                               331  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4", 332  );
        neighbourBits.put(                                                                  333  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));
        carbonTrails.put(cTrailPrefix+"14",                                                 334  );
        elemCntBits.put("Br1",                                                              335  );
        smartsPatternBits.put("O-C-O-C-C"   ,                                               336  );
        smartsPatternBits.put("O=C-C:C-O"   ,                                               337  );
        neighbourBits.put(                                                                  338  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("N",null,false)}                                                                              ));
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2", 339  );
        neighbourBits.put(                                                                  340  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false)}                                                                                               ));
        smartsPatternBits.put("O-C-C-C=C"   ,                                               341  );
        ringSetBits.put(ringsetCountConnectedPrefix+"3" ,                                   342  );
        smartsPatternBits.put("N=C-C-C"     ,                                               343  );
        smartsPatternBits.put("C:C:N:N:C"   ,                                               344  );
        neighbourBits.put(                                                                  345  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
        neighbourBits.put(                                                                  346  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
        smartsPatternBits.put("O-C=C-C"     ,                                               347  );
        neighbourBits.put(                                                                  348  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                   ));
        smartsPatternBits.put("N:C:N:C:N:C"     ,                                           349  );
        neighbourBits.put(                                                                  350  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                  ));
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "3", 351  );
        neighbourBits.put(                                                                  352  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));
        neighbourBits.put(                                                                  353  , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));
        carbonTrails.put(cTrailPrefix+"15",                                                 354  );
        ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "1", 355  );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "1", 356  );
        smartsPatternBits.put("C=C-C=C"     ,                                               357  );
        neighbourBits.put(                                                                  358  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
        neighbourBits.put(                                                                  359  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
        ringSetBits.put(hexRingNonAromNeighPrefix+"2",                                      360  );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "1", 361  );
        smartsPatternBits.put("C-C=C-C=C"   ,                                               362  );
        smartsPatternBits.put("O-C-C:C-C"   ,                                               363  );
        neighbourBits.put(                                                                  364  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
        smartsPatternBits.put("Cl-C:C-C"    ,                                               365  );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "1", 366  );
        smartsPatternBits.put("O=N-C:C"     ,                                               367  );
        neighbourBits.put(                                                                  368  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,true)}                                                                              ));
        ringSetBits.put(ringsetCountHexPrefix+"4"       ,                                   369  );
        ringSetBits.put(ringsetCountPerSet+"5"          ,                                   370  );
        carbonTrails.put(cTrailPrefix+"16",                                                 371  );
        smartsPatternBits.put("S:C:C:N"     ,                                               372  );
        ringSetBits.put(ringsetPairPrefix+"5_5"         ,                                   373  );
        smartsPatternBits.put("N-N-C-C"     ,                                               374  );
        smartsPatternBits.put("C:C-N-C:C"   ,                                               375  );
        smartsPatternBits.put("C-C-S-C-C"   ,                                               376  );
        elemCntBits.put("P1" ,                                                              377  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2", 378  );
        ringSetBits.put(ringsetPairPrefix+"5_7"         ,                                   379  );
        smartsPatternBits.put("O-C:C:C-N"   ,                                               380  );
        smartsPatternBits.put("O-C:C-N"     ,                                               381  );
        atomPairBits.put("P-O",                                                             382  );
        neighbourBits.put(                                                                  383  , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
        smartsPatternBits.put("C-O-C=C"     ,                                               384  );
        neighbourBits.put(                                                                  385  , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));
        neighbourBits.put(                                                                  386  , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
        smartsPatternBits.put("C:N-C:C"     ,                                               387  );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 388  );
        smartsPatternBits.put("O=C-N-N"     ,                                               389  );
        ringBits.put(ringNonaromDoubleBond+"1_2",                                           390  );
        smartsPatternBits.put("O=C-O-C-C-O"     ,                                           391  );
        smartsPatternBits.put("O=C-C-C=O"   ,                                               392  );
        smartsPatternBits.put("N=C-C=C"     ,                                               393  );
        smartsPatternBits.put("N-C-S-C"     ,                                               394  );
        ringSetBits.put(hexRingAromNeighPrefix+"1",                                         395  );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1", 396  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3", 397  );
        smartsPatternBits.put("O-N-C-C"     ,                                               398  );
        carbonTrails.put(cTrailPrefix+"17",                                                 399  );
        ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "1", 400  );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "1", 401  );
        smartsPatternBits.put("S-C:C-C"     ,                                               402  );
        smartsPatternBits.put("O=S-C-C"     ,                                               403  );
        smartsPatternBits.put("C=N-N-C"     ,                                               404  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "5", 405  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1", 406  );
        smartsPatternBits.put("O-C:C:N"     ,                                               407  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2", 408  );
        smartsPatternBits.put("C-C=N-N-C"   ,                                               409  );
        smartsPatternBits.put("N-C:C-N"     ,                                               410  );
        smartsPatternBits.put("S-C:N:C"     ,                                               411  );
        elemCntBits.put("O16",                                                              412  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "4", 413  );
        neighbourBits.put(                                                                  414  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false)}                                                                                               ));
        smartsPatternBits.put("N-C:C:C-N"   ,                                               415  );
        neighbourBits.put(                                                                  416  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
        neighbourBits.put(                                                                  417  , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));
        neighbourBits.put(                                                                  418  , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
        smartsPatternBits.put("N-C:N:N"     ,                                               419  );
        smartsPatternBits.put("Cl-C:C-Cl"   ,                                               420  );
        smartsPatternBits.put("O=C-O-C:C"   ,                                               421  );
        smartsPatternBits.put("O-C:N:C:N:C"     ,                                           422  );
        carbonTrails.put(cTrailPrefix+"18",                                                 423  );
        ringSetBits.put(ringsetCountConnectedPrefix+"4" ,                                   424  );
        smartsPatternBits.put("O-C-C:C-O"   ,                                               425  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2", 426  );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1", 427  );
        neighbourBits.put(                                                                  428  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Cl",null,false)}                                                                             ));
        elemCntBits.put("I1",                                                               429  );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 430  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2", 431  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "3", 432  );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1", 433  );
        carbonTrails.put(cTrailPrefix+"19",                                                 434  );
        smartsPatternBits.put("N#C-C-C"     ,                                               435  );
        ringSetBits.put(ringsetCountPerSet+"6"          ,                                   436  );
        neighbourBits.put(                                                                  437  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
        smartsPatternBits.put("N-N-C-N"     ,                                               438  );
        smartsPatternBits.put("Br-C:C:C-C"  ,                                               439  );
        neighbourBits.put(                                                                  440  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}       ));
        neighbourBits.put(                                                                  441  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));
        smartsPatternBits.put("S-C:C-N"     ,                                               442  );
        smartsPatternBits.put("Cl-C:C-O"    ,                                               443  );
        smartsPatternBits.put("S-C=N-C"     ,                                               444  );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 445  );
        carbonTrails.put(cTrailPrefix+"20",                                                 446  );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "4", 447  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5", 448  );
        neighbourBits.put(                                                                  449  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("S",IBond.Order.DOUBLE,false)}                                                                                               ));
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4", 450  );
        ringSetBits.put(ringsetPairPrefix+"4_6"         ,                                   451  );
        smartsPatternBits.put("O=C-C-O-C=O"     ,                                           452  );
        smartsPatternBits.put("C-C-C#C"     ,                                               453  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2", 454  );
        elemCntBits.put("Na1",                                                              455  );
        smartsPatternBits.put("N-N-C:C"     ,                                               456  );
        atomPairBits.put("S-S",                                                             457  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "3", 458  );
        neighbourBits.put(                                                                  459  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("I",null,false)}                                                                               ));
        smartsPatternBits.put("Cl-C:C-O-C"  ,                                               460  );
        smartsPatternBits.put("S=C-N-C"     ,                                               461  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3", 462  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "5", 463  );
        ringSetBits.put(ringsetPairPrefix+"3_6"         ,                                   464  );
        neighbourBits.put(                                                                  465  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));
        ringSetBits.put(ringsetCountHexPrefix+"5"       ,                                   466  );
        elemCntBits.put("P2" ,                                                              467  );
        smartsPatternBits.put("Cl-C:C-C=O"  ,                                               468  );
        ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "2", 469  );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1", 470  );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 471  );
        ringSetBits.put(ringsetCountPentPrefix+"3"      ,                                   472  );
        ringSetBits.put(hexRingAromNeighPrefix+"2",                                         473  );
        smartsPatternBits.put("N#C-C-C-C"   ,                                               474  );
        elemCntBits.put("Br2",                                                              475  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2", 476  );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1", 477  );
        ringSetBits.put(ringsetPairPrefix+"3_5"         ,                                   478  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3", 479  );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 480  );
        smartsPatternBits.put("N#C-C=C"     ,                                               481  );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "2", 482  );
        neighbourBits.put(                                                                  483  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false)}                                                                             ));
        smartsPatternBits.put("S-C:C:C-N"   ,                                               484  );
        smartsPatternBits.put("O-C-C=N"     ,                                               485  );
        smartsPatternBits.put("Cl-C-C-N-C"  ,                                               486  );
        smartsPatternBits.put("Cl-C-C-C"    ,                                               487  );
        smartsPatternBits.put("C-N-C-N-C-N"     ,                                           488  );
        ringSetBits.put(ringsetPairPrefix+"4_5"         ,                                   489  );
        smartsPatternBits.put("Br-C:C-C"    ,                                               490  );
        atomPairBits.put("O-O",                                                             491  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3", 492  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3", 493  );
        ringSetBits.put(ringsetPairPrefix+"4_7"         ,                                   494  );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 495  );
        ringBits.put(ringNonaromDoubleBond+"2_2",                                           496  );
        smartsPatternBits.put("O-S-C:C"     ,                                               497  );
        smartsPatternBits.put("O=N-C:C-N"   ,                                               498  );
        smartsPatternBits.put("C-O-C-O-C-O"     ,                                           499  );
        smartsPatternBits.put("C-N-C=N-C-N"     ,                                           500  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "4", 501  );
        smartsPatternBits.put("S-C:C-O"     ,                                               502  );
        ringSetBits.put(ringsetCountTotalPrefix+"3"     ,                                   503  );
        ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "2", 504  );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "2", 505  );
        smartsPatternBits.put("N-C:O:C"     ,                                               506  );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "5", 507  );
        ringSetBits.put(ringsetCountPerSet+"8"          ,                                   508  );
        atomPairBits.put("P-N",                                                             509  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3", 510  );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1", 511  );
        smartsPatternBits.put("Cl-C-C=O"    ,                                               512  );
        neighbourBits.put(                                                                  513  , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
        smartsPatternBits.put("Cl-C-C-C-C"  ,                                               514  );
        ringSetBits.put(ringsetPairPrefix+"3_7"         ,                                   515  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4", 516  );
        smartsPatternBits.put("N-C-C:N:C-N"     ,                                           517  );
        elemCntBits.put("B1" ,                                                              518  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "4", 519  );
        smartsPatternBits.put("S-C-S-C"     ,                                               520  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5", 521  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4", 522  );
        elemCntBits.put("Si1",                                                              523  );
        neighbourBits.put(                                                                  524  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                               ));
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3", 525  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2", 526  );
        smartsPatternBits.put("C-N-C-C:C:O"     ,                                           527  );
        elemCntBits.put("Pt1",                                                              528  );
        smartsPatternBits.put("O:C:C:C-C-N"     ,                                           529  );
        neighbourBits.put(                                                                  530  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Si",null,false)}                                                                             ));
        smartsPatternBits.put("C-N-C-C-C:O"     ,                                           531  );
        ringBits.put(ringNonaromDoubleBond+"1_3",                                           532  );
        smartsPatternBits.put("O=N-C:C-O"   ,                                               533  );
        smartsPatternBits.put("Br-C-C-C"    ,                                               534  );
        neighbourBits.put(                                                                  535  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("I",null,false)}                                                                              ));
        atomPairBits.put("B-O",                                                             536  );
        smartsPatternBits.put("N-C-C:N:N-C"     ,                                           537  );
        ringSetBits.put(ringsetCountHexPrefix+"6"       ,                                   538  );
        neighbourBits.put(                                                                  539  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true),new Neighbour("Cl",null,false)}                                                 ));
        smartsPatternBits.put("O=S-C-N"     ,                                               540  );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2", 541  );
        neighbourBits.put(                                                                  542  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("N",null,true)}                                                                              ));
        smartsPatternBits.put("Br-C-C=O"    ,                                               543  );
        smartsPatternBits.put("Cl-C-C-O"    ,                                               544  );
        atomPairBits.put("Si-O",                                                            545  );
        ringSetBits.put(ringsetCountPentPrefix+"4"      ,                                   546  );
        smartsPatternBits.put("C:C:N:C-C:O"     ,                                           547  );
        smartsPatternBits.put("O=C-N-C-C:O"     ,                                           548  );
        smartsPatternBits.put("C-C-C-N:C-O"     ,                                           549  );
        smartsPatternBits.put("C:N:C-C-C:N"     ,                                           550  );
        smartsPatternBits.put("C=N-C=N-C-N"     ,                                           551  );
        smartsPatternBits.put("C-N-C-O-C=O"     ,                                           552  );
        smartsPatternBits.put("C:N:C-C-N=C"     ,                                           553  );
        smartsPatternBits.put("O-C=C-N-C-C"     ,                                           554  );
        smartsPatternBits.put("C:C-N-C-C:O"     ,                                           555  );
        smartsPatternBits.put("C:N:C:C-C:O"     ,                                           556  );
        neighbourBits.put(                                                                  557  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Cl",null,false),new Neighbour("Cl",null,false)}                                                                            ));
        ringSetBits.put(ringsetCountPerSet+"10"         ,                                   558  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "5", 559  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5", 560  );
        smartsPatternBits.put("C:C:C:C=C-C"     ,                                           561  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4", 562  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "5", 563  );
        smartsPatternBits.put("O=C-C:N:C:O"     ,                                           564  );
        smartsPatternBits.put("O:C:C:C-N-C"     ,                                           565  );
        smartsPatternBits.put("C-C-N:C:N-C"     ,                                           566  );
        smartsPatternBits.put("N:C:N:N-C:C"     ,                                           567  );
        smartsPatternBits.put("O-C-C-C-C:O"     ,                                           568  );
        smartsPatternBits.put("O=C-N-C=C-O"     ,                                           569  );
        smartsPatternBits.put("N:C:N:C-N:C"     ,                                           570  );
        smartsPatternBits.put("C:C:N-C-C:N"     ,                                           571  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4", 572  );
        smartsPatternBits.put("O:C-C-C-C-N"     ,                                           573  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3", 574  );
        smartsPatternBits.put("S-S-C:C"     ,                                               575  );
        smartsPatternBits.put("C-C-O-C-C:O"     ,                                           576  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2", 577  );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2", 578  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3", 579  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5", 580  );
        smartsPatternBits.put("C:N:C:C:C:O"     ,                                           581  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4", 582  );
        smartsPatternBits.put("O:C:C:C:C:N"     ,                                           583  );
        ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "2", 584  );
        elemCntBits.put("K1",                                                               585  );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "2", 586  );
        smartsPatternBits.put("N:C:N:N:N:C"     ,                                           587  );
        smartsPatternBits.put("O=C-C-C-C:O"     ,                                           588  );
        ringSetBits.put(ringsetCountTotalPrefix+"4"     ,                                   589  );
        elemCntBits.put("Se1",                                                              590  );
        smartsPatternBits.put("N-C-C-C:C:O"     ,                                           591  );
        smartsPatternBits.put("O:C-N-C:C:C"     ,                                           592  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4", 593  );
        atomPairBits.put("Cl-O",                                                            594  );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2", 595  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5", 596  );
        ringSetBits.put(ringsetPairPrefix+"3_3"         ,                                   597  );
        atomPairBits.put("F-S",                                                             598  );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "2", 599  );
        smartsPatternBits.put("C:S:C:C-C:O"     ,                                           600  );
        smartsPatternBits.put("N:N:C-C-C:N"     ,                                           601  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4", 602  );
        atomPairBits.put("P-S",                                                             603  );
        elemCntBits.put("Li1",                                                              604  );
        ringSetBits.put(ringsetCountPerSet+"30"         ,                                   605  );
        elemCntBits.put("Tc1",                                                              606  );
        elemCntBits.put("Au1",                                                              607  );
        neighbourBits.put(                                                                  608  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,true)}                                                                               ));
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5", 609  );
        elemCntBits.put("Fe1",                                                              610  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5", 611  );
        atomPairBits.put("B-N",                                                             612  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3", 613  );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2", 614  );
        smartsPatternBits.put("Br-C-C-C:C"  ,                                               615  );
        ringSetBits.put(ringsetPairPrefix+"3_4"         ,                                   616  );
        elemCntBits.put("Cu1",                                                              617  );
        neighbourBits.put(                                                                  618  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5", 619  );
        atomPairBits.put("B-F",                                                             620  );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2", 621  );
        smartsPatternBits.put("Cl-C-C-Cl"   ,                                               622  );
        ringBits.put(ringNonaromDoubleBond+"2_3",                                           623  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3", 624  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4", 625  );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5", 626  );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2", 627  );
        elemCntBits.put("Ru1",                                                              628  );
        elemCntBits.put      (otherElem         ,                                           629  );
        elemCntBits.put("Mn1",                                                              630  );
        elemCntBits.put("Zn1",                                                              631  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4", 632  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4", 633  );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5", 634  );
        atomPairBits.put("P-F",                                                             635  );
        neighbourBits.put(                                                                  636  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("P",IBond.Order.DOUBLE,false)}                                                                                               ));
        elemCntBits.put("Re1",                                                              637  );
        elemCntBits.put("Sn1",                                                              638  );
        elemCntBits.put("As1",                                                              639  );
        elemCntBits.put("Te1",                                                              640  );
        atomPairBits.put("P-Cl",                                                            641  );
        elemCntBits.put("Co1",                                                              642  );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2", 643  );
        elemCntBits.put("Bi1",                                                              644  );
        elemCntBits.put("Ni1",                                                              645  );
        elemCntBits.put("Pd1",                                                              646  );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2", 647  );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5", 648  );
        neighbourBits.put(                                                                  649  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false)}                                                  ));
        elemCntBits.put("Sb1",                                                              650  );
        neighbourBits.put(                                                                  651  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}      ));
        elemCntBits.put("Ca1",                                                              652  );
        elemCntBits.put("V1" ,                                                              653  );
        elemCntBits.put("Cr1",                                                              654  );
        elemCntBits.put("Hg1",                                                              655  );
        atomPairBits.put("Cl-N",                                                            656  );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2", 657  );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5", 658  );

    
        Iterator<String> iterator = elemCntBits.keySet().iterator();
        while (iterator.hasNext()) {
            String elemSymbol = iterator.next();
            elemSymbol=elemSymbol.replaceAll("[0-9]","");
            elemFingerprinted.put(elemSymbol, elemSymbol);
        }
        
    }


}
