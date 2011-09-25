/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

/**
 *
 * @author peterkong
 */
public class Score {
    private double score;
    private String name;

    Score(){

    }

    Score(String s, double d){
        name = s;
        score = d;
    }

    public String getName(){
        return name;
    }

    public double getScore(){
        return score;
    }

    public void updateScore(double d){
        score += d;
    }

    public void print(){
        log(name + ":" + score);
    }

    public void log(String s){
        System.out.println(s);
    }
}
