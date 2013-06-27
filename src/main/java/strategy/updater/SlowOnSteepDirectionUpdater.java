/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import geomutils.VectorUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.agent.VectorAgent;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class SlowOnSteepDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log=  Logger.getLogger(SlowOnSteepDirectionUpdater.class.getName());
    private Float lastHeight= null;
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        
        double slope = raster.calculateSlope(new Float(loc[0]).intValue(), new Float(loc[1]).intValue());
//        log.log(Level.INFO, "slope is {0} ", slope);
        
        float max = 3.0f;
        if(slope >= 0.30f){
            VectorUtils.divide(dxDy, max*0.80f/new Float(slope));
        }
        
        double howSlow = VectorUtils.magnitude(dxDy) - ownerAgent.getSpeed();
        if(slope < 0.30f && howSlow < 0){
            dxDy = VectorUtils.multiplyDonTouch(dxDy, new Float(ownerAgent.getSpeed()/howSlow));
        }
        float thisHeight = raster.getCell(loc[0],loc[1]);
        if(lastHeight != null ){
            float diff = Math.abs(lastHeight - thisHeight);
            if(diff > 0 && diff/lastHeight > 0.002f ){
//                log.log(Level.INFO, "last height was {0} with a percent diff of {1} ", new Object[]{new Float(lastHeight),new Float(diff/lastHeight) });
//                log.log(Level.INFO, "slowing down {0}, {1}", new Object[]{new Float(dxDy[0]),new Float(dxDy[1]) });
                dxDy = VectorUtils.add(dxDy, VectorUtils.multiplyDonTouch(dxDy, -0.50f));
//                log.log(Level.INFO, "slowing result: {0}, {1}", new Object[]{new Float(dxDy[0]),new Float(dxDy[1]) });
            }else if (diff > 0){
                // speed up a little
//                log.log(Level.INFO, "speeding: {0}, {1}", new Object[]{new Float(dxDy[0]),new Float(dxDy[1]) });
                dxDy = VectorUtils.add(dxDy, VectorUtils.multiplyDonTouch(dxDy, 0.50f));
//                log.log(Level.INFO, "speeding result: {0}, {1}", new Object[]{new Float(dxDy[0]),new Float(dxDy[1]) });
            }
            
        }
        lastHeight = thisHeight;
    }

    @Override
    public void notifyMe(UpdaterMessage message) {
    }
    
}
