CREATE OR REPLACE PACKAGE orchem_simsearch
AS 
   FUNCTION search (user_query clob, query_type varchar2, cutoff float, topn NUMBER, debug_YN VARCHAR2)
   RETURN orchem_COMPOUND_LIST;
END;
/
SHOW ERRORS



CREATE OR REPLACE PACKAGE BODY orchem_simsearch
AS 
   /*    */
   FUNCTION search (user_query clob, query_type varchar2, cutoff float, topn NUMBER, debug_YN VARCHAR2)
   RETURN orchem_COMPOUND_LIST
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SimilaritySearch.search (java.sql.Clob, java.lang.String, java.lang.Float, java.lang.Integer, java.lang.String) return oracle.sql.ARRAY';
END;        
/
SHOW ERRORS

exit;
