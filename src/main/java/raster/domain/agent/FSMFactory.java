/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.logging.Logger;
import strategy.DirectionUpdater;
import strategy.updater.EastHighsWestLowsDirectionUpdater;
import strategy.updater.BacktrackDirectionUpdater;
import strategy.updater.EasternDirectionUpdater;
import strategy.updater.WesternDirectionUpdater;
import strategy.updater.condition.Conditioner.Condish;
import strategy.updater.condition.DoubleAgentStateCondition;
import strategy.updater.condition.StateCondition;
import strategy.updater.HighGroundDirectionUpdater;
import strategy.updater.LowerGroundDirectionUpdater;
import strategy.updater.SouthernDirectionUpdater;
import strategy.updater.WanderDirectionUpdater;
import strategy.updater.condition.IntegerAgentStateCondition;
import strategy.updater.observer.ClearStackExitObserver;

/**
 *
 * @author Johnny
 */
public class FSMFactory {

    private static final Logger log = Logger.getLogger(FSMFactory.class.getName());

    public static enum MachineName {

        EAST_WEST_HIGHS, EAST_WEST_LOWS, EAST_WEST_LAWN_MOWER, SIMPLE_WANDER, LOW_WANDER;
    }
    private static final EnumMap<MachineName, FSMMaker> machineMap;

    private static interface FSMMaker {

        public List<DirectionUpdater> makeMachine();
    }

    private static class ValleyPeakMaker implements FSMMaker {
        @Override
        public List<DirectionUpdater> makeMachine(){
            List<DirectionUpdater> updaterList = new ArrayList<DirectionUpdater>();

            updaterList.add(new EastHighsWestLowsDirectionUpdater());
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

            // east to backtrack
            StateCondition toBacktrackCondition = new DoubleAgentStateCondition(AgentPropertyManager.AgentProperty.LONGITUDE, Condish.GT, -116.86157);
            easternUpdater.setCondition(toBacktrackCondition);
            toBacktrackCondition.setNextState(backtrackUpdater);

            // backtrack to west
            StateCondition toWestCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.EQ, 0);
            backtrackUpdater.setCondition(toWestCondition);
            toWestCondition.setNextState(westernUpdater);

            // west to backtrack -- owned by westernUpdate
            StateCondition westToBacktrackCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.EQ, 20);
            westernUpdater.setCondition(westToBacktrackCondition);
            westToBacktrackCondition.setNextState(backtrackUpdater2);

            // back to south
            StateCondition back2Condition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.EQ, 0);
            backtrackUpdater2.setCondition(back2Condition);
            back2Condition.setNextState(southernUpdater);

            // back to original east to close the loop
            StateCondition southToEastCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.EQ, 30);
            backtrackUpdater2.setCondition(southToEastCondition);
            southToEastCondition.setNextState(easternUpdater);

            
            backtrackUpdater.addExitObserver(new ClearStackExitObserver());
            backtrackUpdater2.addExitObserver(new ClearStackExitObserver());

            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
            updaters.add(new HighGroundDirectionUpdater());
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
            StateCondition eastToSouthCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 60);
            eastToSouthCondition.setNextState(southernUpdater);
            easternUpdater.setCondition(eastToSouthCondition);
            easternUpdater.addExitObserver(new ClearStackExitObserver());

            // south to west
            StateCondition southToWestCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 20);
            southToWestCondition.setNextState(westernUpdater);
            southernUpdater.setCondition(southToWestCondition);
            southernUpdater.addExitObserver(new ClearStackExitObserver());
            
            // west to south
            StateCondition westToSouthCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 60);
            westToSouthCondition.setNextState(southern2Updater);
            westernUpdater.setCondition(westToSouthCondition);
            westernUpdater.addExitObserver(new ClearStackExitObserver());
            
            // close the loop south back to east
            StateCondition southToOriginalEastCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 20);
            southToOriginalEastCondition.setNextState(easternUpdater);
            southern2Updater.setCondition(southToOriginalEastCondition);
            southern2Updater.addExitObserver(new ClearStackExitObserver());
            
            
            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
            updaters.add(westernUpdater);
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
    
     private static class LowWanderMaker implements FSMMaker {

        @Override
        public List<DirectionUpdater> makeMachine() {
            List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
            
            updaters.add(new LowerGroundDirectionUpdater());
            updaters.add(new EasternDirectionUpdater());
            updaters.add(new WanderDirectionUpdater());
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
        machineMap.put(MachineName.LOW_WANDER, new LowWanderMaker());
    }
//    private static HashMap<
}
