#!/bin/bash

BASEDIR=`pwd | sed 's/\OrChem\/test.*/OrChem/'`
JARDIR="$BASEDIR/public_html/WEB-INF/lib"
CLASSDIR="$BASEDIR/public_html/WEB-INF/classes"
SRCDIR="$BASEDIR/src"

CLASSPATH=$CLASSDIR
CLASSPATH=$CLASSPATH:$BASEDIR/public_html/WEB-INF/lib/cdk.jar
CLASSPATH=$CLASSPATH:$BASEDIR/public_html/WEB-INF/lib/ojdbc5.jar
CLASSPATH=$CLASSPATH:$BASEDIR/public_html/WEB-INF/lib/vecmath.jar

ant -buildfile $BASEDIR/build.xml compile


