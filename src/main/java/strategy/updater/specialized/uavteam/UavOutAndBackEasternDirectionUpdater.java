/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.uavteam;

import java.util.ArrayList;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Communications;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.SkelatalAgent;
import raster.domain.agent.VectorAgent;
import strategy.updater.Direction;

/**
 *
 * @author Johnny
 */
public class UavOutAndBackEasternDirectionUpdater extends UavSkelatalOutAndBackWalkableDirectionUpdater {


    @Override
    public String toString() {
        return "out and back east";
    }

    
    public UavOutAndBackEasternDirectionUpdater(){
        setDirection(Direction.EAST);
    }
    
    @Override
    protected void doOutMode(double[] dxDy, SkelatalAgent ownerAgent) {

        if (!isRegistered()) {
            Communications.register(SkelatalAgent.COMS, ownerAgent);
            setRegistered(true);
        }

        
        // visible cells
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], VectorAgent.SHORT_VIS_RANGE);
        raster.getEasternCells(visibleCells, (int) loc[0], (int) loc[1]);

        // common part
        visibleCells = raster.getSlopeLessThan1D(visibleCells, VectorAgent.WALKABLE_SLOPE);
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);
        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];

        getLocalStack().push(ownerAgent.getLocation());


    }
}
