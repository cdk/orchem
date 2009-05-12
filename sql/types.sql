
prompt creating type "orchem_compound"

    CREATE or REPLACE TYPE orchem_compound AS OBJECT
    EXTERNAL name 'uk.ac.ebi.orchem.bean.OrChemCompound' LANGUAGE JAVA USING SQLData
     (
     id varchar2(80)
     external name 'java.lang.String',
     mol_file clob
     external name 'java.lang.String',
     score float
     external name 'float',
     constructor function orchem_compound
     return self as result
     )
    /

prompt creating type "orchem_compound_list"

    CREATE or REPLACE TYPE ORCHEM_COMPOUND_LIST is TABLE OF orchem_compound
    /


prompt creating type "compound_id_table"
    CREATE or REPLACE TYPE compound_id_table is TABLE OF VARCHAR2(80)
    /

    
exit;