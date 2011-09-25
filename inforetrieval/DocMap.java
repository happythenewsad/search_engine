/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inforetrieval;

/**
 *
 * @author peterkong
 */
public class DocMap {
    int id;
    String name;

    DocMap(int i, String s){
        id = i;
        name = s;
    }

    public int getID(){
        return id;
    }

    public String getName(){
        return name;
    }

    public void print(){
        System.out.println(id + " " + "[" + name + "]");
    }

}
