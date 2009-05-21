 /*
________________________________________________________________________________

Similarity searching
author: markr@ebi.ac.uk, 2009

________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_simsearch
AS 
   FUNCTION search ( user_query clob, query_type varchar2, 
                     cutoff float, topn NUMBER:=NULL, debug_YN VARCHAR2:='N')
   RETURN orchem_COMPOUND_LIST;
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
      user_query CLOB            -- user's query, a Smiles or Mol string
     ,query_type VARCHAR2        -- values can be: "SMILES", "MOL"
     ,cutoff     FLOAT           -- minimum similarity breakout (float 0..1)

     ,topn       NUMBER          -- max number of results breakout, optional
     ,debug_YN   VARCHAR2        -- switch on/off debug statements, optional
   )
   RETURN orchem_COMPOUND_LIST
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SimilaritySearch.search (java.sql.Clob, java.lang.String, java.lang.Float, java.lang.Integer, java.lang.String) return oracle.sql.ARRAY';



 
 /*___________________________________________________________________________
   Hack to allow default values on Java stored procedures
   ___________________________________________________________________________*/
   FUNCTION search (
      user_query CLOB            
     ,query_type VARCHAR2        
     ,cutoff     FLOAT           
     ,topn       NUMBER   := NULL -- default value       
     ,debug_YN   VARCHAR2 := 'N'  -- default value       
   )
   RETURN orchem_COMPOUND_LIST
   IS
   BEGIN 
       RETURN javaSearch(user_query, query_type, cutoff, topn, debug_YN); 
   END;
   

END orchem_simsearch;        
/
SHOW ERRORS



 
