/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.util.ArrayList;

/**
 *
 * @author peterkong
 */
public class Query {
    private int num;
    private ArrayList<String> terms;

    Query(){
        num = 0;
        terms = new ArrayList<String>();
    }

    Query(int n){
        num = n;
        terms = new ArrayList<String>();
    }

    public void addTerm(String s){
        terms.add(s);
    }

    public ArrayList<String> getTerms(){
        return terms;
    }

    public int getNum(){
        return num;
    }

    public void print(){
        log(num);
        if (terms != null){
            for (int i = 0; i < terms.size(); i++){
                System.out.print("[" + terms.get(i) + "]");
            }
        }
        System.out.print("\n");
    }

    public void log(String s){
        System.out.println(s);
    }

    public void log(int n){
        System.out.println(n);
    }

}//Query
