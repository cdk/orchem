CREATE OR REPLACE PACKAGE orchem_subsearch_par
AS 
   TYPE candidate_type is REF CURSOR return orchem_utils.candidate_rec;

   PROCEDURE show_keys
   ;

   FUNCTION setup (user_query Clob, query_type varchar2)
   RETURN   integer;

   FUNCTION search (query_key integer, topN integer:=null, debug_YN varchar2:='N' )
   RETURN  orchem_compound_list
   PIPELINED;

   FUNCTION  parallel_isomorphism_check ( p_candidate in orchem_subsearch_par.candidate_type)
   RETURN    compound_id_table pipelined
   parallel_enable ( partition p_candidate by Hash ( compound_id) )
   ;

END;
/
SHOW ERRORS





CREATE OR REPLACE PACKAGE BODY orchem_subsearch_par
AS 


   PROCEDURE show_keys 
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.showKeys()';

   PROCEDURE remove_from_map (map_key number)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.removeFromMap(java.lang.Integer)';

   PROCEDURE store_query (query_key number, user_query Clob, query_type varchar2)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.storeUserQueryInDB(java.lang.Integer,java.sql.Clob,java.lang.String)';

   FUNCTION getWhereClause (query_key number, debug_YN VARCHAR2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.getWhereClause (java.lang.Integer, java.lang.String) return java.lang.String ';

   FUNCTION setup (user_query Clob, query_type varchar2)
   RETURN   integer
   IS
      query_key number;
   BEGIN
          IF query_type NOT IN ('SMILES','MOL') THEN
          raise_application_error (-20013,'The provided input type '||query_type||' is not valid.');
       END IF;

       SELECT orchem_sequence_queryKeys.nextval
       INTO   query_key
       FROM   dual;
       
       store_query (query_key, user_query, query_type);
       
       return query_key;
   END;



   FUNCTION is_possible_candidate (query_key number, compoundId varchar2, singleBondCount number, doubleBondCount number
                       , tripleBondCount number, aromaticBondCount number, sCount number, oCount number 
                       , nCount number, fCount number, clCount number, brCount number, iCount number
                       , cCount number, debugYN varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.isPossibleCandidate(java.lang.Integer,java.lang.String,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.String) return java.lang.String ';


   FUNCTION isomorphism (query_key number, compoundId varchar2, atoms clob, bonds clob, debugYN varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.isomorphismCheck(java.lang.Integer,java.lang.String,java.sql.Clob,java.sql.Clob,java.lang.String) return java.lang.String ';

   PROCEDURE check_thread_env (query_key number)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.checkThreadEnvironment(java.lang.Integer)';



   FUNCTION  parallel_isomorphism_check ( p_candidate in orchem_subsearch_par.candidate_type)
   RETURN    compound_id_table 
   pipelined
   parallel_enable ( partition p_candidate by Hash ( compound_id) )
   IS
     retval       varchar2(80);
     l_candidate  orchem_utils.candidate_rec;
   BEGIN
      LOOP
         FETCH p_candidate into l_candidate;
         EXIT WHEN p_candidate%NOTFOUND;

         check_thread_env (l_candidate.query_key);

         IF is_possible_candidate  (
              l_candidate.query_key
             ,l_candidate.compound_id         
             ,l_candidate.single_bond_count   
             ,l_candidate.double_bond_count   
             ,l_candidate.triple_bond_count   
             ,l_candidate.aromatic_bond_count 
             ,l_candidate.s_count             
             ,l_candidate.o_count             
             ,l_candidate.n_count             
             ,l_candidate.f_count             
             ,l_candidate.cl_count            
             ,l_candidate.br_count            
             ,l_candidate.i_count             
             ,l_candidate.c_count             
             ,l_candidate.debug_yn            
             ) = 'Y' 
         THEN
             IF l_candidate.atoms IS NULL THEN
               SELECT atoms, bonds
               INTO   l_candidate.atoms, l_candidate.bonds
               FROM   orchem_big_molecules
               WHERE  id = l_candidate.compound_id;
             END IF;

             retval := 
             isomorphism (
                  l_candidate.query_key
                 ,l_candidate.compound_id         
                 ,l_candidate.atoms               
                 ,l_candidate.bonds               
                 ,l_candidate.debug_yn            
             );
             IF retval IS NOT NULL THEN
               PIPE ROW (retval);
             END IF;
         END IF;
      END LOOP;
      RETURN;
   END;


   FUNCTION search (query_key integer, topN integer:=null, debug_YN varchar2:='N' )
   RETURN  orchem_compound_list
   PIPELINED
   AS
      TYPE refCurType IS REF CURSOR;
      myRefCur refCurType;
      compound_tab_name        orchem_parameters.comp_tab_name%TYPE;
      compound_tab_pk_col      orchem_parameters.comp_tab_pk_col%TYPE;
      compound_tab_molfile_col orchem_parameters.comp_tab_molfile_col%TYPE;
      whereClause              varchar2(20000);
      compound_id              varchar2(80);
      moleculeQuery            varchar2(5000);
      prefilterQuery           varchar2(30000);
      molecule                 clob;
      numOfResults integer:=0; 

   BEGIN

       SELECT comp_tab_name,    comp_tab_pk_col,    comp_tab_molfile_col 
       INTO   compound_tab_name,compound_tab_pk_col,compound_tab_molfile_col
       FROM   orchem_parameters;

       moleculeQuery := ' select '|| compound_tab_molfile_col|| 
                        ' from  ' || compound_tab_name       ||
                        ' where  '|| compound_tab_pk_col     ||'=:var01';
       
       whereClause := getWhereClause (query_key, debug_YN );       


       prefilterQuery:=       
       ' select *                                        ' ||
       ' from   table                                    ' ||
       '        ( orchem_subsearch_par.parallel_isomorphism_check ' ||
       '          ( cursor                               ' ||
       '           ( select /*+ FIRST_ROWS */            ' ||
                       query_key                           ||
       '             , s.id                              ' ||
       '             , s.single_bond_count               ' ||
       '             , s.double_bond_count               ' ||
       '             , s.triple_bond_count               ' ||
       '             , s.aromatic_bond_count             ' ||
       '             , s.s_count                         ' ||
       '             , s.o_count                         ' ||
       '             , s.n_count                         ' ||
       '             , s.f_count                         ' ||
       '             , s.cl_count                        ' ||
       '             , s.br_count                        ' ||
       '             , s.i_count                         ' ||
       '             , s.c_count                         ' ||
       '             , s.atoms                           ' ||
       '             , s.bonds                           ' ||
       '             ,'''||debug_YN||''''                  ||  
       '              from  orchem_fingprint_subsearch s ' ||
       '              where 1=1                          ' ||
                     whereClause                           ||
       '           )                                     ' ||
       '          )                                      ' ||
       '         )                                       ';

       OPEN myRefCur FOR prefilterQuery;

       << prefilterloop >>
       LOOP
          FETCH myRefCur INTO compound_id;

          IF (myRefCur%NOTFOUND) THEN
             CLOSE myRefcur;
             EXIT prefilterloop;
          ELSE

             if(compound_id IS NOT NULL)
             then
                execute immediate moleculeQuery into molecule using compound_id;
                pipe row( ORCHEM_COMPOUND (compound_id,  molecule, 1 ) );
                numOfResults:=numOfResults+1;

                IF (topN is not null AND numOfResults >= topN) THEN
                  CLOSE myRefcur;  
                  EXIT prefilterloop;
                END IF;
             else 
                null;
             end if;

          END IF;
       END LOOP;
       remove_from_map (query_key);    
       RETURN;

     EXCEPTION WHEN OTHERS THEN
     IF myRefCur is not null AND myRefCur%ISOPEN THEN
        CLOSE myRefCur;
     END IF;
     remove_from_map (query_key);    
     RAISE;
   END;


END;
/   
SHOW ERR

exit
