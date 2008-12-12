   
    create bitmap index orchem_idx_bmp_simil on orchem_fingprint_simsearch (bit_count)
    /


    
    begin
        for i in 1..512 loop
           execute immediate (' create bitmap index orchem_bmp_idx_fp'||i||' on orchem_fingprint_subsearch (bit'||i||') parallel 4 nologging');
        end loop;
    end;
    /

                
   /*
   declare 
      j binary_integer; 
      job varchar2(1000);
   begin 
      for i in 66..66 loop
        job:='execute immediate(''create bitmap index orchem_bmp_idx_fp'||i||' on orchem_fingprint_subsearch (bit'||i||') parallel 4 nologging'' );'  ;
        dbms_job.submit(j,job,sysdate);
      end loop;
   end;
   */



    -- Archived: Oracle context index. Performance not adequate.
    /*
    CREATE INDEX SUBSTR_SEARCH_INDX ON orchem_fingprint_subsearch (fp_text) INDEXTYPE IS CTXSYS.CONTEXT
    /
    */

    -- Archived: Luciano index. Performance not adequate.
    /*
    create index LUCENE_IDX on orchem_fingprint_subsearch (fp_text) 
    indextype is lucene.LuceneIndex
    parameters('Analyzer:org.apache.lucene.analysis.standard.StandardAnalyzer')
    /
    */
    
    
