/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.routesampler;

import java.util.ArrayList;
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
public class OutAndBackEasternDirectionUpdater extends SkelatalOutAndBackWalkableDirectionUpdater {

//    private static final Logger log = Logger.getLogger(OutAndBackEasternDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "out and back east";
    }
    

    public OutAndBackEasternDirectionUpdater(Direction direction, DirectionUpdater d){
        setNextDirectionUpdater(d);
        setDirection(direction);
    }
    
   
    
    @Override
    protected void doOutMode(double[] dxDy, SkelatalAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], VectorAgent.SHORT_VIS_RANGE);
        
        raster.getEasternCells(visibleCells, (int) loc[0], (int) loc[1]);
        
        // 1) go towards visibles
        goTowardsWalkableCells(visibleCells, raster, loc, dxDy);
        
        // 2) calculate distance and visible portion. more of each is bettah!
        float routeQuality = calculateRouteQuality(ownerAgent, visibleCells);
        
        // 3) add it to runnign tally of route quality
        getVisibleCountList().add(routeQuality);      
        
        float averageQualityOfRoute  = averageFieldOfView();
        ownerAgent.getMemory().put(Direction.EAST.toString(), averageQualityOfRoute);
        
        getLocalStack().push(ownerAgent.getLocation());
//        ArrayList<SlopeDataCell> eastFarCells = raster.getEastVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
//        float portion  = (float) eastFarCells.size() / ( VectorAgent.LONG_VIS_RANGE * VectorAgent.LONG_VIS_RANGE );
        
        
        
        
        
    }
    

}
