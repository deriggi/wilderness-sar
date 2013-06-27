/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public interface UpdaterConditionChecker {
    public boolean checkCondition(VectorAgent va);
    public DirectionUpdater getNextState();
    public void setNextState(DirectionUpdater du);
}
