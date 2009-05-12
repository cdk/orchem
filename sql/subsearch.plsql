

CREATE OR REPLACE PACKAGE orchem_subsearch
AS 
   l_candidate orchem_utils.candidate_rec;

   FUNCTION search (userQuery Clob, input_type varchar2, topN integer:=null, debug_YN varchar2:='N' )
   RETURN   orchem_compound_list
   PIPELINED
   ;

   PROCEDURE show_keys
   ;
END;
/
SHOW ERRORS





CREATE OR REPLACE PACKAGE BODY orchem_subsearch
AS 

   PROCEDURE show_keys 
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.showKeys()';

   PROCEDURE remove_from_map (query_key number)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.removeFromMap(java.lang.Integer)';

   PROCEDURE stash_molecule (query_key number, userQuery Clob, input_type varchar2, debugYN varchar2)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.stashMoleculeInMap(java.lang.Integer,java.sql.Clob,java.lang.String,java.lang.String)';

   FUNCTION getWhereClause (userQuery clob, query_type varchar2, debug_YN VARCHAR2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.getWhereClause (java.sql.Clob, java.lang.String, java.lang.String) return java.lang.String ';

    
   FUNCTION is_possible_candidate (query_key number, compoundId varchar2, singleBondCount number, doubleBondCount number
                       , tripleBondCount number, aromaticBondCount number, sCount number, oCount number 
                       , nCount number, fCount number, clCount number, brCount number, iCount number
                       , cCount number, debugYN varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.isPossibleCandidate(java.lang.Integer,java.lang.String,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.Integer,java.lang.String) return java.lang.String ';

   FUNCTION isomorphism (query_key number, compoundId varchar2, atoms clob, bonds clob, debugYN varchar2)
   RETURN VARCHAR2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.isomorphismCheck(java.lang.Integer,java.lang.String,java.sql.Clob,java.sql.Clob,java.lang.String) return java.lang.String ';

   ----------------------

   FUNCTION search (userQuery Clob, input_type varchar2, topN integer :=null , debug_YN varchar2 := 'N')
   RETURN  orchem_compound_list
   PIPELINED
   AS
      TYPE refCurType IS REF CURSOR;
      myRefCur refCurType;

      compound_tab_name        orchem_parameters.comp_tab_name%TYPE;
      compound_tab_pk_col      orchem_parameters.comp_tab_pk_col%TYPE;
      compound_tab_molfile_col orchem_parameters.comp_tab_molfile_col%TYPE;
      whereClause              varchar2(20000);
      query_key               number;
      moleculeQuery            varchar2(5000);
      prefilterQuery           varchar2(30000);
      molecule                 clob;
      numOfResults integer:=0; 

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
       
       moleculeQuery := ' select '|| compound_tab_molfile_col|| 
                        ' from  ' || compound_tab_name       ||
                        ' where  '|| compound_tab_pk_col     ||'=:var01';

       stash_molecule (query_key, userQuery, input_type, debug_YN);
       whereClause := getWhereClause (userQuery, input_type, debug_YN );

       prefilterQuery:=       
       '            select /*+ FIRST_ROWS */             ' ||
                       query_key                          ||
       '             ,'''||input_type||''''                ||  
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
                      whereClause                         ;
        

       OPEN myRefCur FOR prefilterQuery;

       << prefilterloop >>
       LOOP
          FETCH myRefCur INTO l_candidate;

          IF (myRefCur%NOTFOUND) THEN
             CLOSE myRefcur;
             EXIT prefilterloop;
          ELSE

             IF l_candidate.atoms IS NULL THEN
               SELECT atoms, bonds
               INTO   l_candidate.atoms, l_candidate.bonds
               FROM   orchem_big_molecules
               WHERE  id = l_candidate.compound_id;
             END IF;

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
                IF (isomorphism (
                      l_candidate.query_key          
                     ,l_candidate.compound_id         
                     ,l_candidate.atoms               
                     ,l_candidate.bonds               
                     ,l_candidate.debug_yn            
                 )) IS NOT NULL
                THEN
                    execute immediate moleculeQuery into molecule using l_candidate.compound_id;
                    pipe row( ORCHEM_COMPOUND (l_candidate.compound_id,  molecule, 1 ) );
                    numOfResults:=numOfResults+1;
    
                    IF (topN is not null AND numOfResults >= topN) THEN
                      CLOSE myRefcur;  
                      EXIT prefilterloop;
                    END IF;
                END IF;
             END IF;
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
