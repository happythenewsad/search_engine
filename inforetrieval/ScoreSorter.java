/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.util.Comparator;

/**
 *
 * @author peterkong
 */
public class ScoreSorter implements Comparator<Score>{
    public int compare(Score s1, Score s2){
        Double score1 = s1.getScore();
        Double score2 = s2.getScore();
        if (score1>score2){
                return -1;
        }
        else if (score1==score2){
            return 0;
        }
        else{
            return 1;
        }
    }

}
