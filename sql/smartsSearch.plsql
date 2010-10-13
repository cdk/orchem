/*
________________________________________________________________________________

SMARTS searching, parallelized

author: markr@ebi.ac.uk, 2009

Note: to enable the effect of PIPEd functions, 
- in sql*plus: set arraysize=1
- in jdbc    : conn.setDefaultRowPrefetch(1);

________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_smarts_search
AS 
   /* ? extra result record - l'Oracle doesn't like returning orchem_compound
      objects from the parallel  function... */
   TYPE result_rec is RECORD (
     id varchar2(80)
     ,molecule clob
    );
   
   TYPE result_tab IS TABLE OF result_rec;

   FUNCTION search (smarts_query clob, debug_yn varchar2:='N' , 
                    topN integer:=null, force_full_scan varchar2:='Y' )   
   RETURN  orchem_compound_list
   PIPELINED;

   FUNCTION  parallel_smarts_match 
            (p_candidate in orchem_smarts_search.smarts_candidate_type)
   RETURN    result_tab
   PIPELINED
   PARALLEL_ENABLE (PARTITION p_candidate BY ANY );
   

   TYPE smarts_candidate_rec is RECORD
   (
      compound_id         varchar2(80)
     ,triple_bond_count   number(5)
     ,s_count             number(5)
     ,o_count             number(5)
     ,n_count             number(5)
     ,f_count             number(5)
     ,cl_count            number(5)
     ,br_count            number(5)
     ,i_count             number(5)
     ,c_count             number(5)
     ,db_molecule         clob
     ,smarts_query        clob
   );

   TYPE smarts_candidate_type 
   IS REF CURSOR 
   return smarts_candidate_rec;


END;
/
SHOW ERRORS



CREATE OR REPLACE PACKAGE BODY orchem_smarts_search
AS 

  /*___________________________________________________________________________
   Procedure to get a WHERE clause for a particular user query
   ___________________________________________________________________________*/
   FUNCTION getWhereClause (smarts_query clob, debug_yn varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SMARTS_Search.getWhereClause (java.sql.Clob, java.lang.String) return java.lang.String ';

  /*___________________________________________________________________________
   Atom and bond counting to quickly eliminate candidates without match
   ___________________________________________________________________________*/
   PROCEDURE get_atom_and_bond_counts (smarts IN Clob, vReturnArray OUT orchem_number_table) AS
   LANGUAGE JAVA NAME 'uk.ac.ebi.orchem.search.SMARTS_Search.getAtomAndBondCounts(java.sql.Clob, oracle.sql.ARRAY[])';


  /*___________________________________________________________________________
   Procedure to invoke graph ismorphism check between query and candidate
   ___________________________________________________________________________*/
   FUNCTION smartsMatch (
             db_id varchar2 
            ,db_compound clob
            ,smarts_query clob
   )
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SMARTS_Search.smartsMatch(java.lang.String, java.sql.Clob,java.sql.Clob) return java.lang.String ';


  /*___________________________________________________________________________
   
   This is where the parallelism comes in, note the function definition.
   
   This function is called from a query (see next function below) and 
   is executed by Oracle in parallel threads. These threads are autonomous
   and do NOT share context like Java singleton values.

   ___________________________________________________________________________*/
   FUNCTION  parallel_smarts_match 
            (p_candidate in orchem_smarts_search.smarts_candidate_type)
   RETURN    result_tab
   PIPELINED
   PARALLEL_ENABLE (PARTITION p_candidate BY ANY ) 
   IS
      match_YN                    varchar2(1);
      l_candidate                 orchem_smarts_search.smarts_candidate_rec;
      result                      result_rec;
      first_iteration             boolean:=true;

      atomBondCounts              orchem_number_table := orchem_number_table();
      l_query_triple_bond_count   number(5);
      l_query_s_count             number(5);
      l_query_o_count             number(5);
      l_query_n_count             number(5);
      l_query_f_count             number(5);
      l_query_cl_count            number(5);
      l_query_br_count            number(5);
      l_query_i_count             number(5);
      l_query_c_count             number(5);

   BEGIN

      LOOP
         FETCH p_candidate into l_candidate;
         EXIT WHEN p_candidate%NOTFOUND;
         
         if first_iteration then
             first_iteration :=false;
              get_atom_and_bond_counts(l_candidate.smarts_query,atomBondCounts);
              l_query_triple_bond_count   := atomBondCounts(1);
              l_query_s_count             := atomBondCounts(2);
              l_query_o_count             := atomBondCounts(3);
              l_query_n_count             := atomBondCounts(4);
              l_query_f_count             := atomBondCounts(5);
              l_query_cl_count            := atomBondCounts(6);
              l_query_br_count            := atomBondCounts(7);
              l_query_i_count             := atomBondCounts(8);
              l_query_c_count             := atomBondCounts(9);
         end if;
         

         if(  l_query_triple_bond_count > l_candidate.triple_bond_count OR
              l_query_s_count           > l_candidate.s_count           OR
              l_query_o_count           > l_candidate.o_count           OR
              l_query_n_count           > l_candidate.n_count           OR
              l_query_f_count           > l_candidate.f_count           OR
              l_query_cl_count          > l_candidate.cl_count          OR
              l_query_br_count          > l_candidate.br_count          OR
              l_query_i_count           > l_candidate.i_count           OR
              l_query_c_count           > l_candidate.c_count)
         THEN

            match_YN := 'N';

         ELSE
             match_YN := 
             smartsMatch (
                 l_candidate.compound_id
                ,l_candidate.db_molecule
                ,l_candidate.smarts_query
             );
         END IF;

         IF match_YN ='N' THEN
            result.id := null;
            result.molecule := null;
         ELSE 
            result.id := l_candidate.compound_id;
            result.molecule := l_candidate.db_molecule;
         END IF;
         PIPE ROW (result);

      END LOOP;
      RETURN;
   END;

  /*___________________________________________________________________________

    Main function
    ========================================
    TODO comments here
        
   ___________________________________________________________________________*/
   FUNCTION search (smarts_query clob, debug_yn varchar2:='N', 
                    topN integer:=null, force_full_scan varchar2:='Y' )
   RETURN  orchem_compound_list
   PIPELINED
   AS
      TYPE refCurType IS REF CURSOR;
      myRefCur refCurType;
      
      compound_tab_name        orchem_parameters.comp_tab_name%TYPE;
      compound_tab_pk_col      orchem_parameters.comp_tab_pk_col%TYPE;
      compound_tab_molecule_col orchem_parameters.comp_tab_molecule_col%TYPE;
      whereClause              varchar2(20000);
      prefilterQuery           varchar2(30000);
      compound                 result_rec;
      numOfResults             integer:=0; 

   BEGIN

       SELECT comp_tab_name,    comp_tab_pk_col,    comp_tab_molecule_col 
       INTO   compound_tab_name,compound_tab_pk_col,compound_tab_molecule_col
       FROM   orchem_parameters;
       
       whereClause := getWhereClause (smarts_query, debug_yn);       

       prefilterQuery:=       
       ' select /*+ NO_QKN_BUFF */  * '                         || 
       ' from   table '                                         ||
       '        ( orchem_smarts_search.parallel_smarts_match '  ||
       '          ( cursor '                                    ||
       '           ( select /*+ full(s) parallel(s) */  '       ||
       '               s.id '                                   ||
       '             , s.triple_bond_count '                    ||
       '             , s.s_count '                              ||
       '             , s.o_count '                              ||
       '             , s.n_count '                              ||
       '             , s.f_count '                              ||
       '             , s.cl_count '                             ||
       '             , s.br_count '                             ||
       '             , s.i_count  '                             ||
       '             , s.c_count  '                             ||
       '             , m.'||compound_tab_molecule_col            ||
       '             ,'''||smarts_query||''' as smarts '        ||   
       '              from  orchem_fingprint_subsearch s '      ||
       '             ,'||compound_tab_name||' m          '      ||
       '              where s.id = m.'||compound_tab_pk_col||' '||
                     whereClause                                ||
       '           ) '                                          ||
       '          ) '                                           ||
       '         ) t ';

       OPEN myRefCur FOR prefilterQuery;

       << prefilterloop >>
       LOOP
          FETCH myRefCur INTO compound;

          IF (myRefCur%NOTFOUND) THEN
             CLOSE myRefcur;
             EXIT prefilterloop;
          ELSE
             if(compound.id IS NOT NULL)
             then

                pipe row( new orchem_compound(compound.id,compound.molecule,1));
                numOfResults:=numOfResults+1;

                IF (topN is not null AND numOfResults >= topN) THEN
                  CLOSE myRefcur;  
                  EXIT prefilterloop;
                END IF;
             end if;
          END IF;
       END LOOP;

       RETURN;

  EXCEPTION WHEN OTHERS THEN
       IF myRefCur is not null AND myRefCur%ISOPEN THEN
         CLOSE myRefCur;
       END IF;
       RAISE;
   END search;

END orchem_smarts_search;
/   
SHOW ERR


exit 1;
