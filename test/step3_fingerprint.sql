


prompt deleting current fingerprints
prompt =============================

prompt delete orchem_fingprint_subsearch
delete orchem_fingprint_subsearch
/
prompt delete orchem_fingprint_simsearch
delete orchem_fingprint_simsearch
/
prompt delete orchem_log
delete orchem_log
/
prompt delete orchem_big_molecules 
delete orchem_big_molecules
/
commit;

prompt JIT... please wait
prompt =====================
    declare
         i number;
       begin
         for r_java in (select name from user_java_classes ) loop
             if     r_java.name like 'org/openscience/cdk%'
                 or r_java.name like 'javax/vecmath%'
                 or r_java.name like 'org/_3pq%'
                 or r_java.name like 'uk/ac/ebi%' 
             then

                begin
                  i:=dbms_java.compile_class (r_java.name);
                  dbms_output.put_line ('Compiled '||r_java.name);
                exception
                when others then
                  dbms_output.put_line ('Error for '||r_java.name);
                end;
             end if;
         end loop;
      end;
/

prompt create new fingerprints
prompt =============================
prompt please wait....

begin
 orchem_fingerprinting.LOAD_CDK_FINGERPRINTS(null,null);
end;
/

prompt log results
prompt =============================
col what format a100 
set pages 1000
set lines 150
set long 40000 

select what from orchem_log
/
exit;   
