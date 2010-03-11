package uk.ac.ebi.orchem.codegen;

import org.openscience.cdk.qsar.DescriptorEngine;
import org.openscience.cdk.qsar.IMolecularDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *  This class generates qsar descriptor class and PL/SQL script.
 *  @see uk.ac.ebi.orchem.qsar.DescriptorCalculate
 *
 */
public class QSARGenerator {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        List<String> classNames = DescriptorEngine.getDescriptorClassNameByPackage("org.openscience.cdk.qsar.descriptors.molecular", null);
        StringBuffer sbJava = new StringBuffer();
        StringBuffer sbSQL = new StringBuffer();
        StringBuffer sbSQLPackage = new StringBuffer();
        StringBuffer sbSQLPackageBody = new StringBuffer();

        addJavaFileHeader(sbJava);
        addJavaClassDeclare(sbJava);
        addJavaInvokeDescriptorMethod(sbJava);

        addSQLHeader(sbSQL);

        sbSQLPackage.append("CREATE OR REPLACE PACKAGE orchem_qsar\n" +
                "AS ");
        sbSQLPackageBody.append("CREATE OR REPLACE PACKAGE BODY orchem_qsar\n" +
                "AS\n" +
                "  /*___________________________________________________________________________\n" +
                "   Functions to calculate QSAR descriptors\n" +
                "   ___________________________________________________________________________*/");

        for (String name : classNames) {
            IMolecularDescriptor descriptor = (IMolecularDescriptor) Class.forName(name).newInstance();
            String[] descriptorNames = descriptor.getDescriptorNames();
            IDescriptorResult resultType = descriptor.getDescriptorResultType();
            String descriptorClassSimpleName = descriptor.getClass().getSimpleName();
            String resultTypeSimpleName = resultType.getClass().getSimpleName();

            generateMethodBodyAndSQLDeclare(sbJava, sbSQLPackage, sbSQLPackageBody, descriptorNames, descriptorClassSimpleName, resultTypeSimpleName);

        }
        closeJavaAndSQL(sbJava, sbSQL, sbSQLPackage, sbSQLPackageBody);
        exportFile(sbJava, sbSQL);

    }

    private static void exportFile(StringBuffer sbJava, StringBuffer sbSQL) {
        //     System.out.println(sbJava.toString());
        //     System.out.println(sbSQL.toString());
        File javaFile = new File("src/uk/ac/ebi/orchem/qsar/DescriptorCalculate.java");
        File sqlFile = new File("sql/qsar.plsql");

        if (javaFile.exists()) {
            System.out.println(javaFile.getAbsolutePath() + " exists, overwriting....");
        }
        if (sqlFile.exists()) {
            System.out.println(sqlFile.getAbsolutePath() + " exists, overwriting....");
        }

        try {
            FileWriter fwJava = new FileWriter(javaFile, false);
            FileWriter fwSQL = new FileWriter(sqlFile, false);
            fwJava.write(sbJava.toString());
            fwSQL.write(sbSQL.toString());
            fwJava.close();
            fwSQL.close();
        } catch (IOException e) {
            System.out.println("Failed to write file:\n " + e);
        }
    }

    private static void closeJavaAndSQL(StringBuffer sbJava, StringBuffer sbSQL, StringBuffer sbSQLPackage, StringBuffer sbSQLPackageBody) {
        sbJava.append("}");
        sbSQLPackage.append("END;\n" +
                "/\n" +
                "SHOW ERRORS\n\n\n");
        sbSQLPackageBody.append("END;\n" +
                "/   \n" +
                "SHOW ERR\n" +
                "\n" +
                "exit 1;\n");
        sbSQL.append(sbSQLPackage.toString());
        sbSQL.append(sbSQLPackageBody.toString());
    }

    private static void addSQLHeader(StringBuffer sbSQL) {
        sbSQL.append("/*\n" +
                "________________________________________________________________________________\n" +
                "\n" +
                "QSAR descriptors\n" +
                "\n" +
                "copyright: Duan Lian, EMBL EBI 2010\n" +
                "\n" +
                "________________________________________________________________________________\n" +
                "*/");
    }

    private static void generateMethodBodyAndSQLDeclare(StringBuffer sbJava, StringBuffer sbSQLPackage, StringBuffer sbSQLPackageBody, String[] descriptorNames, String descriptorClassSimpleName, String resultTypeSimpleName) {
        if (resultTypeSimpleName.startsWith("DoubleArrayResult")) {
            for (int i = 0, descriptorNamesLength = descriptorNames.length; i < descriptorNamesLength; i++) {
                String descriptorName = filterDescriptorName(descriptorNames[i]);
                sbJava.append("    public static NUMBER " + descriptorName + "(CLOB Molfile) throws Exception {\n" +
                        "        DoubleArrayResult result= (DoubleArrayResult) invokeDescriptor(\"" + descriptorClassSimpleName + "\",Molfile);\n" +
                        "        return wrapNumber(result.get(" + i + "));\n" +
                        "    }");
                sbJava.append("\n");
                addSQLDeclare(sbSQLPackage, sbSQLPackageBody, descriptorName);
            }
        } else if (resultTypeSimpleName.startsWith("IntegerArrayResult")) {
            for (int i = 0, descriptorNamesLength = descriptorNames.length; i < descriptorNamesLength; i++) {
                String descriptorName = filterDescriptorName(descriptorNames[i]);
                sbJava.append("    public static NUMBER " + descriptorName + "(CLOB Molfile) throws Exception {\n" +
                        "        IntegerArrayResult result= (IntegerArrayResult) invokeDescriptor(\"" + descriptorClassSimpleName + "\",Molfile);\n" +
                        "        return wrapNumber(result.get(" + i + "));\n" +
                        "    }");
                sbJava.append("\n");
                addSQLDeclare(sbSQLPackage, sbSQLPackageBody, descriptorName);
            }
        } else if (resultTypeSimpleName.startsWith("DoubleResult")) {
            String descriptorName = filterDescriptorName(descriptorNames[0]);
            sbJava.append("    public static NUMBER " + descriptorName + "(CLOB Molfile) throws Exception {\n" +
                    "        DoubleResult result= (DoubleResult) invokeDescriptor(\"" + descriptorClassSimpleName + "\",Molfile);\n" +
                    "        return wrapNumber(result.doubleValue());\n" +
                    "    }");
            sbJava.append("\n");
            addSQLDeclare(sbSQLPackage, sbSQLPackageBody, descriptorName);
        } else if (resultTypeSimpleName.startsWith("IntegerResult")) {
            String descriptorName = filterDescriptorName(descriptorNames[0]);
            sbJava.append("    public static NUMBER " + descriptorName + "(CLOB Molfile) throws Exception {\n" +
                    "        IntegerResult result= (IntegerResult) invokeDescriptor(\"" + descriptorClassSimpleName + "\",Molfile);\n" +
                    "        return wrapNumber(result.intValue());\n" +
                    "    }");
            sbJava.append("\n");
            addSQLDeclare(sbSQLPackage, sbSQLPackageBody, descriptorName);
        }
    }

    private static void addSQLDeclare(StringBuffer sbSQLPackage, StringBuffer sbSQLPackageBody, String descriptorName) {
        sbSQLPackage.append("   FUNCTION " + descriptorName + " (molfile clob)\n" +
                "   RETURN NUMBER;\n");
        sbSQLPackageBody.append("   FUNCTION " + descriptorName + " (molfile clob)\n" +
                "   RETURN NUMBER\n" +
                "   IS LANGUAGE JAVA NAME \n" +
                "   'uk.ac.ebi.orchem.qsar.DescriptorCalculate." + descriptorName + "(oracle.sql.CLOB) return oracle.sql.NUMBER ';\n\n");
    }

    private static void addJavaInvokeDescriptorMethod(StringBuffer sbJava) {
        sbJava.append("    /**\n" +
                "     * Method to invoke given descriptor.\n" +
                "     *\n" +
                "     * @param className   Simple name of descriptor class, e.g. ALOGPDescriptor,WeightedPathDescriptor\n" +
                "     * @param Molfile     Molfile input from Oracle SQL\n" +
                "     */\n" +
                "\n" +
                "    private static IDescriptorResult invokeDescriptor(String className,CLOB Molfile) {\n" +
                "        IDescriptorResult resultArray;\n" +
                "        try {\n" +
                "            IMolecularDescriptor descriptor = (IMolecularDescriptor) Class.forName(\"org.openscience.cdk.qsar.descriptors.molecular.\"+className).newInstance();\n" +
                "            MDLV2000Reader mdlReader = new MDLV2000Reader();\n" +
                "            String molfile = Utils.ClobToString(Molfile);\n" +
                "            if (molfile != null) {\n" +
                "                NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, molfile);\n" +
                "                resultArray = descriptor.calculate(molecule).getValue();\n" +
                "            } else {\n" +
                "                resultArray = null;\n" +
                "            }\n" +
                "        } catch (Exception e) {\n" +
                "            resultArray = null;\n" +
                "            System.out.println(Utils.getErrorString(e));\n" +
                "        }\n" +
                "        return resultArray;\n" +
                "    }");
        sbJava.append("\n");
        sbJava.append("    /**\n" +
                "     * Wrap Java double to Oracl NUMBER.\n" +
                "     *\n" +
                "     * @param value   Java double value to wrap\n" +
                "     * @return       Oracle NUMBER\n"+
                "     */\n" +
                "\n" +
                "    public static NUMBER wrapNumber(double value) throws Exception {\n" +
                "        if(value==Double.NaN){\n" +
                "            return null;\n" +
                "        }\n" +
                "        else{\n" +
                "            return new NUMBER(value);\n" +
                "        }\n" +
                "    }\n" +
                "    /**\n" +
                "     * Wrap Java int to Oracl NUMBER.\n" +
                "     *\n" +
                "     * @param value   Java double value to wrap\n" +
                "     * @return       Oracle NUMBER\n"+                
                "     */\n" +
                "\n" +
                "    public static NUMBER wrapNumber(int value){\n" +
                "            return new NUMBER(value);\n" +
                "    }");
        sbJava.append("\n");
        
    }

    private static void addJavaClassDeclare(StringBuffer sbJava) {
        sbJava.append("public class DescriptorCalculate {\n");
        sbJava.append("\n");
    }

    private static void addJavaFileHeader(StringBuffer sbJava) {
        sbJava.append("/*\n" +
                " *  $Author$\n" +
                " *  $Date$\n" +
                " *  $Revision$\n" +
                " *\n" +
                " *  Copyright (C) 2010  Duan Lian\n" +
                " *\n" +
                " *  This program is free software; you can redistribute it and/or\n" +
                " *  modify it under the terms of the GNU Lesser General Public License\n" +
                " *  as published by the Free Software Foundation; either version 2.1\n" +
                " *  of the License, or (at your option) any later version.\n" +
                " *  All we ask is that proper credit is given for our work, which includes\n" +
                " *  - but is not limited to - adding the above copyright notice to the beginning\n" +
                " *  of your source code files, and to any copyright notice that you may distribute\n" +
                " *  with programs based on this work.\n" +
                " *\n" +
                " *  This program is distributed in the hope that it will be useful,\n" +
                " *  but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                " *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                " *  GNU Lesser General Public License for more details.\n" +
                " */\n\n");
        sbJava.append("package uk.ac.ebi.orchem.qsar;\n");
        sbJava.append("import oracle.sql.CLOB;\n" +
                "import oracle.sql.NUMBER;\n" +
                "\n");
        sbJava.append("import org.openscience.cdk.io.MDLV2000Reader;\n" +
                "import org.openscience.cdk.nonotify.NNMolecule;\n" +
                "import org.openscience.cdk.qsar.IMolecularDescriptor;\n" +
                "import org.openscience.cdk.qsar.result.*;\n" +
                "import uk.ac.ebi.orchem.Utils;\n" +
                "import uk.ac.ebi.orchem.shared.MoleculeCreator;\n");
        sbJava.append("\n");
    }

    public static String filterDescriptorName(String descriptorName) {
        descriptorName = descriptorName.replace("-", "");
        descriptorName = descriptorName.replace(".", "");
        return descriptorName;
    }

}
