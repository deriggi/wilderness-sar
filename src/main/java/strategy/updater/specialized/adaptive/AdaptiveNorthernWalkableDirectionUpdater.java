/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.adaptive;

import geomutils.VectorUtils;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;
import strategy.updater.Direction;
import strategy.updater.SkelatalDirectionUpdater;
import strategy.updater.conditionchecker.AlwaysTrueConditionChecker;

/**
 *
 * @author Johnny
 */
public class AdaptiveNorthernWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(AdaptiveNorthernWalkableDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "east walkable";
    }
    // 16 for a moderately healty walker
    private Direction direction;
    private int iteration = 0;
    int visibilityRadius = 10;
    
    public AdaptiveNorthernWalkableDirectionUpdater(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], 10);

        raster.getNorthernCells(visibleCells, (int) loc[0], (int) loc[1]);
        visibleCells = raster.getSlopeLessThan1D(visibleCells, VectorAgent.WALKABLE_SLOPE);

        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);

        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];

        log.log(Level.INFO, "a is {0} {1} with a mag of {2} ", new Object[]{acceleration[0], acceleration[1], VectorUtils.magnitude(acceleration)});


        if (iteration++ > 20) {


            if (this.direction.equals(Direction.WEST) && getWestVisibleCount(raster, loc, visibilityRadius, VectorAgent.WALKABLE_SLOPE) > visibleCells.size()) {
                log.info("north to west");
                AlwaysTrueConditionChecker keepAHoeTrue = new AlwaysTrueConditionChecker();
                keepAHoeTrue.setNextState(new AdaptiveWesternWalkableDirectionUpdater());
                setConditionChecker(keepAHoeTrue);

            } else if (this.direction.equals(Direction.EAST) && getEastVisibleCount(raster, loc, visibilityRadius, VectorAgent.WALKABLE_SLOPE) > visibleCells.size()) {
                log.info("north to east");
                AlwaysTrueConditionChecker keepAHoeTrue = new AlwaysTrueConditionChecker();
                keepAHoeTrue.setNextState(new AdaptiveEasternWalkableDirectionUpdater());
                setConditionChecker(keepAHoeTrue);
            } else if (VectorUtils.magnitude(acceleration) == 0 || ownerAgent.isAgitated()) {
                log.info("mag is zero trying to switch");
                AlwaysTrueConditionChecker keepAHoeTrue = new AlwaysTrueConditionChecker();
                keepAHoeTrue.setNextState(getDu(this.direction));
                setConditionChecker(keepAHoeTrue);
                ownerAgent.getDotProductBuffer().clear();
            }

        }

    }

    private DirectionUpdater getDu(Direction direction) {
        if (Direction.EAST.equals(direction)) {
            log.info("south to east");
            return new AdaptiveEasternWalkableDirectionUpdater();
        }
        log.info("south to west");
        return new AdaptiveWesternWalkableDirectionUpdater();


    }

}