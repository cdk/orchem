
   -- TODO : document this. make user aware that dbms_job should have queue open =>4 to make this run in parallel


   create bitmap index orchem_idx_bmp_simil on orchem_fingprint_simsearch (bit_count)
   /
        
   declare 
      j binary_integer; 
      job varchar2(1000);
   begin 
      for i in 0..7 loop
        job:='begin for i in '||((i*64)+1)||' .. '||((i+1)*64)||' loop execute immediate(''create bitmap index orchem_bmp_idx_fp''||i||'' on orchem_fingprint_subsearch (bit''||i||'') parallel 3 nologging'' ); end loop; end;'  ;
        --dbms_output.put_line(job);
        dbms_job.submit(j,job,sysdate);
      end loop;
      commit; -- must commit to activate dbms job
   end;

    /*
    begin
        for i in 1..512 loop
           execute immediate (' create bitmap index orchem_bmp_idx_fp'||i||' on orchem_fingprint_subsearch (bit'||i||') parallel 4 nologging');
        end loop;
    end;
    /
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
    
    
