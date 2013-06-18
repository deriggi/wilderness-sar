/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

/**
 *
 * @author Johnny
 */
public class VectorHolder {
    private double[][] lonLatVector = null;

    public double calcColumnVectorLength(){
        double[][] vector = getColumnRowVector();
        
        // pythagorous maximous
        return Math.sqrt( Math.pow(vector[0][0] - vector[1][0], 2) +  Math.pow(vector[0][1] - vector[1][1], 2) );
        
    }
    
    public double[][] getColumnRowVector() {
        return columnRowVector;
    }

    public void setColumnRowVector(double[][] columnRowVector) {
        this.columnRowVector = columnRowVector;
    }

    public double[][] getLonLatVector() {
        return lonLatVector;
    }

    public void setLonLatVector(double[][] lonLatVector) {
        this.lonLatVector = lonLatVector;
    }
    private double[][] columnRowVector = null;
    
    
}
