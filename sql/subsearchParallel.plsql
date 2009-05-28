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

   FUNCTION search (query_key integer, topN integer:=null, debug_YN varchar2:='N' )
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
   FUNCTION getWhereClause (query_key number, debug_YN VARCHAR2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.getWhereClause (java.lang.Integer, java.lang.String) return java.lang.String ';


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
   PROCEDURE check_thread_env (query_key number)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.checkThreadEnvironment(java.lang.Integer)';


  /*___________________________________________________________________________
   Procedure to get get a quick verdict if a candidate compound can be a 
   a superstructure of the user's query. 
   ___________________________________________________________________________*/
   FUNCTION is_possible_candidate (query_key number, compoundId varchar2, singleBondCount number, doubleBondCount number
                       , tripleBondCount number, aromaticBondCount number, sCount number, oCount number 
                       , nCount number, fCount number, clCount number, brCount number, iCount number
                       , cCount number, debugYN varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.isPossibleCandidate(java.lang.Integer,java.lang.String,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.String) return java.lang.String ';


  /*___________________________________________________________________________
   Procedure to invoke graph ismorphism check between query and candidate
   ___________________________________________________________________________*/
   FUNCTION isomorphism (query_key number, compoundId varchar2, atoms clob, bonds clob, debugYN varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearchParallel.isomorphismCheck(java.lang.Integer,java.lang.String,java.sql.Clob,java.sql.Clob,java.lang.String) return java.lang.String ';




  /*___________________________________________________________________________
   
   This is where the parallelism comes in, note the function definition.
   
   This function is called from a query (see next function below) and 
   is executed by Oracle in parallel threads. These threads are autonomous
   and do NOT share package context or Java singleton values.
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
   PARALLEL_ENABLE (PARTITION p_candidate BY ANY ) -- HASH ( compound_id) )
   IS
      retval       varchar2(80):=NULL;
      l_candidate  orchem_utils.candidate_rec;
   BEGIN
      LOOP
         FETCH p_candidate into l_candidate;
         EXIT WHEN p_candidate%NOTFOUND;

         -- Extra step:
         -- make sure we have the user's query, initially retrieve it once.
         check_thread_env (l_candidate.query_key);

         -- Business as usual:
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
             --IF retval IS NOT NULL THEN
               PIPE ROW (retval);
             --END IF;
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

       --(1)
       SELECT comp_tab_name,    comp_tab_pk_col,    comp_tab_molfile_col 
       INTO   compound_tab_name,compound_tab_pk_col,compound_tab_molfile_col
       FROM   orchem_parameters;
       
       --(2)  
       whereClause := getWhereClause (query_key, debug_YN );       

       --(3)
       prefilterQuery:=       
       ' select  /*+ NO_QKN_BUFF */ *                    ' ||
       ' from   table                                    ' ||
       '        ( orchem_subsearch_par.parallel_isomorphism_check ' ||
       '          ( cursor                               ' ||
       '           ( select /*+ parallel(s) */           ' ||
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
       '              where 1=1                         ' ||
                     whereClause                           ||
       '           )                                     ' ||
       '          )                                      ' ||
       '         ) t                                      ';

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
                --(7)
                execute immediate moleculeQuery into molecule using compound_id;
                pipe row( ORCHEM_COMPOUND (compound_id,  molecule, 1 ) );
                numOfResults:=numOfResults+1;
                --(8)
                IF (topN is not null AND numOfResults >= topN) THEN
                  CLOSE myRefcur;  
                  EXIT prefilterloop;
                END IF;
             else 
                null;
             end if;

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


exit;--end of script
