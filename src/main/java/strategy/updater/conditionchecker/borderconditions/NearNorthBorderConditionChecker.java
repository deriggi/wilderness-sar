/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker.borderconditions;

import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class NearNorthBorderConditionChecker extends BorderConditionChecker {

    @Override
    public boolean checkCondition(SkelatalAgent va) {
        
        if (Math.abs(va.getLocation()[1] - getNorthEnd()) < getThreshold()) {
            return true;
        }
        
        return false;
        
    }
    
}
