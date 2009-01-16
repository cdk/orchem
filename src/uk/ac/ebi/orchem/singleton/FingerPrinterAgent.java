package uk.ac.ebi.orchem.singleton;

import org.openscience.cdk.fingerprint.IFingerprinter;


/**
 * Singleton to provide a CDK fingerprinter. It would be costly (time-wise) to
 * create a new fingerprinter everytime one is needed, so therefore these
 * are kept available.
 */

public class FingerPrinterAgent {

    public static final FingerPrinterAgent FP = new FingerPrinterAgent();
    private IFingerprinter fingerPrinter;
    private static int FP_SIZE;
    private static int FP_CONDENSED_SIZE;


    /**
     * Private constructor, creates a fingerprinter for OrChem.<BR>
     * The value of search depth (the second argument of the fingerprinter constructor)
     * has direct impact on overall performance, especially the
     * performance of {@link uk.ac.ebi.orchem.load.LoadCDKFingerprints}. <BR>
     * */
    private FingerPrinterAgent() {
        FP_SIZE=new Integer(1024);
        FP_CONDENSED_SIZE=new Integer(512);

        fingerPrinter = new org.openscience.cdk.fingerprint.ExtendedFingerprinter(FP_SIZE,6);
        System.out.println("Fingerprinter ready ");

    }

    public IFingerprinter getFingerPrinter() {
        return fingerPrinter;
    }
    public int getFpSize() {
        return FP_SIZE;
    }

    public int getFpCondensedSize() {
        return FP_CONDENSED_SIZE;
    }

}
