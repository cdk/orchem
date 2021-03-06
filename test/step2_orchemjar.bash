#!/bin/bash
source init.bash
echo
echo ..
echo Creating OrChem JAR file
echo ___________________________________
rm /tmp/orchem.jar







jar -cvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/shared/AtomsBondsCounter.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/shared/MoleculeCreator.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/Utils.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/SimpleMail.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/db/OrChemParameters.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/bean/OrChemCompound.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/bean/OrChemCompoundTanimComparator.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/bean/SimHeapElement.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/bean/SimHeapElementTanimComparator.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/fingerprint/bitpos/Neighbour.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/fingerprint/bitpos/BitPositions.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/fingerprint/bitpos/ExtendedBitPositions.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/fingerprint/bitpos/BitPosApi.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/fingerprint/OrchemFingerprinter.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/fingerprint/OrchemExtendedFingerprinter.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/singleton/FingerPrinterAgent.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/SimilaritySearch.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/OrchemMoleculeBuilder.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/UserQueryMolecule.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/SubstructureSearch.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/SubstructureSearchParallel.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/SMARTS_Search.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/test/VerifyOrchem.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/isomorphism/*.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/load/*.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/convert/ConvertMolecule.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/qsar/*.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/calc/*.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/tautomers/*.class
