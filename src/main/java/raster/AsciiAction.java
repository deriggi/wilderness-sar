/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster;

/**
 *
 * @author Johnny
 */
public interface AsciiAction {

    public void handleNonNullData(double y, double x,   double data);

    public void setCellSize(double cellSize);
    
    public void cleanUp();
    
    public void setup();
    
    
}
