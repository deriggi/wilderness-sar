/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import geomutils.VectorUtils;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class VelocityZeroConditionChecker extends SkelatalUpdaterConditionChecker {
    
    @Override
    public boolean checkCondition(SkelatalAgent va) {
        if(va == null){
            return false;
        }
        
        return VectorUtils.magnitude(va.getVelocityVector()) == 0;
        
    }
    
}
