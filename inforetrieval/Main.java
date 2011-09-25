/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

/**
 *
 * @author peterkong
 */

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.*;
import java.util.Iterator;
import java.io.*;





public class Main {

    /**
     * @param args the command line arguments
     */


    public static void main(String[] args) {
      long maxTermCount = 3;

      //String inputFileName = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/Mini-Trec-Data/BigSample/fr940525.0";
      String parsedFileName = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/parserOutput.txt";
      String stopTermFile = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/Mini-Trec-Data/stops.txt";
      String[] indexTester = new String[1];
      indexTester[0] = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/barf.txt";
      String smallParsedFile = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/smallParserOutput.txt";
      String parserOutput = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/parserOutput.txt";
      String fullParsed = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/parser_outputs/fullParsed.txt";
      String queryFile = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/Mini-Trec-Data/QueryFile/queryfile.txt";


     String parserOutputDir = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/QBtest/parser_outputs";


     //Indexer indexer = new Indexer(parserOutputDir,stopTermFile, maxTermCount);
     //indexer.index();

     //DocIndexer d = new DocIndexer(parserOutputDir,stopTermFile, maxTermCount);
     //d.index();
   
     //StemIndexer si = new StemIndexer(parserOutputDir, stopTermFile);
     //si.index();

     
    //PhraseIndexer pi = new PhraseIndexer(parserOutputDir, stopTermFile, maxTermCount);
    //pi.index();


     //PosIndexer pos = new PosIndexer(smallParsedFile, stopTermFile);
     //pos.Index();
     //pos.printLexicon();
     //pos.writeHashToDisk();


     String queryPath = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/Mini-Trec-Data/QueryFile/queryFile.txt";
     String mapperPath = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/main/parser_outputs";
     int DOTPRODUCT = 1;
     int COSINE = 2;
     boolean conjunctive = true;


     //VSMQP dp = new VSMQP(queryPath, stopTermFile, mapperPath, COSINE);
     //dp.build();

      VSMQP_TermWiseTrim termwise = new VSMQP_TermWiseTrim(queryPath, stopTermFile, mapperPath, COSINE, 20);
     termwise.sortByTFIDF();
     termwise.build();

     //VSMQP_DocWiseTrim docwise = new VSMQP_DocWiseTrim(queryPath, stopTermFile, mapperPath, COSINE, 320);
     //docwise.build();



     //BooleanQP conj = new BooleanQP(queryPath, stopTermFile, mapperPath, conjunctive);
     //conj.build();

     //BooleanQP disj = new BooleanQP(queryPath, stopTermFile, mapperPath, !conjunctive);
     //disj.build();

     //ProbQP pp = new ProbQP(queryPath, stopTermFile, mapperPath);
     //pp.build();


    }//void main

}//main

