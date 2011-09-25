/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.lang.String;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;


/**
 *
 * @author peterkong
 */
public class VSMQP {
    protected ArrayList<Score> scores;
    protected DocMapper docmapper;
    protected ArrayList<Query> queries;

    private String queryPath;
    private String stopTermPath;       
    private int type;
    
    VSMQP(String s, String t, String mapPath, int typ){
        queryPath = s;
        stopTermPath = t;
        scores = new ArrayList<Score>();
        type = typ;
        if (!(type == 1 || type ==2)){
            try {
                throw new Exception();
            }
            catch (Exception ex) {
                log("Invalid parameter");
                ex.printStackTrace();
            }
        }//if

        docmapper = new DocMapper(mapPath);
        docmapper.init();
        //docmapper.print();
        
        QueryBuilder qb = new QueryBuilder(s, t);
        qb.build();
        //qb.print();
        queries = qb.getQueries();
    }

    public void build(){
        long starttime = System.currentTimeMillis();
        for (int i = 0; i < queries.size(); i++){//for each query
            //log("starting query #" + i);
            ArrayList<String> terms = queries.get(i).getTerms();
            for (int j = 0; j < terms.size(); j++){//for each term in query
                Term temp = new Term();
                Term t = temp.readFromDisk(terms.get(j));
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
        log("VSMQP run time: " + ((System.currentTimeMillis())-starttime));
    }//process

    protected void printToResultFile(int j){
        
        Query q = queries.get(j);
        int queryNumber = q.getNum();
        //log("print result file for query# " + queryNumber + " with " + scores.size() + "scores");
        DecimalFormat twoDForm = new DecimalFormat("#.#####");
        try{
                java.io.File outputFile = new java.io.File("/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/main/results/VSMresult.txt");
                java.io.PrintWriter output = new java.io.PrintWriter(new java.io.FileWriter(outputFile, true));
                for (int i = 0; i < scores.size(); i++){
                    String str = scores.get(i).getName().toUpperCase();
                    output.print(queryNumber + " 0 " + str + " " + (i+1) +
                            " " + Double.valueOf(twoDForm.format(scores.get(i).getScore())) + " vsm" + "\n");
                }//
                output.close();

            }//try
            catch (Exception e){
                e.printStackTrace();
            }
    }

    protected void addToScores(Score s){
        boolean docAlreadyHere = false;

        ListIterator it = scores.listIterator();
        while(it.hasNext()){
            Score temp = (Score)it.next();
            if (temp.getName().compareTo(s.getName()) == 0){
                docAlreadyHere = true;
                temp.updateScore(temp.getScore() + s.getScore());
            }
        }//while

        if (!docAlreadyHere){
            scores.add(s);
        }
    }

    public void printScores(){
        for (int i = 0; i < scores.size(); i++){
            scores.get(i).print();
        }
    }
    
    protected double DotProductScore(int df, int QTF){
        int n = docmapper.size();//total number of documents in collection
        return (((df)) * (Math.log10(n/QTF)) * (Math.log10(n/QTF)));
    }

    protected double CosineScore(int df, int QTF, String docname){
        if (QTF == 0){
            return 0;
        }
        int n = docmapper.size();//total number of documents in collection
        WholeDoc wd = new WholeDoc();
        WholeDoc temp = wd.readFromDisk(docname);        
        int length = ((temp.getTermWiseLength()/10)+1);
        return ((((df)) * (Math.log10(n/QTF)) * (Math.log10(n/QTF)))/length);
    }

    public void log(String s){
        System.out.println(s);
    }

}//class
