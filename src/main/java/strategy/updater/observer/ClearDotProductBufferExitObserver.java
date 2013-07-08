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
public class ClearDotProductBufferExitObserver implements DirectionUpdaterObserver{

    @Override
    public void notifyOfStateChange(SkelatalAgent va) {
        va.getDotProductBuffer().clear();
        va.clearLastVelocity();
    }
    
}
