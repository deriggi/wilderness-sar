/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.adaptive;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;
import statsutils.GameUtils;
import strategy.updater.Direction;
import strategy.updater.SkelatalDirectionUpdater;

/**
 *
 * @author Johnny
 */
public class AdaptiveEasternWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(AdaptiveEasternWalkableDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "adaptive east";
    }
    // 16 for a moderately healty walker
    private int visibilityRadius = 10;
    private Direction lastDirection = null;

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();

        int maxVisibleCellCount = 0;
        Direction optimalDirection = null;
        ArrayList<SlopeDataCell> bestCells = null;


        ArrayList<SlopeDataCell> easternCells = getEastVisibleCells(raster, loc, visibilityRadius, VectorAgent.WALKABLE_SLOPE);
        if (easternCells.size() > maxVisibleCellCount) {
            maxVisibleCellCount = easternCells.size();
            optimalDirection = Direction.EAST;
            bestCells = easternCells;
        }

        ArrayList<SlopeDataCell> southernCells = getSouthVisibleCells(raster, loc, visibilityRadius, VectorAgent.WALKABLE_SLOPE);
        if (GameUtils.percentChanceTrue(0.20f) && !directionEquals(lastDirection, Direction.NORTH)
                && southernCells.size() > maxVisibleCellCount) {
            maxVisibleCellCount = southernCells.size();
            optimalDirection = Direction.SOUTH;
            bestCells = southernCells;
        }

        ArrayList<SlopeDataCell> northernCells = getNorthVisibleCells(raster, loc, visibilityRadius, VectorAgent.WALKABLE_SLOPE);
        if (GameUtils.percentChanceTrue(0.20f) && !directionEquals(lastDirection, Direction.SOUTH)
                && northernCells.size() > maxVisibleCellCount) {
            maxVisibleCellCount = northernCells.size();
            optimalDirection = Direction.NORTH;
            bestCells = northernCells;
        }

        if (bestCells != null) {
            log.log(Level.INFO, "adaptive going with {0}", optimalDirection.toString());
            lastDirection = optimalDirection;
            float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, bestCells);

            dxDy[0] = acceleration[0];
            dxDy[1] = acceleration[1];
        } else {
            dxDy[0] = 0;
            dxDy[1] = 0;
            log.warning("velocity is zero");
            log.warning("LOL no good options");

        }

//        Float averageDistance = ownerAgent.averageDistanceLastXPoints(50);
//        if (averageDistance != null && averageDistance < ownerAgent.getSpeed() * 2) {
//            log.log(Level.INFO, "Stuck Alert! {0} points is {1}", new Float[]{(float) 50, ownerAgent.averageDistanceLastXPoints(50)});
//        }
    }

    // make a condiction checker for this mother fucker to go west if stuck
    // make condish checker to go to best opportunity if it sees a great opportunity
    
    private boolean directionEquals(Direction lastDirection, Direction potentialDirection) {
        if (lastDirection == null || potentialDirection == null) {
            return false;
        }
        return lastDirection.equals(potentialDirection);
    }
}
