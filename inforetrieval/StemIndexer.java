/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;
import java.util.regex.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.util.*;
import java.io.*;
import java.util.Collections;
/**
 *
 * @author peterkong
 */
public class StemIndexer {
    private String dir;
    private String stopTermFile;
    private int docNumber;
    private Hashtable hashtable;
    //private ArrayList<String> termList;//keeps term names only, used in mergeTempFiles()
    private long indexTime;



    public StemIndexer(String inputFileName, String s) throws RuntimeException{
        dir = inputFileName;
        stopTermFile = s;
        docNumber = 0;
        //termList = new ArrayList<String>();
        hashtable = new Hashtable();

    }//constructor

    public void index(){
        indexTime = System.currentTimeMillis();
        java.io.File parentDir = new java.io.File(dir);
        File[] list = parentDir.listFiles();
        for (int i = 0; i < list.length; i++){
            System.out.println(list[i].getAbsolutePath());
            if(list[i].getAbsolutePath().contains("DS_Store")){
                //System.out.print("oh no!");
            }
            else{
                makeLexicon(list[i].getAbsolutePath());
            }
        }

        log("Single Term Index Time: " + (System.currentTimeMillis()-indexTime));

        flushPL();
        //flushHashtable();

    }//index



    private void makeLexicon(String path){
        int tokenCounter = 0;
        Vector<String> v = getStopTerms();
        try{
            java.io.File inputFile = new java.io.File(path);
            Scanner scanner = new Scanner(inputFile);

            while (scanner.hasNext()) {
                String currentToken = scanner.next();
                //System.out.println(">" + currentToken + "<");
                if (currentToken.contains("/docno")){
                    docNumber++;
                }


                currentToken = porterStemmer(currentToken);


                if (!v.contains(currentToken) && !isBadTerm(currentToken)){//this if statement excludes stop terms
                    if (hashtable.containsKey(currentToken)){
                        //log ("if hashtable contains key");
                        Term temp = (Term) hashtable.get(currentToken);
                        temp.incrementTF();
                        temp.add(docNumber);
                    }
                    else{
                        Term temp = new Term(currentToken);
                        temp.incrementTF();
                        //log("New term: " + currentToken);
                        temp.add(docNumber);
                        hashtable.put(currentToken, temp);
                        //termList.add(currentToken);//THIS IS FOR mergeTempFiles()

                    }
                    tokenCounter++;

                }//if contains
                else{
                    //log("ALERT###############, stop word " + currentToken + "FOUND!");
                    //this line tests the stop words
                }

            }//while
        }//try
        catch (Exception e){
            System.out.println("stem Indexer: makeLexicon: Exception: " + e);
            e.printStackTrace();
        }

             //flushPL();//flushes residual posting lists still in memory to disk


    }//makeLexicon

      private String porterStemmer(String str){
      char[] word = str.toCharArray();
      Stemmer s = new Stemmer();
      s.add(word, str.length());
      s.stem();
      //char[] stemmedWord = s.getResultBuffer();
      return s.toString();
    }//porter stemmer

    private boolean isBadTerm(String str){
        if (str.contains("/") || str.length() > 50){
            return true;
        }
        else{
            return false;
        }
    }

    private Vector<String> getStopTerms(){
        java.io.File inputFile = new java.io.File(stopTermFile);
        Scanner stopTermScanner;
        Vector<String> v = new Vector<String>();
        int i = 0;
        try {
            stopTermScanner = new Scanner(inputFile);
            while (stopTermScanner.hasNext()){
                String temp = stopTermScanner.next();
                v.add(i,temp);
                i++;
            }//while
        }//try
        catch (FileNotFoundException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }

        return v;
    }//get stop terms

    public void log (String s){
        System.out.println(s);
    }

    //flushes residual posting lists still in memory to disk
    public void flushPL(){
        Collection c = hashtable.values();
        List list = new ArrayList(c);
        ListIterator it = list.listIterator();
        while(it.hasNext()){
            Term tempTerm = (Term)it.next();
            tempTerm.writeToDisk();
            tempTerm.clearPL();
        }
    }//flushPL


}//class

