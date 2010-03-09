/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2010  Duan Lian
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
 */

package uk.ac.ebi.orchem.qsar;

import oracle.sql.BINARY_DOUBLE;
import oracle.sql.CLOB;
import oracle.sql.NUMBER;

import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.shared.MoleculeCreator;

public class DescriptorCalculate {
    public static BINARY_DOUBLE calcALOGP(CLOB Molfile) throws Exception {
        return new BINARY_DOUBLE(invokeALOGPDescriptor(Molfile).get(0));
    }
    public static BINARY_DOUBLE calcALOGP2(CLOB Molfile) throws Exception {
        return new BINARY_DOUBLE(invokeALOGPDescriptor(Molfile).get(1));
    }
    public static BINARY_DOUBLE calcAMR(CLOB Molfile) throws Exception {
        return new BINARY_DOUBLE(invokeALOGPDescriptor(Molfile).get(2));
    }

    private static DoubleArrayResult invokeALOGPDescriptor(CLOB Molfile) {
        DoubleArrayResult resultArray;
        try {
            ALOGPDescriptor descriptor = new ALOGPDescriptor();
            MDLV2000Reader mdlReader = new MDLV2000Reader();
            String molfile = Utils.ClobToString(Molfile);
            if (molfile != null) {
                NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);
                resultArray = (DoubleArrayResult) descriptor.calculate(molecule).getValue();
            } else {
                resultArray = null;
            }
        } catch (Exception e) {
            resultArray = null;
            System.out.println(Utils.getErrorString(e));
        }
        return resultArray;
    }

}
