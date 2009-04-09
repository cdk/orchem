#!/bin/bash

BASEDIR=`pwd | sed 's/\OrChem\/test.*/OrChem/'`
JARDIR="$BASEDIR/public_html/WEB-INF/lib"
CLASSDIR="$BASEDIR/public_html/WEB-INF/classes"
SRCDIR="$BASEDIR/src"

CLASSPATH=$CLASSDIR
CLASSPATH=$CLASSPATH:$BASEDIR/public_html/WEB-INF/lib/cdk-1.1.2.ora11proof.fp.jar
CLASSPATH=$CLASSPATH:$BASEDIR/public_html/WEB-INF/lib/ojdbc5.jar
CLASSPATH=$CLASSPATH:$BASEDIR/public_html/WEB-INF/lib/vecmath.jar

ant -buildfile $BASEDIR/build.xml compile

#activation.jar               commons-collections-3.2.1.jar  freemarker-2.3.14.jar          log4j-1.2.15.jar       ognl-2.6.11.jar  struts2-core-2.0.11.2.jar
#aurora.zip                   commons-logging-1.0.4.jar      javax.servlet_1.0.0.0_2-5.jar  lucene-core-2.3.2.jar  ojdbc5.jar       tabletags-1.0.1.jar
#cdk-1.1.2.jar                commons-logging-api-1.1.jar    jgrapht-0.6.0.jar              mail.jar               old              vecmath.jar
#cdk-1.1.2.ora11proof.fp.jar  CVS                            junit.jar                      MarvinBeans-5.1.0.jar  standard.jar     xwork-2.0.5.jar

