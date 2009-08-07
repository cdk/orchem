
declare
 i number;
begin
 for r_java in (select (select name from user_java_classes where name=dbms_java.longname(b.object_name)) as name from  user_objects b where b.status='INVALID')
 loop
     if       r_java.name like 'uk/ac/ebi%' 
           or r_java.name like 'org/openscience/cdk%' 
           or r_java.name like 'javax/vecmath%' 
           or r_java.name like 'org/_3pq%' 
           or r_java.name like 'org/apache%' 
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
 
