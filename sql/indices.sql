   
    create bitmap index orchem_idx_bmp_simil on orchem_fingprint_simsearch (bit_count)
    /

    begin
        for i in 1..512 loop
           execute immediate (' create bitmap index orchem_bmp_idx_fp'||i||' on orchem_fingprint_subsearch (bit'||i||')');
        end loop;
    end;
    /

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
    
    
