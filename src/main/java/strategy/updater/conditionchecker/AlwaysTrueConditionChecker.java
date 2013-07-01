/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class AlwaysTrueConditionChecker extends SkelatalUpdaterConditionChecker {

    @Override
    public boolean checkCondition(VectorAgent va) {
        
        return true;

    }
}
