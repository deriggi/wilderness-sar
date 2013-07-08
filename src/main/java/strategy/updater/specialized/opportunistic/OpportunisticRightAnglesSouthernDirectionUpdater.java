/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.opportunistic;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.SkelatalAgent;
import raster.domain.agent.VectorAgent;
import statsutils.GameUtils;
import strategy.updater.Direction;

/**
 *
 * @author Johnny
 */
public class OpportunisticRightAnglesSouthernDirectionUpdater extends SkelatalOppoturnisticRightAnglesDirectionUpdater {

    private static final Logger log = Logger.getLogger(OpportunisticRightAnglesSouthernDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "south opportunistic walkable";
    }

    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();

        int maxVisibleCellCount = 0;
        Direction optimalDirection = null;
        ArrayList<SlopeDataCell> bestCells = null;

        ArrayList<SlopeDataCell> southernCells = raster.getSouthVisibleCells(loc, VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
        if (!directionEquals(getLastDirection(), Direction.NORTH)
                && southernCells.size() > maxVisibleCellCount) {
            maxVisibleCellCount = southernCells.size();
            optimalDirection = Direction.SOUTH;
            bestCells = southernCells;
        }

        ArrayList<SlopeDataCell> easternCells = raster.getEastVisibleCells(loc, VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
        if (GameUtils.percentChanceTrue(0.20f) && !directionEquals(getLastDirection(), Direction.WEST)
                && easternCells.size() > maxVisibleCellCount) {
            maxVisibleCellCount = easternCells.size();
            optimalDirection = Direction.EAST;
            bestCells = easternCells;
        }

        ArrayList<SlopeDataCell> westernCells = raster.getWestVisibleCells(loc, VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
        if (GameUtils.percentChanceTrue(0.20f) && !directionEquals(getLastDirection(), Direction.EAST)
                && westernCells.size() > maxVisibleCellCount) {
            maxVisibleCellCount = westernCells.size();
            optimalDirection = Direction.WEST;
            bestCells = westernCells;
        }

        if (bestCells != null) {
            log.log(Level.INFO, "adaptive going with {0}", optimalDirection.toString());
            setLastDirection(optimalDirection);
            float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, bestCells);

            dxDy[0] = acceleration[0];
            dxDy[1] = acceleration[1];
        } else {
            dxDy[0] = 0;
            dxDy[1] = 0;
            log.warning("velocity is zero");
            log.warning("LOL no good options");

        }

        if (GameUtils.percentChanceTrue(ownerAgent.getConsiderAFieldChance())) {
            considerEastWestField( raster, loc, ownerAgent);
        }
    }
}
