package uk.ac.ebi.orchem;


import java.io.StringReader;

import java.util.BitSet;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 *
 * Shared application methods.
 *
 */
public class Utils {


    /**
     * Returns a CDK Molecule when given a valid structure (MDL)
     * @param mdlReader
     * @param structure
     * @return
     * @throws CDKException
     */
    public static Molecule getMolecule(MDLV2000Reader mdlReader, String structure) throws CDKException {
        Molecule mol = null;
        mdlReader.setReader(new StringReader(structure));
        mol = new Molecule();
        mol = (Molecule)mdlReader.read(mol);
        Molecule mol2 = new Molecule(AtomContainerManipulator.removeHydrogens(mol));
        if (mol2 == null || mol2.getAtomCount() == 0)
            throw new RuntimeException("Error parsing molfile is null or mol atom count is zero. ");

        HueckelAromaticityDetector.detectAromaticity(mol2);
        return mol2;
    }


    /**
     * Converts a BitSet into an array of bytes
     * @param bits
     * @return
     */
    public static byte[] toByteArray(BitSet bits, int fixedNumBytes) {
        byte[] bytes = new byte[fixedNumBytes / 8];
        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i)) {
                bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
            }
        }
        return bytes;
    }


    public static final String SINGLE_BOND_COUNT = "sinbc";
    public static final String DOUBLE_BOND_COUNT = "doubc";
    public static final String TRIPLE_BOND_COUNT = "tribc";
    public static final String AROM_BOND_COUNT = "arobc";
    public static final String S_COUNT = "sc";
    public static final String O_COUNT = "oc";
    public static final String N_COUNT = "nc";
    public static final String F_COUNT = "fc";
    public static final String CL_COUNT = "clc";
    public static final String BR_COUNT = "brc";
    public static final String I_COUNT = "ic";
    public static final String C_COUNT = "cc";
    public static final String P_COUNT = "pc";

    /**
     * Given an atom container, calculates atoms and bonds and
     * stores these in a result map. Used for substructure searching, to
     * get provide a quick filter when comparing two molecules.
     *
     * @param iac
     * @return map with various counts for bonds and atoms. See static xxx_COUNT values for map content.
     */
    public static Map atomAndBondCount(IAtomContainer iac) {

        Map result = new HashMap();

        //get atom and bond counts
        Integer molSingleBondCount = 0;
        Integer molDoubleBondCount = 0;
        Integer molTripleBondCount = 0;
        Integer molAromaticBondCount = 0;
        Integer molSCount = 0;
        Integer molOCount = 0;
        Integer molNCount = 0;
        Integer molFCount = 0;
        Integer molClCount = 0;
        Integer molBrCount = 0;
        Integer molICount = 0;
        Integer molCCount = 0;
        Integer molPCount = 0;

        IBond bond;
        IAtom atom;
        for (int i = 0; i < iac.getBondCount(); i++) {
            bond = iac.getBond(i);
            if (bond.getFlag(CDKConstants.ISAROMATIC))
                molAromaticBondCount++;
            else if (bond.getOrder() == 1)
                molSingleBondCount++;
            else if (bond.getOrder() == 2)
                molDoubleBondCount++;
            else if (bond.getOrder() == 3)
                molTripleBondCount++;
        }
        for (int i = 0; i < iac.getAtomCount(); i++) {
            atom = iac.getAtom(i);
            if (atom.getSymbol().equals("S"))
                molSCount++;
            else if (atom.getSymbol().equals("N"))
                molNCount++;
            else if (atom.getSymbol().equals("O"))
                molOCount++;
            else if (atom.getSymbol().equals("F"))
                molFCount++;
            else if (atom.getSymbol().equals("Cl"))
                molClCount++;
            else if (atom.getSymbol().equals("Br"))
                molBrCount++;
            else if (atom.getSymbol().equals("I"))
                molICount++;
            else if (atom.getSymbol().equals("C"))
                molCCount++;
            else if (atom.getSymbol().equals("P"))
                molPCount++;

        }

        result.put(SINGLE_BOND_COUNT, molSingleBondCount);
        result.put(DOUBLE_BOND_COUNT, molDoubleBondCount);
        result.put(TRIPLE_BOND_COUNT, molTripleBondCount);
        result.put(AROM_BOND_COUNT, molAromaticBondCount);
        result.put(S_COUNT, molSCount);
        result.put(O_COUNT, molOCount);
        result.put(N_COUNT, molNCount);
        result.put(F_COUNT, molFCount);
        result.put(CL_COUNT, molClCount);
        result.put(BR_COUNT, molBrCount);
        result.put(I_COUNT, molICount);
        result.put(C_COUNT, molCCount);
        result.put(P_COUNT, molPCount);

        return result;

    }

    /**
     * Converts an error stack into a String, handy for printing
     *
     * @param throwable
     * @return
     */
    public static String getErrorString(Throwable throwable) {
        StringBuffer sb = new StringBuffer(throwable.getMessage() + "\n");
        StackTraceElement[] st = throwable.getStackTrace();
        for (int i = 0; i < st.length; i++) {
            StackTraceElement stackTraceElement = st[i];
            sb.append("\tat " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + " ( " +
                      stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + " )" + "\n");
        }
        if (throwable.getCause() != null) {
            sb.append("\n" + Utils.getErrorString(throwable.getCause()));
        }
        return sb.toString();
    }

}
