/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.notificationhandler;

import strategy.DirectionUpdater;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public interface UpdaterNotificationHandler {
    public void handleNotification(DirectionUpdater me, UpdaterMessage message);
}
