/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import raster.domain.agent.SkelatalAgent;
import strategy.updater.notificationhandler.UpdaterNotificationHandler;
import strategy.updater.conditionchecker.UpdaterConditionChecker;
import strategy.updater.message.UpdaterMessage;
import strategy.updater.observer.DirectionUpdaterObserver;

/**
 *
 * @author Johnny
 */
public interface DirectionUpdater {
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent);
    public DirectionUpdater getNextState();
    public void setConditionChecker(UpdaterConditionChecker sc);
    public boolean switchIfConditionMet(SkelatalAgent vectorAgent);
    public UpdaterConditionChecker getConditionChecker();
    public void notifyMe(UpdaterMessage message);
    public void updateDirectionCaller(double[] velocity, SkelatalAgent va);
    public void setDisabled(boolean isDisabled);
    public boolean isDisabled();
    public void addUpdaterNotificationHandler(UpdaterNotificationHandler un);
    public void addExitObserver(DirectionUpdaterObserver observer);
}
