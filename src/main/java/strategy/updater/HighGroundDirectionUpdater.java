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
public class HighGroundDirectionUpdater extends SkelatalDirectionUpdater{

    @Override
    public String toString(){
        return "Walk High"; 
    }
    
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<ArrayList<SlopeDataCell>> cells = raster.getSlopeDataNeighborhood( (int)loc[0], (int)loc[1], 6);
        ArrayList<SlopeDataCell> highFlats = raster.getHighs(cells);//raster.getHighestFlattest(cells);
        
        float[] attractionForce = raster.calculateForcesAgainst(loc, highFlats);
        // apply this force vector to the agent
        double mag = VectorUtils.magnitude(attractionForce);
        
        dxDy[0] += ownerAgent.getSpeed() * attractionForce[0]/mag;
        dxDy[1] += ownerAgent.getSpeed() * attractionForce[1]/mag;
        
        // apply this force vector to the agent
        
        
    }

    @Override
    public void notifyMe(UpdaterMessage message) {
    }


    
}
