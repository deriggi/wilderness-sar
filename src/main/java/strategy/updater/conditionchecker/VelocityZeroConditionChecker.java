/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import geomutils.VectorUtils;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class VelocityZeroConditionChecker extends SkelatalUpdaterConditionChecker {
    
    @Override
    public boolean checkCondition(VectorAgent va) {
        if(va == null){
            return false;
        }
        
        return VectorUtils.magnitude(va.getVelocityVector()) == 0;
        
    }
    
}
