/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class LocalStackSizeEqualToConditionChecker extends SkelatalUpdaterConditionChecker {

    private Integer compareTo = 0;
    private String mapKey = null;
    
    public void setCompareTo(Integer i){
        this.compareTo = i;
    }
    
    
    public LocalStackSizeEqualToConditionChecker(String mapKey, Integer i){
        this.mapKey = mapKey;
        this.compareTo = i;
    }
    
    private static final Logger log = Logger.getLogger(LocalStackSizeEqualToConditionChecker.class.getName());
    
    @Override
    public boolean checkCondition(VectorAgent va) {
        if(va.getStackedPosition(mapKey) == null){
            log.log(Level.INFO, "local stack is null for key {0} ", mapKey);
            return false;
        }
        
        Stack<float[]> localStack  = va.getStackedPosition(mapKey);
        
        log.log(Level.INFO, "size of local stack in check is {0} ", localStack.size());
        
        return localStack.size() == compareTo;
        
    }

   
    
}
