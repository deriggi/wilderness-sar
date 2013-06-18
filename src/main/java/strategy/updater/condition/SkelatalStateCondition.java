/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalStateCondition<T> implements StateCondition {

    private DirectionUpdater nextDu = null;
    
    @Override
    public DirectionUpdater getNextState() {
        return this.nextDu;
    }

    @Override
    public void setNextState(DirectionUpdater du) {
        this.nextDu = du;
    }
}
