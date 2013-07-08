/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.observer;

import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public interface DirectionUpdaterObserver {
    public void notifyOfStateChange(SkelatalAgent va);
}
