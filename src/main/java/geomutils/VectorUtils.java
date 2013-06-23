package geomutils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Johnny
 */
public class VectorUtils {
    
    public static double distance(int[] start, int[] end){
        return Math.sqrt(Math.pow(end[0] - end[1], 2) + Math.pow(start[0] - start[1], 2));
    }
    
    public static double magnitude(double[] vector){
        return Math.sqrt(Math.pow(vector[0], 2) + Math.pow(vector[1], 2));
    }
    
    public static double[] limit(double[] vector, double limit){
        double mag = magnitude(vector);
        if (mag > limit){
            double scale = mag/limit;
            divide(vector,(float)scale);
        }
        
        return vector;
    }
    
    public static void divide(double[] vector, float divisor){
        vector[0] /= divisor;
        vector[1] /= divisor;
    }
    
    /**
     * Assuming the vector is x,y
     * @param vector
     * @return 
     */
    public static double getBearing(double[] vector){
        return Math.atan2(vector[1], vector[0]);
    }
    
    public static void main(String[] args){
        double[] vec = new double[]{6.0,8.0};
        System.out.println("mag "  + magnitude(vec));
        System.out.println("lim "  + magnitude(limit(vec,3.8)));
        
    }
    
}