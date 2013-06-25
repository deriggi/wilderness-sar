/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class WesternDirectionUpdater extends SkelatalDirectionUpdater{

    @Override
    public String toString(){
        return "Western";
    }
    
    
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        if (dxDy == null || dxDy.length != 2){
            return;
        }
        
        if(dxDy[0] > 0){
            dxDy[0] *= -1;
        }
        dxDy[0] -= ownerAgent.getSpeed();
        ownerAgent.pushLoc();;
        
        
    }

    
}
