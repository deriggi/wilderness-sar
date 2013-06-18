/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Johnny
 */
public class ReferenceTester {
    
    public String someString = "Start";
    
    public void makeChange(String aString){
        aString = "rick";
    }
    
    @Override
    public String toString(){
        return this.someString;
    }
    
    public static void main(String[] args){
        ReferenceTester rt = new ReferenceTester();
        rt.makeChange(rt.someString);
        
        System.out.println(rt.toString());
    }
    
}
