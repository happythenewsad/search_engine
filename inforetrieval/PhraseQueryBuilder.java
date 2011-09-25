/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author peterkong
 */
public class PhraseQueryBuilder {
    private String path;
    private String stopTermPath;
    private ArrayList<Query> queries;

    PhraseQueryBuilder(String s, String t){
        path = s;
        stopTermPath = t;
        queries = new ArrayList<Query>();
    }


    public void build(){

        StringBuilder buffer = new StringBuilder();
        int ch = 0;
        try {
            FileReader input = new FileReader(path);
            while (ch != -1){
                ch = input.read();
                char c = (char)ch;
                buffer.append(c);
            }//while
            input.close();
        }

        catch (FileNotFoundException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
        catch (IOException ex){
            System.out.println(ex);
            ex.printStackTrace();
        }

        Vector<String> v = getStopTerms();

        String stringBuffer = buffer.toString();
        stringBuffer = stringBuffer.replaceAll("[-\\(\\)//]", " ");
        buffer = new StringBuilder(stringBuffer.toLowerCase());

        int pos = 0;
        int start = 0;
        int end = 0;
        String temp;

        Pattern num = Pattern.compile("<num>");
        Pattern title = Pattern.compile("<title>");//<title>[.\\s]*<
        Pattern anyNumber = Pattern.compile("\\d{1,5}");
        Pattern newTag = Pattern.compile("<");

        Matcher numMatcher = num.matcher(buffer);
        Matcher titleMatcher = title.matcher(buffer);
        Matcher anyNumberMatcher = anyNumber.matcher(buffer);
        Matcher newTagMatcher = newTag.matcher(buffer);

        while (numMatcher.find()){
            anyNumberMatcher.find(numMatcher.end());
            start = anyNumberMatcher.start();
            end = anyNumberMatcher.end();
            temp = buffer.substring(start,end);

            //log("this is temp>" + temp + "<");
            Query q = new Query(Integer.parseInt(temp));

            titleMatcher.find(end);
            newTagMatcher.find(titleMatcher.end() +1);

            temp = buffer.substring(titleMatcher.end() +1, newTagMatcher.start());
            //log(">"+temp+"<");
            String[] tokens = temp.split("(.*:\\s+)|(\\s+)");
            for (int i = 0; i < tokens.length; i++){
                if (i >= 2){
                    String buf1 = tokens[i-2];
                    String buf2 = tokens[i-1];
                    boolean buf1IsStop = v.contains(buf1);
                    boolean buf2IsStop = v.contains(buf2);
                    boolean currentTokenIsStop = v.contains(tokens[i]);

                    if ((!buf1IsStop) && (!buf2IsStop) && (!currentTokenIsStop)){
                        if ((tokens[i].compareTo("") == 0)){}
                        else{
                            String s = buf1 + " " + buf2 + " " + tokens[i];
                            q.addTerm(s.trim());
                        }
                    }
                    if ((!buf1IsStop) && (!buf2IsStop)){
                        if ((tokens[i].compareTo("") == 0)){}
                        else{
                            String s = buf1 + " " + buf2;
                            q.addTerm(s.trim());
                        }
                    }

                }
            }//for


            //q.print();
            queries.add(q);
        }
    }//build

    private Vector<String> getStopTerms(){
        java.io.File inputFile = new java.io.File(stopTermPath);
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

    public ArrayList<Query> getQueries(){
        return queries;
    }

    public void print(){
        for (int i = 0; i < queries.size(); i++){
            queries.get(i).print();
        }
    }

    public void log(String s){
        System.out.println(s);
    }
}//class