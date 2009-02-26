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

        int bitPos = 0;
        Neighbour[] arr=null;

        elemCntBits.put("C20",                                                                       0   );  
        elemCntBits.put("C24",                                                                       1   );  
        elemCntBits.put("C28",                                                                       2   );  
        elemCntBits.put("C32",                                                                       3   );  
        elemCntBits.put("Cl1",                                                                       4   );  
        elemCntBits.put("Cl2",                                                                       5   );  
        elemCntBits.put("F1" ,                                                                       6   );  
        elemCntBits.put("F3" ,                                                                       7   );  
        elemCntBits.put("N3" ,                                                                       8   );  
        elemCntBits.put("N4" ,                                                                       9   );  
        elemCntBits.put("N5" ,                                                                      10   );  
        elemCntBits.put("N6" ,                                                                      11   );  
        elemCntBits.put("O3" ,                                                                      12   );  
        elemCntBits.put("O5" ,                                                                      13   );  
        elemCntBits.put("O7" ,                                                                      14   );  
        elemCntBits.put("O12",                                                                      15   );  
        elemCntBits.put("O16",                                                                      16   );  
        elemCntBits.put("S1" ,                                                                      17   );  
        elemCntBits.put("S2" ,                                                                      18   );  
        elemCntBits.put("Br1",                                                                      19   );  
        elemCntBits.put("Br2",                                                                      20   );  
        elemCntBits.put("P1" ,                                                                      21   );  
        elemCntBits.put("P2" ,                                                                      22   );  
        elemCntBits.put("B1" ,                                                                      23   );  
        elemCntBits.put("Ac1",                                                                      24   );  
        elemCntBits.put("Ag1",                                                                      25   );  
        elemCntBits.put("Al1",                                                                      26   );  
        elemCntBits.put("Am1",                                                                      27   );  
        elemCntBits.put("Ar1",                                                                      28   );  
        elemCntBits.put("As1",                                                                      29   );  
        elemCntBits.put("At1",                                                                      30   );  
        elemCntBits.put("Au1",                                                                      31   );  
        elemCntBits.put("Ba1",                                                                      32   );  
        elemCntBits.put("Be1",                                                                      33   );  
        elemCntBits.put("Bh1",                                                                      34   );  
        elemCntBits.put("Bi1",                                                                      35   );  
        elemCntBits.put("Bk1",                                                                      36   );  
        elemCntBits.put("Ca1",                                                                      37   );  
        elemCntBits.put("Cd1",                                                                      38   );  
        elemCntBits.put("Ce1",                                                                      39   );  
        elemCntBits.put("Cf1",                                                                      40   );  
        elemCntBits.put("Cm1",                                                                      41   );  
        elemCntBits.put("Co1",                                                                      42   );  
        elemCntBits.put("Cr1",                                                                      43   );  
        elemCntBits.put("Cs1",                                                                      44   );  
        elemCntBits.put("Cu1",                                                                      45   );  
        elemCntBits.put("Db1",                                                                      46   );  
        elemCntBits.put("Ds1",                                                                      47   );  
        elemCntBits.put("Dy1",                                                                      48   );  
        elemCntBits.put("Er1",                                                                      49   );  
        elemCntBits.put("Es1",                                                                      50   );  
        elemCntBits.put("Eu1",                                                                      51   );  
        elemCntBits.put("Fe1",                                                                      52   );  
        elemCntBits.put("Fm1",                                                                      53   );  
        elemCntBits.put("Fr1",                                                                      54   );  
        elemCntBits.put("Ga1",                                                                      55   );  
        elemCntBits.put("Gd1",                                                                      56   );  
        elemCntBits.put("Ge1",                                                                      57   );  
        elemCntBits.put("He1",                                                                      58   );  
        elemCntBits.put("Hf1",                                                                      59   );  
        elemCntBits.put("Hg1",                                                                      60   );  
        elemCntBits.put("Ho1",                                                                      61   );  
        elemCntBits.put("Hs1",                                                                      62   );  
        elemCntBits.put("I1",                                                                       63   );  
        elemCntBits.put("In1",                                                                      64   );  
        elemCntBits.put("Ir1",                                                                      65   );  
        elemCntBits.put("K1",                                                                       66   );  
        elemCntBits.put("Kr1",                                                                      67   );  
        elemCntBits.put("La1",                                                                      68   );  
        elemCntBits.put("Li1",                                                                      69   );  
        elemCntBits.put("Lr1",                                                                      70   );  
        elemCntBits.put("Lu1",                                                                      71   );  
        elemCntBits.put("Md1",                                                                      72   );  
        elemCntBits.put("Mg1",                                                                      73   );  
        elemCntBits.put("Mn1",                                                                      74   );  
        elemCntBits.put("Mo1",                                                                      75   );  
        elemCntBits.put("Mt1",                                                                      76   );  
        elemCntBits.put("Na1",                                                                      77   );  
        elemCntBits.put("Nb1",                                                                      78   );  
        elemCntBits.put("Nd1",                                                                      79   );  
        elemCntBits.put("Ne1",                                                                      80   );  
        elemCntBits.put("Ni1",                                                                      81   );  
        elemCntBits.put("No1",                                                                      82   );  
        elemCntBits.put("Np1",                                                                      83   );  
        elemCntBits.put("Os1",                                                                      84   );  
        elemCntBits.put("Pa1",                                                                      85   );  
        elemCntBits.put("Pb1",                                                                      86   );  
        elemCntBits.put("Pd1",                                                                      87   );  
        elemCntBits.put("Pm1",                                                                      88   );  
        elemCntBits.put("Po1",                                                                      89   );  
        elemCntBits.put("Pr1",                                                                      90   );  
        elemCntBits.put("Pt1",                                                                      91   );  
        elemCntBits.put("Pu1",                                                                      92   );  
        elemCntBits.put("Ra1",                                                                      93   );  
        elemCntBits.put("Rb1",                                                                      94   );  
        elemCntBits.put("Re1",                                                                      95   );  
        elemCntBits.put("Rf1",                                                                      96   );  
        elemCntBits.put("Rg1",                                                                      97   );  
        elemCntBits.put("Rh1",                                                                      98   );  
        elemCntBits.put("Rn1",                                                                      99   );  
        elemCntBits.put("Ru1",                                                                     100   );  
        elemCntBits.put("Sb1",                                                                     101   );  
        elemCntBits.put("Sc1",                                                                     102   );  
        elemCntBits.put("Se1",                                                                     103   );  
        elemCntBits.put("Sg1",                                                                     104   );  
        elemCntBits.put("Si1",                                                                     105   );  
        elemCntBits.put("Sm1",                                                                     106   );  
        elemCntBits.put("Sn1",                                                                     107   );  
        elemCntBits.put("Sr1",                                                                     108   );  
        elemCntBits.put("Ta1",                                                                     109   );  
        elemCntBits.put("Tb1",                                                                     110   );  
        elemCntBits.put("Tc1",                                                                     111   );  
        elemCntBits.put("Te1",                                                                     112   );  
        elemCntBits.put("Th1",                                                                     113   );  
        elemCntBits.put("Ti1",                                                                     114   );  
        elemCntBits.put("Tl1",                                                                     115   );  
        elemCntBits.put("Tm1",                                                                     116   );  
        elemCntBits.put("U1" ,                                                                     117   );  
        elemCntBits.put("Uub1",                                                                    118   );  
        elemCntBits.put("V1" ,                                                                     119   );  
        elemCntBits.put("W1" ,                                                                     120   );  
        elemCntBits.put("Xe1",                                                                     121   );  
        elemCntBits.put("Y1" ,                                                                     122   );  
        elemCntBits.put("Yb1",                                                                     123   );  
        elemCntBits.put("Zn1",                                                                     124   );  
        elemCntBits.put("Zr1",                                                                     125   );  
        atomPairBits.put("O-O",                                                                    126   );  
        atomPairBits.put("N-O",                                                                    127   );  
        atomPairBits.put("S-O",                                                                    128   );  
        atomPairBits.put("Cl-O",                                                                   129   );  
        atomPairBits.put("P-O",                                                                    130   );  
        atomPairBits.put("B-O",                                                                    131   );  
        atomPairBits.put("Si-O",                                                                   132   );  
        atomPairBits.put("N-N",                                                                    133   );  
        atomPairBits.put("S-N",                                                                    134   );  
        atomPairBits.put("Cl-N",                                                                   135   );  
        atomPairBits.put("P-N",                                                                    136   );  
        atomPairBits.put("B-N",                                                                    137   );  
        atomPairBits.put("S-S",                                                                    138   );  
        atomPairBits.put("F-S",                                                                    139   );  
        atomPairBits.put("P-S",                                                                    140   );  
        atomPairBits.put("P-F",                                                                    141   );  
        atomPairBits.put("B-F",                                                                    142   );  
        atomPairBits.put("P-Cl",                                                                   143   );  
        neighbourBits.put(                                                                         144   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         145   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         146   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));        
        neighbourBits.put(                                                                         147   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));        
        neighbourBits.put(                                                                         148   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false)}                                                                             ));        
        neighbourBits.put(                                                                         149   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                               ));        
        neighbourBits.put(                                                                         150   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,true)}                                                                              ));        
        neighbourBits.put(                                                                         151   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("N",null,true)}                                                                              ));        
        neighbourBits.put(                                                                         152   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                  ));        
        neighbourBits.put(                                                                         153   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));        
        neighbourBits.put(                                                                         154   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                  ));        
        neighbourBits.put(                                                                         155   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                  ));        
        neighbourBits.put(                                                                         156   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                ));        
        neighbourBits.put(                                                                         157   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                ));        
        neighbourBits.put(                                                                         158   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Cl",null,false)}                                                                             ));        
        neighbourBits.put(                                                                         159   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));        
        neighbourBits.put(                                                                         160   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         161   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("S",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         162   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("I",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         163   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("I",null,false)}                                                                               ));        
        neighbourBits.put(                                                                         164   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Si",null,false)}                                                                             ));        
        neighbourBits.put(                                                                         165   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                  ));        
        neighbourBits.put(                                                                         166   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true)}                                                                               ));        
        neighbourBits.put(                                                                         167   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));        
        neighbourBits.put(                                                                         168   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Cl",null,false),new Neighbour("Cl",null,false)}                                                                            ));        
        neighbourBits.put(                                                                         169   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("Cl",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         170   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("F",null,false),new Neighbour("F",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         171   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("F",null,false)}                                                                               ));        
        neighbourBits.put(                                                                         172   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("F",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         173   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         174   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                                               ));        
        neighbourBits.put(                                                                         175   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,false)}                                                  ));        
        neighbourBits.put(                                                                         176   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                  ));        
        neighbourBits.put(                                                                         177   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,false)}                                                                               ));        
        neighbourBits.put(                                                                         178   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         179   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true)}                                                                               ));        
        neighbourBits.put(                                                                         180   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));        
        neighbourBits.put(                                                                         181   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("C",null,true)}                                                                               ));        
        neighbourBits.put(                                                                         182   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("N",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         183   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));        
        neighbourBits.put(                                                                         184   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                   ));        
        neighbourBits.put(                                                                         185   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}                                                                                ));        
        neighbourBits.put(                                                                         186   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                   ));        
        neighbourBits.put(                                                                         187   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         188   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}                                                  ));        
        neighbourBits.put(                                                                         189   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                ));        
        neighbourBits.put(                                                                         190   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         191   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         192   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true)}                                                                               ));        
        neighbourBits.put(                                                                         193   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                  ));        
        neighbourBits.put(                                                                         194   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         195   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,true)}                                                                               ));        
        neighbourBits.put(                                                                         196   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));        
        neighbourBits.put(                                                                         197   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                   ));        
        neighbourBits.put(                                                                         198   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         199   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         200   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         201   , Arrays.asList(new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}                                                                                ));        
        neighbourBits.put(                                                                         202   , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         203   , Arrays.asList(new Neighbour[] { new Neighbour("P",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         204   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         205   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         206   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)}                                                                              ));        
        neighbourBits.put(                                                                         207   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)}                                                ));        
        neighbourBits.put(                                                                         208   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true),new Neighbour("Cl",null,false)}                                                 ));        
        neighbourBits.put(                                                                         209   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         210   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         211   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         212   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         213   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("S",IBond.Order.DOUBLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         214   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         215   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         216   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("P",IBond.Order.DOUBLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         217   , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                                                               ));        
        neighbourBits.put(                                                                         218   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         219   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         220   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));        
        neighbourBits.put(                                                                         221   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));        
        neighbourBits.put(                                                                         222   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}      ));        
        neighbourBits.put(                                                                         223   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));        
        neighbourBits.put(                                                                         224   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));        
        neighbourBits.put(                                                                         225   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));        
        neighbourBits.put(                                                                         226   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}       ));        
        neighbourBits.put(                                                                         227   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         228   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         229   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false)}                                                  ));        
        neighbourBits.put(                                                                         230   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         231   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         232   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         233   , Arrays.asList(new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         234   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         235   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         236   , Arrays.asList(new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         237   , Arrays.asList(new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         238   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         239   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}                                                   ));        
        neighbourBits.put(                                                                         240   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false)}                                                   ));        
        neighbourBits.put(                                                                         241   , Arrays.asList(new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)}       ));
        ringSetBits.put(ringsetCountTotalPrefix+"1"     ,                                          242   );
        ringSetBits.put(ringsetCountTotalPrefix+"2"     ,                                          243   );
        ringSetBits.put(ringsetCountTotalPrefix+"3"     ,                                          244   );
        ringSetBits.put(ringsetCountTotalPrefix+"4"     ,                                          245   );
        ringSetBits.put(ringsetCountPerSet+"3"          ,                                          246   );
        ringSetBits.put(ringsetCountPerSet+"4"          ,                                          247   );
        ringSetBits.put(ringsetCountPerSet+"5"          ,                                          248   );
        ringSetBits.put(ringsetCountPerSet+"6"          ,                                          249   );
        ringSetBits.put(ringsetCountPerSet+"8"          ,                                          250   );
        ringSetBits.put(ringsetCountPerSet+"10"         ,                                          251   );
        ringSetBits.put(ringsetCountPerSet+"30"         ,                                          252   );
        ringSetBits.put(ringsetPairPrefix+"3_3"         ,                                          253   );
        ringSetBits.put(ringsetPairPrefix+"3_4"         ,                                          254   );
        ringSetBits.put(ringsetPairPrefix+"3_5"         ,                                          255   );
        ringSetBits.put(ringsetPairPrefix+"3_6"         ,                                          256   );
        ringSetBits.put(ringsetPairPrefix+"3_7"         ,                                          257   );
        ringSetBits.put(ringsetPairPrefix+"4_5"         ,                                          258   );
        ringSetBits.put(ringsetPairPrefix+"4_6"         ,                                          259   );
        ringSetBits.put(ringsetPairPrefix+"4_7"         ,                                          260   );
        ringSetBits.put(ringsetPairPrefix+"4_8"         ,                                          261   );
        ringSetBits.put(ringsetPairPrefix+"5_5"         ,                                          262   );
        ringSetBits.put(ringsetPairPrefix+"5_6"         ,                                          263   );
        ringSetBits.put(ringsetPairPrefix+"5_7"         ,                                          264   );
        ringSetBits.put(ringsetPairPrefix+"5_8"         ,                                          265   );
        ringSetBits.put(ringsetPairPrefix+"6_7"         ,                                          266   );
        ringSetBits.put(ringsetPairPrefix+"6_8"         ,                                          267   );
        ringSetBits.put(ringsetPairPrefix+"6_12"        ,                                          268   );
        ringSetBits.put(ringsetPairPrefix+"6_15"        ,                                          269   );
        ringSetBits.put(ringsetCountConnectedPrefix+"2" ,                                          270   );
        ringSetBits.put(ringsetCountConnectedPrefix+"3" ,                                          271   );
        ringSetBits.put(ringsetCountConnectedPrefix+"4" ,                                          272   );
        ringSetBits.put(ringsetCountHexPrefix+"3"       ,                                          273   );
        ringSetBits.put(ringsetCountHexPrefix+"4"       ,                                          274   );
        ringSetBits.put(ringsetCountHexPrefix+"5"       ,                                          275   );
        ringSetBits.put(ringsetCountHexPrefix+"6"       ,                                          276   );
        ringSetBits.put(ringsetCountPentPrefix+"2"      ,                                          277   );
        ringSetBits.put(ringsetCountPentPrefix+"3"      ,                                          278   );
        ringSetBits.put(ringsetCountPentPrefix+"4"      ,                                          279   );
        smartsPatternBits.put("Br-C-C-C"    ,                                                      280   );
        smartsPatternBits.put("Br-C-C-C:C"  ,                                                      281   ); 
        smartsPatternBits.put("Br-C-C=O"    ,                                                      282   );
        smartsPatternBits.put("Br-C:C-C"    ,                                                      283   );
        smartsPatternBits.put("Br-C:C:C-C"  ,                                                      284   );
        smartsPatternBits.put("C-C-C#C"     ,                                                      285   );
        smartsPatternBits.put("C-C-C-C-C"   ,                                                      286   );  
        smartsPatternBits.put("C-C-C-C:C"   ,                                                      287   );  
        smartsPatternBits.put("C-C-C:C-C"   ,                                                      288   );  
        smartsPatternBits.put("C-C-C=C"     ,                                                      289   );  
        smartsPatternBits.put("C-C-C=C-C"   ,                                                      290   );  
        smartsPatternBits.put("C-C-N-C-C"   ,                                                      291   );
        smartsPatternBits.put("C-C-O-C-C"   ,                                                      292   );
        smartsPatternBits.put("C-C-O-C:C"   ,                                                      293   );  
        smartsPatternBits.put("C-C-S-C"     ,                                                      294   );
        smartsPatternBits.put("C-C-S-C-C"   ,                                                      295   );
        smartsPatternBits.put("C-C:C-C:C"   ,                                                      296   );
        smartsPatternBits.put("C-C:C-N-C"   ,                                                      297   );
        smartsPatternBits.put("C-C:C-O-C"   ,                                                      298   ); 
        smartsPatternBits.put("C-C:C:C-C"   ,                                                      299   );
        smartsPatternBits.put("C-C:N:C"     ,                                                      300   );  
        smartsPatternBits.put("C-C=C-C"     ,                                                      301   );
        smartsPatternBits.put("C-C=C-C=C"   ,                                                      302   );
        smartsPatternBits.put("C-C=N-N-C"   ,                                                      303   );
        smartsPatternBits.put("C-N-C-C:C"   ,                                                      304   );
        smartsPatternBits.put("C-N-C-N-C"   ,                                                      305   );
        smartsPatternBits.put("C-N-C:C"     ,                                                      306   );
        smartsPatternBits.put("C-O-C-C:C"   ,                                                      307   );
        smartsPatternBits.put("C-O-C-C=C"   ,                                                      308   );
        smartsPatternBits.put("C-O-C-O-C"   ,                                                      309   );
        smartsPatternBits.put("C-O-C=C"     ,                                                      310   );
        smartsPatternBits.put("C-S-C-C-C"   ,                                                      311   );
        smartsPatternBits.put("C-S-C:C"     ,                                                      312   );
        smartsPatternBits.put("C:C-C-C"     ,                                                      313   ); 
        smartsPatternBits.put("C:C-C-C:C"   ,                                                      314   );
        smartsPatternBits.put("C:C-C:C"     ,                                                      315   );
        smartsPatternBits.put("C:C-C=C"     ,                                                      316   ); 
        smartsPatternBits.put("C:C-N-C:C"   ,                                                      317   );
        smartsPatternBits.put("C:C-O-C"     ,                                                      318   );
        smartsPatternBits.put("C:C:N:N:C"   ,                                                      319   );
        smartsPatternBits.put("C:N-C:C"     ,                                                      320   );
        smartsPatternBits.put("C:S:C-C"     ,                                                      321   );
        smartsPatternBits.put("C=C-C-C-C"   ,                                                      322   );
        smartsPatternBits.put("C=C-C:C"     ,                                                      323   );
        smartsPatternBits.put("C=C-C=C"     ,                                                      324   );
        smartsPatternBits.put("C=N-N-C"     ,                                                      325   );
        smartsPatternBits.put("Cl-C-C-C"    ,                                                      326   );
        smartsPatternBits.put("Cl-C-C-C-C"  ,                                                      327   );
        smartsPatternBits.put("Cl-C-C-Cl"   ,                                                      328   );
        smartsPatternBits.put("Cl-C-C-N-C"  ,                                                      329   );
        smartsPatternBits.put("Cl-C-C-O"    ,                                                      330   );
        smartsPatternBits.put("Cl-C-C=O"    ,                                                      331   );
        smartsPatternBits.put("Cl-C:C-C"    ,                                                      332   );
        smartsPatternBits.put("Cl-C:C-C=O"  ,                                                      333   );
        smartsPatternBits.put("Cl-C:C-Cl"   ,                                                      334   );
        smartsPatternBits.put("Cl-C:C-O"    ,                                                      335   );
        smartsPatternBits.put("Cl-C:C-O-C"  ,                                                      336   );
        smartsPatternBits.put("Cl-C:C:C-C"  ,                                                      337   );
        smartsPatternBits.put("N#C-C-C"     ,                                                      338   );
        smartsPatternBits.put("N#C-C-C-C"   ,                                                      339   );
        smartsPatternBits.put("N#C-C=C"     ,                                                      340   );
        smartsPatternBits.put("N-C-C-C-C"   ,                                                      341   ); 
        smartsPatternBits.put("N-C-C-C-N"   ,                                                      342   );
        smartsPatternBits.put("N-C-C-C:C"   ,                                                      343   );
        smartsPatternBits.put("N-C-C-N"     ,                                                      344   );
        smartsPatternBits.put("N-C-C-N-C"   ,                                                      345   );
        smartsPatternBits.put("N-C-C-O-C"   ,                                                      346   );
        smartsPatternBits.put("N-C-C:C"     ,                                                      347   );
        smartsPatternBits.put("N-C-C:C-C"   ,                                                      348   );
        smartsPatternBits.put("N-C-C=C"     ,                                                      349   );
        smartsPatternBits.put("N-C-N-C"     ,                                                      350   );
        smartsPatternBits.put("N-C-N-C-C"   ,                                                      351   );
        smartsPatternBits.put("N-C-N-C:C"   ,                                                      352   );
        smartsPatternBits.put("N-C-O-C-C"   ,                                                      353   );
        smartsPatternBits.put("N-C-S-C"     ,                                                      354   );
        smartsPatternBits.put("N-C:C-C"     ,                                                      355   );
        smartsPatternBits.put("N-C:C-C-C"   ,                                                      356   );
        smartsPatternBits.put("N-C:C-N"     ,                                                      357   );
        smartsPatternBits.put("N-C:C:C-C"   ,                                                      358   );
        smartsPatternBits.put("N-C:C:C-N"   ,                                                      359   );
        smartsPatternBits.put("N-C:C:C:N"   ,                                                      360   );
        smartsPatternBits.put("N-C:C:N"     ,                                                      361   );
        smartsPatternBits.put("N-C:N:C"     ,                                                      362   );
        smartsPatternBits.put("N-C:N:N"     ,                                                      363   );
        smartsPatternBits.put("N-C:O:C"     ,                                                      364   );
        smartsPatternBits.put("N-C=N-C"     ,                                                      365   );
        smartsPatternBits.put("N-N-C-C"     ,                                                      366   );
        smartsPatternBits.put("N-N-C-N"     ,                                                      367   );
        smartsPatternBits.put("N-N-C:C"     ,                                                      368   );
        smartsPatternBits.put("N-S-C:C"     ,                                                      369   );
        smartsPatternBits.put("N:C-C:C"     ,                                                      370   );
        smartsPatternBits.put("N:C:C-C"     ,                                                      371   );
        smartsPatternBits.put("N:C:C:N"     ,                                                      372   );
        smartsPatternBits.put("N:C:N-C"     ,                                                      373   );
        smartsPatternBits.put("N:C:N:C"     ,                                                      374   );
        smartsPatternBits.put("N=C-C-C"     ,                                                      375   );
        smartsPatternBits.put("N=C-C=C"     ,                                                      376   );
        smartsPatternBits.put("N=C-N-C"     ,                                                      377   );
        smartsPatternBits.put("N=C-N-C-C"   ,                                                      378   );
        smartsPatternBits.put("O-C-C-C-C"   ,                                                      379   );
        smartsPatternBits.put("O-C-C-C-N"   ,                                                      380   );
        smartsPatternBits.put("O-C-C-C-O"   ,                                                      381   );
        smartsPatternBits.put("O-C-C-C:C"   ,                                                      382   );
        smartsPatternBits.put("O-C-C-C=C"   ,                                                      383   );
        smartsPatternBits.put("O-C-C-C=O"   ,                                                      384   );
        smartsPatternBits.put("O-C-C-N"     ,                                                      385   );
        smartsPatternBits.put("O-C-C-N-C"   ,                                                      386   );
        smartsPatternBits.put("O-C-C-O"     ,                                                      387   );
        smartsPatternBits.put("O-C-C-O-C"   ,                                                      388   );
        smartsPatternBits.put("O-C-C:C"     ,                                                      389   );
        smartsPatternBits.put("O-C-C:C-C"   ,                                                      390   );
        smartsPatternBits.put("O-C-C:C-O"   ,                                                      391   );
        smartsPatternBits.put("O-C-C=C"     ,                                                      392   );
        smartsPatternBits.put("O-C-C=C-C"   ,                                                      393   );
        smartsPatternBits.put("O-C-C=N"     ,                                                      394   );
        smartsPatternBits.put("O-C-C=O"     ,                                                      395   );
        smartsPatternBits.put("O-C-O-C"     ,                                                      396   );
        smartsPatternBits.put("O-C-O-C-C"   ,                                                      397   );
        smartsPatternBits.put("O-C:C-C"     ,                                                      398   );
        smartsPatternBits.put("O-C:C-C-C"   ,                                                      399   );
        smartsPatternBits.put("O-C:C-N"     ,                                                      400   );
        smartsPatternBits.put("O-C:C-O"     ,                                                      401   );
        smartsPatternBits.put("O-C:C-O-C"   ,                                                      402   );
        smartsPatternBits.put("O-C:C:C-C"   ,                                                      403   );
        smartsPatternBits.put("O-C:C:C-N"   ,                                                      404   );
        smartsPatternBits.put("O-C:C:C-O"   ,                                                      405   );
        smartsPatternBits.put("O-C:C:N"     ,                                                      406   );
        smartsPatternBits.put("O-C=C-C"     ,                                                      407   );
        smartsPatternBits.put("O-N-C-C"     ,                                                      408   );
        smartsPatternBits.put("O-S-C:C"     ,                                                      409   );
        smartsPatternBits.put("O=C-C-C"     ,                                                      410   ); 
        smartsPatternBits.put("O=C-C-C-C"   ,                                                      411   );
        smartsPatternBits.put("O=C-C-C-N"   ,                                                      412   );
        smartsPatternBits.put("O=C-C-C:C"   ,                                                      413   );
        smartsPatternBits.put("O=C-C-C=O"   ,                                                      414   );
        smartsPatternBits.put("O=C-C-N"     ,                                                      415   );
        smartsPatternBits.put("O=C-C-N-C"   ,                                                      416   );
        smartsPatternBits.put("O=C-C-O-C"   ,                                                      417   );
        smartsPatternBits.put("O=C-C:C"     ,                                                      418   );
        smartsPatternBits.put("O=C-C:C-C"   ,                                                      419   );
        smartsPatternBits.put("O=C-C:C-N"   ,                                                      420   );
        smartsPatternBits.put("O=C-C:C-O"   ,                                                      421   );
        smartsPatternBits.put("O=C-C:N"     ,                                                      422   );
        smartsPatternBits.put("O=C-C=C"     ,                                                      423   );
        smartsPatternBits.put("O=C-C=C-C"   ,                                                      424   );
        smartsPatternBits.put("O=C-C=C-N"   ,                                                      425   );
        smartsPatternBits.put("O=C-N-C-C"   ,                                                      426   );
        smartsPatternBits.put("O=C-N-C-N"   ,                                                      427   );
        smartsPatternBits.put("O=C-N-C=O"   ,                                                      428   );
        smartsPatternBits.put("O=C-N-N"     ,                                                      429   );
        smartsPatternBits.put("O=C-O-C:C"   ,                                                      430   );
        smartsPatternBits.put("O=N-C:C"     ,                                                      431   );
        smartsPatternBits.put("O=N-C:C-N"   ,                                                      432   );
        smartsPatternBits.put("O=N-C:C-O"   ,                                                      433   );
        smartsPatternBits.put("O=S-C-C"     ,                                                      434   );
        smartsPatternBits.put("O=S-C-N"     ,                                                      435   );
        smartsPatternBits.put("S-C-S-C"     ,                                                      436   );
        smartsPatternBits.put("S-C:C-C"     ,                                                      437   );
        smartsPatternBits.put("S-C:C-N"     ,                                                      438   );
        smartsPatternBits.put("S-C:C-O"     ,                                                      439   );
        smartsPatternBits.put("S-C:C:C-N"   ,                                                      440   );
        smartsPatternBits.put("S-C:N:C"     ,                                                      441   );
        smartsPatternBits.put("S-C=N-C"     ,                                                      442   );
        smartsPatternBits.put("S-S-C:C"     ,                                                      443   );
        smartsPatternBits.put("S:C:C:C"     ,                                                      444   );
        smartsPatternBits.put("S:C:C:N"     ,                                                      445   );
        smartsPatternBits.put("S=C-N-C"     ,                                                      446   );
        ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "1",        447   );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "1",        448   );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",        449   );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",        450   );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",        451   );
        ringBits.put("3" + ringPrefixRing + ringPrefixAny                            + "2",        452   );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom                        + "2",        453   );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",        454   );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",        455   );
        ringBits.put("3" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",        456   );
        ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "1",        457   );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "1",        458   );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",        459   );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",        460   );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",        461   );
        ringBits.put("4" + ringPrefixRing + ringPrefixAny                            + "2",        462   );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom                        + "2",        463   );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",        464   );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",        465   );
        ringBits.put("4" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",        466   );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "1",        467   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "1",        468   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",        469   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",        470   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",        471   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "1",        472   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",        473   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",        474   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",        475   );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "2",        476   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "2",        477   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",        478   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",        479   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",        480   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "2",        481   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",        482   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",        483   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",        484   );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "3",        485   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "3",        486   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3",        487   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3",        488   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3",        489   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "3",        490   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3",        491   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3",        492   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3",        493   );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "4",        494   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "4",        495   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4",        496   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4",        497   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4",        498   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "4",        499   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4",        500   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4",        501   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4",        502   );
        ringBits.put("5" + ringPrefixRing + ringPrefixAny                            + "5",        503   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom                           + "5",        504   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5",        505   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5",        506   );
        ringBits.put("5" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5",        507   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom                        + "5",        508   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5",        509   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5",        510   );
        ringBits.put("5" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5",        511   );
        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "1",        512   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "1",        513   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",        514   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",        515   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",        516   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "1",        517   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",        518   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",        519   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",        520   );
        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "2",        521   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "2",        522   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",        523   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",        524   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",        525   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "2",        526   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "2",        527   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "2",        528   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "2",        529   );
        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "3",        530   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "3",        531   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "3",        532   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "3",        533   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "3",        534   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "3",        535   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "3",        536   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "3",        537   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "3",        538   );
        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "4",        539   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "4",        540   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "4",        541   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "4",        542   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "4",        543   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "4",        544   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "4",        545   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "4",        546   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "4",        547   );
        ringBits.put("6" + ringPrefixRing + ringPrefixAny                            + "5",        548   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom                           + "5",        549   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "5",        550   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "5",        551   );
        ringBits.put("6" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "5",        552   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom                        + "5",        553   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "5",        554   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "5",        555   );
        ringBits.put("6" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "5",        556   );
        ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "1",        557   );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "1",        558   );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "1",        559   );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "1",        560   );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "1",        561   );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "1",        562   );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixNitro      + "1",        563   );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixHetero     + "1",        564   );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom + ringPrefixCarbonOnly + "1",        565   );
        ringBits.put("7" + ringPrefixRing + ringPrefixAny                            + "2",        566   );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom                           + "2",        567   );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixNitro         + "2",        568   );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixHetero        + "2",        569   );
        ringBits.put("7" + ringPrefixRing + ringPrefixArom + ringPrefixCarbonOnly    + "2",        570   );
        ringBits.put("7" + ringPrefixRing + ringPrefixNonArom                        + "2",        571   );


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
