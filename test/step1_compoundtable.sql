
prompt Creating a table to hold some test compounds

CREATE TABLE orchem_compound_sample
(
  id       integer not null PRIMARY KEY,
   molfile clob    not null
)
/

prompt Setting up 'orchem_parameters'
insert into orchem_parameters values ('ORCHEM_COMPOUND_SAMPLE','ID','MOLFILE','&&1')
/
commit
/

exit 
