/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import directionupdate.Switch;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.agent.SkelatalAgent;
import strategy.updater.condition.ConditionChecker;

/**
 *
 * @author Johnny
 */
public class WanderStrategy implements Strategy {

    private String name;
    private static final Logger log = Logger.getLogger(WanderStrategy.class.getName());
//    private VectorAgent ownerAgent = null;
    // throttles the amount wandering at each step
    private List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
    private int timesteps = 0;
    
    private ConditionChecker conditionChecker = null;

    public ConditionChecker getConditionChecker() {
        return conditionChecker;
    }

    public void setConditionChecker(ConditionChecker conditionChecker) {
        this.conditionChecker = conditionChecker;
    }
    
    @Override
    public boolean getIsTimeToSwitch(SkelatalAgent va){
        
        if(getConditionChecker() == null){
            return false;
        }
        
        return getConditionChecker().checkCondition(va);
    }
    
    @Override
    public Integer getTimestep(){
        return timesteps;
    }
    

    private void replaceIfNotNull(DirectionUpdater oldUpdater, DirectionUpdater newUpdater) {
        if (newUpdater == null) {
            return;
        }
        int index = updaters.indexOf(oldUpdater);
        updaters.remove(oldUpdater);
        updaters.add(index, newUpdater);
    }
    
    

    @Override
    public void calculateNextMove(SkelatalAgent ownerAgent) {

        List<Switch> switches = new ArrayList<Switch>();
        
        // apply the force, then steer by a little
        for (DirectionUpdater updater : updaters) {
            
            // apply update to agent velocity
            updater.updateDirectionCaller(ownerAgent.getVelocityVector(), ownerAgent);
            
            
            
            log.log(Level.FINE, "running {0}", updater.toString());
            
            // if a condition has been met
            if(updater.switchIfConditionMet(ownerAgent)){
                switches.add(new Switch(updater, updater.getNextState()));
            }
            
        }
        
        // swap out accelerators if need be
        for(Switch switchie: switches){
            replaceIfNotNull(switchie.getOld(), switchie.getNewOne());
        }
        
        
        // make the move
        ownerAgent.steer(ownerAgent.getVelocityVector(), 1);
        
        // timesteps for this strategy
        timesteps++;
        

    }

   
    @Override
    public void addDirectionUpdater(DirectionUpdater updater) {
        this.updaters.add(updater);
    }
    
    public void addAllDirectinoUpdaters(List<DirectionUpdater> updadters){
        updaters.addAll(updadters);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
