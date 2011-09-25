/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class PhraseIndexer {
    private String dir;
    private String stopTermFile;
    private int docNumber;
    private Hashtable hashtable;
    //private ArrayList<String> termList;//keeps term names only, used in mergeTempFiles()
    private long indexTime;
    private long maxTermCount;//
    private String buf1;
    private String buf2;
    private String currentToken;


    public PhraseIndexer(String inputFileName, String s, long m) throws RuntimeException{
        dir = inputFileName;
        stopTermFile = s;
        docNumber = 0;
        maxTermCount = m;
        //termList = new ArrayList<String>();
        hashtable = new Hashtable();
        buf1 = "";
        buf2 = "";
        currentToken = "";

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

        log("phrase Term Index Time: " + (System.currentTimeMillis()-indexTime));

        flushPL();

    }//index


    private void makeLexicon(String path){
        int tokenCounter = 0;
        Vector<String> v = getStopTerms();
        try{
            java.io.File inputFile = new java.io.File(path);
            Scanner scanner = new Scanner(inputFile);
            boolean buf1IsStop;
            boolean buf2IsStop;
            boolean currentTokenIsStop;
            String check = "";

            while (scanner.hasNext()) {

                buf1 = buf2;
                buf2 = currentToken;
                currentToken = scanner.next();

                buf1IsStop = v.contains(buf1);
                buf2IsStop = v.contains(buf2);
                currentTokenIsStop = v.contains(currentToken);

                if (currentToken.contains("/docno")){
                    docNumber++;
                }
                if ((!buf1IsStop) && (!buf2IsStop) && (!currentTokenIsStop)){
                    String s = buf1 + " " + buf2 + " " + currentToken;
                    //log("phrase to add: " + s);
                    addPhrase(s);
                    tokenCounter++;
                }
                if ((!buf1IsStop) && (!buf2IsStop)){
                    String s = buf1 + " " + buf2;
                    //log("phrase to add: " + s);
                    addPhrase(s);
                    tokenCounter++;
                }

            }//while
        }//try
        catch (Exception e){
            System.out.println("phrase Indexer: makeLexicon: Exception: " + e);
            e.printStackTrace();
        }

    }//makeLexicon

    private void addPhrase(String s){
        if (!isBadTerm(s)){//this if statement excludes stop terms
                    if (hashtable.containsKey(s)){
                        Term temp = (Term) hashtable.get(s);
                        temp.incrementTF();
                        temp.add(docNumber);

                    }
                    else{
                        Term temp = new Term(s);
                        temp.incrementTF();
                        temp.add(docNumber);
                        hashtable.put(s, temp);
                    }
                }//if contains
    }

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
        //log("START FLUSH");
        Collection c = hashtable.values();
        List list = new ArrayList(c);
        ListIterator it = list.listIterator();
        int counter = 0;
        while(it.hasNext()){
            //if (counter % 100 == 0) log ("in loop");
            Term tempTerm = (Term)it.next();

            tempTerm.writeToDisk();
            tempTerm.clearPL();
            counter++;
        }
        //log("END FLUSH");
    }//flushPL


}//class

