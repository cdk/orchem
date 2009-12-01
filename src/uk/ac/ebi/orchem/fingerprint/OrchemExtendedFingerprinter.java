package uk.ac.ebi.orchem.fingerprint;

import java.util.BitSet;
import java.util.Iterator;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IRingSet;

import uk.ac.ebi.orchem.fingerprint.bitpos.BitPosApi;


/**
 * Extends the default {@link OrchemFingerprinter fingerprinter} with 
 * further bits of which some are very common. Nice for similarity searching 
 * (the more bits the better)
 *
 */
public class OrchemExtendedFingerprinter extends OrchemFingerprinter {

    public final static int FINGERPRINT_SIZE = BitPosApi.bpExtended.getFingerprintSize();

    public int getSize() {
        return FINGERPRINT_SIZE;
    }

    /**
     * Returns an extended fingerprint, which is a normal fingerprint with
     * some extra bits at the end.
     * 
     * @param molecule to be fingerprinted
     * @return
     */
    public BitSet getFingerprint(IAtomContainer molecule) throws CDKException {
        BitSet extendedFingerprint;
        extendedFingerprint = new BitSet(FINGERPRINT_SIZE);
        Integer bitPos = null;
        String mapKey = null;

        /* Copy the basic fingerprint into the extended fingerprint */
        BitSet basicFingerprint = super.getFingerprint(molecule);
        for (int i = 0; i < basicFingerprint.size(); i++) {
            extendedFingerprint.set(i, basicFingerprint.get(i));
        }

        int atomCount = 0;
        for (IAtom atom : molecule.atoms()) {
            /* Fingerprint any element */
            mapKey = atom.getSymbol();
            bitPos = BitPosApi.bpExtended.allElements.get(mapKey);
            if (bitPos != null) {
                extendedFingerprint.set(bitPos, true);
                //System.out.println("setting " + bitPos + "  for " + mapKey);
            }

            /* Atom count related fingerprinting */
            atomCount++;
            mapKey = BitPosApi.bpExtended.atomCountMoreThan + atomCount;
            bitPos = BitPosApi.bpExtended.basicStuff.get(mapKey);
            if (bitPos != null) {
                extendedFingerprint.set(bitPos, true);
                //System.out.println("setting " + bitPos + "  for " + mapKey);
            }
        }

        /* Fingerprint all ring sizes */
        for (IRingSet irs : rslist) {
            Iterator<IAtomContainer> ringsetMembers = irs.atomContainers().iterator();
            while (ringsetMembers.hasNext()) {
                IAtomContainer ring = ringsetMembers.next();
                mapKey = BitPosApi.bp.ringSize+ring.getAtomCount();
                bitPos = BitPosApi.bpExtended.ringSizes.get(mapKey);
                if (bitPos != null) {
                    extendedFingerprint.set(bitPos, true);
                }
            }
        }

        /* Fingerprint aromaticity */
        for (IBond bond : molecule.bonds()) {
            if (bond.getOrder() != null && bond.getFlag(CDKConstants.ISAROMATIC)) {
                mapKey = BitPosApi.bpExtended.hasAromaticBonds;
                bitPos = BitPosApi.bpExtended.basicStuff.get(mapKey);
                extendedFingerprint.set(bitPos, true);
                //System.out.println("setting " + bitPos + "  for " + mapKey);
                break;
            }
        }

        return extendedFingerprint;
    }
}
