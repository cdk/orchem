package uk.ac.ebi.orchem.bean;

import java.util.Comparator;

/**
 * Comparator for comparing two {@link uk.ac.ebi.orchem.bean.OrChemCompound}objects when producing
 * a list of search results (high score first)
 *
 * @author markr@ebi.ac.uk
 */
public class OrChemCompoundTanimComparator implements Comparator {

    public int compare(Object o1, Object o2) {

        if (o1 instanceof OrChemCompound && o2 instanceof OrChemCompound) {
            OrChemCompound b1 = (OrChemCompound)o1;
            OrChemCompound b2 = (OrChemCompound)o2;
            return new Float((b2.getScore())).compareTo(new Float((b1.getScore())));
        } else
            return 0;

    }
}
