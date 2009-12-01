/*
 * TODO
 * doesn't run proper parallel.
 * is actually slower than the other blob
 * need to rethink all this
 *
 */
package uk.ac.ebi.orchem.search;

import java.sql.Clob;

import java.util.BitSet;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesParser;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.shared.MoleculeCreator;
import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 *
 *
 *
 *
 */
public class SimilaritySearchParallel {

    private static final int BIT_COUNT[] =
    { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3,
      3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4,
      3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4,
      4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5,
      3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6,
      6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5,
      4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8 };

    private static SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());


    public static byte[] getFingerPrint(Clob userQuery, String queryType) throws Exception {
        int clobLen = new Long(userQuery.length()).intValue();
        String query = (userQuery.getSubString(1, clobLen));
        BitSet fp = null;
        IAtomContainer molecule = null;

        if (queryType.equals(Utils.QUERY_TYPE_MOL)) {
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            molecule = MoleculeCreator.getNNMolecule(mdlReader, query);
        } else if (queryType.equals(Utils.QUERY_TYPE_SMILES)) {
            molecule = sp.parseSmiles(query);
        } else
            throw new RuntimeException("Query type not recognized");

        fp = FingerPrinterAgent.FP.getExtendedFingerPrinter().getFingerprint(molecule);

        byte[] bytes = new byte[fp.length() / 8 + 1];
        for (int i = 0; i < fp.length(); i++) {
            if (fp.get(i)) {
                bytes[bytes.length - i / 8 - 1] |= 1 << (i % 8);
            }
        }
        return bytes;
    }


    public static int getBitCount(Clob userQuery, String queryType) throws Exception {
        int clobLen = new Long(userQuery.length()).intValue();
        String query = (userQuery.getSubString(1, clobLen));
        if (queryType.equals(Utils.QUERY_TYPE_MOL)) {

            MDLV2000Reader mdlReader = new MDLV2000Reader();
            Molecule molecule = MoleculeCreator.getNNMolecule(mdlReader, query);
            BitSet fp = FingerPrinterAgent.FP.getFingerPrinter().getFingerprint(molecule);
            return fp.cardinality();
        } else if (queryType.equals(Utils.QUERY_TYPE_SMILES)) {
            IAtomContainer molecule = sp.parseSmiles(query);
            BitSet fp = FingerPrinterAgent.FP.getFingerPrinter().getFingerprint(molecule);
            return fp.cardinality();
        } else
            throw new RuntimeException("Query type not recognized");
    }


    //TODO = needed ???

    public static Float getScore(Integer currBucketNum, byte[] fpQuery, byte[] fpTarget) throws Exception {
        float bitsInCommon = 0;
        float tanimotoCoeff = 0f;

        int targetArrLen = fpTarget.length;
        int queryArrLen = fpQuery.length;

        //System.out.println("targetArrLen  "+targetArrLen);
        //System.out.println("queryArrLen   "+queryArrLen);
        
        int arrSizeDiff = targetArrLen - queryArrLen;
        //System.out.println("diff is       "+arrSizeDiff);
        
        int queryBitCount=0;
        for (int i = 0; i < queryArrLen; i++) {
            int q = fpQuery[i];
            //System.out.println("###"+fpQuery[i]);

            if (q < 0) {
                q += 128;
                queryBitCount += BIT_COUNT[q] + 1;
            } else {
                queryBitCount += BIT_COUNT[q];
            }
        }

        int tBitCount=0;
        for (int i = 0; i < targetArrLen; i++) {
            int q = fpTarget[i];
            //System.out.println(">>>"+i+": "+fpTarget[i]);
            if (q < 0) {
                q += 128;
                tBitCount += BIT_COUNT[q] + 1;
            } else {
                tBitCount += BIT_COUNT[q];
            }
        }

        //System.out.println("queryBitCount  is "+queryBitCount);
        //System.out.println("targetBitCount is "+tBitCount);

        int arrLen = 0;
        if (targetArrLen < queryArrLen)
            arrLen = targetArrLen;
        else
            arrLen = queryArrLen;

        //System.out.println("arrLen        "+arrLen);
        // uhm looks like some trailing 0's !! 
        for (int i = 0; i < arrLen; i++) {
            int bAnd = fpTarget[i+arrSizeDiff] & fpQuery[i];
            //System.out.println(fpTarget[i+arrSizeDiff]+" vs "+fpQuery[i]);
            if (bAnd < 0) {
                bAnd += 128;
                bitsInCommon += BIT_COUNT[bAnd] + 1;
            } else {
                bitsInCommon += BIT_COUNT[bAnd];
            }
        }

        //System.out.println("bitsInCommon "+bitsInCommon);

        tanimotoCoeff = bitsInCommon / (queryBitCount + currBucketNum - bitsInCommon);
        //System.out.println("tanimotoCoeff    "+tanimotoCoeff);

        //System.out.println("------------");
        return tanimotoCoeff;
        }

}
