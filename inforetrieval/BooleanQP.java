/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author peterkong
 */
public class BooleanQP {
    private String queryPath;
    private String stopTermPath;
    private ArrayList<Query> queries;
    private boolean conjunctive;
    private DocMapper docmapper;

    BooleanQP(String s, String t, String mapPath, boolean c){
        conjunctive = c;
        queryPath = s;
        stopTermPath = t;

        docmapper = new DocMapper(mapPath);
        docmapper.init();

        QueryBuilder qb = new QueryBuilder(s, t);
        qb.build();
        queries = qb.getQueries();

    }

    public void build(){
            for (int i = 0; i < queries.size(); i++){//for each query
                log("starting query #" + i);
                ArrayList<String> terms = queries.get(i).getTerms();

                if (conjunctive){
                   log("conjunctive");
                   Term temp = new Term();
                   Term start = temp.readFromDisk(terms.get(0));//first iteration

                    for (int j = 0; j < terms.size(); j++){
                        Term t = temp.readFromDisk(terms.get(j));

                        if (t.getName().compareTo("") == 0){//Term not in index
                            log("Did not find term in temp folder");
                        }
                        else{
                            start = conjunct(start,t)  ;
                        }
                    }//for
                    LinkedList<Doc> list = start.getPostList();
                    printToResultFile(list, queries.get(i).getNum());
                }
                else{//disjunctive
                    Term temp = new Term();

                    for (int j = 0; j < terms.size(); j++){
                    Term t = temp.readFromDisk(terms.get(j));

                    if (t.getName().compareTo("") == 0){//Term not in index
                        log("Did not find term in temp folder");
                    }
                    else{
                            temp.setName(t.getName());//work around for merging different PLs
                            temp.merge(t);
                    }
                    log("status of result: ");
                    temp.print();
                    }//for
                    LinkedList<Doc> list = temp.getPostList();
                    printToResultFile(list, queries.get(i).getNum());
                }//else
                
            }//for



    }//build

    private void printToResultFile(LinkedList<Doc> list, int m){
        log("result file: " + list.size() + "matches");
        int queryNumber = m;
        try{
                java.io.File outputFile = new java.io.File("/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/main/results/booleanResult.txt");
                java.io.PrintWriter output = new java.io.PrintWriter(new java.io.FileWriter(outputFile, true));
                for (int i = 0; i < list.size(); i++){//for each doc
                    output.print(queryNumber + " 0 " + docmapper.toStr(list.get(i).getDocID()) + " " + (i) +
                            " " + 5.00 + " bool" + "\n");
                }
                output.close();

            }//try
            catch (Exception e){
                e.printStackTrace();
            }
    }

    public Term conjunct(Term a, Term b){
        LinkedList<Doc> aPL = a.getPostList();
        LinkedList<Doc> bPL = b.getPostList();
        Term result = new Term(a.getName());
        for (int i = 0; i < aPL.size(); i++){
            for (int j = 0; j < bPL.size(); j++){
                if (aPL.get(i).getDocID() == bPL.get(j).getDocID()){
                    log(aPL.get(i).getDocID() + "added to result term");
                    result.add(aPL.get(i).getDocID());
                }
            }//for
        }//for
        return result;
    }

    public void log(String s){
        System.out.println(s);
    }
}//class
