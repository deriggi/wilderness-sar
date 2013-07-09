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
public class EasternDirectionUpdater extends SkelatalDirectionUpdater {

    private final static Logger log = Logger.getLogger(EasternDirectionUpdater.class.getName());
    
    
    @Override
    public String toString() {
        return "EAST";
    }
    
    private String key = null;
    
    public EasternDirectionUpdater(String key){
        this.key  = key;
    }
    
    public EasternDirectionUpdater(){
    }
    
    
    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        if(key != null && ownerAgent.getStackedPosition(key) == null){
            ownerAgent.registerStack(key);
        }
        
        log.info("runnig east");
        if (dxDy == null || dxDy.length != 2) {
            return;
        }

        if (dxDy[0] < 0) {
            dxDy[0] = 0;
            
        }
        
        dxDy[0] += ownerAgent.getSpeed();
        ownerAgent.pushLoc(key);
    }
}
