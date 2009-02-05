package uk.ac.ebi.orchem.fingerprint;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

class Neighbour {

        public String symbol;
        public IBond.Order bondOrder;
        public Boolean aromatic;

        Neighbour (String _symbol, IBond.Order _bondOrder, Boolean _aromatic) {
            this.symbol=_symbol;
            this.bondOrder=_bondOrder;
            this.aromatic=_aromatic;
        }
    }
