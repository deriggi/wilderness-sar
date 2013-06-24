/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.ArrayList;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class DoNothingDirectionUpdater extends SkelatalDirectionUpdater{

    @Override
    public String toString(){
        return "Nothing"; 
    }
    
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        
        
        
        
    }


    
}
