#!/bin/bash
source init.bash
echo
echo ___________________________________
echo
echo "DROPJAVA.. (ignore warnings)"
echo

dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/vecmath.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/commons-collections-3.2.1.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jgrapht-0.6.0.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/log4j-1.2.15.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/cdk-1.1.2.ora11proof.fp.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST /tmp/orchem.jar

#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/shared/AtomsBondsCounter.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/scratch/MyAllRingsFinder.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/shared/MoleculeCreator.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/Utils.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/SimpleMail.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/db/OrChemParameters.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/bean/OrChemCompound.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/bean/OrChemCompoundTanimComparator.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/bean/SimHeapElement.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/bean/SimHeapElementTanimComparator.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/singleton/FingerPrinterAgent.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/fingerprint/bitpos/Neighbour.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/fingerprint/bitpos/BitPositions.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/fingerprint/bitpos/BitPosApi.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/fingerprint/OrchemFingerprinter.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/load/LoadCDKFingerprints.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/search/SimilaritySearch.java
#dropjava -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/search/SubstructureSearch.java



echo
echo "LOADJAVA"
echo
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/vecmath.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/commons-collections-3.2.1.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jgrapht-0.6.0.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/log4j-1.2.15.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/cdk-1.1.2.ora11proof.fp.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST /tmp/orchem.jar

#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/shared/AtomsBondsCounter.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/scratch/MyAllRingsFinder.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/shared/MoleculeCreator.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/Utils.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/SimpleMail.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/db/OrChemParameters.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/bean/OrChemCompound.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/bean/OrChemCompoundTanimComparator.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/bean/SimHeapElement.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/bean/SimHeapElementTanimComparator.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/fingerprint/bitpos/Neighbour.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/fingerprint/bitpos/BitPositions.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/fingerprint/bitpos/BitPosApi.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/fingerprint/OrchemFingerprinter.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/singleton/FingerPrinterAgent.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/load/LoadCDKFingerprints.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/search/SimilaritySearch.java
#loadjava -verbose -resolve -user $DBUSER/$DBPASS\@$DBINST $SRCDIR/uk/ac/ebi/orchem/search/SubstructureSearch.java

