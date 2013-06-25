/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;
import strategy.updater.condition.StateCondition;
import strategy.updater.message.FinishMessage;
import strategy.updater.message.StartMessage;
import strategy.updater.observer.DirectionUpdaterObserver;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalDirectionUpdater implements DirectionUpdater {

    private static Logger log  =  Logger.getLogger(SkelatalDirectionUpdater.class.getName());
    
    private DirectionUpdater nextUpdater = null;
    private StateCondition condition = null;
    private List<DirectionUpdaterObserver> observers = null;
    private List<DirectionUpdater> updaterObservers = null;
    private boolean started = false;
    private boolean isDisabled = false;
    
    public void addUpdaterListener(DirectionUpdater updater) {
        if (updaterObservers == null) {
            updaterObservers = new ArrayList<DirectionUpdater>();
        }
        updaterObservers.add(updater);

    }
    
    @Override
    public void setDisabled(boolean b){
        if(b == false){
            log.info("someone is setting to enabled");
        }
        isDisabled = b;
    }
    
    @Override
    public boolean isDisabled(){
        return isDisabled;
    }
    

    
    @Override
    public void updateDirectionCaller(double velocity[], VectorAgent agent){
        if(!started){
            notifyStartObservers();
            started = true;
        }
        updateDirection(velocity, agent);
        
    }
    
    public void notifyStartObservers() {
        StartMessage start = new StartMessage();
        start.setFrom(this.toString());

        if (updaterObservers != null) {
            for (DirectionUpdater updater : updaterObservers) {
                updater.notifyMe(start);
            }
        }
    }


    private void notifyExitObservers(VectorAgent ownerAgent) {
        if (observers != null) {
            for (DirectionUpdaterObserver obs : observers) {
                obs.notifyOfStateChange(ownerAgent);
            }
        }

        FinishMessage finish = new FinishMessage();
        finish.setFrom(this.toString());

        if (updaterObservers != null) {
            for (DirectionUpdater updater : updaterObservers) {
                updater.notifyMe(finish);
            }
        }
    }

    public void addExitObserver(DirectionUpdaterObserver observer) {
        if (observers == null) {
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
    public boolean switchIfConditionMet(VectorAgent ownerAgent) {
        boolean isConditionMet = false;
        if (this.condition != null && this.condition.checkState(ownerAgent)) {
            this.nextUpdater = this.condition.getNextState();
            isConditionMet = true;
            notifyExitObservers(ownerAgent);
            started = false;
        }
        return isConditionMet;
    }
}
