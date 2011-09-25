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
public class QuerySorter_DF implements Comparator<Term>{

    QuerySorter_DF(){
        //super();

    }

    public int compare(Term t1, Term t2){

        int df1 = getDF(t1);
        int df2 = getDF(t2);


        if (df1>df2){
                return 1;
        }
        else if (df1==df2){
            return 0;
        }
        else{
            return -1;
        }
    }

    private int getDF(Term t){
        return t.getTF();
    }

}