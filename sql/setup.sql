set serveroutput on size 1000000 format wrapped
exec dbms_java.set_output(10000000);
 
@types.sql
@seq.sql
@tables.sql
@utils.plsql
@createfingerprints.plsql
@simsearch.plsql
@subsearch.plsql
@subsearchParallel.plsql
@smartsSearch.plsql
@convert.plsql
@qsar.plsql
@calculate.plsql


