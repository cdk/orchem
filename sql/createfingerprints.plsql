CREATE OR REPLACE PACKAGE orchem_fingerprinting
AS 
   PROCEDURE load_cdk_fingerprints (start_id VARCHAR2, end_id VARCHAR2);
   PROCEDURE slice_load (p_start_id integer, p_end_id integer);
END;
/
SHOW ERRORS


CREATE OR REPLACE PACKAGE BODY orchem_fingerprinting
AS 
   /*    */
   PROCEDURE cdk_fingerprints (start_id VARCHAR2, end_id VARCHAR2)
   IS LANGUAGE JAVA
   NAME 'uk.ac.ebi.orchem.load.LoadCDKFingerprints.load (java.lang.String, java.lang.String)';
   /*    */
   PROCEDURE load_cdk_fingerprints (start_id VARCHAR2, end_id VARCHAR2)
   IS 
   BEGIN
      cdk_fingerprints (start_id, end_id);
   END;
   /*      */
   PROCEDURE slice_load (p_start_id integer, p_end_id integer)
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
        cdk_fingerprints (p_slice_start,p_slice_end);
        exit when p_slice_end=p_end_id;
     END LOOP;
   END;  
END;        
/
SHOW ERRORS

exit;
