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
public class Doc implements java.io.Serializable{
    private int docID;
    private int df;
    private ArrayList<Integer> positions;

    Doc(int d){
        this.docID = d;
        this.df = 0;
    }

    Doc(int d, int p){
        this.docID = d;
        this.df = 0;
        positions = new ArrayList();
        this.positions.add(p);
    }

    public void print(){
        System.out.print("{id=" + docID + "df=" + df+  "}");
        if (positions != null){
            for (int i = 0; i < positions.size(); i++){
                System.out.print(" " + positions.get(i) + " ");
            }
        }
    }
    public void addPos(int pos){
        positions.add(pos);
    }
    
    public void incrementDF(){
        df++;
    }

    public int getDocID(){
        return docID;
    }

    public int getDF(){
        return df;
    }

    public ArrayList<Integer> getPositions(){
        return positions;
    }

    public void merge(Doc d) throws RuntimeException{
        try{
            if (this.docID != d.getDocID())throw new RuntimeException();
        }
        catch (RuntimeException e){
            log("Doc: Merge: Error! invalid doc merge" + this.docID + "," + d.getDocID());
            e.printStackTrace();
        }
        //log("Doc::Merge: this df:" + this.df + "that df: " +d.getDF());
        this.df = this.df + d.getDF();
        if (d.getPositions() != null){
            ArrayList<Integer> dPositions = d.getPositions();
            if (this.positions == null){
                positions = new ArrayList();
            }
            for (int i = 0; i < dPositions.size(); i++){
                addPos(dPositions.get(i));
            }
        }
        //log("Doc::Merge: " + this.docID + " df: " + this.df);
    }

    public void log(String str){
        System.out.println(str);
    }
}//class
