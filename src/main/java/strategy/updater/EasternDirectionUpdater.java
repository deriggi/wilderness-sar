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
public class EasternDirectionUpdater extends SkelatalDirectionUpdater {

    private final static Logger log = Logger.getLogger(EasternDirectionUpdater.class.getName());
    
    
    @Override
    public String toString() {
        return "EAST";
    }
    

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        if (dxDy == null || dxDy.length != 2) {
            return;
        }

        if (dxDy[0] < 0) {
            dxDy[0] *= -1;
        }
        dxDy[0] += ownerAgent.getSpeed();
        ownerAgent.pushLoc();
        

//        double[] lonlat = RasterLoader.get(RasterConfig.BIG).getData().getLonLat(ownerAgent.getLocation()[0], ownerAgent.getLocation()[1]);
//        // replace the below 
//        if(lonlat[0] > -116.86157){
//            Switch sw =  new Switch();
//            sw.setTo(new SoutherDirectionUpdater());
//            setSwitch(sw);
//        }

//        VectorUtils.limit(dxDy, 4.0);

    }
}
