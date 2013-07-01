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
public class NorthernDirectionUpdater extends SkelatalDirectionUpdater {

    private final static Logger log = Logger.getLogger(NorthernDirectionUpdater.class.getName());

    @Override
    public String toString(){
        return "North";
    }
    
   
    
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        if (dxDy == null || dxDy.length != 2) {
            return;
        }

        if (dxDy[1] > 0) {
            dxDy[1]  = 0;
        }
        
        dxDy[1] -= ownerAgent.getSpeed();
//        ownerAgent.pushLoc();
        


    }


}
