/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public interface ConditionChecker {
    public boolean checkCondition(VectorAgent va);
}
