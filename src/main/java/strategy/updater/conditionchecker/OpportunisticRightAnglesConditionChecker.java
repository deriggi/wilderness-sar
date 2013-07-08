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
import raster.domain.agent.SkelatalAgent;
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
public class OpportunisticRightAnglesConditionChecker extends SkelatalUpdaterConditionChecker {

    private final static Logger log = Logger.getLogger(OpportunisticRightAnglesConditionChecker.class.getName());
    private final int POINTS_TO_CHECK = 50;
    private int iterations = 0;
    private boolean doneStartingOut = false;
    private Direction lastDirection = null;
    
    private RightAnglesAdaptiveEasternDirectionUpdater east;
    private RightAnglesAdaptiveWesternDirectionUpdater west;
    private RightAnglesAdaptiveNorthernDirectionUpdater north;
    private RightAnglesAdaptiveSouthernDirectionUpdater south;
    
    
    
    public OpportunisticRightAnglesConditionChecker(Direction lastDirecion) {
        this.lastDirection = lastDirecion;
    }

    @Override
    public boolean checkCondition(SkelatalAgent va) {

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
            
            // handle stuck
            
            Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();

            if (lastDirection.equals(Direction.NORTH) || lastDirection.equals(Direction.SOUTH)) {
                
                int eastWalkableCount = raster.getEastVisibleCount(va.getLocation(), VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
                int westWalkableCount = raster.getWestVisibleCount(va.getLocation(), VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
                
                if(eastWalkableCount > westWalkableCount){
                    
                    // east is the big winner
                    east = new RightAnglesAdaptiveEasternDirectionUpdater();
                    east.setConditionChecker(new OpportunisticRightAnglesConditionChecker(Direction.EAST));
                    setNextState(east);
                    
                }else{
                    
                    // westerly
                    west = new RightAnglesAdaptiveWesternDirectionUpdater();
                    west.setConditionChecker(new OpportunisticRightAnglesConditionChecker(Direction.WEST));
                    setNextState(west);
                    
                }
            }
            else if (lastDirection.equals(Direction.EAST) || lastDirection.equals(Direction.WEST)){
                
                int northWalkableCount = raster.getNorthVisibleCount(va.getLocation(), VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
                int southWalkableCount = raster.getSouthVisibleCount(va.getLocation(), VectorAgent.SHORT_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
                
                if(northWalkableCount > southWalkableCount){
                    
                    // north
                    north = new RightAnglesAdaptiveNorthernDirectionUpdater();
                    north.setConditionChecker(new OpportunisticRightAnglesConditionChecker(Direction.NORTH));
                    setNextState(north);
                    
                }else {
                    
                    // south
                    south = new RightAnglesAdaptiveSouthernDirectionUpdater();
                    south.setConditionChecker(new OpportunisticRightAnglesConditionChecker(Direction.SOUTH));
                    setNextState(south);
                    
                }
            }
            return true;
        }
        return false;
    }
}
