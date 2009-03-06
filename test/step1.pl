#!/usr/local/bin/perl


if (@ARGV ne 3){die "Error, wrong number of input arguments (expecting username,password,instance)"};

$username = $ARGV[0]; 
$password = $ARGV[1];  
$instance = $ARGV[2]; 



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
system("bash ./step1_compoundload.bash");

print "You should now have a schema for further testing in $username\@$instance.\nCompounds are in table called 'orchem_compound_sample' \n";
print "Please check the schema and then continue with the other steps.\n\n\n";

print "----------------------------------------------\n\n";

