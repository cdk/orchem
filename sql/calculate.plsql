/*
________________________________________________________________________________

Package to calculate chemical properties, such as mass and charge.
Copyright: Mark Rijnbeek, EMBL EBI 2010
________________________________________________________________________________
*/

CREATE OR REPLACE PACKAGE orchem_calculate
AS  
   FUNCTION formula (molecule clob               -- the molecule, in format MOLfile or SMILES
                  , input_type varchar2          -- choose 'SMILES' or 'MOL' accordingly
                  , add_hydrogens varchar2       -- add hydrogens Y/N
            )
   RETURN   varchar2;

   FUNCTION mass (molecule clob                  -- the molecule, in format MOLfile or SMILES
                  , input_type varchar2          -- choose 'SMILES' or 'MOL' accordingly
                  , add_hydrogens varchar2       -- add hydrogens Y/N
            )
   RETURN   number;

   FUNCTION charge (molecule clob           -- the molecule, in format MOLfile or SMILES
                  , input_type varchar2     -- choose 'SMILES' or 'MOL' accordingly
            )
   RETURN   number;

END;
/
SHOW ERRORS

CREATE OR REPLACE PACKAGE BODY orchem_calculate
AS
   FUNCTION formula (molecule clob,input_type varchar2, add_hydrogens varchar2)
   RETURN   varchar2
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.calc.ChemicalProperties.calculateFormula(oracle.sql.CLOB , java.lang.String, java.lang.String) return java.lang.String';

   FUNCTION mass (molecule clob,input_type varchar2, add_hydrogens varchar2)
   RETURN   number
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.calc.ChemicalProperties.calculateMass(oracle.sql.CLOB , java.lang.String, java.lang.String) return oracle.sql.NUMBER';

   FUNCTION charge(molecule clob,input_type varchar2)
   RETURN   number
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.calc.ChemicalProperties.calculateCharge(oracle.sql.CLOB , java.lang.String) return oracle.sql.NUMBER';

END;
/   
SHOW ERR

exit 1;
