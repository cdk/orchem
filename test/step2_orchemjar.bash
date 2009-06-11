#!/bin/bash
source init.bash
echo
echo ..
echo Creating OrChem JAR file
echo ___________________________________
rm /tmp/orchem.jar


jar -cvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/shared/AtomsBondsCounter.class


#temporary classes, until available in official CDK release #
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/temp/MyAllRingsFinder.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/temp/IsomorphismSort\$AtomForIsomorphismSort.class 
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/temp/IsomorphismSort\$AtomForIsomorphismSortComparator.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/temp/IsomorphismSort\$1.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/temp/IsomorphismSort.class
################

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
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/fingerprint/bitpos/BitPosApi.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/fingerprint/OrchemFingerprinter.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/singleton/FingerPrinterAgent.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/load/LoadCDKFingerprints.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/SimilaritySearch.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/OrchemMoleculeBuilder.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/UserQueryMolecule.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/SubstructureSearch.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/search/SubstructureSearchParallel.class
jar -uvf /tmp/orchem.jar $CLASSDIR/uk/ac/ebi/orchem/test/VerifyOrchem.class

