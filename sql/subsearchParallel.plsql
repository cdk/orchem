/*
________________________________________________________________________________

Substructure searching, parallelized

The parallelized version of substructure searching requires an extra 
preparation step, as opposed to the non parallelized version.
But depending on your database server (CPUs, cores) the parallelized version will
perform faster especially for difficult substructure queries.

SQL*Plus example how-to invoke this:
  var key number;
  exec :key :=orchem_subsearch_par.setup('O=S=O','SMILES');
  select * from table (orchem_subsearch_par.search(:key,100,'Y') )

author: markr@ebi.ac.uk, 2009

Note: to enable the effect of PIPEd functions, 
- in sql*plus: set arraysize=1
- in jdbc    : conn.setDefaultRowPrefetch(1);

________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_subsearch_par
AS 
   TYPE candidate_type 
   IS REF CURSOR 
   return orchem_utils.candidate_rec;

   FUNCTION setup (user_query Clob, query_type varchar2)
   RETURN   integer;

   FUNCTION search (query_key integer, 
                    topN integer:=null, 
                    force_full_scan varchar2:='Y',
                    strict_stereo_yn VARCHAR2:='N'
                   ) 
   RETURN  orchem_compound_list
   PIPELINED;

   FUNCTION  parallel_isomorphism_check ( p_candidate in orchem_subsearch_par.candidate_type)
   RETURN    compound_id_table 
   pipelined
   parallel_enable ( partition p_candidate by any ) -- by Hash ( compound_id) )
   ;
END;
/
SHOW ERRORS



CREATE OR REPLACE PACKAGE BODY orchem_subsearch_par
AS 


  /*___________________________________________________________________________
   Small procedure to remove query from map after substructure search
   ___________________________________________________________________________*/
   PROCEDURE remove_query_from_map (map_key number)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.removeFromMap(java.lang.Integer)';


  /*___________________________________________________________________________
   Procedure to get a WHERE clause for a particular user query
   ___________________________________________________________________________*/
   FUNCTION getWhereClause (query_key number, query_idx number)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.getWhereClause (java.lang.Integer, java.lang.Integer) return java.lang.String ';


  /*____________________________________________________________________________
   Persists the user query in the database so that it can be retrieved by 
   parallel function threads.
   ___________________________________________________________________________*/
   PROCEDURE store_query_in_db (query_key number, user_query Clob, query_type varchar2)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.storeUserQueryInDB(java.lang.Integer,java.sql.Clob,java.lang.String)';


  /*___________________________________________________________________________
   Prepares the parallel search by sticking the user's query details in the
   database. This is necessary because the parallel threads later on run in 
   their own context (connection/JVM) so nothing in memory can be shared 
   amongst the threads.
   Perhaps there is a more elegant way of doing this, but I haven't figured one
   out yet.
   ___________________________________________________________________________*/
   FUNCTION setup (user_query Clob, query_type varchar2)
   RETURN   integer
   IS
      query_key number;
   BEGIN
       -- Detect if user query type is valid
       IF query_type NOT IN ('SMILES','MOL') THEN
          raise_application_error (-20013,'The provided input type '||query_type||' is not valid.');
       END IF;

       SELECT orchem_sequence_queryKeys.nextval
       INTO   query_key
       FROM   dual;
       
       store_query_in_db (query_key, user_query, query_type);
       return query_key;
   END;

  /*___________________________________________________________________________
   Procedure to check if the environment (context) of a thread has been set up
   already and if not, does set it up
   ___________________________________________________________________________*/
   
   FUNCTION setup_environment (query_key number)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.setUpEnvironment(java.lang.Integer) return java.lang.integer';


  /*___________________________________________________________________________
   Procedure to get get a quick verdict if a candidate compound can be a 
   a superstructure of the user's query. 
   ___________________________________________________________________________*/
   FUNCTION  is_possible_candidate (query_key number, query_idx number, compoundId varchar2
           , tripleBondCount number, sCount number, oCount number 
           , nCount number, fCount number, clCount number, brCount number, iCount number
           , cCount number, debugYN varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.isPossibleCandidate(java.lang.Integer,java.lang.Integer,java.lang.String,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.String) return java.lang.String ';


  /*___________________________________________________________________________
   Procedure to invoke graph ismorphism check between query and candidate
   ___________________________________________________________________________*/
   FUNCTION isomorphism (query_key number, query_idx number, compoundId varchar2, atoms clob, bonds clob, debugYN varchar2, strict_stereo_yn varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.isomorphismCheck(java.lang.Integer,java.lang.Integer,java.lang.String,java.sql.Clob,java.sql.Clob,java.lang.String, java.lang.String) return java.lang.String ';


  /*___________________________________________________________________________
   
   This is where the parallelism comes in, note the function definition.
   
   This function is called from a query (see next function below) and 
   is executed by Oracle in parallel threads. These threads are autonomous
   and do NOT share context like Java singleton values.
   That is why the query was first stored in the database:  each thread will 
   first set up its environment by rerieving the user's query and stashing it 
   in a Map of its own.
   After that it is business as usual, similar to the non parallelized version.
   
   --
   
   An interesting article on parallel functions can be found at Amis.
   http://technology.amis.nl/blog/296/parallelizing-table-functions-instead-of-paralysing
   ___________________________________________________________________________*/

   FUNCTION  parallel_isomorphism_check 
            (p_candidate in orchem_subsearch_par.candidate_type)
   RETURN    compound_id_table 
   PIPELINED
   PARALLEL_ENABLE (PARTITION p_candidate BY ANY ) 
   IS
      retval                varchar2(80):=NULL;
      l_candidate           orchem_utils.candidate_rec;
      parallel_query_hickup EXCEPTION;
      PRAGMA EXCEPTION_INIT(parallel_query_hickup, -12801);
      checked               boolean :=false;
      numOfQueries          integer:=0;

   BEGIN
      LOOP
         FETCH p_candidate into l_candidate;
         EXIT WHEN p_candidate%NOTFOUND;

        -- Extra step:
         -- make sure we have the user's query, initially retrieve it once.
         while not checked loop
             begin
                numOfQueries := setup_environment (l_candidate.query_key);
                checked :=true;
             exception
             when others then -- parallel_query_hickup  then 
                NULL; -- to do log somewhere
             end;
         end loop;

         -- Business as usual:
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
             ,'N'            
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
                 ,l_candidate.query_idx
                 ,l_candidate.compound_id         
                 ,l_candidate.atoms               
                 ,l_candidate.bonds               
                 ,'N'            
                 ,l_candidate.strict_stereo_yn
             );
             PIPE ROW (retval);

             --DEBUG stuff
             --PIPE ROW (userenv('sessionid');
               
         END IF;
      END LOOP;
      RETURN;
   END;



  /*___________________________________________________________________________

        Main function (refer to numbers in code)
        ========================================
        PREPARE:
   (1)  Read user's environment from ORCHEM_PARAMETERS
   (2)  Have a WHERE clause generated to prefilter possible candidates
   (3)  Set up the prefilter query (dynamic SQL)
        This query invokes the parallel isomorphism function above.
   (4)  Set up a lookup query to select details of superstructure compounds

        LOOP:
   (5)  Start looping the prefilter query, finding possible candidates
   (6)  Any compound id returned by the parallel function is a hit and
        can be added to the result list
   (7)  Pull out the hit compound's details (query from (4)) and PIPE out
   (8)  If topN was set, and number of results==topN, exit wounds
   
   ___________________________________________________________________________*/
   FUNCTION search (query_key integer, 
                    topN integer:=null, 
                    force_full_scan varchar2:='Y',
                    strict_stereo_yn VARCHAR2:='N'
                   ) 
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
      numOfResults             integer:=0; 
      full_hint                varchar2(10):='';
      numOfQueries             integer:=0;

      TYPE returned_id_type IS TABLE OF VARCHAR2(1) INDEX BY VARCHAR2(30);
      returned_ids             returned_id_type;
      returned_id              varchar2(30);
      id_already_returned      boolean;

   BEGIN

       --(1)
       SELECT comp_tab_name,    comp_tab_pk_col,    comp_tab_molfile_col 
       INTO   compound_tab_name,compound_tab_pk_col,compound_tab_molfile_col
       FROM   orchem_parameters;

       numOfQueries := setup_environment (query_key);

       << queryLoop >>
       FOR qIdx in 0..(numOfQueries-1) LOOP
           
           --(2)  
           whereClause := getWhereClause (query_key, qIdx );       
    
           --(3)
           /* Note the two hints below. The FULL hint is to force a full table
              scan, otherwise optimizer MAY bick a btree index. But if a btree
              is chosen, there will be NO parallelism (you then revert back to
              the non parallel version essentially). */
           
           if force_full_scan='Y' then
             full_hint:='full(s)';
           end if;
    
           prefilterQuery:=       
           ' select /*+ NO_QKN_BUFF */  *                    ' ||
           ' from   table                                    ' ||
           '        ( orchem_subsearch_par.parallel_isomorphism_check ' ||
           '          ( cursor                               ' ||
           '           ( select /*+ '||full_hint||' parallel(s) */  ' ||  
                           query_key                           ||
           '             ,'||qIdx                              ||
           '             , s.id                              ' ||
           '             , s.triple_bond_count               ' ||
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
           '             ,''N'' '                              ||  
           '             ,'''||strict_stereo_yn||''''          ||  
           '              from  orchem_fingprint_subsearch s ' ||
           '              where 1=1                          ' ||
                         whereClause                           ||
           '           )                                     ' ||
           '          )                                      ' ||
           '         ) t                                      ';
    
    
          -- DEBUG query back
          -- pipe row(ORCHEM_COMPOUND (0,  prefilterQuery, 1 ));
          -- return;
          
           --(4)
           moleculeQuery := ' select '|| compound_tab_molfile_col|| 
                            ' from  ' || compound_tab_name       ||
                            ' where  '|| compound_tab_pk_col     ||'=:var01';
    
           OPEN myRefCur FOR prefilterQuery;
    
           --(5)
           << prefilterloop >>
           LOOP
              FETCH myRefCur INTO compound_id;
    
              IF (myRefCur%NOTFOUND) THEN
                 CLOSE myRefcur;
                 EXIT prefilterloop;
              ELSE
                 --(6)
                 
                 if(compound_id IS NOT NULL)
                 then
                    --was ID already returned? could be for RGroup multiple query scenario
                    id_already_returned:=true;
                    BEGIN
                       returned_id := returned_ids(compound_id);
                    EXCEPTION 
                        WHEN NO_DATA_FOUND THEN
                         id_already_returned:=false;
                    END;       
                    IF NOT id_already_returned THEN

                        --(7)
                        execute immediate moleculeQuery into molecule using compound_id;
                        pipe row( ORCHEM_COMPOUND (compound_id,  molecule, 1 ) );
                        numOfResults:=numOfResults+1;
                        returned_ids(compound_id):=null; -- null entry, hash key is what matters
                        --(8)
                        IF (topN is not null AND numOfResults >= topN) THEN
                          CLOSE myRefcur;  
                          EXIT prefilterloop;
                        END IF;
                    END IF;
                 else 
                    --pipe row( ORCHEM_COMPOUND (null,null,0 ) );
                    null;
                 end if;
    
              END IF;
           END LOOP;
  
          IF (topN is not null AND numOfResults >= topN) THEN
            exit queryLoop;          
          END IF;

       END LOOP;
       remove_query_from_map (query_key);    
       RETURN;


   EXCEPTION WHEN OTHERS THEN
      IF myRefCur is not null AND myRefCur%ISOPEN THEN
         CLOSE myRefCur;
      END IF;
      remove_query_from_map (query_key);    
      RAISE;
   END;
END;
/   
SHOW ERR


exit 1;
