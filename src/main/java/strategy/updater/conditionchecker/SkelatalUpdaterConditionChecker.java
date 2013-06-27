/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalUpdaterConditionChecker implements UpdaterConditionChecker{
    private DirectionUpdater updater = null;
     
   
    
    
    @Override
    public DirectionUpdater getNextState(){
        return updater;
    }
    
    @Override
    public void setNextState(DirectionUpdater du){
        this.updater = du;
    }
    
}
