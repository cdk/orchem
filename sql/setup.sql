set serveroutput on size 1000000 format wrapped
exec dbms_java.set_output(10000000);
 
@types.sql
@seq.sql
@tables.sql
@utils.plsql
@createfingerprints.plsql
@convert.plsql
@simsearch.plsql
@subsearch.plsql
@subsearchParallel.plsql
@smartsSearch.plsql
@qsar.plsql
@calculate.plsql


