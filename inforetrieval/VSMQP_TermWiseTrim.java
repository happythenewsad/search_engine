/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
/**
 *
 * @author peterkong
 */
public class VSMQP_TermWiseTrim extends VSMQP {
    private int stopAt;// % of query terms processed
    private final int DF = 1;
    private final int TFIDF = 2;
    private int sortMethod;


    VSMQP_TermWiseTrim (String s, String t, String mapPath, int typ, int tl){
        super(s, t, mapPath, typ);
        stopAt = tl;
        sortMethod = DF; //defaults to DF sorting
    }

    @Override
     public void build(){
        long starttime = System.currentTimeMillis();

        for (int i = 0; i < queries.size(); i++){//for each query
            //log("starting query #" + i);
            ArrayList<String> termNames = queries.get(i).getTerms();

            //populate an array with terms from array of term names "terms"
            ArrayList<Term> terms = new ArrayList<Term>();
            Term temp = new Term();
            for (int p = 0; p < termNames.size(); p++){
                terms.add(temp.readFromDisk(termNames.get(p)));
            }

            //TESTER LOOP
            /*for (int q = 0; q < terms.size(); q++){
                if (terms.get(q).PLSize() == 0)
                    System.out.println("unsorted score: zero");
                else
                    System.out.println("unsorted tfidf score: " + Math.log10(docmapper.size()/terms.get(q).PLSize() * getTFMax(terms.get(q))));
            }*/

            if (sortMethod == TFIDF){
                Collections.sort(terms, new QuerySorter_TFmaxIDF(docmapper.size()));
            }
            else if (sortMethod == DF){
                //log("df sort method called");
                Collections.sort(terms, new QuerySorter_DF());
            }
            else{
                System.exit(1);
            }
            //TESTER LOOP
            /*for (int q = 0; q < terms.size(); q++){
                if (terms.get(q).PLSize() == 0)
                    System.out.println("sorted score: zero");
                else
                    System.out.println("sorted tfidf score: " + Math.log10(docmapper.size()/terms.get(q).PLSize() * getTFMax(terms.get(q))) +
                            "df score: " + terms.get(q).getTF());
            }*/
            //log("integer returned from computeThreshold: " + computeThreshold(terms.size()));
            for (int j = 0; j < computeThreshold(terms.size()); j++){//this line modified for early termination
                
                
                Term t = terms.get(j);
                //log("Now at: " + t.getName());
                if (t.getName().compareTo("") == 0){//Term not in index
                    //log("Did not find term in temp folder");
                }
                else{
                    LinkedList<Doc> postlist = t.getPostList();

                    for (int k = 0; k < postlist.size(); k++){//for each doc in term in query
                        Doc d = postlist.get(k);
                         //Score s = new Score(docmapper.toStr(d.getDocID()), DotProductScore(d.getDF(), t.PLSize()));
                         Score s = new Score(docmapper.toStr(d.getDocID()), CosineScore(d.getDF(), t.PLSize(), docmapper.toStr(d.getDocID())));
                        addToScores(s);
                    }//for
                }//else
            }//for
            Collections.sort(scores, new ScoreSorter());
            printToResultFile(i);
            scores.clear();
        }//for
        log("VSMQP_TermWiseTrim run time: " + ((System.currentTimeMillis())-starttime));
    }//process

    private int computeThreshold(int sz){
        if (sz ==1){
            return 1;
        }
        else{
           return (Math.round(((float)stopAt/(float)100)*sz));
        }
    }

    public void sortByDF(){
        sortMethod = DF;
    }

    public void sortByTFIDF(){
        sortMethod = TFIDF;
    }

    //TESTER METHOD
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
}//class
