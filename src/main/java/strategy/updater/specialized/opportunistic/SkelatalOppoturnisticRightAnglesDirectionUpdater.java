/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.opportunistic;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.SkelatalAgent;
import raster.domain.agent.VectorAgent;
import statsutils.GameUtils;
import strategy.DirectionUpdater;
import strategy.updater.Direction;
import strategy.updater.SkelatalDirectionUpdater;
import strategy.updater.conditionchecker.AlwaysTrueConditionChecker;
import strategy.updater.conditionchecker.RightAnglesWhenStuckConditionChecker;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalOppoturnisticRightAnglesDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(SkelatalOppoturnisticRightAnglesDirectionUpdater.class.getName());
    private static final float WALKABLE_PORTION_THRESHOLD = 0.61f;
    private Direction lastDirection = null;

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(Direction lastDirection) {
        this.lastDirection = lastDirection;
    }

    public boolean directionEquals(Direction lastDirection, Direction potentialDirection) {
        if (lastDirection == null || potentialDirection == null) {
            return false;
        }
        return lastDirection.equals(potentialDirection);
    }

    /**
     * look far afield for a big open space and goes for it, then reduces the chances of looking for open space again
     * 
     * yeah could definitely generalize this bad boy by passing in the two batches of cells to consider
     * 
     * @param percentChance
     * @param raster
     * @param loc 
     */
    public void considerNorthSouthField(Raster2D raster, float[] loc, SkelatalAgent va) {

        // 15 percent considering the big field to our right or left
        log.info("considering an north south field");

        float maxCellCount = (float)Math.PI*VectorAgent.LONG_VIS_RANGE * VectorAgent.LONG_VIS_RANGE/2.0f;
        ArrayList<SlopeDataCell> northFarCells = raster.getNorthVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
        ArrayList<SlopeDataCell> southFarCells = raster.getSouthVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);

        float northPortion = ((float) northFarCells.size()) / maxCellCount;

        float southPortion = ((float) southFarCells.size()) / maxCellCount;

        log.log(Level.INFO, "field consideration data is {0} {1}", new Float[]{northPortion, southPortion});

        if (northPortion > WALKABLE_PORTION_THRESHOLD || southPortion > WALKABLE_PORTION_THRESHOLD) {
            log.log(Level.INFO, "found a field! ", new Float[]{northPortion, southPortion});
            DirectionUpdater du = null;
            va.setConsiderAFieldChance(va.getConsiderAFieldChance() / 2.0f);

            log.log(Level.INFO, "percent chance is now  {0} ", va.getConsiderAFieldChance());
            // have a winner so go with biggest
            AlwaysTrueConditionChecker trueChecker = new AlwaysTrueConditionChecker();

            if (northPortion > southPortion) {

                du = new OpportunisticRightAnglesNorthernDirectionUpdater();
                du.setConditionChecker(new RightAnglesWhenStuckConditionChecker(Direction.NORTH));

            } else {

                du = new OpportunisticRightAnglesSouthernDirectionUpdater();
                du.setConditionChecker(new RightAnglesWhenStuckConditionChecker(Direction.SOUTH));

            }

            trueChecker.setNextState(du);
            setConditionChecker(trueChecker);

        }
    }

    /**
     * look far afield for a big open space and goes for it, then reduces the chances of looking for open space again
     * 
     * yeah could definitely generalize this bad boy
     * 
     * @param percentChance
     * @param raster
     * @param loc 
     */
    public void considerEastWestField(Raster2D raster, float[] loc, SkelatalAgent va) {

        // 15 percent considering the big field to our right or left
        log.info("considering an east west field");
        float maxCellCount = VectorAgent.LONG_VIS_RANGE * VectorAgent.LONG_VIS_RANGE;
        ArrayList<SlopeDataCell> eastFarCells = raster.getEastVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
        ArrayList<SlopeDataCell> westFarCells = raster.getWestVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);

        float eastPortion = ((float) eastFarCells.size()) / maxCellCount;

        float westPortion = ((float) westFarCells.size()) / maxCellCount;

        log.log(Level.INFO, "field consideration data is {0} {1}", new Float[]{eastPortion, westPortion});

        if (eastPortion > WALKABLE_PORTION_THRESHOLD || westPortion > WALKABLE_PORTION_THRESHOLD) {

            log.info("settling on a far field");
            va.setConsiderAFieldChance(va.getConsiderAFieldChance() / 4.0f);
            log.log(Level.INFO, "percent chance is now  {0} ", va.getConsiderAFieldChance());

            DirectionUpdater du = null;

            // have a winner so go with biggest
            AlwaysTrueConditionChecker trueChecker = new AlwaysTrueConditionChecker();

            if (eastPortion > westPortion) {

                du = new OppoturnisticRightAnglesEasternDirectionUpdater();
                du.setConditionChecker(new RightAnglesWhenStuckConditionChecker(Direction.EAST));

            } else {

                du = new OpportunisticRightAnglesWesternDirectionUpdater();
                du.setConditionChecker(new RightAnglesWhenStuckConditionChecker(Direction.WEST));

            }

            setConditionChecker(trueChecker);
            trueChecker.setNextState(du);

        }
    }
}
