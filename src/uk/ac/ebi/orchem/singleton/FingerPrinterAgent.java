package uk.ac.ebi.orchem.singleton;

import org.openscience.cdk.fingerprint.IFingerprinter;


/**
 * Singleton to provide CDK fingerprinters. It would be costly (time-wise) to 
 * create a new fingerprinter everytime one is needed, so therefore these
 * are kept available.
 */

public class FingerPrinterAgent {

    public static final FingerPrinterAgent FP = new FingerPrinterAgent();
    private IFingerprinter fingerPrinter;
    private static int FP_SIZE;


    /** Private constructor  */
    private FingerPrinterAgent() {
        FP_SIZE=512;
        fingerPrinter = new org.openscience.cdk.fingerprint.ExtendedFingerprinter(FP_SIZE);
    }

    public IFingerprinter getFingerPrinter() {
        return fingerPrinter;
    }
    public int getFpSize() {
        return FP_SIZE;
    }

}
