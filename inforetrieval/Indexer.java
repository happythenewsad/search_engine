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
public class Indexer {
    private String dir;
    private String stopTermFile;
    private int docNumber;
    private Hashtable hashtable;
    //private ArrayList<String> termList;//keeps term names only, used in mergeTempFiles()
    private long indexTime; 
    private long maxTermCount;//
    
    
    public Indexer(String inputFileName, String s, long m) throws RuntimeException{
        dir = inputFileName;
        stopTermFile = s;
        docNumber = 0;
        maxTermCount = m;  
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

    //WARNING: cannot print lexicon if operating under memory constraint
    public void printLexicon(){
        //can use TermComparator or TermComparatorByTF
        //TermComparatorByTF comp = new TermComparatorByTF();
        TermComparator comp = new TermComparator();
        Collection c = hashtable.values();
        List list = new ArrayList(c);
        Collections.sort(list, comp);
        ListIterator it = list.listIterator();

        ArrayList<Integer> intArray = new ArrayList<Integer>();
        int totalterms = 0;


        while (it.hasNext()){
            totalterms++;
            Term temp = (Term) it.next();
            LinkedList<Doc> postList = temp.getPostList();
            ListIterator pl = postList.listIterator();
            StringBuilder plstr = new StringBuilder();
            while(pl.hasNext()){
                Doc tempDoc = (Doc)pl.next();
                int df = tempDoc.getDF();
                intArray.add(df);
                
                int id = tempDoc.getDocID();
                plstr.append("{ id:" + id + ", df:" + df + "}");
            }//while


            System.out.println("//" + temp.getName()+ " , " + temp.getTF() + plstr + "//");
        }//while
        

        int sumDF = 0;
        for (int i = 0; i < intArray.size(); i++){
            sumDF+= intArray.get(i);
            
        }
        int size = intArray.size();
        int mean = (sumDF / size);
        int median = (intArray.get(size/2));
        int high = intArray.get(0);
        int low = intArray.get(size-1);
        
        
        log("Statistics: mean: " + mean + " median: " + median + " high: " + high + " low: "
                +low + "term count: " + totalterms);

    }//print

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
                
                if (!v.contains(currentToken) && !isBadTerm(currentToken)){//this if statement excludes stop terms
                    if (hashtable.containsKey(currentToken)){
                        //log ("if hashtable contains key");
                        Term temp = (Term) hashtable.get(currentToken);
                        temp.incrementTF();
                        temp.add(docNumber);
                        /*if (temp.getTF() >= maxTermCount){
                            //log(temp.getName() + " GREATER THAN MAX TERM COUNT");
                            temp.writeToDisk();
                            //temp.clearPL();
                            hashtable.remove(currentToken);
                            hashtable.put(currentToken, new Term(currentToken));
                            //log("reduced tf:" + temp.getTF());
                        }*/
                        
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
            System.out.println("Indexer: makeLexicon: Exception: " + e);
            e.printStackTrace();
        }

             //flushPL();//flushes residual posting lists still in memory to disk
             log("Indexer: token count is: " + tokenCounter);
             log("Indexer: Doc Count: " + docNumber);
             
    }//makeLexicon

    private boolean isBadTerm(String str){
        if (str.contains("/") || str.length() > 50){
            return true;
        }
        else{
            return false;
        }
    }
    
    /*private void writeTermToMasterFile(Term t){
        String writeFile = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/indexer_master_files/master.dat";
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(writeFile, true));
            output.writeObject(t);
        }
        catch (FileNotFoundException ex) {
            log("Indexer: " + ex);
            ex.printStackTrace();
        }
        catch (IOException ex){
            log("Indexer: " + ex);
            ex.printStackTrace();
        }
        catch(Exception ex){
            log("Indexer: " + ex);
            ex.printStackTrace();
        }

    }*///master

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
        while(it.hasNext()){
            Term tempTerm = (Term)it.next();
            tempTerm.writeToDisk();
            tempTerm.clearPL();
        }
        //log("END FLUSH");
    }//flushPL


    //flushes lexicon as hashtable to disk (one file)

    /*public void flushHashtable(){
        Collection c = hashtable.values();
        List list = new ArrayList(c);
        ListIterator it = list.listIterator();
        while(it.hasNext()){
            Term tempTerm = (Term)it.next();
            tempTerm.clearPL();
        }
        writeHashToDisk();
    }*/

    /*private void writeHashToDisk(){
        String path = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/lexicon/lexicon.dat";
        try{
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(path));
            output.writeObject(this.hashtable);

        }
        catch (FileNotFoundException ex) {
            System.out.print(ex);
            ex.printStackTrace();
        }
        catch (IOException ex){
            System.out.print(ex);
            ex.printStackTrace();
        }
        catch(Exception ex){
            System.out.print(ex);
            ex.printStackTrace();
        }
    }*/





}//class
