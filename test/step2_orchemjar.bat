
call init.bat %1

echo ..
echo Creating OrChem JAR file
echo ___________________________________

del c:\orchem.jar
echo %CLASSDIR%

cd %CLASSDIR%



jar -cvf c:\orchem.jar uk/ac/ebi/orchem/shared/AtomsBondsCounter.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/shared/MoleculeCreator.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/Utils.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/SimpleMail.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/db/OrChemParameters.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/bean/OrChemCompound.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/bean/OrChemCompoundTanimComparator.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/bean/SimHeapElement.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/bean/SimHeapElementTanimComparator.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/fingerprint/bitpos/Neighbour.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/fingerprint/bitpos/BitPositions.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/fingerprint/bitpos/ExtendedBitPositions.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/fingerprint/bitpos/BitPosApi.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/fingerprint/OrchemFingerprinter.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/fingerprint/OrchemExtendedFingerprinter.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/singleton/FingerPrinterAgent.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/search/SimilaritySearch.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/search/OrchemMoleculeBuilder.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/search/UserQueryMolecule.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/search/SubstructureSearch.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/search/SubstructureSearchParallel.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/search/OrchemSMARTSQueryTool.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/search/SMARTS_Search.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/test/VerifyOrchem.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/isomorphism/*.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/load/*.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/convert/ConvertMolecule.class
jar -uvf c:\orchem.jar uk/ac/ebi/orchem/qsar/*.class


