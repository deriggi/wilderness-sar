/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.routesampler;

import java.util.ArrayList;
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
public class OutAndBackNorthernDirectionUpdater extends SkelatalOutAndBackWalkableDirectionUpdater {

    private static final Logger log = Logger.getLogger(OutAndBackNorthernDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "east walkable";
    }

    public OutAndBackNorthernDirectionUpdater(DirectionUpdater du){
        setNextDirectionUpdater(du);
    }
    
    @Override
    protected void doOutMode(double[] dxDy, SkelatalAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], VectorAgent.SHORT_VIS_RANGE);
        raster.getNorthernCells(visibleCells, (int) loc[0], (int) loc[1]);

        // common part
        visibleCells = raster.getSlopeLessThan1D(visibleCells, VectorAgent.WALKABLE_SLOPE);
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);
        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];

        getLocalStack().push(ownerAgent.getLocation());

        ArrayList<SlopeDataCell> northFarCells = raster.getNorthVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
        float portion = (float) northFarCells.size() / (VectorAgent.LONG_VIS_RANGE * VectorAgent.LONG_VIS_RANGE);

        getVisibleCountList().add((int) portion);
        ownerAgent.getMemory().put(Direction.NORTH.toString(), averageFieldOfView());
    }
}