#!/bin/bash
source init.bash
echo
echo ___________________________________
echo
echo "DROPJAVA.. (ignore warnings)"
echo

dropjava  -user $DBUSER/$DBPASS\@$DBINST ../public_html/WEB-INF/classes/uk/ac/ebi/orchem/Utils.class
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/vecmath.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/commons-collections-3.2.1.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jgrapht-0.6.0.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/log4j-1.2.15.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST $JARDIR/cdk-1.1.2.ora11proof.fp.jar
dropjava  -user $DBUSER/$DBPASS\@$DBINST /tmp/orchem.jar

echo
echo "LOADJAVA"
echo
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST ../public_html/WEB-INF/classes/uk/ac/ebi/orchem/Utils.class
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/vecmath.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/commons-collections-3.2.1.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/jgrapht-0.6.0.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/log4j-1.2.15.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST $JARDIR/cdk-1.1.2.ora11proof.fp.jar
loadjava   -verbose -user $DBUSER/$DBPASS\@$DBINST /tmp/orchem.jar
