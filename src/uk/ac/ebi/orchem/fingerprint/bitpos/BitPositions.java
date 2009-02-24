package uk.ac.ebi.orchem.fingerprint.bitpos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openscience.cdk.interfaces.IBond;


/**
 * Holds maps with postional fingerprint information.<BR><BR>
 * Several maps are set up, each containing a pair (information,bit position).<BR>
 * For example, the map elementCntBits holds data related to element counts. It has
 * an entry (C32,n) with n being some number - this data will be used by {@link 
 * uk.ac.ebi.orchem.fingerprint.OrchemFingerprinter} to determine which bit (n) to set
 * when a compound has 32 or more Carbon atoms.
 * And so on..
 * 
 * @author markr@ebi.ac.uk
 */
public class BitPositions {

     public Map<String, Integer> elemCntBits = new HashMap<String, Integer>();
     public Map<String, Integer> atomPairBits = new HashMap<String, Integer>();
     public Map<Integer, List<Neighbour>> neighbourBits = new HashMap<Integer, List<Neighbour>>();

     public Map<String, Integer> ringSetBits = new HashMap<String, Integer>();
     public String ringsetCountTotalPrefix = new String("RsTot");
     public String ringsetCountPerSet = new String("RsSet");
     public String ringsetPairPrefix = new String("RsPair");
     public String ringsetCountConnectedPrefix = new String("RsNeigh");
     public String ringsetCountHexPrefix = new String("RsHex");
     public String ringsetCountPentPrefix = new String("RsPent");

     public Map<String, Integer> ringBits = new HashMap<String, Integer>();
     public String ringPrefixRing = new String("ring_");
     public String ringPrefixAny = new String("any_");
     public String ringPrefixArom = new String("arom_");
     public String ringPrefixNonArom = new String("nonArom_");
     public String ringPrefixNitro = new String("N_");
     public String ringPrefixHetero = new String("Het_");
     public String ringPrefixCarbonOnly = new String("CarbOnly_");


     BitPositions () {
        int bitPos = 0;
        elemCntBits.put("C20", bitPos++);
        elemCntBits.put("C24", bitPos++);
        elemCntBits.put("C28", bitPos++);
        elemCntBits.put("C32", bitPos++);
        elemCntBits.put("Cl1", bitPos++);
        elemCntBits.put("Cl2", bitPos++);
        elemCntBits.put("F1", bitPos++);
        elemCntBits.put("F3", bitPos++);
        elemCntBits.put("N3", bitPos++);
        elemCntBits.put("N4", bitPos++);
        elemCntBits.put("N5", bitPos++);  
        elemCntBits.put("N6", bitPos++);
        elemCntBits.put("O3", bitPos++);
        elemCntBits.put("O5", bitPos++);
        elemCntBits.put("O7", bitPos++);
        elemCntBits.put("O12", bitPos++);
        elemCntBits.put("O16", bitPos++);
        elemCntBits.put("S1", bitPos++);
        elemCntBits.put("S2", bitPos++);
        elemCntBits.put("Br1", bitPos++);
        elemCntBits.put("Br2", bitPos++);
        elemCntBits.put("P1", bitPos++);
        elemCntBits.put("P2", bitPos++);
        elemCntBits.put("I1", bitPos++);  //..
        elemCntBits.put("Na1", bitPos++);
        elemCntBits.put("B1", bitPos++);
        elemCntBits.put("Si1", bitPos++);
        elemCntBits.put("Pt1", bitPos++);
        elemCntBits.put("K1", bitPos++);
        elemCntBits.put("Se1", bitPos++);
        elemCntBits.put("Li1", bitPos++);
        elemCntBits.put("Au1", bitPos++);
        elemCntBits.put("Tc1", bitPos++);
        elemCntBits.put("Fe1", bitPos++);
        elemCntBits.put("Cu1", bitPos++);
        elemCntBits.put("R1", bitPos++);
        elemCntBits.put("Ru1", bitPos++);
        elemCntBits.put("Zn1", bitPos++);
        elemCntBits.put("Mn1", bitPos++);
        elemCntBits.put("Te1", bitPos++);
        elemCntBits.put("Sn1", bitPos++);
        elemCntBits.put("Co1", bitPos++);
        elemCntBits.put("Re1", bitPos++);
        elemCntBits.put("As1", bitPos++);
        atomPairBits.put("O-O",bitPos++);
        atomPairBits.put("N-O",bitPos++); 
        atomPairBits.put("S-O",bitPos++);
        atomPairBits.put("Cl-O",bitPos++);
        atomPairBits.put("P-O",bitPos++);
        atomPairBits.put("Na-O",bitPos++);
        atomPairBits.put("B-O",bitPos++);
        atomPairBits.put("Si-O",bitPos++);
        atomPairBits.put("N-N",bitPos++);
        atomPairBits.put("S-N",bitPos++);
        atomPairBits.put("Cl-N",bitPos++);
        atomPairBits.put("P-N",bitPos++); 
        atomPairBits.put("I-N",bitPos++);
        atomPairBits.put("B-N",bitPos++);
        atomPairBits.put("S-S",bitPos++);
        atomPairBits.put("F-S",bitPos++);
        atomPairBits.put("P-S",bitPos++);
        atomPairBits.put("P-F",bitPos++);
        atomPairBits.put("B-F",bitPos++);
        atomPairBits.put("Si-F",bitPos++);
        atomPairBits.put("P-Cl",bitPos++);

        Neighbour[] myArray=null;
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Br",null,false),new Neighbour("N",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Cl",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("S",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("I",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("I",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("Si",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)}; // very common
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)}; 
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true)}; 
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)}; 
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("Cl",null,false),new Neighbour("Cl",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("Cl",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("F",null,false),new Neighbour("F",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("F",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("F",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("S",null,false),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("N",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("N",null,true),new Neighbour("N",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("P",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,false),new Neighbour("N",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("O",null,false),new Neighbour("C",null,true),new Neighbour("C",null,true)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("P",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("P",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("C",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("S",null,false),new Neighbour("C",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("S",null,false),new Neighbour("N",null,false),new Neighbour("O",null,false),new Neighbour("O",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,false),new Neighbour("N",null,true),new Neighbour("N",null,true),new Neighbour("Cl",null,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));

        // more restrictive than PubChem
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("S",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,null),new Neighbour("N",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,null),new Neighbour("P",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("P",null,null),new Neighbour("P",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.TRIPLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("Cl",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("N",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("N",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("C",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,null),new Neighbour("C",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("N",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("P",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("C",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));
        myArray=new Neighbour[] { new Neighbour("S",null,null),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.DOUBLE,false),new Neighbour("O",IBond.Order.SINGLE,false)};
        neighbourBits.put(bitPos++, Arrays.asList(myArray));


        ringSetBits.put(ringsetCountTotalPrefix+"1",bitPos++);
        ringSetBits.put(ringsetCountTotalPrefix+"2",bitPos++);
        ringSetBits.put(ringsetCountTotalPrefix+"3",bitPos++);
        ringSetBits.put(ringsetCountTotalPrefix+"4",bitPos++);

        ringSetBits.put(ringsetCountPerSet+"3",bitPos++);
        ringSetBits.put(ringsetCountPerSet+"4",bitPos++);
        ringSetBits.put(ringsetCountPerSet+"5",bitPos++);
        ringSetBits.put(ringsetCountPerSet+"6",bitPos++);
        ringSetBits.put(ringsetCountPerSet+"8",bitPos++);
        ringSetBits.put(ringsetCountPerSet+"10",bitPos++);
        ringSetBits.put(ringsetCountPerSet+"30",bitPos++);

        ringSetBits.put(ringsetPairPrefix+"3_3",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"3_4",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"3_5",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"3_6",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"3_7",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"3_8",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"4_3",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"4_5",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"4_6",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"4_7",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"4_8",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"5_5",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"5_6",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"5_7",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"5_8",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"6_7",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"6_8",bitPos++);
        ringSetBits.put(ringsetPairPrefix+"6_12",bitPos++);//194
        ringSetBits.put(ringsetPairPrefix+"6_15",bitPos++);//195

        ringSetBits.put(ringsetCountConnectedPrefix+"2",bitPos++);
        ringSetBits.put(ringsetCountConnectedPrefix+"3",bitPos++);
        ringSetBits.put(ringsetCountConnectedPrefix+"4",bitPos++);

        ringSetBits.put(ringsetCountHexPrefix+"3",bitPos++);
        ringSetBits.put(ringsetCountHexPrefix+"4",bitPos++);
        ringSetBits.put(ringsetCountHexPrefix+"5",bitPos++);
        ringSetBits.put(ringsetCountHexPrefix+"6",bitPos++);

        ringSetBits.put(ringsetCountPentPrefix+"2",bitPos++);
        ringSetBits.put(ringsetCountPentPrefix+"3",bitPos++);
        ringSetBits.put(ringsetCountPentPrefix+"4",bitPos++);

        bitPos = setupRings ("3", bitPos,1);
        bitPos = setupRings ("3", bitPos,2);
        bitPos = setupRings ("4", bitPos,1);
        bitPos = setupRings ("4", bitPos,2);
        bitPos = setupRings ("5", bitPos,1);
        bitPos = setupRings ("5", bitPos,2);
        bitPos = setupRings ("5", bitPos,3);
        bitPos = setupRings ("5", bitPos,4);
        bitPos = setupRings ("5", bitPos,5);
        bitPos = setupRings ("6", bitPos,1);
        bitPos = setupRings ("6", bitPos,2);
        bitPos = setupRings ("6", bitPos,3);
        bitPos = setupRings ("6", bitPos,4);
        bitPos = setupRings ("6", bitPos,5);
        bitPos = setupRings ("7", bitPos,1);
        bitPos = setupRings ("7", bitPos,2);
        bitPos = setupRings ("8", bitPos,1);
        bitPos = setupRings ("8", bitPos,1);
        bitPos = setupRings ("10", bitPos,1);


        ringBits.put("11"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("12"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("13"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("14"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("15"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("16"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("17"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("18"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("19"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("20"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("21"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("22"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("23"+ringPrefixRing+ringPrefixAny+"1",bitPos++);
        ringBits.put("24"+ringPrefixRing+ringPrefixAny+"1",bitPos++);

    }
    
    
    
    private  int setupRings (String ringsize, int bitPos,int count ) {
        ringBits.put(ringsize+ringPrefixRing+ringPrefixAny+count,bitPos++);
        ringBits.put(ringsize+ringPrefixRing+ringPrefixArom+count,bitPos++);
        ringBits.put(ringsize+ringPrefixRing+ringPrefixArom+ringPrefixNitro+count,bitPos++);
        ringBits.put(ringsize+ringPrefixRing+ringPrefixArom+ringPrefixHetero+count,bitPos++);
        ringBits.put(ringsize+ringPrefixRing+ringPrefixNonArom+count,bitPos++);
        ringBits.put(ringsize+ringPrefixRing+ringPrefixNonArom+ringPrefixNitro+count,bitPos++);
        ringBits.put(ringsize+ringPrefixRing+ringPrefixNonArom+ringPrefixHetero+count,bitPos++);
        ringBits.put(ringsize+ringPrefixRing+ringPrefixNonArom+ringPrefixCarbonOnly+count,bitPos++);

        return bitPos;
    }

    public  void dumpContent () {
        
        Map<Integer, String> all = new HashMap<Integer, String>();
        all.putAll(prepareContentDump(elemCntBits));
        all.putAll(prepareContentDump(atomPairBits));
        all.putAll(prepareContentDump(ringSetBits));
        all.putAll(prepareContentDump(ringBits));

        Iterator<Integer> neighItr = neighbourBits.keySet().iterator();
        while (neighItr.hasNext())  {
            int bit = neighItr.next();
            List<Neighbour> nbList = neighbourBits.get(bit);
            StringBuilder sb = new StringBuilder();
            for (Neighbour n : nbList)
                sb.append(n.toString());
            all.put(bit,sb.toString());
        }
                
        
        for (int i = 0; i < 512; i++) {
            if (all.containsKey(i))  {
                String condition=all.get(i);
                System.out.println("Bit  "+i+"  "+ condition);            

            }
        }

    }
    
    private  Map<Integer,String> prepareContentDump (Map<String, Integer> fpMap) {
        Map<Integer,String> ret = new HashMap<Integer,String>();
        Collection c = fpMap.keySet();
        Iterator<String> it = c.iterator();
        while (it.hasNext())  {
            String o = it.next();
            Integer bit =fpMap.get(o);
            ret.put(bit,o);
        }
        return ret;        
    }


}
