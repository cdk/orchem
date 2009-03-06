#!/usr/local/bin/perl

if (@ARGV ne 3){die "Error, wrong number of input arguments (expecting username,password,instance)"};

$ENV{DBUSER}      = $ARGV[0];
$ENV{DBPASS}      = $ARGV[1];
$ENV{DBINST}      = $ARGV[2];

system("bash ./step2_orchemjar.bash");
system("bash ./step2_loadjava.bash");

