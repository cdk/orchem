/*
________________________________________________________________________________

Convert

copyright: Federico Paoli, fpaoli@sienabiotech.it, 2009
           Mark Rijnbeek, EMBL EBI 2009

________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_convert
AS 
   FUNCTION molfileToSmiles (molfile clob)
   RETURN CLOB;

   FUNCTION SmilesToMolfile (smiles clob)
   RETURN CLOB;

   FUNCTION molfileToJpeg (molfile clob, hsize number, vsize number)
   RETURN BLOB;

   FUNCTION molfileToInchi (molfile clob )
   RETURN CLOB;

END;
/
SHOW ERRORS



CREATE OR REPLACE PACKAGE BODY orchem_convert
AS 


  /*___________________________________________________________________________
   Function to convert a Molfile to SMILES 
   ___________________________________________________________________________*/
   FUNCTION molfileToSmiles (molfile clob)
   RETURN CLOB
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.convert.ConvertMolecule.molfileToSmiles(oracle.sql.CLOB) return oracle.sql.CLOB ';

   FUNCTION SmilesToMolfile (smiles clob)
   RETURN CLOB
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.convert.ConvertMolecule.smilesToMolfile(oracle.sql.CLOB) return oracle.sql.CLOB ';

   FUNCTION molfileToJpeg (molfile clob, hsize number, vsize number)
   RETURN BLOB
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.convert.ConvertMolecule.molfileToJpeg(oracle.sql.CLOB, java.lang.Integer, java.lang.Integer) return oracle.sql.BLOB ';

   
   
   /************
   Provide molfile and a database server side directory (like /tmp/ or c:\temp\) 
   This is for the InChi writer to work in as it needs to read/write temporary 
   files. See Orchem documentation for details.
   *************/
   FUNCTION molfileToInchi_java (molfile clob, file_seq_num varchar2, temp_dir varchar2 )
   RETURN CLOB
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.convert.ConvertMolecule.molfileToInchi(oracle.sql.CLOB, java.lang.String, java.lang.String) return oracle.sql.CLOB  ';

   FUNCTION molfileToInchi (molfile clob)
   RETURN CLOB
   IS 
      file_seq_num INTEGER;
      temp_dir_name orchem_parameters.tmp_dir_on_server%TYPE;
   BEGIN
      
      dbms_output.disable; -- prevents C waffle coming from stdinchi

      SELECT orchem_sequence_tmp_filenums.nextval 
      INTO   file_seq_num 
      FROM   dual;

      SELECT tmp_dir_on_server
      INTO   temp_dir_name 
      FROM   orchem_parameters;

      IF temp_dir_name IS NULL THEN
         RAISE_APPLICATION_ERROR (-20001, 
         'You must provide a temporary directory name through ORCHEM_PARAMETERS table');
      END IF;

      RETURN molfileToInchi_java (molfile, file_seq_num, temp_dir_name );

   END;

END;  
/   
SHOW ERR


exit 1;
