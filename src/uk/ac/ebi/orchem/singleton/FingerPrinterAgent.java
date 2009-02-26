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
package uk.ac.ebi.orchem.singleton;

import org.openscience.cdk.fingerprint.IFingerprinter;


/**
 * Singleton to provide a CDK fingerprinter. It would be costly (time-wise) to
 * create a new fingerprinter everytime one is needed, so therefore these
 * are kept available.
 * 
 * @author markr@ebi.ac.uk
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
