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
package uk.ac.ebi.orchem.fingerprint.bitpos;

import org.openscience.cdk.interfaces.IBond;

/**
 * Bean type class to store details relevant to fingerpint neighbouring atoms.
 * These beans grouped together in a collection to form a pattern for fingerprinting.
 * 
 * @author markr@ebi.ac.uk
 *
 */
public class Neighbour {

    private String symbol;
    private IBond.Order bondOrder;
    private Boolean aromatic;

    public Neighbour(String _symbol, IBond.Order _bondOrder, Boolean _aromatic) {
        this.symbol = _symbol;
        this.bondOrder = _bondOrder;
        this.aromatic = _aromatic;
    }


    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setBondOrder(IBond.Order bondOrder) {
        this.bondOrder = bondOrder;
    }

    public IBond.Order getBondOrder() {
        return bondOrder;
    }

    public void setAromatic(Boolean aromatic) {
        this.aromatic = aromatic;
    }

    public Boolean getAromatic() {
        return aromatic;
    }

    public String toString() {
        return symbol + ":" + bondOrder + ":" + aromatic + " ";
    }

}
