CREATE OR REPLACE PACKAGE orchem
AS 
   --FUNCTION to_hex( p_dec IN NUMBER ) RETURN VARCHAR2;
   --FUNCTION to_bin( p_dec IN NUMBER ) RETURN VARCHAR2;
   --FUNCTION to_oct( p_dec IN NUMBER ) RETURN VARCHAR2;
   PROCEDURE load_cdk_fingerprints (start_id VARCHAR2, end_id VARCHAR2, serialize_yn VARCHAR2:='N');
   PROCEDURE slice_load (p_start_id integer, p_end_id integer,serialize_yn VARCHAR2:='N');
   FUNCTION  similarity_search_mol (molfile clob, cutoff float, topn NUMBER, debug_YN VARCHAR2) RETURN orchem_compound_list;
   FUNCTION  similarity_search_smiles (smiles varchar2, cutoff float, topn NUMBER, debug_YN VARCHAR2) RETURN orchem_compound_list;
   FUNCTION  substructure_search_mol (molfile clob, topn NUMBER, debug_YN VARCHAR2) RETURN orchem_compound_list;
   FUNCTION  substructure_search_smiles (smile varchar2, topn NUMBER, debug_YN VARCHAR2) RETURN orchem_compound_list;
   FUNCTION  get_clob_as_char(mol IN clob) RETURN VARCHAR2;
   PRAGMA    RESTRICT_REFERENCES (get_clob_as_char, WNDS, RNDS, WNPS, RNPS);
END;
/
SHOW ERRORS



CREATE OR REPLACE PACKAGE BODY orchem
AS 
   /*                 */
   FUNCTION to_base( p_dec in NUMBER, p_base in NUMBER )
   RETURN VARCHAR2
   is
        l_str   VARCHAR2(255) DEFAULT NULL;
        l_num   NUMBER  DEFAULT p_dec;
        l_hex   VARCHAR2(16) DEFAULT '0123456789ABCDEF';
   BEGIN
        IF ( p_dec IS NULL or p_base IS NULL )
        THEN
                RETURN NULL;
        END IF;
        IF ( trunc(p_dec) <> p_dec OR p_dec < 0 ) THEN
                RAISE PROGRAM_ERROR;
        END IF;
        LOOP
                l_str := substr( l_hex, mod(l_num,p_base)+1, 1 ) || l_str;
                l_num := trunc( l_num/p_base );
                exit when ( l_num = 0 );
        END LOOP;
        RETURN l_str;
   END to_base;
   /*                 */
   FUNCTION to_dec
   ( p_str in VARCHAR2,
     p_from_base in NUMBER DEFAULT 16 ) RETURN NUMBER
   is
        l_num   NUMBER DEFAULT 0;
        l_hex   VARCHAR2(16) DEFAULT '0123456789ABCDEF';
   BEGIN
        IF ( p_str IS NULL or p_from_base IS NULL )
        THEN
                RETURN NULL;
        END IF;
        for i in 1 .. length(p_str) LOOP
                l_num := l_num * p_from_base + instr(l_hex,upper(substr(p_str,i,1)))-1;
        END LOOP;
        RETURN l_num;
   END to_dec;
   /*                 */
   FUNCTION to_hex( p_dec in NUMBER ) RETURN VARCHAR2
   is
   BEGIN
        RETURN to_base( p_dec, 16 );
   END to_hex;
   /*                 */
   FUNCTION to_bin( p_dec in NUMBER ) RETURN VARCHAR2
   is
   BEGIN
        RETURN to_base( p_dec, 2 );
   END to_bin;
   /*                 */
   FUNCTION to_oct( p_dec in NUMBER ) RETURN VARCHAR2
   IS
   BEGIN
        RETURN to_base( p_dec, 8 );
   END to_oct;
   FUNCTION get_clob_as_char(MOL IN clob  )
   RETURN VARCHAR2 AS
      ret VARCHAR2(4000);
   BEGIN
        BEGIN
           ret:=mol;
        EXCEPTION
          WHEN OTHERS THEN
             ret:=null;
        END;
        RETURN ret;
   END;
   /*    */
   PROCEDURE cdk_fingerprints (start_id VARCHAR2, end_id VARCHAR2,serialize_yn VARCHAR2)
   IS LANGUAGE JAVA
   NAME 'uk.ac.ebi.orchem.load.LoadCDKFingerprints.load (java.lang.String, java.lang.String, java.lang.String)';
   PROCEDURE load_cdk_fingerprints (start_id VARCHAR2, end_id VARCHAR2,serialize_yn VARCHAR2:='N')
   IS 
   BEGIN
      cdk_fingerprints (start_id, end_id, serialize_yn);
   END;
   /*      */
   PROCEDURE slice_load (p_start_id integer, p_end_id integer,serialize_yn VARCHAR2:='N')
   IS  
       slice_size    integer :=9999;
       p_slice_start integer :=-1;
       p_slice_end   integer :=-1;
   BEGIN  
     LOOP
        if p_slice_start=-1 then
           p_slice_start:=p_start_id;
        else
           p_slice_start:=p_slice_end+1;
        end if;
        p_slice_end:=p_slice_start+slice_size;
        if p_slice_end>p_end_id then
          p_slice_end:=p_end_id;
        end if;
        --dbms_output.put_line (p_slice_start||'-'|| p_slice_end);
        cdk_fingerprints (p_slice_start,p_slice_end,serialize_yn);
        exit when p_slice_end=p_end_id;
     END LOOP;
   END;  
   /*    */
   FUNCTION similarity_search_mol (molfile clob, cutoff float, topn NUMBER, debug_YN VARCHAR2)
   RETURN orchem_COMPOUND_LIST
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SimilaritySearch.molSearch (java.sql.Clob, java.lang.Float, java.lang.Integer, java.lang.String) return oracle.sql.ARRAY';
   /*    */
   FUNCTION similarity_search_smiles (smiles varchar2, cutoff float, topn NUMBER, debug_YN VARCHAR2)
   RETURN orchem_COMPOUND_LIST
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SimilaritySearch.smilesSearch (java.lang.String , java.lang.Float, java.lang.Integer, java.lang.String) return oracle.sql.ARRAY';
   /*    */
   FUNCTION substructure_search_mol (molfile clob, topn NUMBER, debug_YN VARCHAR2) 
   RETURN ORCHEM_COMPOUND_LIST
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.molSearch (java.sql.Clob, java.lang.Integer, java.lang.String) return oracle.sql.ARRAY';
   /*    */
   FUNCTION substructure_search_smiles (smile VARCHAR2, topn NUMBER, debug_YN VARCHAR2) 
   RETURN ORCHEM_COMPOUND_LIST
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.search.SubstructureSearch.smilesSearch (java.lang.String, java.lang.Integer, java.lang.String) return oracle.sql.ARRAY';


END;        
/
SHOW ERRORS

