/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *
 */
package uk.ac.ebi.orchem;

import java.util.BitSet;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;


/**
 * Shared utilities for OrChem.
 */
public class Utils {

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
            sb.append("\tat " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + " ( " + stackTraceElement.getFileName() + ":" +
                      stackTraceElement.getLineNumber() + " )" + "\n");
        }
        if (throwable.getCause() != null) {
            sb.append("\n" +
                    Utils.getErrorString(throwable.getCause()));
        }
        return sb.toString();
    }


    /**
     * Prints content of atom container (for debuging)
     * @param c
     */
    public static void printContent(IAtomContainer c) {
        for (int i = 0; i < c.getAtomCount(); i++) {
            System.out.println("Atom " + i + ": " + c.getAtom(i).getSymbol() + "   (hashCode " + c.getAtom(i).hashCode() + ")");
        }
        for (int i = 0; i < c.getBondCount(); i++) {
            System.out.print("Bond " + i + "  ");
            IAtom a0 = c.getBond(i).getAtom(0);
            IAtom a1 = c.getBond(i).getAtom(1);
            int from = -1, to = -1;
            for (int j = 0; j < c.getAtomCount(); j++) {
                if (c.getAtom(j).hashCode() == a0.hashCode()) {
                    from = j;
                }
                if (c.getAtom(j).hashCode() == a1.hashCode()) {
                    to = j;
                }
            }
            System.out.print(" from " + from + " to " + to + "; ");
            System.out.println("order is " + c.getBond(i).getOrder() + " aromatic is=" + c.getBond(i).getFlag(CDKConstants.ISAROMATIC));
        }
    }

}
