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
public class RowComparator implements Comparator<SlopeDataCell> {

    @Override
    public int compare(SlopeDataCell o1, SlopeDataCell o2) {
        return o1.getRow() - o2.getRow();
    }
    
    
    
}
