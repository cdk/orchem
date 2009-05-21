system("sqlplus -S $username/$password\@$instance \@../sql/types.sql");
system("sqlplus -S $username/$password\@$instance \@../sql/seq.sql");
system("sqlplus -S $username/$password\@$instance \@../sql/tables.sql");
system("sqlplus -S $username/$password\@$instance \@../sql/indices.sql");
system("sqlplus -S $username/$password\@$instance \@../sql/utils.plsql");
system("sqlplus -S $username/$password\@$instance \@../sql/createfingerprints.plsql");
system("sqlplus -S $username/$password\@$instance \@../sql/simsearch.plsql");
system("sqlplus -S $username/$password\@$instance \@../sql/subsearch.plsql");
system("sqlplus -S $username/$password\@$instance \@../sql/subsearchParallel.plsql");

