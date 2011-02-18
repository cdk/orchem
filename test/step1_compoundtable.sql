
prompt Creating a table to hold some test compounds

CREATE TABLE orchem_compound_sample
(
  id            integer not null PRIMARY KEY,
  smiles_or_mol clob    not null
)
/

prompt Setting up 'orchem_parameters'
insert into orchem_parameters values ('ORCHEM_COMPOUND_SAMPLE','ID','SMILES_OR_MOL','&&1')
/
commit
/

exit 
