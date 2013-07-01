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
public class StackSizeEqualToConditionChecker extends SkelatalUpdaterConditionChecker {

    private Integer compareTo = 0;
    private String key = null;
    public void setCompareTo(Integer i){
        this.compareTo = i;
    }
    
    public StackSizeEqualToConditionChecker(Integer i, String mapKey){
        this.compareTo = i;
        this.key = mapKey;
    }
    
    @Override
    public boolean checkCondition(VectorAgent va) {
        if(va == null || va.getStackedPosition(key) == null){
            return false;
        }
        return va.getStackedPosition(key).size() == compareTo;
        
    }

   
    
}
