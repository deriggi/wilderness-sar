/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public class StackSizeGreaterThanConditionChecker extends SkelatalUpdaterConditionChecker {

    private Integer compareTo = 0;
    public void setCompareTo(Integer i){
        this.compareTo = i;
    }
    
    public StackSizeGreaterThanConditionChecker(Integer i){
        this.compareTo = i;
    }
    
    @Override
    public boolean checkCondition(VectorAgent va) {
        if(va == null){
            return true;
        }
        
        return (va.getStackedPosition().size() > compareTo);
        
    }

   
    
}
