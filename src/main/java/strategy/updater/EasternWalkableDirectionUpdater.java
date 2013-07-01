/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import geomutils.VectorUtils;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.ForceCalculator;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class EasternWalkableDirectionUpdater extends SkelatalDirectionUpdater{

    private static final Logger log = Logger.getLogger(EasternWalkableDirectionUpdater.class.getName());
    
    @Override
    public String toString(){
        return "Walk High"; 
    }
    
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int)loc[0], (int)loc[1], 10);
        raster.getEasternCells(visibleCells, (int)loc[0], (int)loc[1]);
        visibleCells = raster.getSlopeLessThan1D(visibleCells, 0.30f);
        
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int)loc[0], (int)loc[1]}, visibleCells);
        log.log(Level.INFO, "acceleration {0} {1} ", new Object[]{(int)acceleration[0], (int)acceleration[1]} );
        
        dxDy[0] += acceleration[0];
        dxDy[1] += acceleration[1];
        
    }



    
}
