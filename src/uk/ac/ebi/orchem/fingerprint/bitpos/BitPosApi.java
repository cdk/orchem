package uk.ac.ebi.orchem.fingerprint.bitpos;

/**
 * Static singleton wrapper for {@link BitPositions}
 * http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html
 */
public class BitPosApi {
    public static BitPositions bp = new BitPositions();
    
    public  static void main(String[] args) {
        bp.dumpContent();
    }

}
