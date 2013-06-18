/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

/**
 *
 * @author Johnny
 */
public class TempCache {

    private static TempCache cache = new TempCache();
    private double[][] lastVector = null;
    public static TempCache get(){
        
        return cache;
    }
    
    public void setLastVector(double[][] lastVector){
        this.lastVector = lastVector;
    }
    
    public double[][] getLastVector(){
        return lastVector;
    }
}
