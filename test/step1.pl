#!/usr/local/bin/perl

#print "1 $ARGV[0]\n"; 
#print "2 $ARGV[1]\n"; 
#print "3 $ARGV[2]\n"; 
#print "4 $ARGV[3]\n"; 
#print "5 $ARGV[4]\n"; 


if (@ARGV ne 5){die "Error, wrong number of input arguments ! "};

$username = $ARGV[0]; 
$password = $ARGV[1];  
$instance = $ARGV[2]; 
$dir      = $ARGV[3]; 
$os       = $ARGV[4];




print "----------------------------------------------\n\n";
print "DROPPING objects in existing schema $username.. \n\n";
system("sqlplus -S $username/$password\@$instance \@../sql/drop.sql $username ");

print "----------------------------------------------\n\n";
print "CREATING new schema\n\n";
system("sqlplus -S $username/$password\@$instance \@../sql/types.sql");
system("sqlplus -S $username/$password\@$instance \@../sql/tables.sql");
system("sqlplus -S $username/$password\@$instance \@../sql/primarykey.sql");
system("sqlplus -S $username/$password\@$instance \@../sql/indices.sql");
system("sqlplus -S $username/$password\@$instance \@../sql/plsql.sql");

system("sqlplus -S $username/$password\@$instance \@./step1_compoundtable.sql");



print "----------------------------------------------\n\n";
print "Loading compound sample from molfile into db \n";
print "----------------------------------------------\n\n";

if ($os eq "MSWin32") {
  system("step1_compoundload.bat $dir");
}
else {
  system("bash ./step1_compoundload.bash");
}

print "\nYou should now have a schema for further testing in $username\@$instance.\nCompounds are in table called 'orchem_compound_sample' \n";
print "\nPlease check the schema and then continue with the other steps.\n\n\n";

print "----------------------------------------------\n\n";

