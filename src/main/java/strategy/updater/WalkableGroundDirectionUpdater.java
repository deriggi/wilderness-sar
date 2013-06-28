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
public class WalkableGroundDirectionUpdater extends SkelatalDirectionUpdater {

    /**
     * Go towards walkable ground plus momentum
     * @param dxDy
     * @param ownerAgent 
     */
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        
        ArrayList<ArrayList<SlopeDataCell>> hood = raster.getSlopeDataNeighborhood((int)loc[0], (int)loc[1], 6);
//        ArrayList<SlopeDataCell> pointsOfInterest = raster.getCellsLessThan(hood, raster.getCell((int)loc[0], (int)loc[1]), 0.20f);
        float max = raster.getCell(loc[0], loc[1]);
        
//        ArrayList<SlopeDataCell> pointsOfInterest = raster.getCellsLessThan(hood, max, 0.25f);
        ArrayList<SlopeDataCell> pointsOfInterest = raster.getSlopeLessThan(hood, 0.40f);
        
        float[] acceleration = raster.calculateForcesAgainst(loc, pointsOfInterest);
        
        dxDy[0] += acceleration[0] + dxDy[0];
        dxDy[1] += acceleration[1] + dxDy[1];
        
        
        
    }

    
    
}
