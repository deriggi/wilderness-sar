/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.uavteam;

import geomutils.VectorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
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
public class UavOutAndBackWesternDirectionUpdater extends UavSkelatalOutAndBackWalkableDirectionUpdater {

    private static final Logger log = Logger.getLogger(UavOutAndBackWesternDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "west walkable";
    }

    @Override
    protected void doOutMode(double[] dxDy, SkelatalAgent ownerAgent) {
        if (!isRegistered()) {
            Communications.register(SkelatalAgent.COMS, ownerAgent);
            setRegistered(true);
        }

        float distanceFromHome = distanceFromHomeConsideringStuckPenalty(ownerAgent);

        // build message
        HashMap<String, Float> message = new HashMap<String, Float>(1);
        message.put(Direction.WEST.toString(), distanceFromHome);
        Communications.relayMessage(SkelatalAgent.COMS, message);

        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], VectorAgent.SHORT_VIS_RANGE);
        raster.getWesternCells(visibleCells, (int) loc[0], (int) loc[1]);

        // common part
        visibleCells = raster.getSlopeLessThan1D(visibleCells, VectorAgent.WALKABLE_SLOPE);
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);
        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];
        getLocalStack().push(ownerAgent.getLocation());


    }
}
