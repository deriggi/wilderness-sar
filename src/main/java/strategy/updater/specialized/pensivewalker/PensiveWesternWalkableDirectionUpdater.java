/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.pensivewalker;

import geomutils.VectorUtils;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;
import strategy.updater.Direction;
import strategy.updater.SkelatalDirectionUpdater;
import strategy.updater.conditionchecker.AlwaysTrueConditionChecker;

/**
 *
 * @author Johnny
 */
public class PensiveWesternWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(PensiveWesternWalkableDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "west walkable";
    }
    // 16 for a moderately healty walker
    private int visibilityRadius = 10;

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius);

//        raster.getEasternCutoutCells(visibleCells, (int)loc[0], (int)loc[1], 8);
        raster.getWesternCells(visibleCells, (int) loc[0], (int) loc[1]);
        visibleCells = raster.getSlopeLessThan1D(visibleCells, VectorAgent.WALKABLE_SLOPE);

        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);

        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];
        log.log(Level.INFO, "a is {0} {1} with a mag of {2} ", new Object[]{acceleration[0], acceleration[1], VectorUtils.magnitude(acceleration)});
        // if velocity is zero compare north and south options

        if (VectorUtils.magnitude(acceleration) == 0) {
            goNorthOrSouth(raster, loc);
        } else if (ownerAgent.isAgitated()) {
            goNorthOrSouth(raster, loc);
            ownerAgent.getDotProductBuffer().clear();
        }

        // for n or s, once west provides a better option go west


    }

    private void goNorthOrSouth(Raster2D raster, float[] loc) {
        log.info("checking north or south");
        AlwaysTrueConditionChecker keepAHoeTrue = new AlwaysTrueConditionChecker();
        setConditionChecker(keepAHoeTrue);

        int southernCellCount = getSouthVisibleCount(raster, loc, visibilityRadius,VectorAgent.WALKABLE_SLOPE);
        int northernCellCount = getNorthVisibleCount(raster, loc, visibilityRadius, VectorAgent.WALKABLE_SLOPE);
        log.log(Level.INFO, "comparing south {0} to north {1}", new Object[]{southernCellCount, northernCellCount});

        if (southernCellCount > northernCellCount) {
            // goSouth
            log.info("west to south");
            keepAHoeTrue.setNextState(new PensiveSouthernWalkableDirectionUpdater(Direction.EAST));


        } else if (northernCellCount > southernCellCount) {
            // goNorth
            log.info("west to north");
            keepAHoeTrue.setNextState(new PensiveNorthernWalkableDirectionUpdater(Direction.EAST));

        } else if (northernCellCount == 0 && southernCellCount == 0) {
            log.info("both zero so heading back east");
            keepAHoeTrue.setNextState(new PensiveEasternWalkableDirectionUpdater());
        } else if (northernCellCount == southernCellCount) {
            // flip a coin
            if (Math.random() > 0.5) {
                keepAHoeTrue.setNextState(new PensiveNorthernWalkableDirectionUpdater(Direction.EAST));
            } else {
                keepAHoeTrue.setNextState(new PensiveSouthernWalkableDirectionUpdater(Direction.EAST));

            }
        }
    }

}
