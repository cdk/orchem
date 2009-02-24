package uk.ac.ebi.orchem.fingerprint.bitpos;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Bean type class to store details relevant to fingerpint neighbouring atoms.
 * These beans can be put in collections of patterns interesting for fingerprinting.
 * 
 */
public class Neighbour {

    private String symbol;
    private IBond.Order bondOrder;
    private Boolean aromatic;

        public Neighbour (String _symbol, IBond.Order _bondOrder, Boolean _aromatic) {
            this.symbol=_symbol;
            this.bondOrder=_bondOrder;
            this.aromatic=_aromatic;
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
    
    public String toString () {
        return symbol+":"+bondOrder+":"+aromatic+" ";        
    }
}
