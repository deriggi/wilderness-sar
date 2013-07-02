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
public class PensiveNorthernWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(PensiveNorthernWalkableDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "east walkable";
    }
    // 16 for a moderately healty walker
    private float minSlope = .16f;
    private Direction direction;

    public PensiveNorthernWalkableDirectionUpdater(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], 10);

        raster.getNorthernCells(visibleCells, (int) loc[0], (int) loc[1]);
        visibleCells = raster.getSlopeLessThan1D(visibleCells, minSlope);

        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);

        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];
        
        log.log(Level.INFO, "a is {0} {1} with a mag of {2} ", new Object[]{acceleration[0], acceleration[1], VectorUtils.magnitude(acceleration)});


        if (this.direction.equals(Direction.WEST) && getWestVisibleCount(raster, loc) > visibleCells.size()) {
            log.info("north to west");
            AlwaysTrueConditionChecker keepAHoeTrue = new AlwaysTrueConditionChecker();
            keepAHoeTrue.setNextState(new PensiveWesternWalkableDirectionUpdater());
            setConditionChecker(keepAHoeTrue);

        } else if (this.direction.equals(Direction.EAST) && getEastVisibleCount(raster, loc) > visibleCells.size()) {
            log.info("north to east");
            AlwaysTrueConditionChecker keepAHoeTrue = new AlwaysTrueConditionChecker();
            keepAHoeTrue.setNextState(new PensiveEasternWalkableDirectionUpdater());
            setConditionChecker(keepAHoeTrue);
        }

    }

    private int getWestVisibleCount(Raster2D raster, float[] loc) {
        return raster.getSlopeLessThan1D(raster.getWesternCells(raster.getVisibleCells((int) loc[0], (int) loc[1], 10), (int) loc[0], (int) loc[1]), minSlope).size();
    }

    private int getEastVisibleCount(Raster2D raster, float[] loc) {
        return raster.getSlopeLessThan1D(raster.getEasternCells(raster.getVisibleCells((int) loc[0], (int) loc[1], 10), (int) loc[0], (int) loc[1]), minSlope).size();
    }
}