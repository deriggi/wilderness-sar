/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdomain;

import com.vividsolutions.jts.geom.Point;
import java.util.Set;

/**
 *
 * @author Johnny
 */
public class ClimateResult {
    private Double [][] coordinates = null;
    private double max, min, average;

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public Double[][] getCoordinates() {
        
        return coordinates;
    }

    public void setPoints(Set<Point> centroids) {
         Double [][] coords = new Double[centroids.size()][2];
        
        int rowIndex = 0;
        
        for(Point p : centroids){
            coords[rowIndex][0] = p.getX();
            coords[rowIndex][1] = p.getY();
            rowIndex++;
        }
        this.coordinates = coords;
    }
}
