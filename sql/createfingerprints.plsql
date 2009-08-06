 /*
________________________________________________________________________________

 Chemical fingerprinting
 copyright: Mark Rijnbeek, markr@ebi.ac.uk 2009
________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_fingerprinting
AS 
   PROCEDURE load_cdk_fingerprints (start_id VARCHAR2, end_id VARCHAR2);
   PROCEDURE synchronize_cdk_fingerprints;

   PROCEDURE slice_load (p_start_id integer, p_end_id integer);
END;
/
SHOW ERRORS



CREATE OR REPLACE PACKAGE BODY orchem_fingerprinting
AS 

 /*___________________________________________________________________________

   Synchronize fingerprints with base compound data.
   
   Java stored procedure to invoke calculation of chemical fingerprints and
   storing these in OrChem search tables 
   ___________________________________________________________________________*/
   PROCEDURE synchronize_cdk_fingerprints 
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.load.FingerprintSynchronization.synchronize ()';



 /*___________________________________________________________________________
   
   Bulk load fingerprints  (initial load)
   Java stored procedure to invoke calculation of chemical fingerprints and
   storing these in OrChem search tables.
   
   ___________________________________________________________________________*/
   PROCEDURE load_cdk_fingerprints 
             (start_id VARCHAR2, end_id VARCHAR2)
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.load.FingerprintBulkLoad.bulkLoad (java.lang.String, java.lang.String)';




 /*___________________________________________________________________________
   Optional procedure (used in conjuncture with DBMS_JOB) to fingerprint a 
   large number of compound in sliced portions. This makes the load more 
   manageable and easier to restart/recover from a certain point.
   Example: request to fingerprint compounds with p.k. 1 to 50000 results in
      load slice     1-10000
      load slice 10001-20000
      load slice 20001-30000
      load slice 30001-40000
      load slice 40001-50000
   
   ___________________________________________________________________________*/
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
        load_cdk_fingerprints (p_slice_start,p_slice_end);
        exit when p_slice_end=p_end_id;
     END LOOP;
   END;  
END;        
/
SHOW ERRORS

exit 1; 
