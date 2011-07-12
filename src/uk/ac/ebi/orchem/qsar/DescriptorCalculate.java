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

import oracle.sql.CLOB;
import oracle.sql.NUMBER;

import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.DoubleArrayResult;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.qsar.result.IntegerArrayResult;
import org.openscience.cdk.qsar.result.IntegerResult;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


public class DescriptorCalculate {

    /**
     * Method to invoke given descriptor.
     *
     * @param className   Simple name of descriptor class, e.g. ALOGPDescriptor,WeightedPathDescriptor
     * @param Molfile     Molfile input from Oracle SQL
     */

    private static IDescriptorResult invokeDescriptor(String className,CLOB Molfile) {
        IDescriptorResult resultArray;
        try {
            IMolecularDescriptor descriptor = (IMolecularDescriptor) Class.forName("org.openscience.cdk.qsar.descriptors.molecular."+className).newInstance();
            String molfile = Utils.ClobToString(Molfile);
            if (molfile != null) {
                NNMolecule molecule = MoleculeCreator.getMoleculeFromMolfile(molfile);
                resultArray = descriptor.calculate(molecule).getValue();
            } else {
                resultArray = null;
            }
        } catch (Exception e) {
            resultArray = null;
            System.out.println(Utils.getErrorString(e));
        }
        return resultArray;
    }
    /**
     * Wrap Java double to Oracl NUMBER.
     *
     * @param value   Java double value to wrap
     * @return       Oracle NUMBER
     */

    public static NUMBER wrapNumber(double value) throws Exception {
        if(Double.isNaN(value)){
            return null;
        }
        else{
            return new NUMBER(value);
        }
    }
    /**
     * Wrap Java int to Oracl NUMBER.
     *
     * @param value   Java double value to wrap
     * @return       Oracle NUMBER
     */

    public static NUMBER wrapNumber(int value){
            return new NUMBER(value);
    }
    public static NUMBER ATSm1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorMass",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER ATSm2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorMass",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER ATSm3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorMass",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER ATSm4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorMass",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER ATSm5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorMass",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER WTPT1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WeightedPathDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER WTPT2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WeightedPathDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER WTPT3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WeightedPathDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER WTPT4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WeightedPathDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER WTPT5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WeightedPathDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER ALogP(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ALOGPDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER ALogp2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ALOGPDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER AMR(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ALOGPDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER nB(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("BondCountDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER Kier1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("KappaShapeIndicesDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER Kier2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("KappaShapeIndicesDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER Kier3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("KappaShapeIndicesDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER nAtomLC(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("LargestChainDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER BCUTw1l(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("BCUTDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER BCUTw1h(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("BCUTDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER BCUTc1l(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("BCUTDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER BCUTc1h(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("BCUTDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER BCUTp1l(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("BCUTDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER BCUTp1h(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("BCUTDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER SPC4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathClusterDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER SPC5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathClusterDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER SPC6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathClusterDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER VPC4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathClusterDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER VPC5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathClusterDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER VPC6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathClusterDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER topoShape(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("PetitjeanShapeIndexDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER geomShape(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("PetitjeanShapeIndexDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER nAtom(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("AtomCountDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER ATSp1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorPolarizability",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER ATSp2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorPolarizability",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER ATSp3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorPolarizability",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER ATSp4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorPolarizability",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER ATSp5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorPolarizability",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER MolIP(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("IPMolecularLearningDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER PPSA1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER PPSA2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER PPSA3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER PNSA1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER PNSA2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER PNSA3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER DPSA1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER DPSA2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER DPSA3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER FPSA1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(9));
    }
    public static NUMBER FPSA2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(10));
    }
    public static NUMBER FPSA3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(11));
    }
    public static NUMBER FNSA1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(12));
    }
    public static NUMBER FNSA2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(13));
    }
    public static NUMBER FNSA3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(14));
    }
    public static NUMBER WPSA1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(15));
    }
    public static NUMBER WPSA2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(16));
    }
    public static NUMBER WPSA3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(17));
    }
    public static NUMBER WNSA1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(18));
    }
    public static NUMBER WNSA2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(19));
    }
    public static NUMBER WNSA3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(20));
    }
    public static NUMBER RPCG(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(21));
    }
    public static NUMBER RNCG(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(22));
    }
    public static NUMBER RPCS(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(23));
    }
    public static NUMBER RNCS(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(24));
    }
    public static NUMBER THSA(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(25));
    }
    public static NUMBER TPSA(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(26));
    }
    public static NUMBER RHSA(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(27));
    }
    public static NUMBER RPSA(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("CPSADescriptor",Molfile);
        return wrapNumber(result.get(28));
    }
    public static NUMBER LipinskiFailures(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("RuleOfFiveDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER bpol(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("BPolDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER VAdjMat(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("VAdjMaDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER nA(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER nR(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER nN(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER nD(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER nC(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER nF(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER nQ(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER nE(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER nG(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER nH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(9));
    }
    public static NUMBER nI(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(10));
    }
    public static NUMBER nP(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(11));
    }
    public static NUMBER nL(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(12));
    }
    public static NUMBER nK(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(13));
    }
    public static NUMBER nM(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(14));
    }
    public static NUMBER nS(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(15));
    }
    public static NUMBER nT(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(16));
    }
    public static NUMBER nY(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(17));
    }
    public static NUMBER nV(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(18));
    }
    public static NUMBER nW(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("AminoAcidCountDescriptor",Molfile);
        return wrapNumber(result.get(19));
    }
    public static NUMBER nAromBond(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("AromaticBondsCountDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER GRAV1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER GRAV2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER GRAV3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER GRAVH1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER GRAVH2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER GRAVH3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER GRAV4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER GRAV5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER GRAV6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("GravitationalIndexDescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER SP0(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER SP1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER SP2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER SP3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER SP4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER SP5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER SP6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER SP7(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER VP0(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER VP1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(9));
    }
    public static NUMBER VP2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(10));
    }
    public static NUMBER VP3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(11));
    }
    public static NUMBER VP4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(12));
    }
    public static NUMBER VP5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(13));
    }
    public static NUMBER VP6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(14));
    }
    public static NUMBER VP7(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiPathDescriptor",Molfile);
        return wrapNumber(result.get(15));
    }
    public static NUMBER C1SP1(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER C2SP1(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER C1SP2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER C2SP2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER C3SP2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER C1SP3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER C2SP3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER C3SP3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER C4SP3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("CarbonTypesDescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER SCH3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER SCH4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER SCH5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER SCH6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER SCH7(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER VCH3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER VCH4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER VCH5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER VCH6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER VCH7(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiChainDescriptor",Molfile);
        return wrapNumber(result.get(9));
    }
    public static NUMBER SC3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiClusterDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER SC4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiClusterDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER SC5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiClusterDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER SC6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiClusterDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER VC3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiClusterDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER VC4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiClusterDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER VC5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiClusterDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER VC6(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("ChiClusterDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER nRotB(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("RotatableBondsCountDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER Wlambda1unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER Wlambda2unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER Wlambda3unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER Wnu1unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER Wnu2unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER Wgamma1unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER Wgamma2unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER Wgamma3unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER Weta1unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER Weta2unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(9));
    }
    public static NUMBER Weta3unity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(10));
    }
    public static NUMBER WTunity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(11));
    }
    public static NUMBER WAunity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(12));
    }
    public static NUMBER WVunity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(13));
    }
    public static NUMBER WKunity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(14));
    }
    public static NUMBER WGunity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(15));
    }
    public static NUMBER WDunity(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WHIMDescriptor",Molfile);
        return wrapNumber(result.get(16));
    }
    public static NUMBER nHBAcc(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("HBondAcceptorCountDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER MOMIX(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MomentOfInertiaDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER MOMIY(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MomentOfInertiaDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER MOMIZ(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MomentOfInertiaDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER MOMIXY(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MomentOfInertiaDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER MOMIXZ(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MomentOfInertiaDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER MOMIYZ(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MomentOfInertiaDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER MOMIR(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MomentOfInertiaDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER khssLi(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER khsssBe(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER khsssssBe(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER khsssBH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER khssssB(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER khsssssB(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER khssCH3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER khsdCH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER khsssCH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER khstCH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(9));
    }
    public static NUMBER khsdsCH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(10));
    }
    public static NUMBER khsaaCH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(11));
    }
    public static NUMBER khssssCH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(12));
    }
    public static NUMBER khsddC(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(13));
    }
    public static NUMBER khstsC(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(14));
    }
    public static NUMBER khsdssC(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(15));
    }
    public static NUMBER khsaasC(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(16));
    }
    public static NUMBER khsaaaC(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(17));
    }
    public static NUMBER khsssssC(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(18));
    }
    public static NUMBER khssNH3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(19));
    }
    public static NUMBER khssNH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(20));
    }
    public static NUMBER khsssNH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(21));
    }
    public static NUMBER khsdNH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(22));
    }
    public static NUMBER khsssNH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(23));
    }
    public static NUMBER khsaaNH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(24));
    }
    public static NUMBER khstN(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(25));
    }
    public static NUMBER khssssNH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(26));
    }
    public static NUMBER khsdsN(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(27));
    }
    public static NUMBER khsaaN(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(28));
    }
    public static NUMBER khssssN(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(29));
    }
    public static NUMBER khsddsN(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(30));
    }
    public static NUMBER khsaasN(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(31));
    }
    public static NUMBER khsssssN(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(32));
    }
    public static NUMBER khssOH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(33));
    }
    public static NUMBER khsdO(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(34));
    }
    public static NUMBER khsssO(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(35));
    }
    public static NUMBER khsaaO(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(36));
    }
    public static NUMBER khssF(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(37));
    }
    public static NUMBER khssSiH3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(38));
    }
    public static NUMBER khsssSiH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(39));
    }
    public static NUMBER khssssSiH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(40));
    }
    public static NUMBER khsssssSi(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(41));
    }
    public static NUMBER khssPH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(42));
    }
    public static NUMBER khsssPH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(43));
    }
    public static NUMBER khssssP(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(44));
    }
    public static NUMBER khsdsssP(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(45));
    }
    public static NUMBER khssssssP(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(46));
    }
    public static NUMBER khssSH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(47));
    }
    public static NUMBER khsdS(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(48));
    }
    public static NUMBER khsssS(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(49));
    }
    public static NUMBER khsaaS(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(50));
    }
    public static NUMBER khsdssS(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(51));
    }
    public static NUMBER khsddssS(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(52));
    }
    public static NUMBER khssCl(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(53));
    }
    public static NUMBER khssGeH3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(54));
    }
    public static NUMBER khsssGeH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(55));
    }
    public static NUMBER khssssGeH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(56));
    }
    public static NUMBER khsssssGe(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(57));
    }
    public static NUMBER khssAsH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(58));
    }
    public static NUMBER khsssAsH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(59));
    }
    public static NUMBER khssssAs(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(60));
    }
    public static NUMBER khssssdAs(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(61));
    }
    public static NUMBER khssssssAs(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(62));
    }
    public static NUMBER khssSeH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(63));
    }
    public static NUMBER khsdSe(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(64));
    }
    public static NUMBER khsssSe(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(65));
    }
    public static NUMBER khsaaSe(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(66));
    }
    public static NUMBER khsdssSe(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(67));
    }
    public static NUMBER khsddssSe(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(68));
    }
    public static NUMBER khssBr(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(69));
    }
    public static NUMBER khssSnH3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(70));
    }
    public static NUMBER khsssSnH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(71));
    }
    public static NUMBER khssssSnH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(72));
    }
    public static NUMBER khsssssSn(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(73));
    }
    public static NUMBER khssI(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(74));
    }
    public static NUMBER khssPbH3(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(75));
    }
    public static NUMBER khsssPbH2(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(76));
    }
    public static NUMBER khssssPbH(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(77));
    }
    public static NUMBER khsssssPb(CLOB Molfile) throws Exception {
        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor("KierHallSmartsDescriptor",Molfile);
        return wrapNumber(result.get(78));
    }
    public static NUMBER nAtomLAC(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("LongestAliphaticChainDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER TopoPSA(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("TPSADescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER ECCEN(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("EccentricConnectivityIndexDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER nAtomP(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("LargestPiSystemDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER nHBDon(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("HBondDonorCountDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER XLogP(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("XLogPDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER WPATH(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WienerNumbersDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER WPOL(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("WienerNumbersDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER PetitjeanNumber(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("PetitjeanNumberDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER MDEC11(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER MDEC12(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER MDEC13(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER MDEC14(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER MDEC22(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER MDEC23(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(5));
    }
    public static NUMBER MDEC24(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(6));
    }
    public static NUMBER MDEC33(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(7));
    }
    public static NUMBER MDEC34(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(8));
    }
    public static NUMBER MDEC44(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(9));
    }
    public static NUMBER MDEO11(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(10));
    }
    public static NUMBER MDEO12(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(11));
    }
    public static NUMBER MDEO22(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(12));
    }
    public static NUMBER MDEN11(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(13));
    }
    public static NUMBER MDEN12(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(14));
    }
    public static NUMBER MDEN13(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(15));
    }
    public static NUMBER MDEN22(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(16));
    }
    public static NUMBER MDEN23(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(17));
    }
    public static NUMBER MDEN33(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("MDEDescriptor",Molfile);
        return wrapNumber(result.get(18));
    }
    public static NUMBER fragC(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("FragmentComplexityDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER apol(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("APolDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER naAromAtom(CLOB Molfile) throws Exception {
        IntegerResult result= (IntegerResult) invokeDescriptor("AromaticAtomsCountDescriptor",Molfile);
        return wrapNumber(result.intValue());
    }
    public static NUMBER Zagreb(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("ZagrebIndexDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER MLogP(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("MannholdLogPDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER ATSc1(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorCharge",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER ATSc2(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorCharge",Molfile);
        return wrapNumber(result.get(1));
    }
    public static NUMBER ATSc3(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorCharge",Molfile);
        return wrapNumber(result.get(2));
    }
    public static NUMBER ATSc4(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorCharge",Molfile);
        return wrapNumber(result.get(3));
    }
    public static NUMBER ATSc5(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("AutocorrelationDescriptorCharge",Molfile);
        return wrapNumber(result.get(4));
    }
    public static NUMBER MW(CLOB Molfile) throws Exception {
        DoubleResult result= (DoubleResult) invokeDescriptor("WeightDescriptor",Molfile);
        return wrapNumber(result.doubleValue());
    }
    public static NUMBER LOBMAX(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("LengthOverBreadthDescriptor",Molfile);
        return wrapNumber(result.get(0));
    }
    public static NUMBER LOBMIN(CLOB Molfile) throws Exception {
        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor("LengthOverBreadthDescriptor",Molfile);
        return wrapNumber(result.get(1));
    }
}