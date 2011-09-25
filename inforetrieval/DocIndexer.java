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
public class DocIndexer {
    private String dir;
    private String stopTermFile;
    private int docNumber;
    private Hashtable hashtable;
    //private ArrayList<String> termList;//keeps term names only, used in mergeTempFiles()
    private long indexTime;
    private double avgLength;


    public DocIndexer(String inputFileName, String s, long m) throws RuntimeException{
        dir = inputFileName;
        stopTermFile = s;
        docNumber = 0;
        //termList = new ArrayList<String>();
        hashtable = new Hashtable();
        avgLength = 0;

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

        log("Doc Indexer Index Time: " + (System.currentTimeMillis()-indexTime));

        flushPL();

    }//index


    private void makeLexicon(String path){
        java.io.File inputFile = new java.io.File(path);
        StringBuilder buffer = new StringBuilder();
        try {
            Scanner scanner = new Scanner(inputFile);
            while (scanner.hasNext()){
                buffer.append(scanner.next());
            }
            scanner.close();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        String temp = "";

        Pattern p = Pattern.compile("<docno>.{0,30}</docno>");//be sure to switch cases...
        Matcher m = p.matcher(buffer);
        Matcher n = p.matcher(buffer);
        int start = 0;
        int end = 0;

        while (m.find()){
            temp = (String) buffer.subSequence(m.start()+7, m.end()-8);
            WholeDoc wd = new WholeDoc(temp);
            
            start = m.end();
            if (n.find(start)){
                end = n.start();
            }
            else{
                break;
            }
            
            int length = buffer.subSequence(start, end).toString().length()/10;
            wd.setLength(length);
            hashtable.put(wd.getName(), wd);

            //print to disk
        }

    }//makeLexicon

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
            WholeDoc temp = (WholeDoc)it.next();
            avgLength += temp.getTermWiseLength();
            temp.writeToDisk();
        }
        avgLength = avgLength / hashtable.size();
        log("Avg doc length: " + avgLength);

    }//flushPL
}