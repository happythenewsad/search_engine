/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * extends VSMQP, adds additional int param for doc number cutoff per query
 * @author peterkong
 */
public class VSMQP_DocWiseTrim extends VSMQP {
    private final int docLimit;
    private int numberOfDocs;

    VSMQP_DocWiseTrim(String s, String t, String mapPath, int typ, int dl){
        super(s, t, mapPath, typ);
        docLimit = dl;
        numberOfDocs = 0;

    }

    @Override
     public void build(){
        long starttime = System.currentTimeMillis();

        for (int i = 0; i < queries.size(); i++){//for each query
            numberOfDocs = 0;
            //log("starting query #" + i);
            ArrayList<String> termNames = queries.get(i).getTerms();

            //populate an array with terms from array of term names "terms"
            ArrayList<Term> terms = new ArrayList<Term>();
            Term temp = new Term();
            for (int p = 0; p < termNames.size(); p++){
                terms.add(temp.readFromDisk(termNames.get(p)));
            }

            for (int j = 0; j < terms.size(); j++){
                if (numberOfDocs > docLimit) break;
                Term t = terms.get(j);
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
                        numberOfDocs++;
                        if (numberOfDocs > docLimit) break;
                    }//for
                }//else
            }//for
            Collections.sort(scores, new ScoreSorter());
            printToResultFile(i);
            scores.clear();
            //log("total number of docs for this query: " + numberOfDocs);
        }//for
        log("VSMQP_DocWiseTrim run time: " + ((System.currentTimeMillis())-starttime));
    }//process

}
