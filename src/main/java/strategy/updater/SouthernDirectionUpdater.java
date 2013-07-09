/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.logging.Logger;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class SouthernDirectionUpdater extends SkelatalDirectionUpdater {

    private final static Logger log = Logger.getLogger(SouthernDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "South";
    }
    private String key = null;

    public SouthernDirectionUpdater(String key) {
        this.key = key;
    }

    public SouthernDirectionUpdater() {
    }

    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {

        if (key != null && ownerAgent.getStackedPosition(key) == null) {
            ownerAgent.registerStack(key);
        }
        if (dxDy == null || dxDy.length != 2) {
            return;
        }
        if (dxDy[1] < 0) {
            dxDy[1] = 0;
        }

        dxDy[1] += ownerAgent.getSpeed();
        ownerAgent.pushLoc(key);
    }
}
