/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;
import java.util.Comparator;
/**
 *
 * @author peterkong
 */
public class TermComparator implements Comparator<Term>{
    public int compare(Term t1, Term t2){
        String t1name = t1.getName();
        String t2name = t2.getName();
        int compare = t1name.compareTo(t2name);
        return compare;
    }
}//class
