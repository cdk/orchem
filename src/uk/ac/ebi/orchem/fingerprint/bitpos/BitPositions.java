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
    // Commented out - bits that are very common (may reconsider)
    //94%        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "1",        512   );
    //88%        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "1",        513   );
    //83%        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",        516   );
    //80%        neighbourBits.put(                                                                         144   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    //80%        neighbourBits.put(                                                                         145   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    //77%        neighbourBits.put(                                                                         146   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    //77%        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "2",        521   );

    /* Position 0 is reserved !*/
    neighbourBits.put(                                                                    1  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "2",   2  );
    elemCntBits.put("O3" ,                                                                3  );
    elemCntBits.put("C20",                                                                4  );
    ringSetBits.put(ringsetCountTotalPrefix+"1"     ,                                     5  );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "1",   6  );
    elemCntBits.put("N3" ,                                                                7  );
    smartsPatternBits.put("C-C-N-C-C"   ,                                                 8  );
    neighbourBits.put(                                                                    9  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",  10  );
    smartsPatternBits.put("C:C-C-C"     ,                                                11  );
    smartsPatternBits.put("C-C-C-C-C"   ,                                                12  );
    neighbourBits.put(                                                                   13  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                   14  , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "3",  15  );
    smartsPatternBits.put("N-C-C-C-C"   ,                                                16  );
    neighbourBits.put(                                                                   17  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));
    neighbourBits.put(                                                                   18  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O=C-C-C"     ,                                                19  );
    neighbourBits.put(                                                                   20  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));
    neighbourBits.put(                                                                   21  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                ));
    neighbourBits.put(                                                                   22  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                                                ));
    neighbourBits.put(                                                                   23  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));
    smartsPatternBits.put("O=C-N-C-C"   ,                                                24  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "1",  25  );
    neighbourBits.put(                                                                   26  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                                               ));
    neighbourBits.put(                                                                   27  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true)}                                                                               ));
    elemCntBits.put("C24",                                                               28  );
    elemCntBits.put("N4" ,                                                               29  );
    neighbourBits.put(                                                                   30  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                ));
    neighbourBits.put(                                                                   31  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                   32  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    smartsPatternBits.put("C-C-C-C:C"   ,                                                33  );
    smartsPatternBits.put("O=C-C-C-C"   ,                                                34  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "1",  35  );
    neighbourBits.put(                                                                   36  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("O-C-C-C-C"   ,                                                37  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",  38  );
    smartsPatternBits.put("N-C-C:C"     ,                                                39  );
    neighbourBits.put(                                                                   40  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                  ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "3",  41  );
    ringSetBits.put(ringsetPairPrefix+"5_6"         ,                                    42  );
    neighbourBits.put(                                                                   43  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",  44  );
    elemCntBits.put("O5" ,                                                               45  );
    neighbourBits.put(                                                                   46  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("C-N-C:C"     ,                                                47  );
    smartsPatternBits.put("C:C-O-C"     ,                                                48  );
    smartsPatternBits.put("C-N-C-C:C"   ,                                                49  );
    smartsPatternBits.put("N-C-C-N"     ,                                                50  );
    elemCntBits.put("S1" ,                                                               51  );
    smartsPatternBits.put("N-C-C-N-C"   ,                                                52  );
    smartsPatternBits.put("O=C-C:C"     ,                                                53  );
    smartsPatternBits.put("O-C-C-N"     ,                                                54  );
    neighbourBits.put(                                                                   55  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true)}                                                                               ));
    neighbourBits.put(                                                                   56  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "1",  57  );
    ringSetBits.put(hexRingMixNeighPrefix+"1",                                           58  );
    smartsPatternBits.put("N-C-C-C:C"   ,                                                59  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",  60  );
    elemCntBits.put("N5" ,                                                               61  );
    smartsPatternBits.put("O=C-C-N"     ,                                                62  );
    elemCntBits.put("C28",                                                               63  );
    smartsPatternBits.put("O-C-C-N-C"   ,                                                64  );
    smartsPatternBits.put("C-C:N:C"     ,                                                65  );
    smartsPatternBits.put("N:C:C-C"     ,                                                66  );
    neighbourBits.put(                                                                   67  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                  ));
    smartsPatternBits.put("O=C-C-N-C"   ,                                                68  );
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "4",  69  );
    smartsPatternBits.put("C-C=C-C"     ,                                                70  );
    neighbourBits.put(                                                                   71  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,false)}                                                                              ));
    neighbourBits.put(                                                                   72  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));
    neighbourBits.put(                                                                   73  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                   ));
    smartsPatternBits.put("C-C-O-C-C"   ,                                                74  );
    elemCntBits.put("Cl1",                                                               75  );
    ringSetBits.put(ringsetCountPerSet+"3"          ,                                    76  );
    ringSetBits.put(ringsetCountConnectedPrefix+"2" ,                                    77  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3",  78  );
    smartsPatternBits.put("N:C:N:C"     ,                                                79  );
    ringBits.put(ringNonaromDoubleBond+"1_1",                                            80  );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "2",  81  );
    smartsPatternBits.put("C-C-C=C"     ,                                                82  );
    neighbourBits.put(                                                                   83  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));
    smartsPatternBits.put("O-C-C:C"     ,                                                84  );
    smartsPatternBits.put("O-C-C-C-N"   ,                                                85  );
    smartsPatternBits.put("C-C-O-C:C"   ,                                                86  );
    elemCntBits.put("F1" ,                                                               87  );
    neighbourBits.put(                                                                   88  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true)}                                                                               ));
    neighbourBits.put(                                                                   89  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("Cl",null,false)}                                                                              ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",  90  );
    smartsPatternBits.put("O=C-C=C"     ,                                                91  );
    smartsPatternBits.put("C-C:C:C-C"   ,                                                92  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",  93  );
    elemCntBits.put("O7" ,                                                               94  );
    smartsPatternBits.put("C-C-C:C-C"   ,                                                95  );
    neighbourBits.put(                                                                   96  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O-C:C:C-C"   ,                                                97  );
    elemCntBits.put("C32",                                                               98  );
    smartsPatternBits.put("C-C-C=C-C"   ,                                                99  );
    neighbourBits.put(                                                                  100  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                                               ));
    neighbourBits.put(                                                                  101  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("N-C:C-C"     ,                                               102  );
    elemCntBits.put("N6" ,                                                              103  );
    smartsPatternBits.put("O-C:C-C"     ,                                               104  );
    smartsPatternBits.put("N:C:C:N"     ,                                               105  );
    smartsPatternBits.put("O-C-C-O"     ,                                               106  );
    smartsPatternBits.put("C:C-C:C"     ,                                               107  );
    smartsPatternBits.put("N-C:N:C"     ,                                               108  );
    smartsPatternBits.put("N-C-N-C"     ,                                               109  );
    atomPairBits.put("N-N",                                                             110  );
    atomPairBits.put("S-O",                                                             111  );
    neighbourBits.put(                                                                  112  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));
    smartsPatternBits.put("N-C-C-O-C"   ,                                               113  );
    smartsPatternBits.put("O=C-C=C-C"   ,                                               114  );
    smartsPatternBits.put("N-C-C-C-N"   ,                                               115  );
    smartsPatternBits.put("O-C-C-O-C"   ,                                               116  );
    neighbourBits.put(                                                                  117  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    neighbourBits.put(                                                                  118  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                   ));
    neighbourBits.put(                                                                  119  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("S",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 120  );
    neighbourBits.put(                                                                  121  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("C",null,true)}                                                                               ));
    neighbourBits.put(                                                                  122  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                  123  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                  124  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C-O-C-C:C"   ,                                               125  );
    smartsPatternBits.put("C=C-C-C-C"   ,                                               126  );
    smartsPatternBits.put("C:C-C=C"     ,                                               127  );
    smartsPatternBits.put("C=C-C:C"     ,                                               128  );
    smartsPatternBits.put("N-C-N-C-C"   ,                                               129  );
    neighbourBits.put(                                                                  130  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                  ));
    neighbourBits.put(                                                                  131  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                  132  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "2", 133  );
    smartsPatternBits.put("C-C:C-N-C"   ,                                               134  );
    smartsPatternBits.put("N-C-C=C"     ,                                               135  );
    smartsPatternBits.put("O=C-C-C-N"   ,                                               136  );
    smartsPatternBits.put("O-C-C-C-O"   ,                                               137  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "4", 138  );
    smartsPatternBits.put("C-C:C-O-C"   ,                                               139  );
    ringSetBits.put(ringsetCountHexPrefix+"3"       ,                                   140  );
    neighbourBits.put(                                                                  141  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                  ));
    smartsPatternBits.put("N:C-C:C"     ,                                               142  );
    atomPairBits.put("N-O",                                                             143  );
    smartsPatternBits.put("N:C:N-C"     ,                                               144  );
    smartsPatternBits.put("C-N-C-N-C"   ,                                               145  );
    neighbourBits.put(                                                                  146  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 147  );
    neighbourBits.put(                                                                  148  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("F",null,false)}                                                                               ));
    smartsPatternBits.put("N-C:C:C-C"   ,                                               149  );
    smartsPatternBits.put("N-C:C:C:N"   ,                                               150  );
    atomPairBits.put("S-N",                                                             151  );
    smartsPatternBits.put("N-C-O-C-C"   ,                                               152  );
    smartsPatternBits.put("O-C-C=C"     ,                                               153  );
    smartsPatternBits.put("C-C-S-C"     ,                                               154  );
    smartsPatternBits.put("C:C-C-C:C"   ,                                               155  );
    neighbourBits.put(                                                                  156  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                  157  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));
    neighbourBits.put(                                                                  158  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                  ));
    smartsPatternBits.put("O-C-C-C=O"   ,                                               159  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "2", 160  );
    ringSetBits.put(ringsetCountPerSet+"4"          ,                                   161  );
    smartsPatternBits.put("N-C=N-C"     ,                                               162  );
    smartsPatternBits.put("O-C:C-O"     ,                                               163  );
    smartsPatternBits.put("O-C-C=O"     ,                                               164  );
    neighbourBits.put(                                                                  165  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));
    smartsPatternBits.put("N-C-C:C-C"   ,                                               166  );
    smartsPatternBits.put("O-C:C-C-C"   ,                                               167  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 168  );
    neighbourBits.put(                                                                  169  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));
    smartsPatternBits.put("O-C-C=C-C"   ,                                               170  );
    smartsPatternBits.put("N=C-N-C"     ,                                               171  );
    smartsPatternBits.put("O=C-C:C-C"   ,                                               172  );
    smartsPatternBits.put("O=C-C-C:C"   ,                                               173  );
    ringSetBits.put(ringsetCountTotalPrefix+"2"     ,                                   174  );
    smartsPatternBits.put("O-C:C-O-C"   ,                                               175  );
    neighbourBits.put(                                                                  176  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("F",null,false)}                                                                              ));
    elemCntBits.put("F3" ,                                                              177  );
    elemCntBits.put("Cl2",                                                              178  );
    smartsPatternBits.put("N-C:C-C-C"   ,                                               179  );
    neighbourBits.put(                                                                  180  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("F",null,false),new Neighbour("F",null,false)}                                                                              ));
    smartsPatternBits.put("N-S-C:C"     ,                                               181  );
    ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "5", 182  );
    smartsPatternBits.put("O-C-C-C:C"   ,                                               183  );
    elemCntBits.put("S2" ,                                                              184  );
    smartsPatternBits.put("O-C-O-C"     ,                                               185  );
    smartsPatternBits.put("C-C:C-C:C"   ,                                               186  );
    ringBits.put(ringNonaromDoubleBond+"2_1",                                           187  );
    smartsPatternBits.put("C-S-C-C-C"   ,                                               188  );
    smartsPatternBits.put("N-C-N-C:C"   ,                                               189  );
    ringSetBits.put(hexRingNonAromNeighPrefix+"1",                                      190  );
    smartsPatternBits.put("N-C:C:N"     ,                                               191  );
    neighbourBits.put(                                                                  192  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    smartsPatternBits.put("C-O-C-C=C"   ,                                               193  );
    smartsPatternBits.put("O=C-N-C=O"   ,                                               194  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2", 195  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 196  );
    smartsPatternBits.put("N=C-N-C-C"   ,                                               197  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2", 198  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1", 199  );
    smartsPatternBits.put("O=C-C:C-N"   ,                                               200  );
    smartsPatternBits.put("O=C-C-O-C"   ,                                               201  );
    smartsPatternBits.put("O-C:C:C-O"   ,                                               202  );
    smartsPatternBits.put("C-S-C:C"     ,                                               203  );
    neighbourBits.put(                                                                  204  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "1", 205  );
    ringSetBits.put(ringsetCountPentPrefix+"2"      ,                                   206  );
    smartsPatternBits.put("O=C-C:N"     ,                                               207  );
    smartsPatternBits.put("S:C:C:C"     ,                                               208  );
    ringSetBits.put(hexRingMixNeighPrefix+"2",                                          209  );
    elemCntBits.put("O12",                                                              210  );
    smartsPatternBits.put("C-O-C-O-C"   ,                                               211  );
    smartsPatternBits.put("O=C-C=C-N"   ,                                               212  );
    smartsPatternBits.put("C:S:C-C"     ,                                               213  );
    smartsPatternBits.put("O=C-N-C-N"   ,                                               214  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "2", 215  );
    neighbourBits.put(                                                                  216  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("Cl-C:C:C-C"  ,                                               217  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4", 218  );
    neighbourBits.put(                                                                  219  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));
    elemCntBits.put("Br1",                                                              220  );
    smartsPatternBits.put("O-C-O-C-C"   ,                                               221  );
    smartsPatternBits.put("O=C-C:C-O"   ,                                               222  );
    neighbourBits.put(                                                                  223  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("N",null,false)}                                                                              ));
    neighbourBits.put(                                                                  224  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false)}                                                                                               ));
    smartsPatternBits.put("O-C-C-C=C"   ,                                               225  );
    smartsPatternBits.put("N=C-C-C"     ,                                               226  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2", 227  );
    smartsPatternBits.put("C:C:N:N:C"   ,                                               228  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "3", 229  );
    neighbourBits.put(                                                                  230  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                  231  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));
    smartsPatternBits.put("O-C=C-C"     ,                                               232  );
    neighbourBits.put(                                                                  233  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                   ));
    neighbourBits.put(                                                                  234  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                  ));
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "3", 235  );
    neighbourBits.put(                                                                  236  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));
    neighbourBits.put(                                                                  237  , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));
    ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "1", 238  );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "1", 239  );
    smartsPatternBits.put("C=C-C=C"     ,                                               240  );
    ringSetBits.put(ringsetPairPrefix+"6_7"         ,                                   241  );
    neighbourBits.put(                                                                  242  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    neighbourBits.put(                                                                  243  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C-C=C-C=C"   ,                                               244  );
    smartsPatternBits.put("O-C-C:C-C"   ,                                               245  );
    neighbourBits.put(                                                                  246  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    ringSetBits.put(ringsetCountConnectedPrefix+"3" ,                                   247  );
    smartsPatternBits.put("Cl-C:C-C"    ,                                               248  );
    smartsPatternBits.put("O=N-C:C"     ,                                               249  );
    neighbourBits.put(                                                                  250  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,true)}                                                                              ));
    ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "1", 251  );
    smartsPatternBits.put("S:C:C:N"     ,                                               252  );
    ringSetBits.put(ringsetPairPrefix+"5_5"         ,                                   253  );
    smartsPatternBits.put("N-N-C-C"     ,                                               254  );
    smartsPatternBits.put("C:C-N-C:C"   ,                                               255  );
    smartsPatternBits.put("C-C-S-C-C"   ,                                               256  );
    elemCntBits.put("P1" ,                                                              257  );
    ringSetBits.put(hexRingNonAromNeighPrefix+"2",                                      258  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2", 259  );
    smartsPatternBits.put("O-C:C:C-N"   ,                                               260  );
    smartsPatternBits.put("O-C:C-N"     ,                                               261  );
    atomPairBits.put("P-O",                                                             262  );
    ringSetBits.put(ringsetCountPerSet+"5"          ,                                   263  );
    neighbourBits.put(                                                                  264  , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));
    smartsPatternBits.put("C-O-C=C"     ,                                               265  );
    neighbourBits.put(                                                                  266  , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));
    neighbourBits.put(                                                                  267  , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("C:N-C:C"     ,                                               268  );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 269  );
    smartsPatternBits.put("O=C-N-N"     ,                                               270  );
    ringSetBits.put(ringsetCountHexPrefix+"4"       ,                                   271  );
    smartsPatternBits.put("O=C-C-C=O"   ,                                               272  );
    smartsPatternBits.put("N=C-C=C"     ,                                               273  );
    smartsPatternBits.put("N-C-S-C"     ,                                               274  );
    smartsPatternBits.put("O-N-C-C"     ,                                               275  );
    ringSetBits.put(hexRingAromNeighPrefix+"1",                                         276  );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1", 277  );
    ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "1", 278  );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "1", 279  );
    smartsPatternBits.put("S-C:C-C"     ,                                               280  );
    smartsPatternBits.put("O=S-C-C"     ,                                               281  );
    smartsPatternBits.put("C=N-N-C"     ,                                               282  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3", 283  );
    ringBits.put(ringNonaromDoubleBond+"1_2",                                           284  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1", 285  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "5", 286  );
    smartsPatternBits.put("O-C:C:N"     ,                                               287  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2", 288  );
    smartsPatternBits.put("C-C=N-N-C"   ,                                               289  );
    smartsPatternBits.put("N-C:C-N"     ,                                               290  );
    carbonTrails.put(cTrailPrefix+"14",                                                 291  );
    smartsPatternBits.put("S-C:N:C"     ,                                               292  );
    elemCntBits.put("O16",                                                              293  );
    neighbourBits.put(                                                                  294  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false)}                                                                                               ));
    smartsPatternBits.put("N-C:C:C-N"   ,                                               295  );
    neighbourBits.put(                                                                  296  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));
    neighbourBits.put(                                                                  297  , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));
    neighbourBits.put(                                                                  298  , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "1", 299  );
    smartsPatternBits.put("N-C:N:N"     ,                                               300  );
    smartsPatternBits.put("Cl-C:C-Cl"   ,                                               301  );
    smartsPatternBits.put("O=C-O-C:C"   ,                                               302  );
    smartsPatternBits.put("O-C-C:C-O"   ,                                               303  );
    carbonTrails.put(cTrailPrefix+"15",                                                 304  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2", 305  );
    neighbourBits.put(                                                                  306  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Cl",null,false)}                                                                             ));
    elemCntBits.put("I1",                                                               307  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2", 308  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "3", 309  );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1", 310  );
    carbonTrails.put(cTrailPrefix+"16",                                                 311  );
    smartsPatternBits.put("N#C-C-C"     ,                                               312  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "4", 313  );
    neighbourBits.put(                                                                  314  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));
    smartsPatternBits.put("N-N-C-N"     ,                                               315  );
    ringSetBits.put(ringsetPairPrefix+"5_7"         ,                                   316  );
    smartsPatternBits.put("Br-C:C:C-C"  ,                                               317  );
    neighbourBits.put(                                                                  318  , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}       ));
    ringSetBits.put(ringsetCountPerSet+"6"          ,                                   319  );
    neighbourBits.put(                                                                  320  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));
    smartsPatternBits.put("S-C:C-N"     ,                                               321  );
    ringSetBits.put(ringsetCountConnectedPrefix+"4" ,                                   322  );
    smartsPatternBits.put("Cl-C:C-O"    ,                                               323  );
    smartsPatternBits.put("S-C=N-C"     ,                                               324  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5", 325  );
    neighbourBits.put(                                                                  326  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("S",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "4", 327  );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1", 328  );
    ringSetBits.put(ringsetPairPrefix+"4_6"         ,                                   329  );
    smartsPatternBits.put("C-C-C#C"     ,                                               330  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2", 331  );
    elemCntBits.put("Na1",                                                              332  );
    smartsPatternBits.put("N-N-C:C"     ,                                               333  );
    atomPairBits.put("S-S",                                                             334  );
    neighbourBits.put(                                                                  335  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("I",null,false)}                                                                               ));
    smartsPatternBits.put("Cl-C:C-O-C"  ,                                               336  );
    smartsPatternBits.put("S=C-N-C"     ,                                               337  );
    ringSetBits.put(ringsetCountPentPrefix+"3"      ,                                   338  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3", 339  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "3", 340  );
    carbonTrails.put(cTrailPrefix+"17",                                                 341  );
    ringSetBits.put(ringsetCountHexPrefix+"5"       ,                                   342  );
    neighbourBits.put(                                                                  343  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));
    elemCntBits.put("P2" ,                                                              344  );
    smartsPatternBits.put("Cl-C:C-C=O"  ,                                               345  );
    carbonTrails.put(cTrailPrefix+"18",                                                 346  );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 347  );
    smartsPatternBits.put("N#C-C-C-C"   ,                                               348  );
    elemCntBits.put("Br2",                                                              349  );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1", 350  );
    ringSetBits.put(hexRingAromNeighPrefix+"2",                                         351  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2", 352  );
    ringSetBits.put(ringsetPairPrefix+"6_8"         ,                                   353  );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 354  );
    smartsPatternBits.put("N#C-C=C"     ,                                               355  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "5", 356  );
    neighbourBits.put(                                                                  357  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false)}                                                                             ));
    smartsPatternBits.put("S-C:C:C-N"   ,                                               358  );
    smartsPatternBits.put("O-C-C=N"     ,                                               359  );
    ringSetBits.put(ringsetPairPrefix+"3_5"         ,                                   360  );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1", 361  );
    smartsPatternBits.put("Cl-C-C-N-C"  ,                                               362  );
    smartsPatternBits.put("Cl-C-C-C"    ,                                               363  );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 364  );
    smartsPatternBits.put("Br-C:C-C"    ,                                               365  );
    atomPairBits.put("O-O",                                                             366  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3", 367  );
    ringSetBits.put(ringsetPairPrefix+"4_5"         ,                                   368  );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1", 369  );
    ringBits.put(ringNonaromDoubleBond+"2_2",                                           370  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4", 371  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3", 372  );
    ringSetBits.put(ringsetCountTotalPrefix+"3"     ,                                   373  );
    smartsPatternBits.put("O-S-C:C"     ,                                               374  );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1", 375  );
    ringSetBits.put(ringsetPairPrefix+"3_6"         ,                                   376  );
    ringSetBits.put(ringsetCountPerSet+"8"          ,                                   377  );
    smartsPatternBits.put("O=N-C:C-N"   ,                                               378  );
    carbonTrails.put(cTrailPrefix+"19",                                                 379  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "4", 380  );
    smartsPatternBits.put("S-C:C-O"     ,                                               381  );
    ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "2", 382  );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "2", 383  );
    smartsPatternBits.put("N-C:O:C"     ,                                               384  );
    atomPairBits.put("P-N",                                                             385  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3", 386  );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1", 387  );
    ringSetBits.put(ringsetCountPentPrefix+"4"      ,                                   388  );
    smartsPatternBits.put("Cl-C-C=O"    ,                                               389  );
    ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "5", 390  );
    neighbourBits.put(                                                                  391  , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));
    carbonTrails.put(cTrailPrefix+"20",                                                 392  );
    smartsPatternBits.put("Cl-C-C-C-C"  ,                                               393  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4", 394  );
    elemCntBits.put("B1" ,                                                              395  );
    smartsPatternBits.put("S-C-S-C"     ,                                               396  );
    elemCntBits.put("Si1",                                                              397  );
    neighbourBits.put(                                                                  398  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "4", 399  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2", 400  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4", 401  );
    elemCntBits.put("Pt1",                                                              402  );
    neighbourBits.put(                                                                  403  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Si",null,false)}                                                                             ));
    ringSetBits.put(ringsetCountPerSet+"10"         ,                                   404  );
    ringSetBits.put(ringsetCountHexPrefix+"6"       ,                                   405  );
    smartsPatternBits.put("O=N-C:C-O"   ,                                               406  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3", 407  );
    smartsPatternBits.put("Br-C-C-C"    ,                                               408  );
    neighbourBits.put(                                                                  409  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("I",null,false)}                                                                              ));
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5", 410  );
    atomPairBits.put("B-O",                                                             411  );
    ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "2", 412  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3", 413  );
    neighbourBits.put(                                                                  414  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true),new Neighbour("Cl",null,false)}                                                 ));
    smartsPatternBits.put("O=S-C-N"     ,                                               415  );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2", 416  );
    neighbourBits.put(                                                                  417  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("N",null,true)}                                                                              ));
    ringBits.put(ringNonaromDoubleBond+"1_3",                                           418  );
    smartsPatternBits.put("Br-C-C=O"    ,                                               419  );
    smartsPatternBits.put("Cl-C-C-O"    ,                                               420  );
    atomPairBits.put("Si-O",                                                            421  );
    ringSetBits.put(ringsetPairPrefix+"6_12"        ,                                   422  );
    neighbourBits.put(                                                                  423  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Cl",null,false),new Neighbour("Cl",null,false)}                                                                            ));
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4", 424  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "5", 425  );
    smartsPatternBits.put("S-S-C:C"     ,                                               426  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3", 427  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2", 428  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "5", 429  );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2", 430  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3", 431  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5", 432  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5", 433  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4", 434  );
    elemCntBits.put("K1",                                                               435  );
    ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "2", 436  );
    ringSetBits.put(ringsetPairPrefix+"5_8"         ,                                   437  );
    ringSetBits.put(ringsetCountTotalPrefix+"4"     ,                                   438  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4", 439  );
    elemCntBits.put("Se1",                                                              440  );
    atomPairBits.put("Cl-O",                                                            441  );
    ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2", 442  );
    ringSetBits.put(ringsetPairPrefix+"6_15"        ,                                   443  );
    ringSetBits.put(ringsetPairPrefix+"3_3"         ,                                   444  );
    ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "2", 445  );
    atomPairBits.put("F-S",                                                             446  );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "2", 447  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4", 448  );
    atomPairBits.put("P-S",                                                             449  );
    elemCntBits.put("Li1",                                                              450  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4", 451  );
    elemCntBits.put("Tc1",                                                              452  );
    elemCntBits.put("Au1",                                                              453  );
    neighbourBits.put(                                                                  454  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,true)}                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5", 455  );
    elemCntBits.put("Fe1",                                                              456  );
    atomPairBits.put("B-N",                                                             457  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5", 458  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3", 459  );
    smartsPatternBits.put("Br-C-C-C:C"  ,                                               460  );
    elemCntBits.put("Cu1",                                                              461  );
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5", 462  );
    neighbourBits.put(                                                                  463  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5", 464  );
    atomPairBits.put("B-F",                                                             465  );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "2", 466  );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2", 467  );
    smartsPatternBits.put("Cl-C-C-Cl"   ,                                               468  );
    ringSetBits.put(ringsetPairPrefix+"3_7"         ,                                   469  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3", 470  );
    ringBits.put(ringNonaromDoubleBond+"2_3",                                           471  );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2", 472  );
    elemCntBits.put("Ru1",                                                              473  );
    elemCntBits.put("Mn1",                                                              474  );
    elemCntBits.put("Zn1",                                                              475  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4", 476  );
    ringSetBits.put(ringsetPairPrefix+"4_7"         ,                                   477  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4", 478  );
    ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5", 479  );
    ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5", 480  );
    ringSetBits.put(ringsetCountPerSet+"30"         ,                                   481  );
    atomPairBits.put("P-F",                                                             482  );
    neighbourBits.put(                                                                  483  , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("P",IBond.Order.DOUBLE,false)}                                                                                               ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4", 484  );
    elemCntBits.put("Re1",                                                              485  );
    elemCntBits.put("Sn1",                                                              486  );
    elemCntBits.put("As1",                                                              487  );
    elemCntBits.put("Te1",                                                              488  );
    atomPairBits.put("P-Cl",                                                            489  );
    elemCntBits.put("Co1",                                                              490  );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2", 491  );
    elemCntBits.put("Bi1",                                                              492  );
    ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2", 493  );
    ringSetBits.put(ringsetPairPrefix+"3_4"         ,                                   494  );
    elemCntBits.put("Ni1",                                                              495  );
    elemCntBits.put("Pd1",                                                              496  );
    neighbourBits.put(                                                                  497  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false)}                                                  ));
    ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5", 498  );
    elemCntBits.put("Sb1",                                                              499  );
    neighbourBits.put(                                                                  500  , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}      ));
    elemCntBits.put("Ca1",                                                              501  );
    elemCntBits.put("V1" ,                                                              502  );
    elemCntBits.put("Cr1",                                                              503  );
    elemCntBits.put("Hg1",                                                              504  );
    atomPairBits.put("Cl-N",                                                            505  );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2", 506  );
    elemCntBits.put("Ge1",                                                              507  );
    elemCntBits.put("In1",                                                              508  );
    elemCntBits.put("Mg1",                                                              509  );
    elemCntBits.put("Gd1",                                                              510  );
    elemCntBits.put("Mo1",                                                              511  );
    elemCntBits.put("Y1" ,                                                              512  );
    ringSetBits.put(ringsetPairPrefix+"4_8"         ,                                   513  );
    elemCntBits.put("Ag1",                                                              514  );
    elemCntBits.put("Al1",                                                              515  );
    elemCntBits.put("Os1",                                                              516  );
    elemCntBits.put("Ga1",                                                              517  );
    elemCntBits.put("Rh1",                                                              518  );
    elemCntBits.put("Eu1",                                                              519  );
    elemCntBits.put("W1" ,                                                              520  );
    ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5", 521  );
    elemCntBits.put("La1",                                                              522  );
    elemCntBits.put("Nb1",                                                              523  );
    elemCntBits.put("Pb1",                                                              524  );
    elemCntBits.put("Ti1",                                                              525  );
    elemCntBits.put("Tl1",                                                              526  );
    ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2", 527  );
    elemCntBits.put("Ac1",                                                              528  );
    elemCntBits.put("Am1",                                                              529  );
    elemCntBits.put("Ar1",                                                              530  );
    elemCntBits.put("At1",                                                              531  );
    elemCntBits.put("Ba1",                                                              532  );
    elemCntBits.put("Be1",                                                              533  );
    elemCntBits.put("Bh1",                                                              534  );
    elemCntBits.put("Bk1",                                                              535  );
    elemCntBits.put("Cd1",                                                              536  );
    elemCntBits.put("Ce1",                                                              537  );
    elemCntBits.put("Cf1",                                                              538  );
    elemCntBits.put("Cm1",                                                              539  );
    elemCntBits.put("Cs1",                                                              540  );
    elemCntBits.put("Db1",                                                              541  );
    elemCntBits.put("Ds1",                                                              542  );
    elemCntBits.put("Dy1",                                                              543  );
    elemCntBits.put("Er1",                                                              544  );
    elemCntBits.put("Es1",                                                              545  );
    elemCntBits.put("Fm1",                                                              546  );
    elemCntBits.put("Fr1",                                                              547  );
    elemCntBits.put("He1",                                                              548  );
    elemCntBits.put("Hf1",                                                              549  );
    elemCntBits.put("Ho1",                                                              550  );
    elemCntBits.put("Hs1",                                                              551  );
    elemCntBits.put("Ir1",                                                              552  );
    elemCntBits.put("Kr1",                                                              553  );
    elemCntBits.put("Lr1",                                                              554  );
    elemCntBits.put("Lu1",                                                              555  );
    elemCntBits.put("Md1",                                                              556  );
    elemCntBits.put("Mt1",                                                              557  );
    elemCntBits.put("Nd1",                                                              558  );
    elemCntBits.put("Ne1",                                                              559  );
    elemCntBits.put("No1",                                                              560  );
    elemCntBits.put("Np1",                                                              561  );
    elemCntBits.put("Pa1",                                                              562  );
    elemCntBits.put("Pm1",                                                              563  );
    elemCntBits.put("Po1",                                                              564  );
    elemCntBits.put("Pr1",                                                              565  );
    elemCntBits.put("Pu1",                                                              566  );
    elemCntBits.put("Ra1",                                                              567  );
    elemCntBits.put("Rb1",                                                              568  );
    elemCntBits.put("Rf1",                                                              569  );
    elemCntBits.put("Rg1",                                                              570  );
    elemCntBits.put("Rn1",                                                              571  );
    elemCntBits.put("Sc1",                                                              572  );
    elemCntBits.put("Sg1",                                                              573  );
    elemCntBits.put("Sm1",                                                              574  );
    elemCntBits.put("Sr1",                                                              575  );
    elemCntBits.put("Ta1",                                                              576  );
    elemCntBits.put("Tb1",                                                              577  );
    elemCntBits.put("Th1",                                                              578  );
    elemCntBits.put("Tm1",                                                              579  );
    elemCntBits.put("U1" ,                                                              580  );
    elemCntBits.put("Uub1",                                                             581  );
    elemCntBits.put("Xe1",                                                              582  );
    elemCntBits.put("Yb1",                                                              583  );
    elemCntBits.put("Zr1",                                                              584  );

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
