/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import raster.domain.agent.SkelatalAgent;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class DoNothingDirectionUpdater extends SkelatalDirectionUpdater{

    @Override
    public String toString(){
        return "Nothing"; 
    }
    
    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        
        
        
        
    }

    @Override
    public void notifyMe(UpdaterMessage message) {
    }


    
}
