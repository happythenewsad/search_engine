/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author peterkong
 */
public class DocMapper {
    private String dir;
    private ArrayList<DocMap> docmaps;
    private int docNumber;

    public DocMapper(String s){
        dir = s;
        docmaps = new ArrayList<DocMap>();
        docNumber = 0;

    }

    public void init(){
        java.io.File parentDir = new java.io.File(dir);
        File[] list = parentDir.listFiles();
        for (int i = 0; i < list.length; i++){
            //log("in docmapper for loop &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + list[i].getAbsolutePath());
            map(list[i].getAbsolutePath());            
        }

    }

    public void initSingleFile(){
        map(dir);

    }

    public int size(){
        return docmaps.size();
    }

    private void map(String path){
        int currentNumber = docNumber;
        //log("STARTING " + path + "current doc count: " + docNumber);
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

        //int start = 0;
        //int end = 0;
        String temp = "";

        Pattern p = Pattern.compile("<docno>.{0,30}</docno>");//be sure to switch cases...
        Matcher m = p.matcher(buffer);
        while (m.find()){

            //start = m.start();
            //end = m.end();
            temp = (String) buffer.subSequence(m.start()+7, m.end()-8);
            docmaps.add(new DocMap(docNumber, temp));
            docNumber++;
        }
        log("THIS DOC CONTAINED " + (docNumber-currentNumber));

    }//map

    public String toStr(int i){
        return docmaps.get(i-1).getName();//note, incremental doc no's and actual names are offset by one
    }

    //Test method
    public void print(){
        for (int i = 0; i < docmaps.size(); i++){
            DocMap temp = docmaps.get(i);
            temp.print();
        }
    }

    public ArrayList<DocMap> getDocMaps(){
        return docmaps;
    }

    private void log(String s){
        System.out.println(s);
    }


}//class
