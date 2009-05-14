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

package uk.ac.ebi.orchem.shared;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Bean wrapper around IAtomContainer to also store its database id.<BR>
 * Only used for Junit testing.
 */
public class WrappedAtomContainer {
    private IAtomContainer atomContainer;
    private int dbId;

    WrappedAtomContainer(IAtomContainer a, int id) {
        this.atomContainer = a;
        this.dbId = id;
    }

    public void setAtomContainer(IAtomContainer atomContainer) {
        this.atomContainer = atomContainer;
    }

    public IAtomContainer getAtomContainer() {
        return atomContainer;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

    public int getDbId() {
        return dbId;
    }
}
