/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.routesampler;


import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.SkelatalAgent;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;
import strategy.updater.Direction;

/**
 *
 * @author Johnny
 */
public class OutAndBackWesternDirectionUpdater extends SkelatalOutAndBackWalkableDirectionUpdater {

    private static final Logger log = Logger.getLogger(OutAndBackWesternDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "west walkable";
    }

    public OutAndBackWesternDirectionUpdater(Direction direction, DirectionUpdater du){
        setNextDirectionUpdater(du);
        setDirection(direction);
    }
    
    
    @Override
    protected void doOutMode(double[] dxDy, SkelatalAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], VectorAgent.SHORT_VIS_RANGE);
        raster.getWesternCells(visibleCells, (int) loc[0], (int) loc[1]);
        
        // common part
        // 1) go towards visibles
        goTowardsWalkableCells(visibleCells, raster, loc, dxDy);

        // 2) calculate distance and visible portion. more of each is bettah!
        float routeQuality = calculateRouteQuality(ownerAgent, visibleCells);
        
        // 3) add it to runnign tally of route quality
        getVisibleCountList().add(routeQuality);

        getLocalStack().push(ownerAgent.getLocation());
        
        float averageFieldOfView  = averageFieldOfView();
        log.log(Level.INFO, "average field of view is {0} ", averageFieldOfView);
        ownerAgent.getMemory().put(Direction.WEST.toString(), averageFieldOfView());
        
    }
}
