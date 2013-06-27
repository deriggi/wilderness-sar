/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.notificationhandler;

import java.util.logging.Level;
import java.util.logging.Logger;
import strategy.DirectionUpdater;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class DisableOnSouthNotificationHandler implements UpdaterNotificationHandler {
    private static final Logger log =  Logger.getLogger(UpdaterNotificationHandler.class.getName());
    
    @Override
    public void handleNotification(DirectionUpdater observer, UpdaterMessage message){
        
        log.log(Level.INFO, "notified! {0} {1}", new Object[]{message.getStage(), message.getFrom()});
        
        if (message.getStage().equals("START") && message.getFrom().equals("South")) {
            observer.setDisabled(true);
        }else if (message.getStage().equals("END") && message.getFrom().equals("South")) {
            observer.setDisabled(false);
        }
        
    }
}
