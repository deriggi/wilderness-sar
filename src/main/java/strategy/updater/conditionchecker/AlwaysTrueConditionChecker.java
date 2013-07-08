/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class AlwaysTrueConditionChecker extends SkelatalUpdaterConditionChecker {

    @Override
    public boolean checkCondition(SkelatalAgent va) {
        
        return true;

    }
}
