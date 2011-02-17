 /*
________________________________________________________________________________

Similarity searching
copyright: Mark Rijnbeek, markr@ebi.ac.uk 2009

________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_simsearch
AS 
   FUNCTION search ( user_query clob
                    ,query_type varchar2
                    ,cutoff float
                    ,topn NUMBER:=NULL
                    ,debug_YN VARCHAR2:='N'
                    ,return_ids_only_YN VARCHAR2:='N'
                    ,extra_where_clause VARCHAR2 := NULL
                   )
   RETURN orchem_COMPOUND_LIST;

   FUNCTION single_compound_similarity ( user_query clob, query_type varchar2, compound_id VARCHAR2)
   RETURN NUMBER;

END;
/
SHOW ERRORS





CREATE OR REPLACE PACKAGE BODY orchem_simsearch
AS 

 /*___________________________________________________________________________
   Java stored procedure for Similarity searching
   ___________________________________________________________________________*/
   FUNCTION javaSearch 
   (
      user_query         CLOB            -- user's query, a Smiles or Mol string
     ,query_type         VARCHAR2        -- values can be: "SMILES", "MOL"
     ,cutoff             FLOAT           -- minimum similarity breakout (float 0..1)

     ,topn               NUMBER          -- max number of results breakout, optional
     ,debug_YN           VARCHAR2        -- switch on/off debug statements, optional
     ,return_ids_only_YN VARCHAR2        -- set to Y if you only want IDs returned, not structures
     ,extra_where_clause VARCHAR2        -- add an extra SQL where clause that is valid for your base table (like ' some_column > 10 ')

   )
   RETURN orchem_COMPOUND_LIST
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SimilaritySearch.search (java.sql.Clob, java.lang.String, java.lang.Float, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String) return oracle.sql.ARRAY';

 
 /*___________________________________________________________________________
   Hack to allow default values on Java stored procedures
   ___________________________________________________________________________*/
   FUNCTION search (
      user_query         CLOB            
     ,query_type         VARCHAR2        
     ,cutoff             FLOAT           
     ,topn               NUMBER   := NULL -- default value       
     ,debug_YN           VARCHAR2 :='N'  -- default value    
     ,return_ids_only_YN VARCHAR2 :='N'
     ,extra_where_clause VARCHAR2 := NULL

   )
   RETURN orchem_COMPOUND_LIST
   IS
   BEGIN 
       RETURN javaSearch(user_query, query_type, cutoff, topn, debug_YN,return_ids_only_YN, extra_where_clause); 
   END;


 /*___________________________________________________________________________
   Java stored procedure for doing similarity between one database compound
   and a user's query compound.
   ___________________________________________________________________________*/

   FUNCTION single_compound_similarity
   (
      user_query         CLOB            -- user's query, a Smiles or Mol string
     ,query_type         VARCHAR2        -- values can be: "SMILES", "MOL"
     ,compound_id        VARCHAR2        -- database compound ID
   )
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SimilaritySearch.singleCompoundSimilarity (java.sql.Clob, java.lang.String, java.lang.String) return float';

END orchem_simsearch;        
/
SHOW ERRORS


exit 1;
