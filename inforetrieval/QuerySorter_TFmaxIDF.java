/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *
 * @author peterkong
 */
public class QuerySorter_TFmaxIDF implements Comparator<Term>{
    private int numberOfDocs;

    QuerySorter_TFmaxIDF(int n){
        //super();
        numberOfDocs = n;
    }

    public int compare(Term t1, Term t2){

        int tfmax1 = getTFMax(t1);
        int tfmax2 = getTFMax(t2);

        double idf1;
        double idf2;

        //check for division by zero
        if (t1.PLSize() == 0){
            idf1 = 0;
        }
        else{
            idf1 = Math.log10(numberOfDocs/t1.PLSize());
        }

        if (t2.PLSize() == 0){
            idf2 = 0;
        }
        else{
            idf2 = Math.log10(numberOfDocs/t2.PLSize());
        }





        Double score1 = tfmax1 * idf1;
        Double score2 = tfmax2 * idf2;
        if (score1>score2){
                return 1;
        }
        else if (score1==score2){
            return 0;
        }
        else{
            return -1;
        }
    }

    private int getTFMax(Term t){
        int max = 0;
        LinkedList<Doc> postlist = t.getPostList();
        for (int k = 0; k < postlist.size(); k++){
            Doc d = postlist.get(k);
            if (d.getDF() > max){
                max = d.getDF();
            }

         }//for
        return max;
    }

}