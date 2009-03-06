#!/bin/bash
source init.bash
echo
echo ___________________________________
echo

sqlplus -S $DBUSER/$DBPASS\@$DBINST \@step3_fingerprint.sql
