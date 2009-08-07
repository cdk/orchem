/*
________________________________________________________________________________

Convert

copyright: Federico Paoli, fpaoli@sienabiotech.it, 2009

________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_convert
AS 
   FUNCTION MolfileToSmiles (molfile clob)
   RETURN CLOB;

   FUNCTION SmilesToMolfile (smiles clob)
   RETURN CLOB;

   FUNCTION MolfileToJpeg (molfile clob, hsize number, vsize number)
   RETURN BLOB;

END;
/
SHOW ERRORS



CREATE OR REPLACE PACKAGE BODY orchem_convert
AS 


  /*___________________________________________________________________________
   Function to convert a Molfile to SMILES 
   ___________________________________________________________________________*/
   FUNCTION MolfileToSmiles (molfile clob)
   RETURN CLOB
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.convert.ConvertMolecule.MolfileToSmiles(oracle.sql.CLOB) return oracle.sql.CLOB ';

   FUNCTION SmilesToMolfile (smiles clob)
   RETURN CLOB
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.convert.ConvertMolecule.SmilesToMolfile(oracle.sql.CLOB) return oracle.sql.CLOB ';

   FUNCTION MolfileToJpeg (molfile clob, hsize number, vsize number)
   RETURN BLOB
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.convert.ConvertMolecule.MolfileToJpeg(oracle.sql.CLOB, java.lang.Integer, java.lang.Integer) return oracle.sql.BLOB ';

END;  
/   
SHOW ERR


exit 1;
