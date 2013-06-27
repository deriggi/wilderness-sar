/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import geomutils.VectorUtils;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class NearOriginConditionChecker implements ConditionChecker {

    @Override
    public boolean checkCondition(VectorAgent va) {
        float[] origin = va.getOrigin();
        float[] loc = va.getLocation();
        
        if(VectorUtils.distance(loc, origin) < va.getSpeed()){
            return true;
        }
        return false;
        
    }
    
}
