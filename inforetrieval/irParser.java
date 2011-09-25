/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

/**
 *
 * @author peterkong
 */

import java.io.File;
import java.util.Scanner;
import java.lang.StringBuilder;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


//parser trims all elements that are not docno or text. it also tracks number of docs in file
public class irParser {
    //StringBuilder docText;
    StringBuilder buffer; //primary buffer for the input file
    String strBuf; //used when methods are required in the String class and not in StringBuilder
    boolean insideTextTag = false;
    boolean insideDocTag = false;
    boolean insideCommentTag = false;
    int docCounter = 0;
    int lineNumber = 0;
    int pos = 0;

    
    public irParser(String dir) throws RuntimeException {
        java.io.File parentDir = new java.io.File(dir);
        File[] list = parentDir.listFiles();

        //docText = new StringBuilder();
        buffer = new StringBuilder(); //primary buffer for the input file
        strBuf = new String();
        StringBuilder testBuffer = new StringBuilder();

        //REGEX STRINGS FOR PATTERN MATCHING
        String abbrevRegex = "([a-z]\\.[a-z]\\.[a-z]\\.)|([a-z]{1,2}\\.[a-z]\\.)"; //i.e. p.h.d, b.s.
        String a1HyphenRegex = "\\p{Space}([a-z]{1,2}-\\d{1,2}\\p{Space})|(\\d{1,2}-[a-z]{1,2}\\p{Space})";//i.e. f-16 and 16-f
        String longa1HyphenRegex = "\\p{Space}[a-z]{3,9}-\\d{0,2}\\p{Space}";// i.e. cdc-50
        String long1aHyphenRegex = "\\p{Space}\\d{1,2}-[a-z]{3,9}\\p{Space}";//i.e. 1-hour
        String prefixRegex = "\\p{Space}([a-z]{3,9}-[a-z]{2,9}-[a-z]{3,9})|([a-z]{3,9}-[a-z]{3,9})\\p{Space}";
        String replacePeriods = "";

        

        for (int i = 0; i < list.length; i++){

            testBuffer = trim(list[i].getAbsolutePath());
            buffer = testBuffer;
            
            strBuf = buffer.toString().toLowerCase();
            //strBuf = strBuf.replaceAll("(\\r\\n)|[\\r\\n\\e\\a\\f\\t]", "N");//TEST
            //strBuf = strBuf.replaceAll("(\r\n)|[\r\n\f\t]", "N2");//TEST
            strBuf = strBuf.replaceAll("(&blank;)|(&hyph;)|[\\(\\)`\\'\",;\\?_\\^\\*#%\\[\\]\\{\\}]", " ");
            strBuf = strBuf.replaceAll("&hyph;", "-");
           
            buffer = new StringBuilder(matchHelper(strBuf, abbrevRegex, "abbrevHelper"));
            buffer = new StringBuilder(matchHelper(buffer.toString(), a1HyphenRegex, "a1HyphenHelper"));
            buffer = new StringBuilder(matchHelper(buffer.toString(), longa1HyphenRegex, "longa1HyphenHelper"));
            buffer = new StringBuilder(matchHelper(buffer.toString(), long1aHyphenRegex, "long1aHyphenHelper"));
            buffer = new StringBuilder(matchHelper(buffer.toString(), prefixRegex, "prefixHelper"));

            try{
            
                java.io.File outputFile = new java.io.File("/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/QBtest/parser_outputs/parsed"+list[i].getName()+".txt");
                java.io.PrintWriter output = new java.io.PrintWriter(new java.io.FileWriter(outputFile));
                output.print(buffer);
                output.close();
            
            }//try
            catch (Exception e){
                System.out.println("error in constructor");
                e.printStackTrace();
            }

            
        }//for
    }//constructor


    public StringBuilder dumpStream(){
        return buffer;
    }

    private String matchHelper(String buffer, String regex, String method){
        Pattern abbrev_p = Pattern.compile(regex);//norms U.S.A., B.S., PH.D, PH.D.
        Matcher abbrev_matcher = abbrev_p.matcher(buffer);
        StringBuffer result = new StringBuffer();//must use stringbuffer here!
        while (abbrev_matcher.find()){
            if (method.equals("abbrevHelper")){
                abbrev_matcher.appendReplacement(result, abbrevHelper(abbrev_matcher));
            }
            else if (method.equals("a1HyphenHelper")){
                abbrev_matcher.appendReplacement(result, a1HyphenHelper(abbrev_matcher));
            }
            else if (method.equals("longa1HyphenHelper")){
                abbrev_matcher.appendReplacement(result, longa1HyphenHelper(abbrev_matcher));
            }
            else if (method.equals("long1aHyphenHelper")){
                abbrev_matcher.appendReplacement(result, long1aHyphenHelper(abbrev_matcher));
            }
            else if (method.equals("prefixHelper")){
                abbrev_matcher.appendReplacement(result, prefixHelper(abbrev_matcher));
            }
            else{
                log("method not defined");
                System.exit(0);
            }
            //result.append(abbrev_matcher.group().replaceAll("\\.", ""));
            //abbrev_matcher.appendReplacement(result, getReplacement(abbrev_matcher));

        }
        abbrev_matcher.appendTail(result);
        String tempResult = result.toString();
      return tempResult;

    }//norm abbrev

    private static String prefixHelper(Matcher aMatcher){
        int dashCounter = 0;
        String tempMatcher = aMatcher.group().toString();
        StringBuilder temp = new StringBuilder();
        StringBuilder temp2 = new StringBuilder();
        StringBuilder temp3 = new StringBuilder();
        for (int i = 0; i < tempMatcher.length(); i++){
            if (tempMatcher.charAt(i) != '-'){
                temp.append(tempMatcher.charAt(i));            
            }
            else {dashCounter++;}
        }//for

        //black-tie. return black + tie
        if (dashCounter == 1){
            boolean pastDash = false;
            for (int i = 0; i < tempMatcher.length(); i++){
                if (tempMatcher.charAt(i) == '-'){
                    pastDash = true;
                }
                else if (pastDash == true){
                    temp2.append(tempMatcher.charAt(i));
                }
                else{
                     temp3.append(tempMatcher.charAt(i));
                }           
            }//for
            temp3.append(' ');
            return temp.toString() + temp2.toString() + temp3.toString();
        }

        //point-of-view. return point + view
        if (dashCounter == 2){
            int dashes = 0;
            for (int i = 0; i < tempMatcher.length(); i++){
                if (tempMatcher.charAt(i) == '-')
                    dashes++;
                if (dashes == 1){ }
                else if (dashes == 2){
                    if (tempMatcher.charAt(i) != '-'){
                        temp3.append(tempMatcher.charAt(i));
                    }
                }
                else if (dashes == 0){
                    temp2.append(tempMatcher.charAt(i));
                }                
            }//for
            temp2.append(' ');
            temp3.append(' ');
            return temp.toString() + temp2.toString() + temp3.toString();
        }
        return temp.toString();
     
    }//normalize prefix helper

    private static String long1aHyphenHelper(Matcher aMatcher){
        String tempMatcher = aMatcher.group().toString();
        StringBuilder temp = new StringBuilder();
        StringBuilder temp2 = new StringBuilder();
        for (int i = 0; i < tempMatcher.length(); i++){
            if (tempMatcher.charAt(i) != '-'){
                temp.append(tempMatcher.charAt(i));
            }
        }//for
        boolean pastDash = false;
        for (int i = 0; i < tempMatcher.length(); i++){
            if (pastDash == true){
                temp2.append(tempMatcher.charAt(i));
            }
            if (tempMatcher.charAt(i) == '-')
                pastDash = true;
        }//for
       return temp.toString() + temp2.toString();
    }//normalize 1a long hyphen helper

     private static String longa1HyphenHelper(Matcher aMatcher){
        //System.out.println("longa1hyphen CALLED");
        String tempMatcher = aMatcher.group().toString();
        StringBuilder temp = new StringBuilder();
        StringBuilder temp2 = new StringBuilder();
        for (int i = 0; i < tempMatcher.length(); i++){
            if (tempMatcher.charAt(i) != '-'){
                temp.append(tempMatcher.charAt(i));
            }
        }//for
        for (int i = 0; i < tempMatcher.length(); i++){
            if (tempMatcher.charAt(i) == '-')
                break;
            temp2.append(tempMatcher.charAt(i));
        }//for

       //System.out.println("HERE" + temp2.toString());
       //System.out.println(temp.toString());
       return temp2.toString() + temp.toString();
    }//normalize long hyphen helper

    private static String a1HyphenHelper(Matcher aMatcher){
        String tempMatcher = aMatcher.group().toString();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < tempMatcher.length(); i++){
            if (tempMatcher.charAt(i) != '-'){
                temp.append(tempMatcher.charAt(i));
            }
        }//for
       return temp.toString();
    }//normalize hyphen helper


     //this method inspired by code from: http://stackoverflow.com/questions/2264603/help-using-java-matcher-to-modify-a-group
    //and: http://www.javapractices.com/topic/TopicAction.do?Id=80
    private static String abbrevHelper(Matcher aMatcher){
        String tempMatcher = aMatcher.group().toString();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < tempMatcher.length(); i++){
            if (tempMatcher.charAt(i) != '.'){
                temp.append(tempMatcher.charAt(i));
            }
        }//for
       return temp.toString();
    }//normalize abbrev helper

    //the irParser::trim strips out everything except <DOCNO> and <TEXT> nodes
    // also customizes delimeters. returns Stringbuilder
    public StringBuilder trim(String inputFileName) throws RuntimeException {
        StringBuilder docText = new StringBuilder();
       try{

            java.io.File inputFile = new java.io.File(inputFileName);
            Pattern delim = Pattern.compile("[\\p{Punct}\\p{javaWhitespace}+]");
            Scanner input = new Scanner(inputFile);//.useDelimiter(delim);


            while (input.hasNext()){
                lineNumber++;
                pos = 0;

                String currentLine = input.nextLine();


                if((currentLine.indexOf("<DOCNO>") == -1 ) && //there are no tags in this line
                        (currentLine.indexOf("</DOCNO>") == -1 ) &&
                        (currentLine.indexOf("<TEXT>") == -1 ) &&
                        (currentLine.indexOf("</TEXT>") == -1 ) &&
                        (currentLine.indexOf("<!--") == -1 ) &&
                        (currentLine.indexOf("-->") == -1 )
                        ){
                    if ((insideTextTag == true) || (insideDocTag == true)){ //we are inside an XML tag
                       docText.append(currentLine);
                    }
                }//if
                else{//there are tags in this line
                    StringBuilder buf1 = new StringBuilder();
                    for(int i = 0; i < currentLine.length(); i++){
                       buf1.append(currentLine.charAt(i));
                       //System.out.print(" for ");//DEBUG
                       //System.out.print(" " + buf1);//DEBUG

                        int docTag = buf1.indexOf("<DOCNO>");
                        int unDocTag = buf1.indexOf("</DOCNO>");
                        int textTag = buf1.indexOf("<TEXT>");
                        int unTextTag = buf1.indexOf("</TEXT>");
                        int commentTag = buf1.indexOf("<!--");
                        int unCommentTag= buf1.indexOf("-->");

                        if(docTag >= pos && insideCommentTag == false){ //log("docno called");
                            if ( (insideDocTag == true) || (insideTextTag == true))
                                throw new RuntimeException("Malformed XML: <docno>: ");
                            docText.append("<DOCNO>");
                            docText.append(" ");//so tag won't be tokenized with next input
                            pos = docTag + 7;
                            insideDocTag = true;
                            docCounter++;
                        }
                        else if(unDocTag >= pos && insideCommentTag == false){ //log("/docno called");
                            if ((insideDocTag == false) || (insideTextTag == true))
                                throw new RuntimeException("Malformed XML: </docno>: ");
                            insideDocTag = false;
                            docText.append(currentLine.substring(pos, unDocTag+8));
                            docText.append(" ");
                            pos = unDocTag+8;
                        }
                        else if(textTag >= pos && insideCommentTag == false){ //log("text called");
                           if ((insideDocTag == true) || (insideTextTag == true))
                                throw new RuntimeException("Malformed XML: <text>: ");
                           docText.append("<TEXT>");
                           docText.append(" ");
                           insideTextTag = true;
                           pos = textTag + 6;
                        }
                        else if (unTextTag >= pos && insideCommentTag == false){ //log("/text called");

                            if ((insideDocTag == true) || (insideTextTag == false))
                                throw new RuntimeException("Malformed XML: </text>: ");
                            docText.append(currentLine.substring(pos, unTextTag+7));
                            docText.append(" ");
                            insideTextTag = false;
                        }
                        else if (commentTag >= pos){
                            insideCommentTag = true;
                            pos = commentTag + 4;
                        }
                        else if (unCommentTag >= pos){
                            insideCommentTag = false;
                            pos = unCommentTag;
                        }

                        else{
                            //do nothing
                            
                        }

                       }//for
                }//else
                
            }//while
            System.out.println("irParser: number of docs: " + docCounter);

            input.close();
        }
        catch (RuntimeException e){
            System.out.println(e + "line:" + lineNumber + "pos:" + pos);
            e.printStackTrace();
        }
        catch (Exception e){
            System.out.println("Exception in irParser: could not construct/initialize");
            e.printStackTrace();
        }
       
       return docText;
    }//trim

    public void log (String s){
        System.out.println(s);
    }

    public void call(String param) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}//class
