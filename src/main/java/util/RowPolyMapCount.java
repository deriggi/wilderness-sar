/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Johnny
 */
public class RowPolyMapCount {
    
    private int polyCount = 0;
    private HashMap<Integer,ArrayList<ArrayList<Integer>>> rowPolysMap = null;

    public int getPolyCount() {
        return polyCount;
    }

    public HashMap<Integer, ArrayList<ArrayList<Integer>>> getRowPolysMap() {
        return rowPolysMap;
    }
    
    public RowPolyMapCount(HashMap<Integer,ArrayList<ArrayList<Integer>>> rowPolysMap, int count){
        this.rowPolysMap = rowPolysMap;
        this.polyCount = count;
    }
    
}
