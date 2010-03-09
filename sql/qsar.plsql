/*
________________________________________________________________________________

QSAR descriptors

copyright: Duan Lian, EMBL EBI 2010

________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_qsar
AS 
   FUNCTION calcALOGP (molfile clob)
   RETURN BINARY_DOUBLE;
   FUNCTION calcALOGP2 (molfile clob)
   RETURN BINARY_DOUBLE;
   FUNCTION calcAMR (molfile clob)
   RETURN BINARY_DOUBLE;
END;
/
SHOW ERRORS



CREATE OR REPLACE PACKAGE BODY orchem_qsar
AS
  /*___________________________________________________________________________
   Functions to calculate QSAR descriptors
   ___________________________________________________________________________*/
   FUNCTION calcALOGP (molfile clob)
   RETURN BINARY_DOUBLE
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.calcALOGP(oracle.sql.CLOB) return oracle.sql.BINARY_DOUBLE ';

   FUNCTION calcALOGP2 (molfile clob)
   RETURN BINARY_DOUBLE
   IS LANGUAGE JAVA NAME
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.calcALOGP2(oracle.sql.CLOB) return oracle.sql.BINARY_DOUBLE ';

   FUNCTION calcAMR (molfile clob)
   RETURN BINARY_DOUBLE
   IS LANGUAGE JAVA NAME
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.calcAMR(oracle.sql.CLOB) return oracle.sql.BINARY_DOUBLE ';
END;
/   
SHOW ERR

exit 1;
