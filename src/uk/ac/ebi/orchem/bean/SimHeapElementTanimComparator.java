package uk.ac.ebi.orchem.bean;


import java.util.Comparator;

/**
 * Comparator for comparing two {@link uk.ac.ebi.orchem.bean.SimHeapElement}objects
 *
 * @author markr@ebi.ac.uk
 */
public class SimHeapElementTanimComparator implements Comparator {

    public int compare(Object o1, Object o2) {

        if (o1 instanceof SimHeapElement && o2 instanceof SimHeapElement) {
            SimHeapElement b1 = (SimHeapElement)o1;
            SimHeapElement b2 = (SimHeapElement)o2;

            if (b1.getTanimotoCoeff() != null && b2.getTanimotoCoeff() != null)
                return b1.getTanimotoCoeff().compareTo(b2.getTanimotoCoeff());
            else
                return 0;
        } else
            return 0;
    }
}
