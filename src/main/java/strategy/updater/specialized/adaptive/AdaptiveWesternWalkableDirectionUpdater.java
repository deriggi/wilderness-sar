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
public class AdaptiveWesternWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(AdaptiveWesternWalkableDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "west walkable";
    }
    // 16 for a moderately healty walker
    private Direction lastDirection = null;
    

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();

        int maxVisibleCellCount = 0;
        Direction optimalDirection = null;
        ArrayList<SlopeDataCell> bestCells = null;


        ArrayList<SlopeDataCell> westernCells = raster.getWestVisibleCells( loc, VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
        if (westernCells.size() > maxVisibleCellCount) {
            maxVisibleCellCount = westernCells.size();
            optimalDirection = Direction.WEST;
            bestCells = westernCells;
        }

        ArrayList<SlopeDataCell> southernCells = raster.getSouthVisibleCells( loc, VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
        if (GameUtils.percentChanceTrue(0.20f) && !directionEquals(lastDirection, Direction.NORTH)
                && southernCells.size() > maxVisibleCellCount) {
            maxVisibleCellCount = southernCells.size();
            optimalDirection = Direction.SOUTH;
            bestCells = southernCells;
        }

        ArrayList<SlopeDataCell> northernCells = raster.getNorthVisibleCells( loc, VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
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
        
       
    }
    

    

    private boolean directionEquals(Direction lastDirection, Direction potentialDirection) {
        if (lastDirection == null || potentialDirection == null) {
            return false;
        }
        return lastDirection.equals(potentialDirection);
    }
}
