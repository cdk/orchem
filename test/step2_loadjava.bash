#!/bin/bash
source init.bash
echo
echo ___________________________________
echo
echo "DROPJAVA.. (ignore warnings)"
echo

#dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/vecmath.jar
#dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/commons-collections-3.2.1.jar
#dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jgrapht-0.6.0.jar
#dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/log4j-1.2.15.jar
#dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST $JARDIR/cdk.jar
dropjava  -nowarn -user $DBUSER/$DBPASS\@$DBINST /tmp/orchem.jar


echo
echo "LOADJAVA"
echo
#loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/vecmath.jar
#loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/commons-collections-3.2.1.jar
#loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jgrapht-0.6.0.jar
#loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/log4j-1.2.15.jar
#loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/cdk.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST /tmp/orchem.jar
