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
public class PosIndexer {
    private String input;
    private String stopTermFile;
    private StringBuilder prevLineBuffer;
    private int docNumber;
    private Hashtable hashtable;
    private long indexTime;

    PosIndexer(String inputFileName, String s) throws RuntimeException{
        input = inputFileName;
        stopTermFile = s;
        docNumber = 0;
        hashtable = new Hashtable();
    }//constructor

    public void Index(){
        indexTime = System.currentTimeMillis();
        try{
            //reads doc file into memory/////////////
            java.io.File inputFile = new java.io.File(input);
            StringBuilder inputAsSB = new StringBuilder();
            Scanner scanner = new Scanner(inputFile);

            while (scanner.hasNext()) {
                String buf = scanner.nextLine();
                buf.trim();
                inputAsSB.append(buf);
            }

            String inputAsString = inputAsSB.toString();

            makeLexicon(inputAsString);
            ////////////////////////////////////////



            //TEST INPUT FILE///////////////////////
            /*java.io.File outputFile = new java.io.File("/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/indexer_outputs/posOutput.txt");
            java.io.PrintWriter output = new java.io.PrintWriter(outputFile);
            output.print(inputAsString);
            output.close();*/
            //END TEST//////////////////////////////

        }//try

        catch (Exception e){
            System.out.println("PosIndexer: Exception: " + e);
            e.printStackTrace();
        }
        log("Pos Index Time: " + (System.currentTimeMillis()-indexTime));
    }//index

    //edited to include doc positions in PosIndexer
    public void printLexicon(){
        //can use TermComparator or TermComparatorByTF
        TermComparatorByTF comp = new TermComparatorByTF();
        //TermComparator comp = new TermComparator();
        Collection c = hashtable.values();
        List list = new ArrayList(c);
        Collections.sort(list, comp);
        ListIterator it = list.listIterator();

        ArrayList<Integer> intArray = new ArrayList<Integer>();
        int totalterms = 0;

        while (it.hasNext()){
            Term temp = (Term) it.next();
            LinkedList<Doc> postList = temp.getPostList();
            ListIterator pl = postList.listIterator();
            StringBuilder plstr = new StringBuilder();
            totalterms++;
            while(pl.hasNext()){
                Doc tempDoc = (Doc)pl.next();
                int df = tempDoc.getDF();
                intArray.add(df);
                int id = tempDoc.getDocID();
                ArrayList<Integer> positions = tempDoc.getPositions();
                plstr.append("{ id:" + id + ", df: " + df + "positions: "+ positions +"}");
            }//while


            //System.out.println("//" + temp.getName()+ " , " + temp.getTF() + plstr + "//");
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
                +low + " term count: " + totalterms);


    }//print

    //for PosIndexer, the override "add" method for Term is used to include positions
    private void makeLexicon(String inputAsString){
        int tokenCounter = 0;
       //!"#%&'()*+,-./:;<=>?@[\]^_`{|}~

        String[] tokenArray = inputAsString.split("[(\\s+)([!\"#%&\'*+,-.:;<=>?@^_`{|}~]+\\s+)]");
        //Vector<String> v = getStopTerms();


            for (int i = 0; i < tokenArray.length; i++){
                //System.out.println(">" + tokenArray[i] + "<");
                String currentToken = tokenArray[i];
                if (currentToken.contains("/docno")){
                    docNumber++;
                }

                    if (hashtable.containsKey(currentToken)){
                        Term temp = (Term) hashtable.get(currentToken);
                        temp.incrementTF();
                        temp.add(docNumber, i);
                    }
                    else{
                        Term temp = new Term(currentToken);
                        temp.incrementTF();
                        temp.add(docNumber, i);
                        hashtable.put(currentToken, temp);
                    }
                    tokenCounter++;

             }//for
             log("PosIndexer: token count is: " + tokenCounter);
             log("PosIndexer: Doc Number: " + docNumber);
    }//makeLexicon

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

    public void writeToFile(){
        try{

        }//try
        catch (Exception e){
            System.out.println("PosIndexer: error in writing to file");
        }
    }//write to file

    public void log (String s){
        System.out.println(s);
    }

    public void writeHashToDisk(){
        String writeFile = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/indexer_outputs/pos.dat";
        try {
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(writeFile, true));
            output.writeObject(this.hashtable);
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

    }//write
}//class