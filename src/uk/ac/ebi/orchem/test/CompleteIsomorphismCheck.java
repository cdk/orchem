/*  
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  OrChem project
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
 *
 *
 */
package uk.ac.ebi.orchem.test;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;

import org.openscience.cdk.nonotify.NNMolecule;

import uk.ac.ebi.orchem.isomorphism.SubgraphIsomorphism;
import uk.ac.ebi.orchem.shared.MoleculeCreator;


/**
 *
 */
public class CompleteIsomorphismCheck {

    MDLV2000Reader mdlReader = new MDLV2000Reader();
    
    class MyAtomContainer {
        IAtomContainer atomContainer;
        int dbId;
        MyAtomContainer (IAtomContainer a, int id) {
            this.atomContainer=a;
            this.dbId=id;
        }
    }

    public List<Integer> check(int id, Connection conn) throws SQLException, CDKException, CloneNotSupportedException {

        List<Integer> result = new ArrayList<Integer>();
        List<MyAtomContainer> queryMoleculeList = getMols(conn,id,id);
        if (queryMoleculeList.size()==1)  {
            MyAtomContainer query = queryMoleculeList.get(0);
            List<MyAtomContainer> targetMolecules= getMols(conn,0,1000000);
            for(MyAtomContainer mac : targetMolecules ) {
                   SubgraphIsomorphism s =  new SubgraphIsomorphism(mac.atomContainer, query.atomContainer);
                if (s.matchSingle()) {
                    result.add(mac.dbId);
                }
            }
        }
        return result;
    }

    private List<MyAtomContainer> getMols (Connection conn,int startid, int endid) throws SQLException, CDKException {
        List<MyAtomContainer> molecules = new ArrayList<MyAtomContainer>();
        PreparedStatement pStmt = conn.prepareStatement("select id, molfile from orchem_compound_sample where id between ? and ?");
        pStmt.setInt(1, startid);
        pStmt.setInt(2, endid);

        ResultSet res = pStmt.executeQuery();
        Clob molFileClob = null;
        String mdl = null;
        while (res.next()) {
            molFileClob = res.getClob("molfile");
            int clobLen = new Long(molFileClob.length()).intValue();
            mdl = (molFileClob.getSubString(1, clobLen));
            try {
                NNMolecule molecule = MoleculeCreator.getNNMolecule(mdlReader, mdl);
                molecules.add(new MyAtomContainer(molecule,res.getInt("id")));
            } catch (Exception e) {
                System.err.println("Error for ID " +res.getInt("id") +": "+e.getMessage());
            }
            
        }
        res.close();
        pStmt.close();
        return molecules;

    }
        

}
