   -- create pks after the loading ? 
   -- loads faster but can cock up if dbms_job fails and re-inserts..

    alter table orchem_compounds  add constraint pk__orchem_compounds primary key (id)
    /
    alter table orchem_fingprint_subsearch  add constraint pk__orchem_subsrch primary key (id)
    /
    --alter table orchem_fingprint_simsearch  add constraint pk_orchem_simsrch primary key (id)
    --/
