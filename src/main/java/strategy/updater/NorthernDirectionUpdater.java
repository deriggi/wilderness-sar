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
            dxDy[0] *= -1;
        }
        
        dxDy[1] -= ownerAgent.getSpeed();
        ownerAgent.pushLoc();
        
        
//        float[] origin = ownerAgent.getOrigin();
//        double[] originLonLat = RasterLoader.get(RasterConfig.BIG).getData().getLonLat(origin[0], origin[1]);
//        double[] nowLonLat = RasterLoader.get(RasterConfig.BIG).getData().getLonLat(ownerAgent.getLocation()[0], ownerAgent.getLocation()[1]);
//        
//        if(Math.abs(nowLonLat[1] - originLonLat[1]) < 40){
//            
//            this.switche = new Switch();
//            this.switche.setTo(new EasternDirectionUpdater());
//            
//        }
            

//        VectorUtils.limit(dxDy, 4.0);

    }

}
