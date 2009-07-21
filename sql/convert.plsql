/*
________________________________________________________________________________

Convert

author: fpaoli@sienabiotech.it, 2009

________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_convert
AS 
   FUNCTION MolfileToSmiles (molfile clob)
   RETURN CLOB;

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

END;  
/   
SHOW ERR


exit 1;
