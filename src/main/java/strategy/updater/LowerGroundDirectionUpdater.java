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
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class LowerGroundDirectionUpdater extends SkelatalDirectionUpdater {

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        
        ArrayList<ArrayList<SlopeDataCell>> hood = raster.getSlopeDataNeighborhood((int)loc[0], (int)loc[1], 6);
//        ArrayList<SlopeDataCell> pointsOfInterest = raster.getCellsLessThan(hood, raster.getCell((int)loc[0], (int)loc[1]), 0.20f);
        ArrayList<SlopeDataCell> pointsOfInterest = raster.getLows(hood);
        
        float[] acceleration = raster.calculateForcesAgainst(loc, pointsOfInterest);
        
        dxDy[0] += acceleration[0];
        dxDy[1] += acceleration[1];
        
        
        
    }

    @Override
    public void notifyMe(UpdaterMessage message) {
    }
    
}
