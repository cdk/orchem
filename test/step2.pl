#!/usr/local/bin/perl

if (@ARGV ne 5){die "Error, wrong number of input arguments (expecting username,password,instance)"};

$os=$ARGV[4];

if ($os eq "MSWin32") { 
	$username = $ARGV[0]; 
	$password = $ARGV[1];  
	$instance = $ARGV[2]; 
	$dir      = $ARGV[3]; 
	system("step2_orchemjar.bat $dir");
	system("step2_loadjava.bat $username $password $instance $dir");
}
else { 
	$ENV{DBUSER}      = $ARGV[0];
	$ENV{DBPASS}      = $ARGV[1];
	$ENV{DBINST}      = $ARGV[2];
	system("bash ./step2_orchemjar.bash");
	system("bash ./step2_loadjava.bash");
}


