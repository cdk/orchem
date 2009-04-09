package uk.ac.ebi.orchem.bean;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.Iterator;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;


/**
 * Hold the basic molecule data of a CDK molecule - the atoms and bonds.<BR><BR>
 * Raison d'être: OrChem likes to serialize/deserialize compounds instead of creating
 * them on the fly from say a mol-file, because serialization works a bit faster.<BR>
 * BasicMoleculeData gets serialized into the database instead of directly serializing CDK Molecules.
 * This makes the serialization safer, because minor changes in the CDK class hierarchy would
 * invalidate the serialized molecules. Deserialization errors would mean that OrChem's
 * compound table would have to be rebuilt from scratch.<BR><BR>
 * The BasicMoleculeData is not a full blown molecule (certain Molecule attributes are
 * not represented) but it is good enough for OrChem's fingerprinting and isomorphism
 * needs.<BR><BR>
 * Use case:
 * <UL>
 *   <LI>given a Molecule "m"</LI>
 *   <LI>create a BasicMoleculeData object "bmd" passing "m" into its constructor</LI>
 *   <LI>serialize "bmd"</LI>
 *   <LI></LI>
 *   <LI>deserialize "bmd"</LI>
 *   <LI>create a (partly populated) basic Molecule "m2" using {@link #getBasicMolecule }</LI>
 *   <LI>use "m2" for fingerprinting or isomorphism checks, or pole dancing
 * </UL>
 */
public class BasicMoleculeData implements Serializable {

    private int atomCount;
    private int bondCount;
    private IAtom[] atoms;
    private IBond[] bonds;

    /**
     * Constructor, populates basic molecule data from input molecule
     * @param inputMolecule
     */
    public BasicMoleculeData(Molecule inputMolecule) {
        /* Set attributes from input molecule */
        this.atomCount = inputMolecule.getAtomCount();
        this.bondCount = inputMolecule.getBondCount();

        atoms = new IAtom[inputMolecule.getAtomCount()];
        int atomIndex = 0;
        for (Iterator<IAtom> iterator = inputMolecule.atoms().iterator(); iterator.hasNext(); )
            atoms[atomIndex++] = iterator.next();

        bonds = new IBond[inputMolecule.getBondCount()];
        int bondIndex = 0;
        for (Iterator<IBond> iterator = inputMolecule.bonds().iterator(); iterator.hasNext(); )
            bonds[bondIndex++] = iterator.next();
    }

    /**
     * Return a CDK molecule data with basic data
     * @return CDK Molecule, with atoms and bonds set only
     */
    public Molecule getBasicMolecule() {
        Molecule outputMolecule = new Molecule(atomCount, bondCount, 0, 0);
        outputMolecule.setAtoms(atoms);
        outputMolecule.setBonds(bonds);
        return outputMolecule;
    }

    /**
     * Deserialization
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        //System.out.println("de-serializing");
        atomCount = in.readInt();
        bondCount = in.readInt();
        atoms = (IAtom[])in.readObject();
        bonds = (IBond[])in.readObject();
    }

    /**
     * Serialization
     * @param out
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        //System.out.println("serializing");
        out.writeInt(atomCount);
        out.writeInt(bondCount);
        out.writeObject(atoms);
        out.writeObject(bonds);
    }
}
