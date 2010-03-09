/*
________________________________________________________________________________

QSAR descriptors

copyright: Duan Lian, EMBL EBI 2010

________________________________________________________________________________
*/CREATE OR REPLACE PACKAGE orchem_qsar
AS    FUNCTION ATSm1 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSm2 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSm3 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSm4 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSm5 (molfile clob)
   RETURN NUMBER;
   FUNCTION WTPT1 (molfile clob)
   RETURN NUMBER;
   FUNCTION WTPT2 (molfile clob)
   RETURN NUMBER;
   FUNCTION WTPT3 (molfile clob)
   RETURN NUMBER;
   FUNCTION WTPT4 (molfile clob)
   RETURN NUMBER;
   FUNCTION WTPT5 (molfile clob)
   RETURN NUMBER;
   FUNCTION ALogP (molfile clob)
   RETURN NUMBER;
   FUNCTION ALogp2 (molfile clob)
   RETURN NUMBER;
   FUNCTION AMR (molfile clob)
   RETURN NUMBER;
   FUNCTION nB (molfile clob)
   RETURN NUMBER;
   FUNCTION Kier1 (molfile clob)
   RETURN NUMBER;
   FUNCTION Kier2 (molfile clob)
   RETURN NUMBER;
   FUNCTION Kier3 (molfile clob)
   RETURN NUMBER;
   FUNCTION nAtomLC (molfile clob)
   RETURN NUMBER;
   FUNCTION BCUTw1l (molfile clob)
   RETURN NUMBER;
   FUNCTION BCUTw1h (molfile clob)
   RETURN NUMBER;
   FUNCTION BCUTc1l (molfile clob)
   RETURN NUMBER;
   FUNCTION BCUTc1h (molfile clob)
   RETURN NUMBER;
   FUNCTION BCUTp1l (molfile clob)
   RETURN NUMBER;
   FUNCTION BCUTp1h (molfile clob)
   RETURN NUMBER;
   FUNCTION SPC4 (molfile clob)
   RETURN NUMBER;
   FUNCTION SPC5 (molfile clob)
   RETURN NUMBER;
   FUNCTION SPC6 (molfile clob)
   RETURN NUMBER;
   FUNCTION VPC4 (molfile clob)
   RETURN NUMBER;
   FUNCTION VPC5 (molfile clob)
   RETURN NUMBER;
   FUNCTION VPC6 (molfile clob)
   RETURN NUMBER;
   FUNCTION topoShape (molfile clob)
   RETURN NUMBER;
   FUNCTION geomShape (molfile clob)
   RETURN NUMBER;
   FUNCTION nAtom (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSp1 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSp2 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSp3 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSp4 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSp5 (molfile clob)
   RETURN NUMBER;
   FUNCTION MolIP (molfile clob)
   RETURN NUMBER;
   FUNCTION PPSA1 (molfile clob)
   RETURN NUMBER;
   FUNCTION PPSA2 (molfile clob)
   RETURN NUMBER;
   FUNCTION PPSA3 (molfile clob)
   RETURN NUMBER;
   FUNCTION PNSA1 (molfile clob)
   RETURN NUMBER;
   FUNCTION PNSA2 (molfile clob)
   RETURN NUMBER;
   FUNCTION PNSA3 (molfile clob)
   RETURN NUMBER;
   FUNCTION DPSA1 (molfile clob)
   RETURN NUMBER;
   FUNCTION DPSA2 (molfile clob)
   RETURN NUMBER;
   FUNCTION DPSA3 (molfile clob)
   RETURN NUMBER;
   FUNCTION FPSA1 (molfile clob)
   RETURN NUMBER;
   FUNCTION FPSA2 (molfile clob)
   RETURN NUMBER;
   FUNCTION FPSA3 (molfile clob)
   RETURN NUMBER;
   FUNCTION FNSA1 (molfile clob)
   RETURN NUMBER;
   FUNCTION FNSA2 (molfile clob)
   RETURN NUMBER;
   FUNCTION FNSA3 (molfile clob)
   RETURN NUMBER;
   FUNCTION WPSA1 (molfile clob)
   RETURN NUMBER;
   FUNCTION WPSA2 (molfile clob)
   RETURN NUMBER;
   FUNCTION WPSA3 (molfile clob)
   RETURN NUMBER;
   FUNCTION WNSA1 (molfile clob)
   RETURN NUMBER;
   FUNCTION WNSA2 (molfile clob)
   RETURN NUMBER;
   FUNCTION WNSA3 (molfile clob)
   RETURN NUMBER;
   FUNCTION RPCG (molfile clob)
   RETURN NUMBER;
   FUNCTION RNCG (molfile clob)
   RETURN NUMBER;
   FUNCTION RPCS (molfile clob)
   RETURN NUMBER;
   FUNCTION RNCS (molfile clob)
   RETURN NUMBER;
   FUNCTION THSA (molfile clob)
   RETURN NUMBER;
   FUNCTION TPSA (molfile clob)
   RETURN NUMBER;
   FUNCTION RHSA (molfile clob)
   RETURN NUMBER;
   FUNCTION RPSA (molfile clob)
   RETURN NUMBER;
   FUNCTION LipinskiFailures (molfile clob)
   RETURN NUMBER;
   FUNCTION bpol (molfile clob)
   RETURN NUMBER;
   FUNCTION VAdjMat (molfile clob)
   RETURN NUMBER;
   FUNCTION nA (molfile clob)
   RETURN NUMBER;
   FUNCTION nR (molfile clob)
   RETURN NUMBER;
   FUNCTION nN (molfile clob)
   RETURN NUMBER;
   FUNCTION nD (molfile clob)
   RETURN NUMBER;
   FUNCTION nC (molfile clob)
   RETURN NUMBER;
   FUNCTION nF (molfile clob)
   RETURN NUMBER;
   FUNCTION nQ (molfile clob)
   RETURN NUMBER;
   FUNCTION nE (molfile clob)
   RETURN NUMBER;
   FUNCTION nG (molfile clob)
   RETURN NUMBER;
   FUNCTION nH (molfile clob)
   RETURN NUMBER;
   FUNCTION nI (molfile clob)
   RETURN NUMBER;
   FUNCTION nP (molfile clob)
   RETURN NUMBER;
   FUNCTION nL (molfile clob)
   RETURN NUMBER;
   FUNCTION nK (molfile clob)
   RETURN NUMBER;
   FUNCTION nM (molfile clob)
   RETURN NUMBER;
   FUNCTION nS (molfile clob)
   RETURN NUMBER;
   FUNCTION nT (molfile clob)
   RETURN NUMBER;
   FUNCTION nY (molfile clob)
   RETURN NUMBER;
   FUNCTION nV (molfile clob)
   RETURN NUMBER;
   FUNCTION nW (molfile clob)
   RETURN NUMBER;
   FUNCTION nAromBond (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAV1 (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAV2 (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAV3 (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAVH1 (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAVH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAVH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAV4 (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAV5 (molfile clob)
   RETURN NUMBER;
   FUNCTION GRAV6 (molfile clob)
   RETURN NUMBER;
   FUNCTION SP0 (molfile clob)
   RETURN NUMBER;
   FUNCTION SP1 (molfile clob)
   RETURN NUMBER;
   FUNCTION SP2 (molfile clob)
   RETURN NUMBER;
   FUNCTION SP3 (molfile clob)
   RETURN NUMBER;
   FUNCTION SP4 (molfile clob)
   RETURN NUMBER;
   FUNCTION SP5 (molfile clob)
   RETURN NUMBER;
   FUNCTION SP6 (molfile clob)
   RETURN NUMBER;
   FUNCTION SP7 (molfile clob)
   RETURN NUMBER;
   FUNCTION VP0 (molfile clob)
   RETURN NUMBER;
   FUNCTION VP1 (molfile clob)
   RETURN NUMBER;
   FUNCTION VP2 (molfile clob)
   RETURN NUMBER;
   FUNCTION VP3 (molfile clob)
   RETURN NUMBER;
   FUNCTION VP4 (molfile clob)
   RETURN NUMBER;
   FUNCTION VP5 (molfile clob)
   RETURN NUMBER;
   FUNCTION VP6 (molfile clob)
   RETURN NUMBER;
   FUNCTION VP7 (molfile clob)
   RETURN NUMBER;
   FUNCTION C1SP1 (molfile clob)
   RETURN NUMBER;
   FUNCTION C2SP1 (molfile clob)
   RETURN NUMBER;
   FUNCTION C1SP2 (molfile clob)
   RETURN NUMBER;
   FUNCTION C2SP2 (molfile clob)
   RETURN NUMBER;
   FUNCTION C3SP2 (molfile clob)
   RETURN NUMBER;
   FUNCTION C1SP3 (molfile clob)
   RETURN NUMBER;
   FUNCTION C2SP3 (molfile clob)
   RETURN NUMBER;
   FUNCTION C3SP3 (molfile clob)
   RETURN NUMBER;
   FUNCTION C4SP3 (molfile clob)
   RETURN NUMBER;
   FUNCTION SCH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION SCH4 (molfile clob)
   RETURN NUMBER;
   FUNCTION SCH5 (molfile clob)
   RETURN NUMBER;
   FUNCTION SCH6 (molfile clob)
   RETURN NUMBER;
   FUNCTION SCH7 (molfile clob)
   RETURN NUMBER;
   FUNCTION VCH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION VCH4 (molfile clob)
   RETURN NUMBER;
   FUNCTION VCH5 (molfile clob)
   RETURN NUMBER;
   FUNCTION VCH6 (molfile clob)
   RETURN NUMBER;
   FUNCTION VCH7 (molfile clob)
   RETURN NUMBER;
   FUNCTION SC3 (molfile clob)
   RETURN NUMBER;
   FUNCTION SC4 (molfile clob)
   RETURN NUMBER;
   FUNCTION SC5 (molfile clob)
   RETURN NUMBER;
   FUNCTION SC6 (molfile clob)
   RETURN NUMBER;
   FUNCTION VC3 (molfile clob)
   RETURN NUMBER;
   FUNCTION VC4 (molfile clob)
   RETURN NUMBER;
   FUNCTION VC5 (molfile clob)
   RETURN NUMBER;
   FUNCTION VC6 (molfile clob)
   RETURN NUMBER;
   FUNCTION nRotB (molfile clob)
   RETURN NUMBER;
   FUNCTION Wlambda1unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Wlambda2unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Wlambda3unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Wnu1unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Wnu2unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Wgamma1unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Wgamma2unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Wgamma3unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Weta1unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Weta2unity (molfile clob)
   RETURN NUMBER;
   FUNCTION Weta3unity (molfile clob)
   RETURN NUMBER;
   FUNCTION WTunity (molfile clob)
   RETURN NUMBER;
   FUNCTION WAunity (molfile clob)
   RETURN NUMBER;
   FUNCTION WVunity (molfile clob)
   RETURN NUMBER;
   FUNCTION WKunity (molfile clob)
   RETURN NUMBER;
   FUNCTION WGunity (molfile clob)
   RETURN NUMBER;
   FUNCTION WDunity (molfile clob)
   RETURN NUMBER;
   FUNCTION nHBAcc (molfile clob)
   RETURN NUMBER;
   FUNCTION MOMIX (molfile clob)
   RETURN NUMBER;
   FUNCTION MOMIY (molfile clob)
   RETURN NUMBER;
   FUNCTION MOMIZ (molfile clob)
   RETURN NUMBER;
   FUNCTION MOMIXY (molfile clob)
   RETURN NUMBER;
   FUNCTION MOMIXZ (molfile clob)
   RETURN NUMBER;
   FUNCTION MOMIYZ (molfile clob)
   RETURN NUMBER;
   FUNCTION MOMIR (molfile clob)
   RETURN NUMBER;
   FUNCTION khssLi (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssBe (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssssBe (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssBH (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssB (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssssB (molfile clob)
   RETURN NUMBER;
   FUNCTION khssCH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdCH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssCH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khstCH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdsCH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaaCH (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssCH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsddC (molfile clob)
   RETURN NUMBER;
   FUNCTION khstsC (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdssC (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaasC (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaaaC (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssssC (molfile clob)
   RETURN NUMBER;
   FUNCTION khssNH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION khssNH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssNH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdNH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssNH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaaNH (molfile clob)
   RETURN NUMBER;
   FUNCTION khstN (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssNH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdsN (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaaN (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssN (molfile clob)
   RETURN NUMBER;
   FUNCTION khsddsN (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaasN (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssssN (molfile clob)
   RETURN NUMBER;
   FUNCTION khssOH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdO (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssO (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaaO (molfile clob)
   RETURN NUMBER;
   FUNCTION khssF (molfile clob)
   RETURN NUMBER;
   FUNCTION khssSiH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssSiH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssSiH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssssSi (molfile clob)
   RETURN NUMBER;
   FUNCTION khssPH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssPH (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssP (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdsssP (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssssP (molfile clob)
   RETURN NUMBER;
   FUNCTION khssSH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdS (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssS (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaaS (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdssS (molfile clob)
   RETURN NUMBER;
   FUNCTION khsddssS (molfile clob)
   RETURN NUMBER;
   FUNCTION khssCl (molfile clob)
   RETURN NUMBER;
   FUNCTION khssGeH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssGeH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssGeH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssssGe (molfile clob)
   RETURN NUMBER;
   FUNCTION khssAsH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssAsH (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssAs (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssdAs (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssssAs (molfile clob)
   RETURN NUMBER;
   FUNCTION khssSeH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdSe (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssSe (molfile clob)
   RETURN NUMBER;
   FUNCTION khsaaSe (molfile clob)
   RETURN NUMBER;
   FUNCTION khsdssSe (molfile clob)
   RETURN NUMBER;
   FUNCTION khsddssSe (molfile clob)
   RETURN NUMBER;
   FUNCTION khssBr (molfile clob)
   RETURN NUMBER;
   FUNCTION khssSnH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssSnH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssSnH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssssSn (molfile clob)
   RETURN NUMBER;
   FUNCTION khssI (molfile clob)
   RETURN NUMBER;
   FUNCTION khssPbH3 (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssPbH2 (molfile clob)
   RETURN NUMBER;
   FUNCTION khssssPbH (molfile clob)
   RETURN NUMBER;
   FUNCTION khsssssPb (molfile clob)
   RETURN NUMBER;
   FUNCTION nAtomLAC (molfile clob)
   RETURN NUMBER;
   FUNCTION TopoPSA (molfile clob)
   RETURN NUMBER;
   FUNCTION ECCEN (molfile clob)
   RETURN NUMBER;
   FUNCTION nAtomP (molfile clob)
   RETURN NUMBER;
   FUNCTION nHBDon (molfile clob)
   RETURN NUMBER;
   FUNCTION XLogP (molfile clob)
   RETURN NUMBER;
   FUNCTION WPATH (molfile clob)
   RETURN NUMBER;
   FUNCTION WPOL (molfile clob)
   RETURN NUMBER;
   FUNCTION PetitjeanNumber (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC11 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC12 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC13 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC14 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC22 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC23 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC24 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC33 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC34 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEC44 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEO11 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEO12 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEO22 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEN11 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEN12 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEN13 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEN22 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEN23 (molfile clob)
   RETURN NUMBER;
   FUNCTION MDEN33 (molfile clob)
   RETURN NUMBER;
   FUNCTION fragC (molfile clob)
   RETURN NUMBER;
   FUNCTION apol (molfile clob)
   RETURN NUMBER;
   FUNCTION naAromAtom (molfile clob)
   RETURN NUMBER;
   FUNCTION Zagreb (molfile clob)
   RETURN NUMBER;
   FUNCTION MLogP (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSc1 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSc2 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSc3 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSc4 (molfile clob)
   RETURN NUMBER;
   FUNCTION ATSc5 (molfile clob)
   RETURN NUMBER;
   FUNCTION MW (molfile clob)
   RETURN NUMBER;
   FUNCTION LOBMAX (molfile clob)
   RETURN NUMBER;
   FUNCTION LOBMIN (molfile clob)
   RETURN NUMBER;
END;
/
SHOW ERRORS


CREATE OR REPLACE PACKAGE BODY orchem_qsar
AS
  /*___________________________________________________________________________
   Functions to calculate QSAR descriptors
   ___________________________________________________________________________*/   FUNCTION ATSm1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSm1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSm2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSm2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSm3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSm3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSm4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSm4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSm5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSm5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WTPT1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WTPT1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WTPT2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WTPT2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WTPT3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WTPT3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WTPT4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WTPT4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WTPT5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WTPT5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ALogP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ALogP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ALogp2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ALogp2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION AMR (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.AMR(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nB (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nB(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Kier1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Kier1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Kier2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Kier2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Kier3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Kier3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nAtomLC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nAtomLC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION BCUTw1l (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.BCUTw1l(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION BCUTw1h (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.BCUTw1h(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION BCUTc1l (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.BCUTc1l(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION BCUTc1h (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.BCUTc1h(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION BCUTp1l (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.BCUTp1l(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION BCUTp1h (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.BCUTp1h(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SPC4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SPC4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SPC5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SPC5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SPC6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SPC6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VPC4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VPC4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VPC5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VPC5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VPC6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VPC6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION topoShape (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.topoShape(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION geomShape (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.geomShape(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nAtom (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nAtom(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSp1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSp1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSp2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSp2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSp3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSp3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSp4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSp4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSp5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSp5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MolIP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MolIP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION PPSA1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.PPSA1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION PPSA2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.PPSA2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION PPSA3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.PPSA3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION PNSA1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.PNSA1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION PNSA2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.PNSA2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION PNSA3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.PNSA3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION DPSA1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.DPSA1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION DPSA2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.DPSA2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION DPSA3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.DPSA3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION FPSA1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.FPSA1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION FPSA2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.FPSA2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION FPSA3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.FPSA3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION FNSA1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.FNSA1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION FNSA2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.FNSA2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION FNSA3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.FNSA3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WPSA1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WPSA1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WPSA2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WPSA2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WPSA3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WPSA3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WNSA1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WNSA1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WNSA2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WNSA2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WNSA3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WNSA3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION RPCG (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.RPCG(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION RNCG (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.RNCG(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION RPCS (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.RPCS(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION RNCS (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.RNCS(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION THSA (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.THSA(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION TPSA (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.TPSA(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION RHSA (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.RHSA(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION RPSA (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.RPSA(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION LipinskiFailures (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.LipinskiFailures(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION bpol (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.bpol(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VAdjMat (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VAdjMat(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nA (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nA(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nR (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nR(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nD (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nD(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nF (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nF(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nQ (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nQ(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nE (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nE(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nG (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nG(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nI (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nI(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nL (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nL(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nK (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nK(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nM (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nM(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nS (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nS(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nT (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nT(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nY (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nY(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nV (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nV(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nW (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nW(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nAromBond (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nAromBond(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAV1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAV1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAV2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAV2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAV3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAV3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAVH1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAVH1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAVH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAVH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAVH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAVH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAV4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAV4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAV5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAV5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION GRAV6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.GRAV6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SP0 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SP0(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SP1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SP1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SP2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SP2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SP3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SP3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SP4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SP4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SP5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SP5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SP6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SP6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SP7 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SP7(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VP0 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VP0(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VP1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VP1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VP2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VP2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VP3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VP3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VP4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VP4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VP5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VP5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VP6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VP6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VP7 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VP7(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C1SP1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C1SP1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C2SP1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C2SP1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C1SP2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C1SP2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C2SP2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C2SP2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C3SP2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C3SP2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C1SP3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C1SP3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C2SP3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C2SP3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C3SP3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C3SP3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION C4SP3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.C4SP3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SCH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SCH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SCH4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SCH4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SCH5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SCH5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SCH6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SCH6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SCH7 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SCH7(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VCH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VCH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VCH4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VCH4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VCH5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VCH5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VCH6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VCH6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VCH7 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VCH7(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SC3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SC3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SC4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SC4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SC5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SC5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION SC6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.SC6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VC3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VC3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VC4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VC4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VC5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VC5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION VC6 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.VC6(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nRotB (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nRotB(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Wlambda1unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Wlambda1unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Wlambda2unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Wlambda2unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Wlambda3unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Wlambda3unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Wnu1unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Wnu1unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Wnu2unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Wnu2unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Wgamma1unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Wgamma1unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Wgamma2unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Wgamma2unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Wgamma3unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Wgamma3unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Weta1unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Weta1unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Weta2unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Weta2unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Weta3unity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Weta3unity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WTunity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WTunity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WAunity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WAunity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WVunity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WVunity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WKunity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WKunity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WGunity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WGunity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WDunity (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WDunity(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nHBAcc (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nHBAcc(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MOMIX (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MOMIX(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MOMIY (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MOMIY(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MOMIZ (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MOMIZ(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MOMIXY (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MOMIXY(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MOMIXZ (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MOMIXZ(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MOMIYZ (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MOMIYZ(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MOMIR (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MOMIR(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssLi (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssLi(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssBe (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssBe(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssssBe (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssssBe(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssBH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssBH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssB (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssB(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssssB (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssssB(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssCH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssCH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdCH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdCH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssCH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssCH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khstCH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khstCH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdsCH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdsCH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaaCH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaaCH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssCH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssCH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsddC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsddC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khstsC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khstsC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdssC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdssC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaasC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaasC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaaaC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaaaC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssssC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssssC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssNH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssNH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssNH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssNH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssNH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssNH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdNH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdNH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssNH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssNH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaaNH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaaNH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khstN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khstN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssNH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssNH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdsN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdsN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaaN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaaN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsddsN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsddsN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaasN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaasN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssssN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssssN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssOH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssOH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdO (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdO(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssO (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssO(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaaO (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaaO(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssF (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssF(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssSiH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssSiH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssSiH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssSiH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssSiH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssSiH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssssSi (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssssSi(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssPH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssPH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssPH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssPH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdsssP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdsssP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssssP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssssP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssSH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssSH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdS (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdS(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssS (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssS(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaaS (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaaS(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdssS (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdssS(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsddssS (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsddssS(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssCl (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssCl(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssGeH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssGeH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssGeH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssGeH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssGeH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssGeH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssssGe (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssssGe(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssAsH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssAsH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssAsH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssAsH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssAs (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssAs(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssdAs (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssdAs(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssssAs (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssssAs(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssSeH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssSeH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdSe (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdSe(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssSe (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssSe(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsaaSe (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsaaSe(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsdssSe (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsdssSe(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsddssSe (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsddssSe(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssBr (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssBr(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssSnH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssSnH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssSnH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssSnH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssSnH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssSnH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssssSn (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssssSn(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssI (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssI(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssPbH3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssPbH3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssPbH2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssPbH2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khssssPbH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khssssPbH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION khsssssPb (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.khsssssPb(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nAtomLAC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nAtomLAC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION TopoPSA (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.TopoPSA(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ECCEN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ECCEN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nAtomP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nAtomP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION nHBDon (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.nHBDon(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION XLogP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.XLogP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WPATH (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WPATH(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION WPOL (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.WPOL(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION PetitjeanNumber (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.PetitjeanNumber(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC11 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC11(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC12 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC12(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC13 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC13(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC14 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC14(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC22 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC22(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC23 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC23(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC24 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC24(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC33 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC33(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC34 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC34(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEC44 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEC44(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEO11 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEO11(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEO12 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEO12(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEO22 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEO22(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEN11 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEN11(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEN12 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEN12(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEN13 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEN13(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEN22 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEN22(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEN23 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEN23(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MDEN33 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MDEN33(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION fragC (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.fragC(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION apol (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.apol(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION naAromAtom (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.naAromAtom(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION Zagreb (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.Zagreb(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MLogP (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MLogP(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSc1 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSc1(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSc2 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSc2(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSc3 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSc3(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSc4 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSc4(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION ATSc5 (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.ATSc5(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION MW (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.MW(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION LOBMAX (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.LOBMAX(oracle.sql.CLOB) return oracle.sql.NUMBER ';

   FUNCTION LOBMIN (molfile clob)
   RETURN NUMBER
   IS LANGUAGE JAVA NAME 
   'uk.ac.ebi.orchem.qsar.DescriptorCalculate.LOBMIN(oracle.sql.CLOB) return oracle.sql.NUMBER ';

END;
/   
SHOW ERR

exit 1;
