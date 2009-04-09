package uk.ac.ebi.orchem.test;

import org.openscience.cdk.interfaces.IAtomContainer;

/**
 * Bean wrapper around IAtomContainer to also store its database id.<BR>
 * Only used for Junit testing.
 */
class MyAtomContainer {
    private IAtomContainer atomContainer;
    private int dbId;

    MyAtomContainer(IAtomContainer a, int id) {
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
