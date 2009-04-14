#!/usr/local/bin/perl

if (@ARGV ne 3){die "Error, wrong number of input arguments (expecting username,password,instance)"};

system ("sqlplus -S $ARGV[0]/$ARGV[1]\@$ARGV[2] \@step3_fingerprint.sql");