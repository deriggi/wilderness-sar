/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized;

import java.util.ArrayList;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;
import strategy.updater.SkelatalDirectionUpdater;

/**
 *
 * @author Johnny
 */
public class EasternWalkableDirectionUpdater extends SkelatalDirectionUpdater {
    
    private static final Logger log = Logger.getLogger(EasternWalkableDirectionUpdater.class.getName());
    
    @Override
    public String toString() {
        return "east walkable";        
    }
    
    // 16 for a moderately healty walker
    private float minSlope = .16f;
    
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], 10);

//        raster.getEasternCutoutCells(visibleCells, (int)loc[0], (int)loc[1], 8);
        raster.getEasternCells(visibleCells, (int) loc[0], (int) loc[1]);
        visibleCells = raster.getSlopeLessThan1D(visibleCells, minSlope);
        
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);
        
        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];
        
        
        
    }
    
}
