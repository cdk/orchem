/*  
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2008-2009  Mark Rijnbeek
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


