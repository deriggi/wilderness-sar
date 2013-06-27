/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import strategy.DirectionUpdater;
import strategy.updater.EastHighsWestLowsDirectionUpdater;
import strategy.updater.DoNothingDirectionUpdater;
import strategy.updater.BacktrackDirectionUpdater;
import strategy.updater.EasternDirectionUpdater;
import strategy.updater.GoHomeDirectionUpdater;
import strategy.updater.WesternDirectionUpdater;
import strategy.updater.HighGroundDirectionUpdater;
import strategy.updater.LowerGroundDirectionUpdater;
import strategy.updater.SouthernDirectionUpdater;
import strategy.updater.WanderDirectionUpdater;
import strategy.updater.conditionchecker.UpdaterConditionChecker;
import strategy.updater.conditionchecker.StackSizeEqualToConditionChecker;
import strategy.updater.conditionchecker.StackSizeGreaterThanConditionChecker;
import strategy.updater.conditionchecker.StepsTakenGreaterThanConditionChecker;
import strategy.updater.notificationhandler.DisableOnBacktrackNotificationHandler;
import strategy.updater.notificationhandler.DisableOnSouthNotificationHandler;
import strategy.updater.observer.ClearStackExitObserver;
import strategy.updater.observer.ClearStepsTakenExitObserver;

/**
 *
 * @author Johnny
 */
public class FSMFactory {

    private static final Logger log = Logger.getLogger(FSMFactory.class.getName());

    public static enum MachineName {

        EAST_WEST_HIGHS("East west highs"),
        EAST_WEST_LOWS("East west lows"),
        EAST_WEST_LAWN_MOWER("East west lawnmower"),
        SIMPLE_WANDER("Wander"),
        LOW_EAST_WANDER("East lows"),
        LOW_WEST_WANDER("West lows"),
        EAST_WEST_VALLEY_RIDGE("East highs west lows"),
        GO_TO_ORIGIN("Wander home"),
        DO_NOTHING("Do nothing");
        private String displayName;
        private String description;

        MachineName(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public String getDescription() {
            return this.description;
        }
    }
    private static final EnumMap<MachineName, FSMMaker> machineMap;

    public static MachineName getMachineName(String behaviour) {
        MachineName machine = null;

        try {
            machine = MachineName.valueOf(behaviour);

        } catch (IllegalArgumentException iae) {
            log.log(Level.SEVERE, "unknown machine name {0}", new Object[]{behaviour});
        }

        return machine;
    }

    private static interface FSMMaker {

        public List<DirectionUpdater> makeMachine();
    }

    /**
     *
     *   East highs west lows in a lawn mower behavior 
     */
    private static class ValleyRidgeMowerMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {
            //=============
            // track 1
            EasternDirectionUpdater east = new EasternDirectionUpdater();
            SouthernDirectionUpdater south = new SouthernDirectionUpdater();
            WesternDirectionUpdater west = new WesternDirectionUpdater();
            SouthernDirectionUpdater south2 = new SouthernDirectionUpdater();

            //==============
            // track 2
            EastHighsWestLowsDirectionUpdater eastHighsWestLows = new EastHighsWestLowsDirectionUpdater();
            eastHighsWestLows.addUpdaterNotificationHandler(new DisableOnSouthNotificationHandler());
            
            // easthighswestlows must shut off when south starts and on when south ends so it listens for both
            south.addUpdaterListener(eastHighsWestLows);
            south2.addUpdaterListener(eastHighsWestLows);

            // east to south
            UpdaterConditionChecker stackGreaterThan200 = new StackSizeGreaterThanConditionChecker(200);
            stackGreaterThan200.setNextState(south);
            east.setConditionChecker(stackGreaterThan200);
            east.addExitObserver(new ClearStackExitObserver());

            // south to west
            UpdaterConditionChecker stackGreaterThan90 = new StackSizeGreaterThanConditionChecker(90);
            stackGreaterThan90.setNextState(west);
            south.setConditionChecker(stackGreaterThan90);
            south.addExitObserver(new ClearStackExitObserver());

            // west to south
            UpdaterConditionChecker stackGreaterThan200_B = new StackSizeGreaterThanConditionChecker(200);
            stackGreaterThan200_B.setNextState(south2);
            west.setConditionChecker(stackGreaterThan200_B);
            west.addExitObserver(new ClearStackExitObserver());

            // close the loop south back to east
            UpdaterConditionChecker stackGreaterThan90_B = new StackSizeGreaterThanConditionChecker(90);
            stackGreaterThan90_B.setNextState(east);
            south2.setConditionChecker(stackGreaterThan90_B);
            south2.addExitObserver(new ClearStackExitObserver());

            List<DirectionUpdater> updaterList = new ArrayList<DirectionUpdater>();
            updaterList.add(east);
            updaterList.add(eastHighsWestLows);
            updaterList.add(new WanderDirectionUpdater());

            return updaterList;

        }
    }

    /**
     * Swings east bactracks west, backtracks then goes south and repeats
     * Has no wander built into it
     *          ------------------------
     *                  |
     *                  |
     *           -----------------------
     */
    private static class EastWestHighsMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {

            // the accelerators
            EasternDirectionUpdater easternUpdater = new EasternDirectionUpdater();
            SouthernDirectionUpdater southernUpdater = new SouthernDirectionUpdater();
            WesternDirectionUpdater westernUpdater = new WesternDirectionUpdater();
            BacktrackDirectionUpdater backtrackUpdater = new BacktrackDirectionUpdater();
            BacktrackDirectionUpdater backtrackUpdater2 = new BacktrackDirectionUpdater();

            // second track
            HighGroundDirectionUpdater highGroundUpdater = new HighGroundDirectionUpdater();


            // east to backtrack
            UpdaterConditionChecker stackGreaterThan50 = new StackSizeGreaterThanConditionChecker(50);
            easternUpdater.setConditionChecker(stackGreaterThan50);
            stackGreaterThan50.setNextState(backtrackUpdater);

            // backtrack to west
            UpdaterConditionChecker stackEqualToChecker  = new StackSizeEqualToConditionChecker(0);
            backtrackUpdater.setConditionChecker( stackEqualToChecker );
            stackEqualToChecker.setNextState(westernUpdater);

            // west to backtrack -- owned by westernUpdate
            UpdaterConditionChecker stackGreaterThan50_b = new StackSizeGreaterThanConditionChecker(50);
            westernUpdater.setConditionChecker(stackGreaterThan50_b);
            stackGreaterThan50_b.setNextState(backtrackUpdater2);

            // back to south
            UpdaterConditionChecker stackEqualToCheckerB  = new StackSizeEqualToConditionChecker(0);
            backtrackUpdater2.setConditionChecker(stackEqualToCheckerB);
            stackEqualToCheckerB.setNextState(southernUpdater);

            // back to original east to close the loop
            UpdaterConditionChecker stackGreaterThan50_b2 = new StackSizeGreaterThanConditionChecker(50);
            southernUpdater.setConditionChecker(stackGreaterThan50_b2);
            stackGreaterThan50_b2.setNextState(easternUpdater);

            // // back to original east to close the loop
            // StateCondition southToNothingCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STEPS_TAKEN, Condish.GT, 50);
            // sitAndDoNothingUpdater.setCondition(southToNothingCondition);
            // southToNothingCondition.setNextState(easternUpdater);

            backtrackUpdater.addExitObserver(new ClearStackExitObserver());
            backtrackUpdater2.addExitObserver(new ClearStackExitObserver());
            southernUpdater.addExitObserver(new ClearStackExitObserver());

            backtrackUpdater.addUpdaterListener(highGroundUpdater);
            backtrackUpdater2.addUpdaterListener(highGroundUpdater);
            southernUpdater.addUpdaterListener(highGroundUpdater);
            highGroundUpdater.addUpdaterNotificationHandler(new DisableOnBacktrackNotificationHandler());
            highGroundUpdater.addUpdaterNotificationHandler(new DisableOnSouthNotificationHandler());
            
            // ===============================
            // concurrent state machine track need this to disable seeking high on backtrack
            // ===============================
//            UpdaterConditionChecker stepsTakenGT50Checker = new StepsTakenGreaterThanConditionChecker(50);
//            highGroundUpdater.setConditionChecker(stepsTakenGT50Checker);
//            stepsTakenGT50Checker.setNextState(nothingUpdater);
//
//            UpdaterConditionChecker stepsTakenGT100Checker = new StepsTakenGreaterThanConditionChecker(100);
//            nothingUpdater.setConditionChecker(stepsTakenGT100Checker);
//            stepsTakenGT100Checker.setNextState(highGroundUpdater2);
//
//            UpdaterConditionChecker stepsTakenGT50bChecker = new StepsTakenGreaterThanConditionChecker(50);
//            highGroundUpdater2.setConditionChecker(stepsTakenGT50bChecker);
//            stepsTakenGT50bChecker.setNextState(nothingUpdater2);
//
//            UpdaterConditionChecker stepsTakenGT100bChecker = new StepsTakenGreaterThanConditionChecker(100);
//            nothingUpdater2.setConditionChecker(stepsTakenGT100bChecker);
//            stepsTakenGT100bChecker.setNextState(highGroundUpdater3);
//
//            UpdaterConditionChecker stepsTakenGT100cChecker = new StepsTakenGreaterThanConditionChecker(100);
//            highGroundUpdater3.setConditionChecker(stepsTakenGT100cChecker);
//            stepsTakenGT100cChecker.setNextState(highGroundUpdater);
//
//
//            nothingUpdater.addExitObserver(new ClearStepsTakenExitObserver());
//            nothingUpdater2.addExitObserver(new ClearStepsTakenExitObserver());
            


            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
            updaters.add(highGroundUpdater);
            updaters.add(easternUpdater);

            return updaters;

        }
    }

    /**
     *   Creates a lawnmower motion with no wander
     *
     */
    private static class LawnMowerMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {
            EasternDirectionUpdater easternUpdater = new EasternDirectionUpdater();
            SouthernDirectionUpdater southernUpdater = new SouthernDirectionUpdater();
            WesternDirectionUpdater westernUpdater = new WesternDirectionUpdater();
            SouthernDirectionUpdater southern2Updater = new SouthernDirectionUpdater();

            // east to south
            UpdaterConditionChecker stackGreaterThan200 = new StackSizeGreaterThanConditionChecker(200);
            stackGreaterThan200.setNextState(southernUpdater);
            easternUpdater.setConditionChecker(stackGreaterThan200);
            easternUpdater.addExitObserver(new ClearStackExitObserver());

            // south to west
            UpdaterConditionChecker stackGreaterThan10 = new StackSizeGreaterThanConditionChecker(10);
            stackGreaterThan10.setNextState(westernUpdater);
            southernUpdater.setConditionChecker(stackGreaterThan10);
            southernUpdater.addExitObserver(new ClearStackExitObserver());

            // west to south
            UpdaterConditionChecker stackGreaterThan200b = new StackSizeGreaterThanConditionChecker(200);
            stackGreaterThan200b.setNextState(southern2Updater);
            westernUpdater.setConditionChecker(stackGreaterThan200b);
            westernUpdater.addExitObserver(new ClearStackExitObserver());

            // close the loop south back to east
            UpdaterConditionChecker stackGreaterThan10c = new StackSizeGreaterThanConditionChecker(10);
            stackGreaterThan10c.setNextState(easternUpdater);
            southern2Updater.setConditionChecker(stackGreaterThan10c);
            southern2Updater.addExitObserver(new ClearStackExitObserver());

            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
            updaters.add(westernUpdater);
            return updaters;


        }
    }

    private static class GoHomeWanderMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {
            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();

            updaters.add(new WanderDirectionUpdater());
            updaters.add(new GoHomeDirectionUpdater());

            return updaters;
        }
    }

    private static class SimpleWanderMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {
            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
            updaters.add(new WanderDirectionUpdater());
            return updaters;
        }
    }

    private static class LowEastWanderMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {
            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();

            updaters.add(new LowerGroundDirectionUpdater());
            updaters.add(new EasternDirectionUpdater());
            updaters.add(new WanderDirectionUpdater());
            return updaters;
        }
    }

    private static class LowWestWanderMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {
            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();

            updaters.add(new LowerGroundDirectionUpdater());
            updaters.add(new WesternDirectionUpdater());
            updaters.add(new WanderDirectionUpdater());
            return updaters;
        }
    }

    private static class DoNothingWanderMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {
            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
            return updaters;
        }
    }

    public static List<DirectionUpdater> getMachine(MachineName name) {
        return machineMap.get(name).makeMachine();
    }

    static {
        machineMap = new EnumMap(MachineName.class);
        machineMap.put(MachineName.EAST_WEST_HIGHS, new EastWestHighsMaker());
        machineMap.put(MachineName.EAST_WEST_LAWN_MOWER, new LawnMowerMaker());
        machineMap.put(MachineName.SIMPLE_WANDER, new SimpleWanderMaker());
        machineMap.put(MachineName.LOW_EAST_WANDER, new LowEastWanderMaker());
        machineMap.put(MachineName.LOW_WEST_WANDER, new LowWestWanderMaker());
        machineMap.put(MachineName.EAST_WEST_VALLEY_RIDGE, new ValleyRidgeMowerMaker());
        machineMap.put(MachineName.DO_NOTHING, new DoNothingWanderMaker());
        machineMap.put(MachineName.GO_TO_ORIGIN, new GoHomeWanderMaker());


    }
//    private static HashMap<
}
