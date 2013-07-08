/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class AlmostOutOfBoundsConditionChecker implements ConditionChecker {

    @Override
    public boolean checkCondition(SkelatalAgent va) {
        return va.isNextStepOutOfBounds();
    }
    
}
