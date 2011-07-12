/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Mark Rijnbeek
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
package uk.ac.ebi.orchem;

import java.io.Reader;
import java.io.StringReader;

import java.util.BitSet;

import java.util.StringTokenizer;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDriver;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;

import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;

import uk.ac.ebi.orchem.singleton.FingerPrinterAgent;


/**
 * Contains shared utilities for OrChem.<BR>
 * Contains shared constants.
 *
 */
public class Utils {

    public static final String SESSION_WEB_SEARCH_RESULTS = new String("wsr");
    public static final String QUERY_TYPE_SMILES = new String("SMILES");
    public static final String QUERY_TYPE_MOL = new String("MOL");
    public static final String QUERY_TYPE_SMARTS = new String("SMARTS");

    /**
     * Converts a BitSet into an array of bytes
     * @param bits
     * @return
     */
    public static byte[] toByteArray(BitSet bits, int fixedNumBytes) {
        Double size = Math.ceil(new Double(fixedNumBytes) / 8);
        byte[] bytes = new byte[size.intValue()];

        for (int i = 0; i < fixedNumBytes; i++) {
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
            System.out.println("Atom " + i + ": " + c.getAtom(i).getSymbol() + "   (hashCode " +
                               c.getAtom(i).hashCode() + ")");
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
            System.out.println("order is " + c.getBond(i).getOrder() + " aromatic is=" +
                               c.getBond(i).getFlag(CDKConstants.ISAROMATIC));
        }
    }

    /**
     * @param hclob
     * @return
     * @throws Exception
     */
    public static String ClobToString(CLOB hclob) throws Exception {
        StringBuffer hstringbuffer;
        hstringbuffer = new StringBuffer();
        Reader clobReader = hclob.getCharacterStream();
        char[] buffer = new char[hclob.getBufferSize()];
        int read = 0;
        int bufflen = buffer.length;
        while ((read = clobReader.read(buffer, 0, bufflen)) > 0) {
            hstringbuffer.append(new String(buffer, 0, read));
        }
        clobReader.close();
        return hstringbuffer.toString();
    }

    /**
     * @param hstring
     * @return
     * @throws Exception
     */
    public static CLOB StringToClob(String hstring) throws Exception {
        CLOB hclob;
        OracleConnection conn = null;
        conn = (OracleConnection)new OracleDriver().defaultConnection();
        hclob = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
        hclob.open(CLOB.MODE_READWRITE);
        hclob.setString(1, hstring);
        hclob.close();
        return hclob;
    }

    /**
     * @param hbyte
     * @return
     * @throws Exception
     */
    public static BLOB ByteToBlob(byte[] hbyte) throws Exception {
        BLOB hblob;
        OracleConnection conn = null;
        conn = (OracleConnection)new OracleDriver().defaultConnection();
        hblob = BLOB.createTemporary(conn, false, BLOB.DURATION_SESSION);
        hblob.open(CLOB.MODE_READWRITE);
        hblob.setBytes(1, hbyte);
        hblob.close();
        return hblob;
    }


    /**
     * Helper method to circumvent the CDK's lack of ability to deal with 'query bond' types
     * which are bonds with type > 4 in a molfile. See CTFile documentation on the web.
     * Well, this is a hack really.
     *
     * @param mdl input molfile
     * @return mdl with 5,6,7,8 bond types replace with 1 (single)
     */
    public static String removeSSSQueryBondFromMolfile(String mdl) {
        mdl=mdl.replace("\n", " \n");
        mdl=mdl.replaceAll("\n\n", "\n \n");
        String lineSep = System.getProperty("line.separator");
        StringTokenizer st = new StringTokenizer(mdl, lineSep);
        StringBuilder mdlOut = new StringBuilder();
        while (st.hasMoreTokens()) {
            String mdlLine = st.nextToken();
            if (mdlLine == null || mdl.equals("")) {
                mdlOut.append(".").append(lineSep);
            }
            else {
                if (mdlLine.length() > 9 &&
                    (mdlLine.substring(0, 3).matches("(\\s){0,2}(\\d){1,3}")) &&
                    (mdlLine.substring(3, 6).matches("(\\s){0,2}(\\d){1,3}")) &&
                    (mdlLine.substring(6, 9).matches("(\\s){2}[5678]"))) // SSS query bond types
                {
                    mdlLine = mdlLine.replaceAll("[5678]", "1");
                }
                mdlOut.append(mdlLine).append(lineSep);
            }
        }
        String returnMDL = mdlOut.toString();
        returnMDL = returnMDL.substring(0, returnMDL.length()-1);
        return returnMDL;
    }
}

