/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public interface StateCondition {
    
    public boolean checkState(VectorAgent va);
    public DirectionUpdater getNextState();
    public void setNextState(DirectionUpdater du);
    
}
