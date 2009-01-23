
package uk.ac.ebi.orchem.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import java.sql.Connection;

import oracle.jdbc.OraclePreparedStatement;

import oracle.sql.CLOB;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.io.iterator.IteratingMDLReader;

import uk.ac.ebi.orchem.db.PubChemConnection;


/**
 * Imports pubchem SDF file into a local test schema at the EBI
 * Reads MDL mol file, an SDF file in this case
 *
 */
public class

PubChemImport {

    public static void main(String[] args) throws Exception {
        PubChemImport imp = new PubChemImport();
        imp.importPubChem(args[0]);

    }

    public void importPubChem(String dirNum) throws Exception {

        String dataDir = "/mnt/c/linux/data/pubchem/Compound_05/02/";
        File dir = new File(dataDir);
        String[] list = dir.list();
        File pubChemfile;
        if (list.length == 0)
            return;

        String insertCommand =
            " insert into compounds " + " (cid, os_dirnum, iupac_name, openeye_iso_smiles, openeye_mf, mdl ) " +
            " values (?,?,?, ?,?,?) ";
        Connection conn = new PubChemConnection().getDbConnection();
        conn.setAutoCommit(false);
        OraclePreparedStatement pstmt = (OraclePreparedStatement)conn.prepareStatement(insertCommand);

        for (int i = 0; i < list.length; i++) {
            pubChemfile = new File(dataDir + list[i]);
            InputStream ins = new FileInputStream(pubChemfile);
            IteratingMDLReader reader = new IteratingMDLReader(ins, DefaultChemObjectBuilder.getInstance());
            System.out.println("Start file pubChemfile " + pubChemfile.toString() + " process at " + new java.util.Date());

            int molCount = 0;
            while (reader.hasNext()) {
                Object object = reader.next();
                if (object != null && object instanceof Molecule) {

                    molCount++;
                    Molecule m = (Molecule)object;

                    //MDL
                    StringWriter writer = new StringWriter();
                    MDLWriter mdlWriter = new MDLWriter(writer);
                    mdlWriter.write(m);
                    String mdl = writer.toString();
                    CLOB mdlClob = CLOB.createTemporary(conn, false, CLOB.DURATION_SESSION);
                    mdlClob.open(CLOB.MODE_READWRITE);
                    mdlClob.setString(1, mdl);

                    String cid = (String)(m.getProperties().get("PUBCHEM_COMPOUND_CID"));

                    String iupacName = (String)(m.getProperties().get("PUBCHEM_IUPAC_NAME"));
                    if (iupacName != null && iupacName.length() > 4000)
                        iupacName = iupacName.substring(0, 4000);

                    String isoSmiles = (String)(m.getProperties().get("PUBCHEM_OPENEYE_ISO_SMILES"));
                    if (isoSmiles != null && isoSmiles.length() > 4000)
                        isoSmiles = isoSmiles.substring(0, 4000);

                    String mf = (String)(m.getProperties().get("PUBCHEM_OPENEYE_MF"));
                    if (mf != null && mf.length() > 4000)
                        mf = mf.substring(0, 4000);

                    pstmt.setString(1, cid);
                    pstmt.setString(2, dirNum);
                    pstmt.setString(3, iupacName);
                    pstmt.setString(4, isoSmiles);
                    pstmt.setString(5, mf);
                    pstmt.setCLOB(6, mdlClob);

                    pstmt.executeUpdate();

                    mdlClob.close();
                    mdlClob.freeTemporary();

                    if (molCount > 500) {
                        molCount = 0;
                        conn.commit();
                    }
                }
            }
            conn.commit();
        }
    }
}

