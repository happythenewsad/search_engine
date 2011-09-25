/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;
//import java.util.Comparator;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 *
 * @author peterkong
 */
public class Term implements Comparator<Term>, java.io.Serializable {
    private String name;
    private int tf;
    private LinkedList<Doc> postList;

    //updates existing term or creates new term
    public void add (int docID){//override this method for positional
        boolean docAlreadyHere = false;
        

        ListIterator it = postList.listIterator();
        while(it.hasNext()){
            Doc temp = (Doc)it.next();
            if (temp.getDocID()== docID){
                docAlreadyHere = true;
                temp.incrementDF();
            }
        }//while
        
        if (!docAlreadyHere){
            Doc temp = new Doc(docID);
            temp.incrementDF();
            postList.add(temp);
        }

    }//add

    public int PLSize(){
        return postList.size();
    }

    public void add (int docID, int pos){
        boolean docAlreadyHere = false;

        ListIterator it = postList.listIterator();
        while(it.hasNext()){
            Doc temp = (Doc)it.next();
            if (temp.getDocID()== docID){
                docAlreadyHere = true;
                temp.incrementDF();
                temp.addPos(pos);
            }
        }//while

        if (!docAlreadyHere){
            Doc temp = new Doc(docID, pos);
            temp.incrementDF();
            //no need to add pos - added in constructor override
            postList.add(temp);
        }

    }//add
    
    public Term(String n){
        this.name = n;
        postList = new LinkedList<Doc>();
    }

    public Term(){
        this.name = "";
        postList = new LinkedList<Doc>();
    }

    public String getName(){
        return name;
    }

    public void setName(String s){
        this.name = s;
    }

    public void incrementTF(){
        this.tf++;
    }

    public int getTF(){
        return tf;
    }

    public LinkedList<Doc> getPostList(){
        return postList;
    }

    public void clearPL(){
        this.tf = 0;
        this.postList.clear();
    }

    public void merge(Term t) throws RuntimeException{
        try{
            if (this.name.compareTo(t.getName()) != 0)throw new RuntimeException();
        }
        catch (RuntimeException e){
            log("Error! invalid term merge: " + this.name + "," + t.getName());
            //e.printStackTrace();
        }
        this.tf = this.tf + t.getTF();

       LinkedList<Doc> foreignPL = t.getPostList();
       boolean match = false;

        for (int i = 0; i < foreignPL.size(); i++){
            
            Doc foreignCurrent = foreignPL.get(i);
            int counter = 0;

            while (counter < postList.size() && !match){
                Doc current = postList.get(counter);
                //log("while loop: " + current.getDocID());


                if (current.getDocID() == foreignCurrent.getDocID()){
                   // log("simple match with " + foreignCurrent.getDocID() + " df: " + foreignCurrent.getDF()+ "," + current.getDocID() + " df: " + current.getDF());
                    /*Doc temp = postList.get(counter);
                    temp.print();
                    temp.merge(current);
                    temp.print();
                    postList.remove(counter);
                    postList.add(counter, temp);*/
                    //THIS IS MERGING THE SAME TWO THINGS

                    postList.get(counter).merge(foreignCurrent);
                    match = true;
                }
                else if(counter == 0 && (current.getDocID() > foreignCurrent.getDocID())){
                    //log("first add with " + foreignCurrent.getDocID());
                    postList.add(0, foreignCurrent);
                    counter--;
                    match = true;
                }
                else if(counter+1 < postList.size()){
                    if (postList.get(counter+1).getDocID() > foreignCurrent.getDocID()){
                        //log("middle add with " + foreignCurrent.getDocID());
                        postList.add(counter+1, foreignCurrent);
                        match = true;
                    }
                }
                else{
                    //log("Term::Merge: for: while: nothing clause");
                }
                counter++;
            }//while
            if (!match){
                //log("last add with " + foreignCurrent.getDocID());
                postList.add(foreignCurrent);
            }
            match = false;
        }//for
        

        //TESTING PRINTING
         /*for (int i = 0; i < postList.size(); i++){
            Doc currentDoc = postList.get(i);
            log("postList doc: " + currentDoc.getDocID());
        }*///for

        /*for (int i = 0; i < foreignPL.size(); i++){
            Doc currentDoc = foreignPL.get(i);
            log("foreignPL doc: " + currentDoc.getDocID());
        }*///for

         //END TESTING


    }//merge
    
    public int compare(Term t1, Term t2) {
        String t1name = t1.getName();
        String t2name = t2.getName();
        int compare = t1name.compareTo(t2name);
        return compare;
    }

    //reads one term's posting list from its file on disk. returns that term
    //reads one term into memory in order to merge disk PL with memory PL
    //only call this when you are certain that a file for term already exists
    public Term readFromDisk(String nm){
        String path = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/main/indexer_temp_files/" + nm + ".dat";
        Term temp = new Term();
        try {
            java.io.File outputFile = new java.io.File(path);

            if (!outputFile.exists()){
                //throw new FileNotFoundException();
                //log("Term: term temp file did not exist");
                return new Term("");

            }
            
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
            temp = (Term)inputStream.readObject();
            inputStream.close();
        }
         catch (EOFException ex) { //This exception will be caught when EOF is reached
            System.out.println("End of file reached.");
         }
        catch (FileNotFoundException ex) {
            //log("Term: term temp file did not exist");
            ex.printStackTrace();
        }
        catch (IOException ex){
            System.out.print("Term: io ex");
           ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
        return temp;
        
    }//readFromDisk

    public void writeToDisk(){

        String path = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/main/indexer_temp_files/" + this.name + ".dat";
        try {

            java.io.File outputFile = new java.io.File(path);

            if (outputFile.exists()){
                Term temp = readFromDisk(this.name);
                this.merge(temp);
                
            }            
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(path));
            output.writeObject(this);
            output.close();
           
        }
        catch (FileNotFoundException ex) {
            System.out.print("Term: " + ex);
            ex.printStackTrace();
        }
        catch (IOException ex){
            System.out.print("Term: " + ex);
            ex.printStackTrace();
        }
        catch(Exception ex){
            System.out.print("Term: " + ex);
            ex.printStackTrace();
        }

    }//write to disk

    public void log(String str){
        System.out.println(str);
    }

    void setTF(int i){
        this.tf = i;
    }

    public void print(){
        log("["+this.name+"]" + "total tf: " + tf);
        if (postList != null){
            System.out.print("   ");
            for (int i = 0; i < postList.size(); i++){
                postList.get(i).print();
            }
        }
        else{
            System.out.print("null postlist");
        }
        System.out.print("\n");
    }
    
}//class Term
