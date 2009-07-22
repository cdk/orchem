 /*___________________________________________________________________________
   Gather statistics of the OrChem schema
   ___________________________________________________________________________*/

   exec dbms_stats.GATHER_TABLE_STATS (ownname=> user, tabname=> 'orchem_big_molecules'      , degree=>4);
   exec dbms_stats.GATHER_TABLE_STATS (ownname=> user, tabname=> 'orchem_fingprint_simsearch', degree=>4);
   exec dbms_stats.GATHER_TABLE_STATS (ownname=> user, tabname=> 'orchem_fingprint_subsearch', degree=>4, estimate_percent=>25);

--pubchem@CHEZTST    exec dbms_stats.GATHER_TABLE_STATS (ownname=> user, tabname=> 'orchem_fingprint_subsearch', degree=>4, estimate_percent=>25);
--PL/SQL procedure successfully completed.
--Elapsed: 00:17:50.93




 
