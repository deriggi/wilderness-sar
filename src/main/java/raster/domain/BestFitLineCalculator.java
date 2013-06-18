/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

import java.util.ArrayList;

/**
 *
 * @author Johnny
 */
public class BestFitLineCalculator {
    
    
    //UNFINISHED DO NOT USE
    public static void calcBestLine(ArrayList<ArrayList<SlopeDataCell>> cells, ArrayList<SlopeDataCell> poinstOfInterest){
        
        // get center y interecept
        float yIntercept = (cells.size()-1)/2.0f;
        int topRow = cells.get(0).get(0).getRow();
        int leftColumn = cells.get(0).get(0).getColumn();
        int bottomRow = cells.get(cells.size()-1).get(0).getRow();
        
        // the big winners
        SlopeDataCell lineMakerCell = null;
        float minSumSquare = 0;
        
        // go through top row
        ArrayList<SlopeDataCell> top = cells.get(0);
        
        for(int i = 0; i < top.size(); i++){
            SlopeDataCell cell = top.get(i);
            float rise = cell.getRow()-bottomRow;
            float run = i;
            if (i != 0){
                float slope = rise/run;
                //UNFINISHED PART
                sumSquares(slope,yIntercept, poinstOfInterest, topRow,leftColumn);
            }
            
        }
        
    }
    
    private static double sumSquares(float slope, float yIntercept, ArrayList<SlopeDataCell> pointsOfInterest, int topRowReference, int leftColumnReference){
        float sumOfSquaredDifferences = 0;
        for(SlopeDataCell cell: pointsOfInterest){
            int y = topRowReference - cell.getRow();
            int x = cell.getColumn() - leftColumnReference;
            
            float lineY =  slope*x  +yIntercept;
            double squaredDifference = Math.pow(lineY-y,2);
            sumOfSquaredDifferences += squaredDifference;
        }
        
        return sumOfSquaredDifferences;
    }
    
    
}
