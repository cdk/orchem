/*
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Federico Paoli
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 */

package uk.ac.ebi.orchem.test;

import java.sql.Clob;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Properties;

import junit.framework.TestCase;

import oracle.jdbc.driver.OracleConnection;

import org.openscience.cdk.exception.CDKException;

import uk.ac.ebi.orchem.PropertyLoader;


/**
 * Junit test for OrChem Convert<P>
 *
 * Contains a list of test methods, each for some "query compound" with the
 * number in the method is the database id (primary key) of that compound in
 * table ORCHEM_COMPOUND_SAMPLE.<P>
 * You can add more query compounds to this table by adding them to
 * the queries mol file in the unit test directory. They will get loaded by {@link LoadCompounds}.<P>
 * Target compounds are loaded from a second file with an id offset of 1000.<P>
 *
 */
public class TestConvert extends TestCase {


    static OracleConnection conn;
    static {
        try {
            Properties properties = PropertyLoader.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = (OracleConnection)DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Test conversion from MDL Molfile format to SMILES format
     * @param id test set primary key
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void molfileToSmiles(int id) throws SQLException, ClassNotFoundException {
        PreparedStatement pStmt = conn.prepareStatement("select id, orchem_convert.molfiletosmiles(molfile) as molfile from orchem_compound_sample where id = ?");
        pStmt.setInt(1, id);
        ResultSet res = pStmt.executeQuery();
        Clob molFileClob = null;
        int clobLen = 0; 
        String mdl = null;
        if (res.next()) {
            System.out.println("\n______________________________________________");
            System.out.println("db id is " + res.getInt("id"));
            molFileClob = res.getClob("molfile");
            clobLen = new Long(molFileClob.length()).intValue();
            mdl = (molFileClob.getSubString(1, clobLen));
            System.out.println("results # : " + mdl);
            System.out.println("length # : " + clobLen);
        }
        assertTrue("Result test:",resultlen(clobLen));

        res.close();
        pStmt.close();
    }

    /**
     * Test conversion from SMILES format to MDL Molfile
     * @param id test set primary key
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void smilesToMolfile(int id) throws SQLException, ClassNotFoundException {
      PreparedStatement pStmt = conn.prepareStatement("select id, orchem_convert.smilestomolfile(orchem_convert.molfiletosmiles(molfile)) as smiles from orchem_compound_sample where id = ?");
      pStmt.setInt(1, id);
      ResultSet res = pStmt.executeQuery();
      Clob smilesClob = null;
      int clobLen = 0; 
      String mdl = null;
      if (res.next()) {
          System.out.println("\n______________________________________________");
          System.out.println("db id is " + res.getInt("id"));
          smilesClob = res.getClob("smiles");
          clobLen = new Long(smilesClob.length()).intValue();
          mdl = (smilesClob.getSubString(1, clobLen));
          System.out.println("results # : " + mdl);
          System.out.println("length # : " + clobLen);
      }
      assertTrue("Result test:",resultlen(clobLen));

      res.close();
      pStmt.close();
    }



    /**
     * Convert from Molfile descriptor to an Inchi String
     * @param id test set primary key
     * @param expectedInchi expected result, as from C binary
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void molFileToInchi(int id, String expectedInchi) 
    throws SQLException, ClassNotFoundException {

      PreparedStatement pStmt = conn.prepareStatement
          ("select id, orchem_convert.molfiletoinchi(molfile) as inchi " +
          " from orchem_compound_sample where id = ?");

      pStmt.setInt(1, id);
      ResultSet res = pStmt.executeQuery();
      Clob inchiClob = null;
      int clobLen = 0; 
      String inChi = null;
      if (res.next()) {
          System.out.println("\n______________________________________________");
          System.out.println("db id is " + res.getInt("id"));
          inchiClob = res.getClob("inchi");
          clobLen = new Long(inchiClob.length()).intValue();
          inChi = (inchiClob.getSubString(1, clobLen));
      }
      System.out.println("InChi created is "+inChi);
      assertTrue("Inchi result",inChi.equals(expectedInchi));

      res.close();
      pStmt.close();
    }




  
    private static boolean resultlen(int hcloblen) {
      if (hcloblen>0) { return true; } else {return false;}
    }
    
    public void testCompoundID_1_1() throws Exception {
     molfileToSmiles(1);
    }

    public void testCompoundID_1_2() throws Exception {
     molfileToSmiles(2);
    }

    public void testCompoundID_1_3() throws Exception {
     molfileToSmiles(3);
    }

    public void testCompoundID_2_1() throws Exception {
     smilesToMolfile(1);
    }

    public void testCompoundID_2_2() throws Exception {
      smilesToMolfile(2);
    }

    public void testCompoundID_2_3() throws Exception {
      smilesToMolfile(3);
    }

    public void testCompoundID_1111_Inchi() throws Exception {
      molFileToInchi(1111,
      "InChI=1S/C15H14ClNO3.Na/c1-9-7-12(8-13(18)19)17(2)14(9)15(20)10-3-5-11(16)6-4-10;/h3-7H,8H2,1-2H3,(H,18,19);/q;+1/p-1"
      );
    }

    public void testCompoundID_1300_Inchi() throws Exception {
      molFileToInchi(1300,
      "InChI=1S/C22H25N3O2S2/c1-16-3-5-17(6-4-16)28-20-14-23-15-21-18(20)13-19(29-21)22(26)24-7-2-8-25-9-11-27-12-10-25/h3-6,13-15H,2,7-12H2,1H3,(H,24,26)"
      );
    }

    public void testCompoundID_1553_Inchi() throws Exception {
      molFileToInchi(1553,
      "InChI=1S/C410H515N83O55/c1-17-237(13)352(415)387(525)470-316(189-233(5)6)391(529)474-169-77-144-334(474)375(513)450-308(193-241-213-423-281-115-41-21-95-261(241)281)363(501)444-303(136-61-68-162-411)359(497)458-320(201-249-221-431-289-123-49-29-103-269(249)289)395(533)478-173-81-148-338(478)379(517)454-312(197-245-217-427-285-119-45-25-99-265(245)285)367(505)462-324(205-253-225-435-293-127-53-33-107-273(253)293)399(537)482-177-85-152-342(482)383(521)466-328(209-257-229-439-297-131-57-37-111-277(257)297)403(541)490-185-91-158-348(490)407(545)486-181-75-142-332(486)371(509)421-167-73-66-140-302(448-373(511)346-156-89-183-488(346)409(547)350-160-93-187-492(350)405(543)330(211-259-231-441-299-133-59-39-113-279(259)299)468-385(523)344-154-87-179-484(344)401(539)326(207-255-227-437-295-129-55-35-109-275(255)295)464-369(507)314(199-247-219-429-287-121-47-27-101-267(247)287)456-381(519)340-150-83-175-480(340)397(535)322(203-251-223-433-291-125-51-31-105-271(251)291)460-361(499)305(138-63-70-164-413)446-365(503)310(195-243-215-425-283-117-43-23-97-263(243)283)452-377(515)336-146-79-171-476(336)393(531)318(191-235(9)10)472-389(527)354(417)239(15)19-3)357(495)420-166-72-65-135-301(356(419)494)443-358(496)307(449-374(512)347-157-90-184-489(347)410(548)351-161-94-188-493(351)406(544)331(212-260-232-442-300-134-60-40-114-280(260)300)469-386(524)345-155-88-180-485(345)402(540)327(208-256-228-438-296-130-56-36-110-276(256)296)465-370(508)315(200-248-220-430-288-122-48-28-102-268(248)288)457-382(520)341-151-84-176-481(341)398(536)323(204-252-224-434-292-126-52-32-106-272(252)292)461-362(500)306(139-64-71-165-414)447-366(504)311(196-244-216-426-284-118-44-24-98-264(244)284)453-378(516)337-147-80-172-477(337)394(532)319(192-236(11)12)473-390(528)355(418)240(16)20-4)141-67-74-168-422-372(510)333-143-76-182-487(333)408(546)349-159-92-186-491(349)404(542)329(210-258-230-440-298-132-58-38-112-278(258)298)467-384(522)343-153-86-178-483(343)400(538)325(206-254-226-436-294-128-54-34-108-274(254)294)463-368(506)313(198-246-218-428-286-120-46-26-100-266(246)286)455-380(518)339-149-82-174-479(339)396(534)321(202-250-222-432-290-124-50-30-104-270(250)290)459-360(498)304(137-62-69-163-412)445-364(502)309(194-242-214-424-282-116-42-22-96-262(242)282)451-376(514)335-145-78-170-475(335)392(530)317(190-234(7)8)471-388(526)353(416)238(14)18-2/h21-60,95-134,213-240,301-355,423-442H,17-20,61-94,135-212,411-418H2,1-16H3,(H2,419,494)(H,420,495)(H,421,509)(H,422,510)(H,443,496)(H,444,501)(H,445,502)(H,446,503)(H,447,504)(H,448,511)(H,449,512)(H,450,513)(H,451,514)(H,452,515)(H,453,516)(H,454,517)(H,455,518)(H,456,519)(H,457,520)(H,458,497)(H,459,498)(H,460,499)(H,461,500)(H,462,505)(H,463,506)(H,464,507)(H,465,508)(H,466,521)(H,467,522)(H,468,523)(H,469,524)(H,470,525)(H,471,526)(H,472,527)(H,473,528)/t237-,238-,239-,240-,301?,302?,303+,304+,305+,306+,307?,308+,309+,310+,311+,312+,313+,314+,315+,316+,317+,318+,319+,320+,321+,322+,323+,324+,325+,326+,327+,328+,329+,330+,331+,332+,333+,334+,335+,336+,337+,338+,339+,340+,341+,342+,343+,344+,345+,346+,347+,348+,349+,350+,351+,352+,353+,354+,355+/m0/s1"
      );
    }

}
