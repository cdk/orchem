package uk.ac.ebi.orchem.search;

import java.util.Map;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;

/**
 * Class to store user's query structures/molecules, well atom containers. 
 * User during substructure searching.
 */
class UserQueryMolecule {
    public IAtomContainer mol;
    public Map atomsAndBonds;    
}
