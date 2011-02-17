#!/bin/bash
source init.bash
echo
echo ___________________________________
echo
echo "DROPJAVA.. (ignore warnings)"
echo







dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/vecmath.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jama-1.0.2.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/commons-collections-3.2.1.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jgrapht-0.6.0.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/log4j-1.2.15.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/nestedVm.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/inchi103.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/cdk.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/ambit2.smarts.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jcp.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST /tmp/orchem.jar



echo
echo "LOADJAVA"
echo
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/vecmath.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jama-1.0.2.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/commons-collections-3.2.1.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jgrapht-0.6.0.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/log4j-1.2.15.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/nestedVm.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/inchi103.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/cdk.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/ambit2.smarts.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jcp.jar
loadjava -f  -verbose -user $DBUSER/$DBPASS\@$DBINST /tmp/orchem.jar
