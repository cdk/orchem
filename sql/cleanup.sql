/*
  ___________________________________________________________________________

  Script to drop OrChem objects from your Oracle schema, for public use.
  
  WARNING: check the online documentation on upgrading Orchem.
  WARNING: check that the selection of Java packages in "my_array" below does not
           not conflict with any other packages you may have uploaded into Oracle.

  
  Developers: keep this file in sync with DML in rest of these scripts.

  ___________________________________________________________________________
*/

truncate table ORCHEM_LOG                  ;
truncate table ORCHEM_BIG_MOLECULES        ;
truncate table ORCHEM_FINGPRINT_SIMSEARCH  ;
truncate table ORCHEM_FINGPRINT_SUBSEARCH  ;
truncate table ORCHEM_PARAMETERS           ;
drop  table ORCHEM_LOG                     ;
drop  table ORCHEM_BIG_MOLECULES           ;
drop  table ORCHEM_FINGPRINT_SIMSEARCH     ;
drop  table ORCHEM_FINGPRINT_SUBSEARCH     ;
drop  table ORCHEM_PARAMETERS              ;

drop TYPE ORCHEM_COMPOUND_LIST;
drop TYPE ORCHEM_COMPOUND;
drop TYPE ORCHEM_NUMBER_TABLE;
drop TYPE COMPOUND_ID_TABLE;
  
drop PACKAGE ORCHEM_CONVERT        ;
drop PACKAGE ORCHEM_FINGERPRINTING ;
drop PACKAGE ORCHEM_QSAR           ;
drop PACKAGE ORCHEM_SIMSEARCH      ;
drop PACKAGE ORCHEM_SMARTS_SEARCH  ;
drop PACKAGE ORCHEM_SUBSEARCH      ;
drop PACKAGE ORCHEM_SUBSEARCH_PAR     ;
drop PACKAGE ORCHEM_UTILS          ;

drop SEQUENCE ORCHEM_SEQUENCE_LOG_ID       ;
drop SEQUENCE ORCHEM_SEQUENCE_QUERYKEYS    ;
drop SEQUENCE ORCHEM_SEQUENCE_TMP_FILENUMS ;


declare
  type my_varray is varray(20) of VARCHAR2(50);
  my_array my_varray;
begin
  my_array:=my_varray(
   'org/apache/commons/collections/%'
  ,'org/apache/commons/cli/%'
  ,'org/apache/commons/lang/%'
  ,'ambit2/smarts%'
  ,'javax/vecmath/%'
  ,'org/_3pq/jgrapht%'
  ,'org/apache/log4j/%'
  ,'Jama%'
  ,'org/openscience/cdk%'
  ,'org/openscience/jchempaint%'
  ,'org/iupac/StdInChI%'
  ,'org/ibex/nestedvm/%'
  ,'uk/ac/ebi/orchem%'
  );
  
  for i in my_array.first..my_array.last
  loop
     dbms_output.put_line ('Dropped all java classes in package '||my_array(i));
     for r_class in (select name from user_java_classes where name like my_array(i)) loop
        EXECUTE IMMEDIATE ('drop java class "'||r_class.name||'"' );
     end loop;
  end loop;
end;
/   

exit;