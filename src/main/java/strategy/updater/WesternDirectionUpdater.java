/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.logging.Logger;
import raster.domain.agent.VectorAgent;

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
    
    private String key = null;
    public WesternDirectionUpdater(String key){
        this.key = key;
    }
    
    public WesternDirectionUpdater(){
    }
    

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        
         if(key != null && ownerAgent.getStackedPosition(key) == null){
            ownerAgent.registerStack(key);
            
         }
        log.info("runnig west");
        if (dxDy == null || dxDy.length != 2) {
            return;
        }

        if (dxDy[0] > 0) {
            dxDy[0] = 0;
        }
        dxDy[0] -= ownerAgent.getSpeed();
        
        ownerAgent.pushLoc(key);
//        ownerAgent.pushLoc();

    }

}
