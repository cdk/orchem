package uk.ac.ebi.orchem.test;

import java.io.File;

import java.io.FileOutputStream;

import java.io.InputStream;

import javax.imageio.ImageIO;

import oracle.sql.BLOB;
import oracle.sql.CLOB;

import uk.ac.ebi.orchem.Utils;
import uk.ac.ebi.orchem.convert.ConvertMolecule;

public class TestFromJava {


  public static void test() throws Exception {
    try {
     CLOB cmolfile=null;
     CLOB csmiles=null;
     BLOB cjpeg=null;
     System.out.println("Begin Test");
     csmiles=ConvertMolecule.StringToClob("O=C(N)CCC1NC(=O)C(NC(=O)C(N5C=CC(NC(=O)C(NC(=O)C(NC1(=O))CC3=CNC=2C=CC=CC=23)CC=4C=CC=CC=4)C5(=O))CC(C)C)CCSC");
     cmolfile=ConvertMolecule.smilesToMolfile(csmiles);
     cjpeg=ConvertMolecule.molfileToJpeg(cmolfile,200,200);
     System.out.println(cjpeg.length());
     /* begin test*/
     File              blobFile   = new File("/tmp/test.jpg");
     FileOutputStream  outStream  = new FileOutputStream(blobFile);
     InputStream       inStream   = cjpeg.getBinaryStream();
     int     length  = -1;
     int     size    = cjpeg.getBufferSize();
     byte[]  buffer  = new byte[size];
     while ((length = inStream.read(buffer)) != -1)
           {
             outStream.write(buffer, 0, length);
             outStream.flush();
           }
     inStream.close();
     outStream.close(); 
     System.out.println("End Test");
    } catch (Exception e) {
        System.out.println(Utils.getErrorString(e));
    }
  }



  public static void main(String[] args) throws Exception {
   try {
     test();
   } catch (Exception e) {
      System.out.println(Utils.getErrorString(e));
   }
  }
}
