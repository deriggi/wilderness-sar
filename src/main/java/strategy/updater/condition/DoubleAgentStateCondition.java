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
public  class DoubleAgentStateCondition extends SkelatalStateCondition{
    private AgentProperty agentProp;
    private Conditioner.Condish condish;
//    private T compareToDouble = null;
    private Integer compareToInteger = null;
    private Double compareToDouble = null;
    private Float compareToFloat = null;
    private static final Logger log = Logger.getLogger(DoubleAgentStateCondition.class.getName());
    
    
    
    public DoubleAgentStateCondition(AgentProperty p, Conditioner.Condish c, Double compareTo){
        this.agentProp = p;
        this.condish = c;
        this.compareToDouble = compareTo;
    }
    
   
    
     @Override
    public boolean checkState(VectorAgent va) {
        boolean checkedCondition = false;
        if(compareToDouble != null){
            checkedCondition = Conditioner.doComparison(condish, AgentPropertyManager.getDoubleAgentProperty(agentProp, va) , compareToDouble); 
        }
       
        else{
            log.warning("compare to value not set for this condition - returing false no matter what");
        }
        
        return checkedCondition;
        
    }
   

   
    
    
}
