package uk.ac.ebi.orchem.bean;

import java.io.Serializable;

import java.util.BitSet;


/**
 * Bean used to store intermediate search results during the process of 
 * similarity searching. As the name says, the bean gets stored on the heap.
 * 
 * @author markr@ebi.ac.uk
 * 
 */
public class SimHeapElement implements Serializable{

    String ID;
    BitSet fingerprint;
    Float tanimotoCoeff;


    public void setID(String iD) {
        this.ID = iD;
    }

    public String getID() {
        return ID;
    }

    public void setFingerprint(BitSet fingerprint) {
        this.fingerprint = fingerprint;
    }

    public BitSet getFingerprint() {
        return fingerprint;
    }

    public void setTanimotoCoeff(Float tanimotoCoeff) {
        this.tanimotoCoeff = tanimotoCoeff;
    }

    public Float getTanimotoCoeff() {
        return tanimotoCoeff;
    }

    public boolean equals(Object o) {
        if ((o instanceof SimHeapElement) && (((SimHeapElement)o).ID.equals(this.ID))) {
            return true;
        } else {
            return false;
        }
    }

}


