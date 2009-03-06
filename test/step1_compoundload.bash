#!/bin/bash
source init.bash

echo Load query molecules
java -cp $CLASSPATH  uk.ac.ebi.orchem.test.LoadCompounds $BASEDIR/test/step1_compoundtable.queries.mol 0

echo Load target molecules
java -cp $CLASSPATH  uk.ac.ebi.orchem.test.LoadCompounds $BASEDIR/test/step1_compoundtable.mol 1000 
