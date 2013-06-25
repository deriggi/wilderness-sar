/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import raster.domain.agent.VectorAgent;
import strategy.updater.condition.StateCondition;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public interface DirectionUpdater {
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent);
    public DirectionUpdater getNextState();
    public void setCondition(StateCondition sc);
    public boolean switchIfConditionMet(VectorAgent vectorAgent);
    public void getCondition(StateCondition sc);
    public void notifyMe(UpdaterMessage message);
    public void updateDirectionCaller(double[] velocity, VectorAgent va);
    public void setDisabled(boolean isDisabled);
    public boolean isDisabled();
}
