/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import raster.domain.agent.AgentPropertyManager.AgentProperty;
import raster.domain.agent.FSMFactory;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;
import strategy.updater.HighGroundDirectionUpdater;
import strategy.updater.EasternDirectionUpdater;
import strategy.WanderStrategy;
import strategy.updater.BacktrackDirectionUpdater;
import strategy.updater.WanderDirectionUpdater;
import strategy.updater.WesternDirectionUpdater;
import strategy.updater.condition.Conditioner.Condish;
import strategy.updater.condition.DoubleAgentStateCondition;
import strategy.updater.condition.IntegerAgentStateCondition;
import strategy.updater.condition.StateCondition;
import strategy.updater.observer.ClearStackExitObserver;
import strategy.updater.observer.DirectionUpdaterObserver;

/**
 *
 * @author Johnny
 */
public class AgentService {

    private static AgentService service = new AgentService();
    private HashMap<Integer, VectorAgent> agents = new HashMap<Integer, VectorAgent>();
    private int nextId = 1;
    private static final Logger log = Logger.getLogger(AgentService.class.getName());
    

    private int getNextId() {
        return nextId++;
    }
    
    public VectorAgent createLostPersonAgent(float column, float row, float speed, FSMFactory.MachineName behaviour){
        VectorAgent a = new VectorAgent();
        a.setSpeed(speed);
        a.setLocation(new float[]{column,row});
        a.setOrigin(new float[]{column,row});
        a.setId(getNextId());
        agents.put(a.getId(), a);
        
        // strategery
        WanderStrategy wanderStrat = new WanderStrategy();
        wanderStrat.addAllDirectinoUpdaters( FSMFactory.getMachine(behaviour) );
        a.setMovementStrategy(wanderStrat);
        
        return a;
    }
    
    public VectorAgent createUAVAgent(float column, float row){
        VectorAgent a = new VectorAgent();
        a.setSpeed(4);
        a.setLocation(new float[]{column,row});
        a.setOrigin(new float[]{column,row});
        a.setId(getNextId());
        agents.put(a.getId(), a);
        
        // strategery
        WanderStrategy wanderStrat = new WanderStrategy();
        wanderStrat.addAllDirectinoUpdaters(FSMFactory.getMachine(FSMFactory.MachineName.EAST_WEST_LAWN_MOWER));
//        WanderDirectionUpdater wanderUpdater = new WanderDirectionUpdater();
//        wanderStrat.addDirectionUpdater(wanderUpdater);
        a.setMovementStrategy(wanderStrat);
        
        return a;
    }
    
    // the dynamic part that would be user supplied
    private List<DirectionUpdater> createStandardLostPersonBehavior(){
        ArrayList<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
        
        EasternDirectionUpdater easternUpdater = new EasternDirectionUpdater();
        WesternDirectionUpdater westernUpdater = new WesternDirectionUpdater();
        BacktrackDirectionUpdater backtrackUpdater = new BacktrackDirectionUpdater();
        BacktrackDirectionUpdater backtrackUpdater2 = new BacktrackDirectionUpdater();
        
        // east to backtrack
        StateCondition toBacktrackCondition = new DoubleAgentStateCondition(AgentProperty.LONGITUDE, Condish.GT, -116.86157);
        easternUpdater.setCondition(toBacktrackCondition);        
        toBacktrackCondition.setNextState(backtrackUpdater);
        
        // backtrack to west
        StateCondition toWestCondition = new IntegerAgentStateCondition(AgentProperty.STACK_SIZE, Condish.EQ, 0);
        backtrackUpdater.setCondition(toWestCondition);
        toWestCondition.setNextState(westernUpdater);
        
        // west to backtrack -- owned by westernUpdate
        StateCondition westToBacktrackCondition = new IntegerAgentStateCondition(AgentProperty.STACK_SIZE, Condish.EQ, 20);
        westernUpdater.setCondition(westToBacktrackCondition);        
        westToBacktrackCondition.setNextState(backtrackUpdater2);
        
        // back to original east
        StateCondition back2Condition = new IntegerAgentStateCondition(AgentProperty.STACK_SIZE, Condish.EQ, 0);
        backtrackUpdater2.setCondition(back2Condition);
        back2Condition.setNextState(easternUpdater);
        
        
        // when backtrack is done let's empty the stack
        backtrackUpdater.addExitObserver(new ClearStackExitObserver());
        backtrackUpdater2.addExitObserver(new ClearStackExitObserver());
        
        updaters.add(new HighGroundDirectionUpdater());
        updaters.add(westernUpdater);
        return updaters;
    }
    

    public static AgentService get() {

        return service;
    }

    public VectorAgent getAgent(int id) {
        return agents.get(id);
    }

    public Collection<VectorAgent> getAllAgents() {
        return agents.values();
    }

    private void load() {
        agents.put(getNextId(), new VectorAgent());
    }
}
