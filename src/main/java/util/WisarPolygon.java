/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;

/**
 *
 * @author Johnny
 */
public class WisarPolygon {
    private ArrayList<ArrayList<Double>> vertexList = new ArrayList<ArrayList<Double>>();
    
    public void addCoord(double[] someCoord){
        if(someCoord == null || someCoord.length != 2){
            return;
        }
        
        ArrayList<Double> coord = new ArrayList<Double>();
        coord.add(someCoord[0]);
        coord.add(someCoord[1]);
        vertexList.add(coord);
    }
}
