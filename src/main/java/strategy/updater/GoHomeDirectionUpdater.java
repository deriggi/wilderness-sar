/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import geomutils.VectorUtils;
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
public class GoHomeDirectionUpdater extends SkelatalDirectionUpdater {

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        float[] origin = ownerAgent.getOrigin();
        
        
        
        float[] acceleration = raster.calculateForcesAgainst(loc, origin);
        
        dxDy[0] += acceleration[0];
        dxDy[1] += acceleration[1];
        VectorUtils.limit(dxDy, ownerAgent.getSpeed());
        
        
    }

    @Override
    public void notifyMe(UpdaterMessage message) {
    }
    
}
