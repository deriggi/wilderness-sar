/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker.borderconditions;

import raster.domain.agent.FSMFactory.LawnMowerMaker;
import raster.domain.agent.SkelatalAgent;
import strategy.DirectionUpdater;
import strategy.updater.Direction;
import strategy.updater.EasternDirectionUpdater;
import strategy.updater.NorthernDirectionUpdater;
import strategy.updater.SouthernDirectionUpdater;
import strategy.updater.WesternDirectionUpdater;
import strategy.updater.conditionchecker.LocalStackSizeEqualToConditionChecker;
import strategy.updater.observer.ClearLocalStackExitObserver;

/**
 * go north to east or north to west
 * or
 * south to east or south to west
 * @author Johnny
 */
public class NearEastWestBorderConditionChecker extends BorderConditionChecker {

    

    @Override
    public boolean checkCondition(SkelatalAgent va) {

        float[] loc = va.getLocation();

        // at west end and if MOTION IS WESTERLY then swtich
        if (Math.abs(loc[0] - getWestEnd()) < getThreshold()) {

            // at western edge!

            DirectionUpdater northSouthUpdater = null;
            if (va.getNorthSouthIntention().equals(Direction.NORTH)) {
                northSouthUpdater = new NorthernDirectionUpdater(LawnMowerMaker.MOWER_KEY);
            } else {
                northSouthUpdater = new SouthernDirectionUpdater(LawnMowerMaker.MOWER_KEY);
            }
            // go to east

            LocalStackSizeEqualToConditionChecker localCheck = new LocalStackSizeEqualToConditionChecker(LawnMowerMaker.MOWER_KEY, 10);
            northSouthUpdater.setConditionChecker(localCheck);
            setNextState(northSouthUpdater);
            northSouthUpdater.addExitObserver(new ClearLocalStackExitObserver(LawnMowerMaker.MOWER_KEY));

            EasternDirectionUpdater east = new EasternDirectionUpdater();
            east.setConditionChecker(new NearEastWestBorderConditionChecker());
            localCheck.setNextState(east);

            return true;
        }
        
        // if at east end and MOTION IS EASTERLY then switch
        if (Math.abs(loc[0] - getEastEnd()) < getThreshold()) {

            // at eastern edge!
            DirectionUpdater northSouthUpdater = null;
            if (va.getNorthSouthIntention().equals(Direction.NORTH)) {
                northSouthUpdater = new NorthernDirectionUpdater(LawnMowerMaker.MOWER_KEY);
            } else {
                northSouthUpdater = new SouthernDirectionUpdater(LawnMowerMaker.MOWER_KEY);
            }
            // go to east

            LocalStackSizeEqualToConditionChecker localCheck = new LocalStackSizeEqualToConditionChecker(LawnMowerMaker.MOWER_KEY, 10);
            northSouthUpdater.setConditionChecker(localCheck);
            setNextState(northSouthUpdater);
            northSouthUpdater.addExitObserver(new ClearLocalStackExitObserver(LawnMowerMaker.MOWER_KEY));
            
            WesternDirectionUpdater west = new WesternDirectionUpdater();
            west.setConditionChecker(new NearEastWestBorderConditionChecker());
            localCheck.setNextState(west);

            return true;
        }

        return false;

    }
}
