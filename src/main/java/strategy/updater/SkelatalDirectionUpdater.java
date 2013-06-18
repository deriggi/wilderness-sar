/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.ArrayList;
import java.util.List;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;
import strategy.updater.condition.StateCondition;
import strategy.updater.observer.DirectionUpdaterObserver;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalDirectionUpdater implements DirectionUpdater {

    private DirectionUpdater nextUpdater = null;
    private StateCondition condition = null;
    private List<DirectionUpdaterObserver> observers = null;
   
    private void notifyExitObservers(VectorAgent ownerAgent){
        if( observers != null){
            for(DirectionUpdaterObserver obs: observers){
                obs.notifyOfStateChange(ownerAgent);
            }
        }
    }
    
    public void addExitObserver(DirectionUpdaterObserver observer){
        if(observers == null){
            observers = new ArrayList<DirectionUpdaterObserver>();
        }
        observers.add(observer);
    }
    
    
    @Override
    public DirectionUpdater getNextState() {
        DirectionUpdater nexty = this.nextUpdater;
        this.nextUpdater = null;

        return nexty;
    }

    /**
     * 
     * @param sc 
     */
    @Override
    public void setCondition(StateCondition sc) {
        this.condition = sc;
    }
    
    /**
     * 
     * @param sc 
     */
    @Override
    public void getCondition(StateCondition sc) {
        this.condition = sc;
    }
    
    
    /**
     * Checks if the condition is met then switches to the next tactic
     * @param ownerAgent 
     */
    @Override
    public boolean switchIfConditionMet(VectorAgent ownerAgent){
        boolean isConditionMet = false;
        if(this.condition != null && this.condition.checkState(ownerAgent)){
            this.nextUpdater = this.condition.getNextState();
            isConditionMet = true;
            notifyExitObservers(ownerAgent);
        }   
        return isConditionMet;
    }
    
     
    
    
}
