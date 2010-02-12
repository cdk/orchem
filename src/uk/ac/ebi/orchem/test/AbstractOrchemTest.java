/*
 *  $Author$
 *  $Date$
 *  $$
 *
 *  Copyright (C) 2010  Mark Rijnbeek
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

import java.sql.DriverManager;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import oracle.jdbc.driver.OracleConnection;

import uk.ac.ebi.orchem.PropertyLoader;
import uk.ac.ebi.orchem.shared.DatabaseAccess;
import uk.ac.ebi.orchem.shared.WrappedAtomContainer;

/**
 * Parent class for Unit testing OrChem.
 */
public abstract class AbstractOrchemTest extends TestCase{

    DatabaseAccess dbApi = new DatabaseAccess();

    static OracleConnection conn;
    static List<WrappedAtomContainer> targetMolecules;
    /* connect and pull all the unittest compounds into a working list (for performance)*/
    static {
        try {
            System.out.println("___ static : Begin set up target list (once) ");
            Properties properties = PropertyLoader.getUnittestProperties();
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = (OracleConnection)DriverManager.getConnection(properties.getProperty("dbUrl"), properties.getProperty("dbUser"), properties.getProperty("dbPass"));
            targetMolecules = new DatabaseAccess().getAllFingerprintedCompounds(conn);
            System.out.println("Number of target molecules is "+targetMolecules.size());
            System.out.println("___ static : End set up target list");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
