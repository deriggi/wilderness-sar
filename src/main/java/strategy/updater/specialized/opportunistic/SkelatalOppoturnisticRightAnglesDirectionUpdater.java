/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.opportunistic;

import java.util.ArrayList;
import java.util.logging.Logger;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;
import statsutils.GameUtils;
import strategy.updater.Direction;
import strategy.updater.SkelatalDirectionUpdater;
import strategy.updater.conditionchecker.AlwaysTrueConditionChecker;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalOppoturnisticRightAnglesDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(SkelatalOppoturnisticRightAnglesDirectionUpdater.class.getName());
    public static final float CONSIDER_A_FIELD_CHANCE = 0.15f;
    // 16 for a moderately healty walker
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

    public void considerNorthSouthField(float percentChance, Raster2D raster, float[] loc) {

        // 15 percent considering the big field to our right or left
        if (GameUtils.percentChanceTrue(percentChance)) {

            float maxCellCount = VectorAgent.LONG_VIS_RANGE * VectorAgent.LONG_VIS_RANGE;
            ArrayList<SlopeDataCell> northFarCells = raster.getNorthVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
            ArrayList<SlopeDataCell> southFarCells = raster.getSouthVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);

            float northPortion = ((float) northFarCells.size()) / maxCellCount;

            float southPortion = ((float) southFarCells.size()) / maxCellCount;

            if (northPortion > 0.75f || southPortion > 0.75f) {

                // have a winner so go with biggest
                AlwaysTrueConditionChecker trueChecker = new AlwaysTrueConditionChecker();
                if (northPortion > southPortion) {
                    trueChecker.setNextState(new OpportunisticRightAnglesNorthernDirectionUpdater());
                } else {
                    trueChecker.setNextState(new OpportunisticRightAnglesSouthernDirectionUpdater());
                }
                setConditionChecker(trueChecker);

                // set always true with north opportune

            }
        }
    }

    public void considerEastWestField(float percentChance, Raster2D raster, float[] loc) {

        // 15 percent considering the big field to our right or left
        if (GameUtils.percentChanceTrue(percentChance)) {

            float maxCellCount = VectorAgent.LONG_VIS_RANGE * VectorAgent.LONG_VIS_RANGE;
            ArrayList<SlopeDataCell> eastFarCells = raster.getEastVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);
            ArrayList<SlopeDataCell> westFarCells = raster.getWestVisibleCells(loc, VectorAgent.LONG_VIS_RANGE, VectorAgent.WALKABLE_SLOPE);

            float eastPortion = ((float) eastFarCells.size()) / maxCellCount;

            float westPortion = ((float) westFarCells.size()) / maxCellCount;

            if (eastPortion > 0.75f || westPortion > 0.75f) {

                // have a winner so go with biggest
                AlwaysTrueConditionChecker trueChecker = new AlwaysTrueConditionChecker();
                if (eastPortion > westPortion) {
                    trueChecker.setNextState(new OppoturnisticRightAnglesEasternDirectionUpdater());
                } else {
                    trueChecker.setNextState(new OpportunisticRightAnglesWesternDirectionUpdater());
                }
                setConditionChecker(trueChecker);

            }
        }
    }
}
