/*  
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *
 */
package uk.ac.ebi.orchem;

import java.util.BitSet;


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
            sb.append("\tat " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + " ( " +
                      stackTraceElement.getFileName() + ":" + stackTraceElement.getLineNumber() + " )" + "\n");
        }
        if (throwable.getCause() != null) {
            sb.append("\n" + Utils.getErrorString(throwable.getCause()));
        }
        return sb.toString();
    }

}
