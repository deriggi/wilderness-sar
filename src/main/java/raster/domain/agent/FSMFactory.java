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
import strategy.updater.WesternDirectionUpdater;
import strategy.updater.condition.Conditioner.Condish;
import strategy.updater.condition.StateCondition;
import strategy.updater.HighGroundDirectionUpdater;
import strategy.updater.LowerGroundDirectionUpdater;
import strategy.updater.SouthernDirectionUpdater;
import strategy.updater.WanderDirectionUpdater;
import strategy.updater.condition.IntegerAgentStateCondition;
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
        EAST_WEST_LAWN_MOWER("East west lawnmore"),
        SIMPLE_WANDER("Wander"), 
        LOW_EAST_WANDER("East lows"), 
        LOW_WEST_WANDER("West lows"),
        EAST_WEST_VALLEY_RIDGE("East highs west lows"),
        DO_NOTHING("Do nothing");

        private String displayName;
        private String description;

        MachineName(String displayName){
            this.displayName = displayName;
        }

        public String getDisplayName(){
            return this.displayName;
        }

        public String getDescription(){
            return this.description;
        }
        

    }


    private static final EnumMap<MachineName, FSMMaker> machineMap;

    public static MachineName getMachineName(String behaviour){
        MachineName machine = null;

        try{
            
             machine = MachineName.valueOf(behaviour);
            

        } catch (IllegalArgumentException iae){
            log.log(Level.SEVERE,"unknown machine name {0}", new Object[]{behaviour});
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
        public List<DirectionUpdater> makeMachine(){
            //=============
            // track 1
            EasternDirectionUpdater east = new EasternDirectionUpdater();
            SouthernDirectionUpdater south = new SouthernDirectionUpdater();
            WesternDirectionUpdater west = new WesternDirectionUpdater();
            SouthernDirectionUpdater south2 = new SouthernDirectionUpdater();
            
            //==============
            // track 2
            EastHighsWestLowsDirectionUpdater eastHighsWestLows =  new EastHighsWestLowsDirectionUpdater();
            
            // easthighswestlows must shut off when south starts and on when south ends so it listens for both
            south.addUpdaterListener(eastHighsWestLows);
            south2.addUpdaterListener(eastHighsWestLows);
            

             // east to south
            StateCondition eastToSouthCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 200);
            eastToSouthCondition.setNextState(south);
            east.setCondition(eastToSouthCondition);
            east.addExitObserver(new ClearStackExitObserver());

            // south to west
            StateCondition southToWestCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 90);
            southToWestCondition.setNextState(west);
            south.setCondition(southToWestCondition);
            south.addExitObserver(new ClearStackExitObserver());
            
            // west to south
            StateCondition westToSouthCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 200);
            westToSouthCondition.setNextState(south2);
            west.setCondition(westToSouthCondition);
            west.addExitObserver(new ClearStackExitObserver());
            
            // close the loop south back to east
            StateCondition southToOriginalEastCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 90 );
            southToOriginalEastCondition.setNextState(east);
            south2.setCondition(southToOriginalEastCondition);
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
            DoNothingDirectionUpdater nothingUpdater = new DoNothingDirectionUpdater();

            HighGroundDirectionUpdater highGroundUpdater2 = new HighGroundDirectionUpdater();
            DoNothingDirectionUpdater nothingUpdater2 = new DoNothingDirectionUpdater();
            HighGroundDirectionUpdater highGroundUpdater3 = new HighGroundDirectionUpdater();


            // east to backtrack
            StateCondition toBacktrackCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.GT, 50);
            easternUpdater.setCondition(toBacktrackCondition);
            toBacktrackCondition.setNextState(backtrackUpdater);

            // backtrack to west
            StateCondition toWestCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.EQ, 0);
            backtrackUpdater.setCondition(toWestCondition);
            toWestCondition.setNextState(westernUpdater);

            // west to backtrack -- owned by westernUpdate
            StateCondition westToBacktrackCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.EQ, 50);
            westernUpdater.setCondition(westToBacktrackCondition);
            westToBacktrackCondition.setNextState(backtrackUpdater2);

            // back to south
            StateCondition back2Condition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.EQ, 0);
            backtrackUpdater2.setCondition(back2Condition);
            back2Condition.setNextState(southernUpdater);

            // back to original east to close the loop
            StateCondition southToEastCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STACK_SIZE, Condish.EQ, 100);
            southernUpdater.setCondition(southToEastCondition);
            southToEastCondition.setNextState(easternUpdater);

            // // back to original east to close the loop
            // StateCondition southToNothingCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STEPS_TAKEN, Condish.GT, 50);
            // sitAndDoNothingUpdater.setCondition(southToNothingCondition);
            // southToNothingCondition.setNextState(easternUpdater);

            backtrackUpdater.addExitObserver(new ClearStackExitObserver());
            backtrackUpdater2.addExitObserver(new ClearStackExitObserver());
            southernUpdater.addExitObserver(new ClearStackExitObserver());


            // ===============================
            // concurrent state machine track need this to disable seeking high on backtrack
            // ===============================
            StateCondition highToNothingCondition = new IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STEPS_TAKEN, Condish.GT, 50);
            highGroundUpdater.setCondition(highToNothingCondition);
            highToNothingCondition.setNextState(nothingUpdater);

            StateCondition nothingToHighCondition = new  IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STEPS_TAKEN, Condish.GT, 100);
            nothingUpdater.setCondition(nothingToHighCondition);
            nothingToHighCondition.setNextState(highGroundUpdater2);

            StateCondition highToNothingCondition2 = new  IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STEPS_TAKEN, Condish.GT, 50);
            highGroundUpdater2.setCondition(highToNothingCondition2);
            highToNothingCondition2.setNextState(nothingUpdater2);

            StateCondition nothingToHighCondition2 = new  IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STEPS_TAKEN, Condish.GT, 100);
            nothingUpdater2.setCondition(nothingToHighCondition2);
            nothingToHighCondition2.setNextState(highGroundUpdater3);

            StateCondition highToHighCondition = new  IntegerAgentStateCondition(AgentPropertyManager.AgentProperty.STEPS_TAKEN, Condish.GT, 100);
            highGroundUpdater3.setCondition(highToHighCondition);
            highToHighCondition.setNextState(highGroundUpdater);


            nothingUpdater.addExitObserver(new ClearStepsTakenExitObserver());
            nothingUpdater2.addExitObserver(new ClearStepsTakenExitObserver());
            highGroundUpdater3.addExitObserver(new ClearStepsTakenExitObserver());


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

    private static class DoNothingWanderMaker implements FSMMaker{
        
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


    }
//    private static HashMap<
}
