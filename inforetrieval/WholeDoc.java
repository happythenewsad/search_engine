/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *
 * @author peterkong
 */
public class WholeDoc implements Comparator<WholeDoc>, java.io.Serializable{
    private String name;
    private int termWiseLength;
    //private LinkedList<Term> postList;

    public WholeDoc(){
        name = "";
        termWiseLength = 0;
    }

    public WholeDoc(String s){
        name = s;
        termWiseLength = 0;
    }

    public WholeDoc readFromDisk(String nm){
        String path = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/main/docindexer_temp_files/" + nm + ".dat";
        WholeDoc temp = new WholeDoc();
        try {
            java.io.File outputFile = new java.io.File(path);

            if (!outputFile.exists()){
                //throw new FileNotFoundException();
                log("Term: term temp file did not exist");
                return new WholeDoc();

            }

            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
            temp = (WholeDoc)inputStream.readObject();
            inputStream.close();
        }
         catch (EOFException ex) { //This exception will be caught when EOF is reached
            System.out.println("End of file reached.");
         }
        catch (FileNotFoundException ex) {
            log("WholeDoc: term temp file did not exist");
            ex.printStackTrace();
        }
        catch (IOException ex){
            System.out.print("WholeDoc: io ex");
           ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
        return temp;

    }//readFromDisk

    public void writeToDisk(){
        String path = "/Users/peterkong/Peter/Georgetown/Georgetown2010/Goharian/main/docindexer_temp_files/" + this.name + ".dat";
        try {

            java.io.File outputFile = new java.io.File(path);

            if (outputFile.exists()){
                log("WholeDoc: wholedoc temp file already exists!");
                throw new Exception();

            }
            ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(path));
            output.writeObject(this);
            output.close();

        }
        catch (FileNotFoundException ex) {
            System.out.print("WholeDoc: " + ex);
            ex.printStackTrace();
        }
        catch (IOException ex){
            System.out.print("WholeDoc: " + ex);
            ex.printStackTrace();
        }
        catch(Exception ex){
            System.out.print("WholeDoc: " + ex);
            ex.printStackTrace();
        }

    }//write to disk

    private void incrementLength(){
        termWiseLength += 1 ;
    }

    public void setLength(int i){
        termWiseLength = i;
    }

    public void print(){
        log(name + " , " + termWiseLength);
    }


    public int getTermWiseLength(){
        return termWiseLength;
    }

    public String getName(){
        return name;
    }

    public int compare(WholeDoc o1, WholeDoc o2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void log(String s){
        System.out.println(s);
    }

}//whole odc
