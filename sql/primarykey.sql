   -- create pks after the loading

    alter table orchem_compounds  add constraint pk_orchem_compounds primary key (id)
    /
    alter table orchem_fingprint_subsearch  add constraint pk_orchem_subsrch primary key (id)
    /
    alter table orchem_fingprint_simsearch  add constraint pk_orchem_simsrch primary key (id)
    /
