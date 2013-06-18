/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

import java.util.ArrayList;
import middletier.RasterConfig;
import middletier.RasterLoader;

/**
 *
 * @author Johnny
 */
public class ForceCalculator {

    public static void main(String[] args) {
        double[][] vec1 = new double[2][2];
        double[][] vec2 = new double[2][2];

        vec1[0][0] = 0;
        vec1[0][1] = 0;
        vec1[1][0] = 0;
        vec1[1][1] = 3;

        vec2[0][0] = 5;
        vec2[0][1] = 5;
        vec2[1][0] = 5;
        vec2[1][1] = 4;

//        new ForceCalculator().dotProduct(vec1, vec2);
        double[] move = new ForceCalculator().getOffset(vec1, 0.5f);
        System.out.println(move[0] + " " + move[1]);
    }

    public double dotProduct(double[][] vectorOne, double[][] vectorTwo) {
        double xa1 = vectorOne[0][0];
        double xa2 = vectorOne[1][0];

        double ya1 = vectorOne[0][1];
        double ya2 = vectorOne[1][1];

        double xb1 = vectorTwo[0][0];
        double xb2 = vectorTwo[1][0];

        double yb1 = vectorTwo[0][1];
        double yb2 = vectorTwo[1][1];

        double v1[] = {xa2 - xa1, ya2 - ya1};
        double v2[] = {xb2 - xb1, yb2 - yb1};


        System.out.println(v1[0] + " " + v1[1]);
        System.out.println(v2[0] + " " + v2[1]);
        

        double v1magnitude = Math.sqrt(Math.pow(v1[0], 2) + Math.pow(v1[1], 2));
        double v2magnitude = Math.sqrt(Math.pow(v2[0], 2) + Math.pow(v2[1], 2));

        double[] v1Unit = {v1[0] / v1magnitude, v1[1] / v1magnitude};
        double[] v2Unit = {v2[0] / v2magnitude, v2[1] / v2magnitude};

        double dotProduct = 0;
        dotProduct += v1Unit[0] * v2Unit[0];
        dotProduct += v1Unit[1] * v2Unit[1];



        return dotProduct;

    }

    /**public double[] getOffset(double [][] theLine, float distance){
    // dsquared/ (riseSquared + runSquared = xFactor
    
    double[] theMotherFucker = new double[2];
    theMotherFucker[0] = theLine[1][0] - theLine[0][0];
    theMotherFucker[1] = theLine[1][1] - theLine[0][1];
    
    double xFactor = Math.sqrt(Math.pow(distance, 2)/(Math.pow(theMotherFucker[0], 2) + Math.pow(theMotherFucker[1],2)));
    
    theMotherFucker[0] = theMotherFucker[0]*xFactor;
    theMotherFucker[1] = theMotherFucker[1]*xFactor;
    return theMotherFucker;
    }**/
    public double[] getOffset(double[][] theLine, float distance) {
        // dsquared/ (riseSquared + runSquared = xFactor
        
        // get the slope
        double[] theMotherFucker = new double[2];
        theMotherFucker[0] = theLine[1][0] - theLine[0][0];
        theMotherFucker[1] = theLine[1][1] - theLine[0][1];
        
        // length
        double magnitude = Math.sqrt(Math.pow(theMotherFucker[0], 2) + Math.pow(theMotherFucker[1], 2));
        
        double[] unitVector = new double[2];
        unitVector[0] = theMotherFucker[0]/magnitude;
        unitVector[1] = theMotherFucker[1]/magnitude;
        
        unitVector[0] = unitVector[0]*distance;
        unitVector[1] = unitVector[1]*distance;
        
        return unitVector;
    }
    
    

    public VectorHolder calculateAttractiveForceVector(int originColumn, int originRow, ArrayList<ArrayList<SlopeDataCell>> cells, ArrayList<SlopeDataCell> pointsOfInterest, int unitScale) {
//        ArrayList<SlopeDataCell> row = cells.get((cells.size() - 1) / 2);
//        SlopeDataCell origin = row.get((row.size() - 1) / 2);
        SlopeDataCell origin = new SlopeDataCell(0.0,0.0,originColumn, originRow);

        // sum vectors
        int dx = 0, dy = 0;
        for (SlopeDataCell point : pointsOfInterest) {
            dx += point.getColumn() - origin.getColumn();
            dy += point.getRow() - origin.getRow();
        }

        double magnitude = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
        double unitDx = unitScale * dx / magnitude;
        double unitDy = unitScale * dy / magnitude;
        double[][] vector = new double[2][2];
        double[][] columnRowVector = new double[2][2];
        columnRowVector[0][0] = origin.getColumn();
        columnRowVector[0][1] = origin.getRow();
        columnRowVector[1][0] = origin.getColumn() + dx;
        columnRowVector[1][1] = origin.getRow() + dy;


        vector[0] = RasterLoader.get(RasterConfig.BIG).getData().getLonLat(origin.getColumn(), origin.getRow());
        vector[1] = RasterLoader.get(RasterConfig.BIG).getData().getLonLat(origin.getColumn() + dx, origin.getRow() + dy);

        VectorHolder holder = new VectorHolder();
        holder.setColumnRowVector(columnRowVector);
        holder.setLonLatVector(vector);
        return holder;

//        System.out.println(" -------------------------");
//        
//        System.out.println(" resolving forces motha fucka");
//        
//        System.out.println("column " + origin.getColumn() );
//        System.out.println("row "  +  origin.getRow());
//        System.out.println("dx column " + origin.getColumn() + unitDx);
//        System.out.println("dy row " + origin.getRow() + unitDy);
//        System.out.println(" -------------------------");
//        System.out.println();

    }
}
