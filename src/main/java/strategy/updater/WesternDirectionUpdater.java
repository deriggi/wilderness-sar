/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.logging.Logger;
import raster.domain.agent.VectorAgent;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class WesternDirectionUpdater extends SkelatalDirectionUpdater {
    private static final Logger log = Logger.getLogger(WesternDirectionUpdater.class.getName());
    
    @Override
    public String toString() {
        return "Western";
    }

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        log.info("runnig west");
        if (dxDy == null || dxDy.length != 2) {
            return;
        }

        if (dxDy[0] > 0) {
            dxDy[0] = 0;
        }
        dxDy[0] -= ownerAgent.getSpeed();
        
        ownerAgent.pushLoc();

    }

    @Override
    public void notifyMe(UpdaterMessage message) {
    }
}
