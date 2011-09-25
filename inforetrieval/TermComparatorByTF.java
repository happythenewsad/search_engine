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
public class TermComparatorByTF implements Comparator<Term>{
    public int compare(Term t1, Term t2){
        int t1TF = t1.getTF();
        int t2TF = t2.getTF();


        if (t1TF < t2TF){
            return 1;
        }
        else if (t1TF > t2TF){
            return -1;
        }
        else{
            return 0;
        }
    }

}
