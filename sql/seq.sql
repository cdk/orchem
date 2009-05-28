/*___________________________________________________________________________
  Creates sequences needed by OrChem
  ___________________________________________________________________________*/

prompt creating sequence "orchem_sequence_log_id" for "orchem_parameters"    
   create sequence orchem_sequence_log_id
   increment by 1 
   minvalue 1 
   nocycle 
   nocache
   /


prompt creating sequence "orchem_sequence_querykeys"
   create sequence orchem_sequence_querykeys
   increment by 1 
   minvalue 1 
   maxvalue 5000 
   cycle  --!! important -> roll over sequence
   nocache
/

exit;--end of script


 
