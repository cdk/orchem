package uk.ac.ebi.orchem.fingerprint.bitpos;

import java.util.HashMap;
import java.util.Map;

/**
 * An extension to the {@link BitPositions basic} Orchem fingerprint bit positions.
 * Set up with similarity searching in mind: this type of 
 * search only has the fingerprint comparison to determine similarity
 * and thus benefits from a large amount of bits, especially when searching is done
 * for very small fragments (or even atoms) that would flag next to nothing
 * up in the regular set of bits.
 * 
 */
public class ExtendedBitPositions extends BitPositions{

    /* Map to hold all elements */
    public final Map<String, Integer> allElements = new HashMap<String, Integer>();

    /* Map to hold basic aspects of a structure */
    public final Map<String, Integer> basicStuff = new HashMap<String, Integer>();


    /* Reserved prefix strings for readable labeling of bit position dump */
    public final String hasAromaticBonds = new String("hasAromaticBonds");
    public final String atomCountMoreThan = new String("atomCountMoreOrEqual");

    /**
     * Constructor, extends constructor of {@link BitPositions}.
     */
    ExtendedBitPositions() {

        /* Call mamma */
        super();

        /* Add the additional bit positions */
        ringSizes.put(ringSize+"5" , ++bitpos  );
        ringSizes.put(ringSize+"6" , ++bitpos  );

        basicStuff.put(hasAromaticBonds,++bitpos);

        basicStuff.put(atomCountMoreThan+"2",++bitpos);
        basicStuff.put(atomCountMoreThan+"4",++bitpos);
        basicStuff.put(atomCountMoreThan+"8",++bitpos);
        basicStuff.put(atomCountMoreThan+"16",++bitpos);
        basicStuff.put(atomCountMoreThan+"32",++bitpos);
        basicStuff.put(atomCountMoreThan+"64",++bitpos);
                
        allElements.put("H", ++bitpos  );        
        allElements.put("He",++bitpos );
        allElements.put("Li",++bitpos );
        allElements.put("Be",++bitpos );
        allElements.put("B",++bitpos );
        allElements.put("C",++bitpos );
        allElements.put("N",++bitpos );
        allElements.put("O",++bitpos );
        allElements.put("F",++bitpos );
        allElements.put("Ne",++bitpos );
        allElements.put("Na",++bitpos );
        allElements.put("Mg",++bitpos );
        allElements.put("Al",++bitpos );
        allElements.put("Si",++bitpos );
        allElements.put("P",++bitpos );
        allElements.put("S",++bitpos );
        allElements.put("Cl",++bitpos );
        allElements.put("Ar",++bitpos );
        allElements.put("K",++bitpos );
        allElements.put("Ca",++bitpos );
        allElements.put("Sc",++bitpos );
        allElements.put("Ti",++bitpos );
        allElements.put("V",++bitpos );
        allElements.put("Cr",++bitpos );
        allElements.put("Mn",++bitpos );
        allElements.put("Fe",++bitpos );
        allElements.put("Co",++bitpos );
        allElements.put("Ni",++bitpos );
        allElements.put("Cu",++bitpos );
        allElements.put("Zn",++bitpos );
        allElements.put("Ga",++bitpos );
        allElements.put("Ge",++bitpos );
        allElements.put("As",++bitpos );
        allElements.put("Se",++bitpos );
        allElements.put("Br",++bitpos );
        allElements.put("Kr",++bitpos );
        allElements.put("Rb",++bitpos );
        allElements.put("Sr",++bitpos );
        allElements.put("Y",++bitpos );
        allElements.put("Zr",++bitpos );
        allElements.put("Nb",++bitpos );
        allElements.put("Mo",++bitpos );
        allElements.put("Tc",++bitpos );
        allElements.put("Ru",++bitpos );
        allElements.put("Rh",++bitpos );
        allElements.put("Pd",++bitpos );
        allElements.put("Ag",++bitpos );
        allElements.put("Cd",++bitpos );
        allElements.put("In",++bitpos );
        allElements.put("Sn",++bitpos );
        allElements.put("Sb",++bitpos );
        allElements.put("Te",++bitpos );
        allElements.put("I",++bitpos );
        allElements.put("Xe",++bitpos );
        allElements.put("Cs",++bitpos );
        allElements.put("Ba",++bitpos );
        allElements.put("La",++bitpos );
        allElements.put("Ce",++bitpos );
        allElements.put("Pr",++bitpos );
        allElements.put("Nd",++bitpos );
        allElements.put("Pm",++bitpos );
        allElements.put("Sm",++bitpos );
        allElements.put("Eu",++bitpos );
        allElements.put("Gd",++bitpos );
        allElements.put("Tb",++bitpos );
        allElements.put("Dy",++bitpos );
        allElements.put("Ho",++bitpos );
        allElements.put("Er",++bitpos );
        allElements.put("Tm",++bitpos );
        allElements.put("Yb",++bitpos );
        allElements.put("Lu",++bitpos );
        allElements.put("Hf",++bitpos );
        allElements.put("Ta",++bitpos );
        allElements.put("W",++bitpos );
        allElements.put("Re",++bitpos );
        allElements.put("Os",++bitpos );
        allElements.put("Ir",++bitpos );
        allElements.put("Pt",++bitpos );
        allElements.put("Au",++bitpos );
        allElements.put("Hg",++bitpos );
        allElements.put("Tl",++bitpos );
        allElements.put("Pb",++bitpos );
        allElements.put("Bi",++bitpos );
        allElements.put("Po",++bitpos );
        allElements.put("At",++bitpos );
        allElements.put("Rn",++bitpos );
        allElements.put("Fr",++bitpos );
        allElements.put("Ra",++bitpos );
        allElements.put("Ac",++bitpos );
        allElements.put("Th",++bitpos );
        allElements.put("Pa",++bitpos );
        allElements.put("U",++bitpos );
        allElements.put("Np",++bitpos );
        allElements.put("Pu",++bitpos );
        allElements.put("Am",++bitpos );
        allElements.put("Cm",++bitpos );
        allElements.put("Bk",++bitpos );
        allElements.put("Cf",++bitpos );
        allElements.put("Es",++bitpos );
        allElements.put("Fm",++bitpos );
        allElements.put("Md",++bitpos );
        allElements.put("No",++bitpos );
        allElements.put("Lr",++bitpos );
        allElements.put("Rf",++bitpos );
        allElements.put("Db",++bitpos );
        allElements.put("Sg",++bitpos );
        allElements.put("Bh",++bitpos );
        allElements.put("Hs",++bitpos );
        allElements.put("Mt",++bitpos );
    }

}
