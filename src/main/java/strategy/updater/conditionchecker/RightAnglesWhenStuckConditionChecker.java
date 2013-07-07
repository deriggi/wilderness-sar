/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.agent.VectorAgent;
import strategy.updater.Direction;
import strategy.updater.specialized.rightangles.RightAnglesAdaptiveEasternDirectionUpdater;
import strategy.updater.specialized.rightangles.RightAnglesAdaptiveNorthernDirectionUpdater;
import strategy.updater.specialized.rightangles.RightAnglesAdaptiveSouthernDirectionUpdater;
import strategy.updater.specialized.rightangles.RightAnglesAdaptiveWesternDirectionUpdater;

/**
 *
 * @author Johnny
 */
public class RightAnglesWhenStuckConditionChecker extends SkelatalUpdaterConditionChecker {

    private final static Logger log = Logger.getLogger(RightAnglesWhenStuckConditionChecker.class.getName());
    private final int POINTS_TO_CHECK = 50;
    private int iterations = 0;
    private boolean doneStartingOut = false;
    private Direction lastDirection = null;
    
    private RightAnglesAdaptiveEasternDirectionUpdater east;
    private RightAnglesAdaptiveWesternDirectionUpdater west;
    private RightAnglesAdaptiveNorthernDirectionUpdater north;
    private RightAnglesAdaptiveSouthernDirectionUpdater south;
    
    
    public RightAnglesWhenStuckConditionChecker(Direction lastDirecion) {
        this.lastDirection = lastDirecion;
        east = new RightAnglesAdaptiveEasternDirectionUpdater();
        // do the same with nort south etc..
    }

    @Override
    public boolean checkCondition(VectorAgent va) {

        // over the last fifty points, is this agent's straight line distance less 
        // than twice it's velocity

        // wait fifty steps before we check if stuck, thus to clear the agent buffer
        if (!doneStartingOut && iterations++ < POINTS_TO_CHECK) {
            return false;
        } else {
            iterations = 0;
            doneStartingOut = true;
        }

        Float average = va.averageDistanceLastXPoints(POINTS_TO_CHECK);
        log.log(Level.INFO, "average of last fifty is {0} comparing to {1} ", new Float[]{average, va.getSpeed() * 2});

        if (average != null && average < va.getSpeed() * 2) {
            Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();

            if (lastDirection.equals(Direction.NORTH) || lastDirection.equals(Direction.SOUTH)) {
                int eastWalkableCount = raster.getEastVisibleCount(va.getLocation(), VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
                int westWalkableCount = raster.getWestVisibleCount(va.getLocation(), VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
                if(eastWalkableCount > westWalkableCount){
                    // set next to right angle east with a new one of these condition checkers
                    east.setConditionChecker(new RightAnglesWhenStuckConditionChecker(Direction.EAST));
                    setNextState(east);
                }
            }
            else if (lastDirection.equals(Direction.EAST) || lastDirection.equals(Direction.WEST)){
                int northWalkableCount = raster.getNorthVisibleCount(va.getLocation(), VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
                int southWalkableCount = raster.getSouthVisibleCount(va.getLocation(), VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
            }
            return true;
        }

        return false;


    }
}
