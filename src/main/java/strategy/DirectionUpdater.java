/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import raster.domain.agent.VectorAgent;
import strategy.updater.notificationhandler.UpdaterNotificationHandler;
import strategy.updater.conditionchecker.UpdaterConditionChecker;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public interface DirectionUpdater {
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent);
    public DirectionUpdater getNextState();
    public void setConditionChecker(UpdaterConditionChecker sc);
    public boolean switchIfConditionMet(VectorAgent vectorAgent);
    public UpdaterConditionChecker getConditionChecker();
    public void notifyMe(UpdaterMessage message);
    public void updateDirectionCaller(double[] velocity, VectorAgent va);
    public void setDisabled(boolean isDisabled);
    public boolean isDisabled();
    public void addUpdaterNotificationHandler(UpdaterNotificationHandler un);
}
