/*
________________________________________________________________________________

Substructure searching, non parallelized style
copyright: Mark Rijnbeek, markr@ebi.ac.uk 2009


Note: to enable the effect of PIPEd functions, 
- in sql*plus: set arraysize=1
- in jdbc    : conn.setDefaultRowPrefetch(1);
________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_subsearch
AS 
   l_candidate orchem_utils.candidate_rec;

   FUNCTION search (userQuery Clob
                  , input_type varchar2
                  , topN integer:=null
                  , debug_YN varchar2:='N'
                  , return_ids_only_YN VARCHAR2:='N'
                  , strict_stereo_yn VARCHAR2:='N') 
   RETURN   orchem_compound_list
   PIPELINED
   ;

   FUNCTION searchLimitedSet (userQuery Clob, input_type varchar2
                  , id_list compound_id_table
                  , topN integer:=null, debug_YN varchar2:='N'
                  , return_ids_only_YN VARCHAR2:='N'
                  , strict_stereo_yn VARCHAR2:='N')
   RETURN   orchem_compound_list
   PIPELINED
   ;
END;
/
SHOW ERRORS




CREATE OR REPLACE PACKAGE BODY orchem_subsearch
AS 

 /*___________________________________________________________________________
   Puts user query (atom container) in a map, where it can be looked up by the
   isomorphism method when needed.
   ___________________________________________________________________________*/
   FUNCTIOn stash_queries_in_map (query_key number, userQuery Clob, input_type varchar2, debugYN varchar2)
   RETURN number
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.stashQueriesInMap(java.lang.Integer,java.sql.Clob,java.lang.String,java.lang.String) return java.lang.Integer';


  /*___________________________________________________________________________
   Small procedure to remove query from map after substructure search
   ___________________________________________________________________________*/
   PROCEDURE remove_query_from_map (query_key number)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.removeFromMap(java.lang.Integer)';


  /*___________________________________________________________________________
   Procedure to get a WHERE clause for a particular user query
   ___________________________________________________________________________*/
   FUNCTION getWhereClause (query_key number, query_idx number, debug_YN VARCHAR2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.getWhereClause (java.lang.Integer, java.lang.Integer, java.lang.String) return java.lang.String ';


  /*___________________________________________________________________________
   Procedure to get get a quick verdict if a candidate compound can be a 
   a superstructure of the user's query. 
   ___________________________________________________________________________*/
   FUNCTION is_possible_candidate (query_key number, query_idx number, compoundId varchar2
                       , tripleBondCount number, sCount number, oCount number 
                       , nCount number, fCount number, clCount number, brCount number, iCount number
                       , cCount number, debugYN varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.isPossibleCandidate(java.lang.Integer,java.lang.Integer,java.lang.String,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.String) return java.lang.String ';


  /*___________________________________________________________________________
   Procedure to invoke graph ismorphism check between query and candidate
   ___________________________________________________________________________*/
   FUNCTION isomorphism 
   (query_key number, query_idx number, compoundId varchar2, atoms clob, bonds clob, debugYN varchar2, strict_stereo_yn varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.isomorphismCheck(java.lang.Integer,java.lang.Integer,java.lang.String,java.sql.Clob,java.sql.Clob,java.lang.String,java.lang.String) return java.lang.String ';


   PROCEDURE debug(msg varchar2) 
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.debug(java.lang.String)' ;


  /*___________________________________________________________________________

        Main function (refer to numbers in code)
        ========================================
        PREPARE:
   (1)  Detect if user query type is valid
   (2)  Read user's environment from ORCHEM_PARAMETERS
   (3)  Pull a (cycled) sequence integer value to get a key to store user's 
        query structure in a map
   (4)  Stash the user query structure in the map
   (5)  Have a WHERE clause generated to prefilter possible candidates
   (6)  Set up the prefilter query (dynamic SQL)
   (7)  Set up a lookup query to select details of superstructure compounds

        LOOP:
   (8)  Start looping the prefilter query, finding candidates
   (9)  Sidestep: for particularly large candidates, find some details in a 
                  special CLOB table
   (10) Test if the candidate can be ruled out quickly based on atom and bond
        counts
   (11) If possible candidate still, then run the VF2 isomorphism check between
        the candidate and the user's query structure 
   (12) If user's query structure is indeed valid substructure of candidate,
        then pull out the candidate's details (query from (7)) and PIPE out
   (13) If topN was set, and number of results==topN, exit wounds
   
   ___________________________________________________________________________*/
   FUNCTION search (userQuery Clob, input_type varchar2, topN integer :=null , 
                    debug_YN varchar2 := 'N',return_ids_only_YN VARCHAR2:='N',
                    strict_stereo_yn VARCHAR2:='N')
   RETURN  orchem_compound_list
   PIPELINED
   AS
      TYPE refCurType IS REF CURSOR;
      myRefCur refCurType;

      TYPE returned_id_type IS TABLE OF VARCHAR2(1) INDEX BY VARCHAR2(30);
      returned_ids returned_id_type;
      returned_id varchar2(30);
      
      compound_tab_name        orchem_parameters.comp_tab_name%TYPE;
      compound_tab_pk_col      orchem_parameters.comp_tab_pk_col%TYPE;
      compound_tab_molfile_col orchem_parameters.comp_tab_molfile_col%TYPE;
      whereClause              varchar2(20000);
      query_key                number;
      moleculeQuery            varchar2(5000);
      prefilterQuery           varchar2(30000);
      molecule                 clob;
      numOfResults             integer:=0; 
      countCompoundsLooped     integer:=0;
      compound_id              VARCHAR2(255);
      numOfQueries             integer:=0;
      id_already_returned      boolean;
      
   BEGIN
       --(1)
       IF input_type NOT IN ('SMILES','MOL') THEN
          raise_application_error (-20013,'The provided input type '||input_type||' is not valid.');
       END IF;

       --(2)
       SELECT comp_tab_name,    comp_tab_pk_col,    comp_tab_molfile_col 
       INTO   compound_tab_name,compound_tab_pk_col,compound_tab_molfile_col
       FROM   orchem_parameters;

       --(3)       
       SELECT orchem_sequence_querykeys.nextval
       INTO   query_key
       FROM   dual;
       
       --(4)
       numOfQueries := stash_queries_in_map (query_key, userQuery, input_type, debug_YN);

       << queryLoop >>
       FOR qIdx in 0..(numOfQueries-1) LOOP

           --Abuse pipe for debug:
           --pipe row( ORCHEM_COMPOUND ('loop is'||qIdx,  null, 1 ) );  
            
           --(5)
           whereClause := getWhereClause (query_key, qIdx, debug_YN );
    
           --(6)
           prefilterQuery:=       
           ' select /*+ FIRST_ROWS */            ' ||
               query_key                           ||
           ' ,'||qIdx                              ||
           ' , s.id                              ' ||
           ' , s.triple_bond_count               ' ||
           ' , s.s_count                         ' ||
           ' , s.o_count                         ' ||
           ' , s.n_count                         ' ||
           ' , s.f_count                         ' ||
           ' , s.cl_count                        ' ||
           ' , s.br_count                        ' ||
           ' , s.i_count                         ' ||
           ' , s.c_count                         ' ||
           ' , s.atoms                           ' ||
           ' , s.bonds                           ' ||
           ' ,'''||debug_YN||''''                  ||  
           ' ,'''||strict_stereo_yn||''''          ||  
           '  from  orchem_fingprint_subsearch s ' ||
           '  where 1=1                          ' ||
              whereClause                         ;
            
           --(7)
           moleculeQuery := ' select '|| compound_tab_molfile_col|| 
                            ' from  ' || compound_tab_name       ||
                            ' where  '|| compound_tab_pk_col     ||'=:var01';
    
           OPEN myRefCur FOR prefilterQuery;
    
           --(8)
           << prefilterloop >>
           LOOP
              FETCH myRefCur INTO l_candidate;
    
              IF (myRefCur%NOTFOUND) THEN
                 CLOSE myRefcur;
                 EXIT prefilterloop;
              ELSE
                 countCompoundsLooped:=countCompoundsLooped+1;
                --(9)
                 IF l_candidate.atoms IS NULL THEN
                   SELECT atoms, bonds
                   INTO   l_candidate.atoms, l_candidate.bonds
                   FROM   orchem_big_molecules
                   WHERE  id = l_candidate.compound_id;
                 END IF;
    
                --(10)
                 IF is_possible_candidate  (
                      l_candidate.query_key          
                     ,l_candidate.query_idx
                     ,l_candidate.compound_id         
                     ,l_candidate.triple_bond_count   
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
                    --(11)
                    compound_id :=isomorphism (
                      l_candidate.query_key          
                     ,l_candidate.query_idx
                     ,l_candidate.compound_id         
                     ,l_candidate.atoms               
                     ,l_candidate.bonds               
                     ,l_candidate.debug_yn            
                     ,l_candidate.strict_stereo_yn
                    ); 
    
                    IF compound_id IS NOT NULL
                    THEN
                       --(12)
                        BEGIN
                            /*
                              TODO : remove DEBUG. Make this id_already_returned
                              only there for RG files (new java boolean function).
                              Reproduce this solution to limited set and the parallel query
                            */
                        
                            id_already_returned:=true;
                            --was ID already returned? could be for RGroup multiple query scenario
                            BEGIN
                               returned_id := returned_ids(compound_id);
                            EXCEPTION 
                                WHEN NO_DATA_FOUND THEN
                                 id_already_returned:=false;
                            END;       

                            if not id_already_returned then
                                if return_ids_only_YN='N' then
                                       execute immediate moleculeQuery into molecule using l_candidate.compound_id;    
                                       pipe row( ORCHEM_COMPOUND (l_candidate.compound_id,  molecule, 1 ) );  
                                else 
                                    pipe row( ORCHEM_COMPOUND (l_candidate.compound_id,  null, 1 ) );  
                                end if;
                                returned_ids(compound_id):=null; -- null entry, hash key is what matters
                                numOfResults:=numOfResults+1;
                            end if;

                        EXCEPTION
                            WHEN NO_DATA_FOUND THEN
                                 dbms_output.put_line ('>> Warning: compound could not be found '||l_candidate.compound_id);
                            WHEN TOO_MANY_ROWS THEN
                               raise_application_error (-20001, 'Query in compound table/view with id '||l_candidate.compound_id||' gave more than one row. Aborting!');
                        END;
                       --(13)
                        IF (topN is not null AND numOfResults >= topN) THEN
                          CLOSE myRefcur;  
                          EXIT prefilterloop;
                        END IF;
                    END IF;
                 END IF;
              END IF;
           END LOOP;

           IF (topN is not null AND numOfResults >= topN) THEN
                exit queryLoop;
           END IF;
       END LOOP;

       remove_query_from_map (query_key);    
       debug ('Amount of compounds inspected = '||countCompoundsLooped);
       RETURN;

     EXCEPTION WHEN OTHERS THEN
         IF myRefCur is not null AND myRefCur%ISOPEN THEN
            CLOSE myRefCur;
         END IF;
         remove_query_from_map (query_key);    
         dbms_output.put_line ('>>>>ERROR:'||chr(10)||'>>>>Search could not complete: '||chr(10)||'>>>>'||sqlerrm);
         RAISE; -- raise where ? doesn't really work with pipelined :(
         
   END;


  /*___________________________________________________________________________

    A variant on the normal search function, documented above.
    This variant was put on for ChEbi as they wanted the substructure search
    to work on a give set of IDs only. This makes it possible for ChEbi to
    do a combined search (say on compound name AND substructure), with the
    name search being done first, resulting in a limited set of candidates,
    for which Orchem is then invoked.

   ___________________________________________________________________________*/
   FUNCTION searchLimitedSet (userQuery Clob, input_type varchar2
                  , id_list compound_id_table
                  , topN integer:=null, debug_YN varchar2:='N'
                  , return_ids_only_YN VARCHAR2:='N'
                  , strict_stereo_yn VARCHAR2:='N')

   RETURN   orchem_compound_list
   PIPELINED
   AS
      TYPE refCurType IS REF CURSOR;
      myRefCur refCurType;

      compound_tab_name        orchem_parameters.comp_tab_name%TYPE;
      compound_tab_pk_col      orchem_parameters.comp_tab_pk_col%TYPE;
      compound_tab_molfile_col orchem_parameters.comp_tab_molfile_col%TYPE;
      whereClause              varchar2(20000);
      query_key                number;
      moleculeQuery            varchar2(5000);
      prefilterQuery           varchar2(30000);
      molecule                 clob;
      numOfResults             integer:=0;
      countCompoundsLooped     integer:=0;
      numOfQueries             integer:=0;

   BEGIN
       IF input_type NOT IN ('SMILES','MOL') THEN
          raise_application_error (-20013,'The provided input type '||input_type||' is not valid.');
       END IF;

       SELECT comp_tab_name,    comp_tab_pk_col,    comp_tab_molfile_col
       INTO   compound_tab_name,compound_tab_pk_col,compound_tab_molfile_col
       FROM   orchem_parameters;

       SELECT orchem_sequence_querykeys.nextval
       INTO   query_key
       FROM   dual;

       numOfQueries := stash_queries_in_map (query_key, userQuery, input_type, debug_YN);

       << queryLoop >>
       FOR qIdx in 0..(numOfQueries-1) LOOP 
           --(5)
           whereClause := getWhereClause (query_key, qIdx, debug_YN );
    
           prefilterQuery:=
           ' select /*+ INDEX (s pk_orchem_subsrch) */ ' ||
               query_key                           ||
           ' ,'||qIdx                              ||
           ' , s.id                              ' ||
           ' , s.triple_bond_count               ' ||
           ' , s.s_count                         ' ||
           ' , s.o_count                         ' ||
           ' , s.n_count                         ' ||
           ' , s.f_count                         ' ||
           ' , s.cl_count                        ' ||
           ' , s.br_count                        ' ||
           ' , s.i_count                         ' ||
           ' , s.c_count                         ' ||
           ' , s.atoms                           ' ||
           ' , s.bonds                           ' ||
           ' ,'''||debug_YN||''''                  ||
           ' ,'''||strict_stereo_yn||''''          ||  
           '  from  orchem_fingprint_subsearch s ' ||
           '  where id=:1                        ' || -- !!!!
              whereClause                         ;
    
           moleculeQuery := ' select '|| compound_tab_molfile_col||
                            ' from  ' || compound_tab_name       ||
                            ' where  '|| compound_tab_pk_col     ||'=:var01';
    
           << id_loop >>
           FOR x in 1.. id_list.last LOOP
    
              OPEN myRefCur FOR prefilterQuery USING id_list(x);
              FETCH myRefCur INTO l_candidate;
    
              IF (myRefCur%FOUND) THEN
                 countCompoundsLooped:=countCompoundsLooped+1;
                 IF l_candidate.atoms IS NULL THEN
                   SELECT atoms, bonds
                   INTO   l_candidate.atoms, l_candidate.bonds
                   FROM   orchem_big_molecules
                   WHERE  id = l_candidate.compound_id;
                 END IF;
    
                 IF is_possible_candidate  (
                      l_candidate.query_key
                     ,l_candidate.query_idx
                     ,l_candidate.compound_id
                     ,l_candidate.triple_bond_count
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
                    IF (isomorphism (
                          l_candidate.query_key
                         ,l_candidate.query_idx
                         ,l_candidate.compound_id
                         ,l_candidate.atoms
                         ,l_candidate.bonds
                         ,l_candidate.debug_yn
                         ,l_candidate.strict_stereo_yn
                     )) IS NOT NULL
                    THEN
                        BEGIN
                        if return_ids_only_YN='N' then
                            BEGIN
                               execute immediate moleculeQuery into molecule using l_candidate.compound_id;
                               pipe row( ORCHEM_COMPOUND (l_candidate.compound_id,  molecule, 1 ) );
                            EXCEPTION
                            WHEN NO_DATA_FOUND THEN
                                 dbms_output.put_line ('>> Warning: compound could not be found '||l_candidate.compound_id);
                            END;
                        else
                            pipe row( ORCHEM_COMPOUND (l_candidate.compound_id,  null, 1 ) );
                        end if;
                        
    
                        numOfResults:=numOfResults+1;
                        EXCEPTION
                        WHEN TOO_MANY_ROWS THEN
                           raise_application_error (-20001, 'Query in compound table/view with id '||l_candidate.compound_id||' gave more than one row. Aborting!');
                        END;
                        IF (topN is not null AND numOfResults >= topN) THEN
                          CLOSE myRefcur;
                          EXIT id_loop;
                        END IF;
                    END IF;
                 END IF;
              END IF;
              CLOSE myRefcur;
           END LOOP;

           IF (topN is not null AND numOfResults >= topN) THEN
                exit queryLoop;
           END IF;
       END LOOP;

       remove_query_from_map (query_key);
       debug ('Amount of compounds inspected = '||countCompoundsLooped);
       RETURN;

     EXCEPTION WHEN OTHERS THEN
     IF myRefCur is not null AND myRefCur%ISOPEN THEN
        CLOSE myRefCur;
     END IF;
     remove_query_from_map (query_key);
     dbms_output.put_line ('>>>>ERROR:'||chr(10)||'>>>>Search could not complete: '||chr(10)||'>>>>'||sqlerrm);
     RAISE;
   END;



   --PROCEDURE show_keys 
   --IS LANGUAGE JAVA NAME 
   --'uk.ac.ebi.orchem.search.SubstructureSearch.showKeys()';

END;
/   
SHOW ERR


exit 1;
