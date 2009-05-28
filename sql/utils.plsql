CREATE OR REPLACE PACKAGE orchem_utils
AS 
   TYPE candidate_rec is RECORD
   (
      query_key           integer
     ,compound_id         varchar2(80)
     ,single_bond_count   number(5)
     ,double_bond_count   number(5)
     ,triple_bond_count   number(5)
     ,aromatic_bond_count number(5)
     ,s_count             number(5)
     ,o_count             number(5)
     ,n_count             number(5)
     ,f_count             number(5)
     ,cl_count            number(5)
     ,br_count            number(5)
     ,i_count             number(5)
     ,c_count             number(5)
     ,atoms               clob
     ,bonds               clob
     ,debug_yn            varchar2(1)
   );
   PROCEDURE verify_orchem (p_primary_key varchar2);
   FUNCTION to_hex( p_dec IN NUMBER ) RETURN VARCHAR2;
   FUNCTION to_bin( p_dec IN NUMBER ) RETURN VARCHAR2;
   FUNCTION to_oct( p_dec IN NUMBER ) RETURN VARCHAR2;
   FUNCTION get_clob_as_char(mol IN clob) RETURN VARCHAR2;
   PRAGMA   RESTRICT_REFERENCES (get_clob_as_char, WNDS, RNDS, WNPS, RNPS);
END;
/
SHOW ERRORS


CREATE OR REPLACE PACKAGE BODY orchem_utils
AS 
   PROCEDURE verify_orchem (p_primary_key varchar2)
   IS LANGUAGE JAVA NAME
   'uk.ac.ebi.orchem.test.VerifyOrchem.verify(java.lang.String)'
   ;

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
END;        
/
SHOW ERRORS




exit 1;
