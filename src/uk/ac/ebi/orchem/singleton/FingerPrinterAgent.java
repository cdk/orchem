package uk.ac.ebi.orchem.singleton;

import org.openscience.cdk.fingerprint.IFingerprinter;


/**
 * Singleton to provide CDK fingerprinters. It would be costly (time-wise) to 
 * create a new fingerprinter everytime one is needed, so therefore these
 * are kept available.
 */

public class FingerPrinterAgent {

    public static final FingerPrinterAgent FP = new FingerPrinterAgent();
    private IFingerprinter fingerPrinter1024;
    private IFingerprinter fingerPrinter512;


    /** Private constructor  */
    private FingerPrinterAgent() {
        fingerPrinter1024 = new org.openscience.cdk.fingerprint.ExtendedFingerprinter();
        fingerPrinter512 = new org.openscience.cdk.fingerprint.ExtendedFingerprinter(512);

    }

    public IFingerprinter getFingerPrinter1024() {
        return fingerPrinter1024;
    }

    public IFingerprinter getFingerPrinter512() {
        return fingerPrinter512;
    }


}
