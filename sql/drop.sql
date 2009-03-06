set heading off
set feedback off
set linesize 200
set termout on
SET SERVEROUT ON

set verify off


declare 
  user_prefix VARCHAR2(30) :='&1';
begin
    dbms_output.enable (1000000);
    user_prefix := UPPER(user_prefix);

    dbms_output.put_line ( 'Dropping all objects schema "&1" ..');
	
    for tr in ( select trigger_name, 'drop trigger ' ||user_prefix||'.'|| trigger_name stmt
               from user_triggers  where trigger_name like 'ORCHEM%')
    loop begin
       dbms_output.put_line ( 'Dropping trigger ' ||user_prefix||'.'|| tr.trigger_name );
       execute immediate ( tr.stmt );
    exception when others then null; end; end loop;

    for seq in ( select sequence_name, 'drop sequence ' ||user_prefix||'.'|| sequence_name stmt
                 from user_sequences where sequence_name like 'ORCHEM%' )
    loop begin
       dbms_output.put_line ( 'Dropping sequence ' ||user_prefix||'.'|| seq.sequence_name );
       execute immediate ( seq.stmt );
    exception when others then null; end; end loop;

    for vie in ( select view_name, 'drop view ' || view_name ||
                 ' CASCADE CONSTRAINTS' stmt
                 from user_views where view_name like 'ORCHEM%' )
    loop begin
       dbms_output.put_line ( 'Dropping view ' ||user_prefix||'.'|| vie.view_name );
       execute immediate ( vie.stmt );
    exception when others then null; end; end loop;

    for tab in ( select table_name, 'drop table ' ||user_prefix||'.'|| table_name ||
                 ' CASCADE CONSTRAINTS' stmt
                 from user_tables where table_name like 'ORCHEM%')
    loop begin
       dbms_output.put_line ( 'Dropping table ' ||user_prefix||'.'|| tab.table_name );
       execute immediate ( tab.stmt );
    exception when others then null; end; end loop;

    for pro in ( select object_name, 'drop procedure ' ||user_prefix||'.'|| object_name stmt
                 from user_objects where object_type='PROCEDURE' and object_name like 'ORCHEM%' )
    loop begin
       dbms_output.put_line ( 'Dropping procedure ' ||user_prefix||'.'|| pro.object_name );
       execute immediate ( pro.stmt );
    exception when others then null; end; end loop;

    for fun in ( select object_name, 'drop function ' ||user_prefix||'.'|| object_name stmt
                 from user_objects where object_type='FUNCTION' and object_name like 'ORCHEM%' )
    loop begin
       dbms_output.put_line ( 'Dropping function ' ||user_prefix||'.'|| fun.object_name );
       execute immediate ( fun.stmt );
    exception when others then null; end; end loop;

    for pac in ( select object_name, 'drop package ' ||user_prefix||'.'|| object_name stmt
                 from user_objects where object_type='PACKAGE' and object_name like 'ORCHEM%' )
    loop begin
       dbms_output.put_line ( 'Dropping package ' ||user_prefix||'.'|| pac.object_name );
       execute immediate ( pac.stmt );
    exception when others then null; end; end loop;

    for typ in ( select object_name, 'drop type ' ||user_prefix||'.'|| object_name || ' FORCE' stmt
                 from user_objects where object_type='TYPE'  and object_name like 'ORCHEM%' )
    loop begin
       dbms_output.put_line ( 'Dropping type ' ||user_prefix||'.'|| typ.object_name );
       execute immediate ( typ.stmt );
    exception when others then null; end; end loop;

    dbms_output.put_line ( '.. completed.'||chr(10)||chr(10));
end;
/


exit

