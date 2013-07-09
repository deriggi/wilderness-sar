/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.mower;

import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.agent.SkelatalAgent;
import strategy.updater.SkelatalDirectionUpdater;
import strategy.updater.conditionchecker.AlwaysTrueConditionChecker;
import strategy.updater.conditionchecker.UpdaterConditionChecker;

/**
 *
 * @author Johnny
 */
public class MowerDirectionUpdater extends SkelatalDirectionUpdater {
    
    private static final Logger log = Logger.getLogger(MowerDirectionUpdater.class.getName());
    
    @Override
    public String toString() {
        return "east walkable";        
    }
    
    
    /**
     * Let's just use one updater for the mower. Mow south until near bottom edge
     * then mow north. Should probably use separate ones for north and south
     * @param dxDy
     * @param ownerAgent 
     */
    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        int eastEnd = raster.countColumns() - 1;
        int westEnd = 0;
        
        int northEnd = 0;
        int southEnd = raster.countRows() - 1;
        
        int threshold = 30;
        
        if( Math.abs( loc[0] - eastEnd )  < threshold || Math.abs( loc[0] - westEnd ) < threshold){
            
            // at an eastern or western wall so dip south yall!
            
            dxDy[0] *= 1/2;
            dxDy[1] += ownerAgent.getSpeed();
            
        }
        
        if( Math.abs ( loc[1] - southEnd ) < threshold){
            
            // at the bottom so switcheroo!
            
//            UpdaterConditionChecker cc = new AlwaysTrueConditionChecker();
            //cc.setNextState( );
//            setConditionChecker(cc);
        }
        
        
    }
    
}
