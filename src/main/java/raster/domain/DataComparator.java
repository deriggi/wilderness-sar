/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

import java.util.Comparator;

/**
 *
 * @author Johnny
 */
public class DataComparator implements Comparator<SlopeDataCell> {

    @Override
    public int compare(SlopeDataCell o1, SlopeDataCell o2) {
        
        if ((o1.getData() - o2.getData()) == 0 ){
            return 0;
        }
        
        if( o1.getData() > o2.getData()){
            return 1;
        }
        
        return -1;
        
            
        
    }
    
}
