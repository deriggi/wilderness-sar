/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker.borderconditions;

import raster.domain.agent.SkelatalAgent;
import strategy.updater.NorthernDirectionUpdater;

/**
 *
 * @author Johnny
 */
public class NearSouthBorderConditionChecker extends BorderConditionChecker {

    @Override
    public boolean checkCondition(SkelatalAgent va) {
        
        if (Math.abs(va.getLocation()[1] - getSouthEnd()) < getThreshold()) {
            setNextState(new NorthernDirectionUpdater());
            return true;
        }
        
        return false;
        
    }
    
}
