/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import java.util.logging.Logger;
import raster.domain.agent.AgentPropertyManager;
import raster.domain.agent.AgentPropertyManager.AgentProperty;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
//public  class NumericalAgentStateCondition<T extends Number> extends SkelatalStateCondition{
public  class IntegerAgentStateCondition extends SkelatalStateCondition{
    private AgentProperty agentProp;
    private Conditioner.Condish condish;
//    private T compareToDouble = null;
    private Integer compareToInteger = null;
    private static final Logger log = Logger.getLogger(IntegerAgentStateCondition.class.getName());
    
    
    
    public IntegerAgentStateCondition(AgentProperty p, Conditioner.Condish c, Integer compareTo){
        this.agentProp = p;
        this.condish = c;
        this.compareToInteger = compareTo;
    }
    
   
    
     @Override
    public boolean checkState(VectorAgent va) {
        boolean checkedCondition = false;
        if(compareToInteger != null){
            checkedCondition = Conditioner.doComparison(condish, AgentPropertyManager.getIntegerAgentProperty(agentProp, va) , compareToInteger); 
        }
       
        else{
            log.warning("compare to value not set for this condition - returing false no matter what");
        }
        
        return checkedCondition;
        
    }
   

   
    
    
}
