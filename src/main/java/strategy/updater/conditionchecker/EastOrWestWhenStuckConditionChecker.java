/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class EastOrWestWhenStuckConditionChecker extends SkelatalUpdaterConditionChecker {

    private final static Logger log = Logger.getLogger(EastOrWestWhenStuckConditionChecker.class.getName());
    private final int POINTS_TO_CHECK = 50;
    private int iterations = 0;
    private boolean doneStartingOut = false;
    
    @Override
    public boolean checkCondition(VectorAgent va) {
        
        // over the last fifty points, is this agent's straight line distance less 
        // than twice it's velocity
        
        // wait fifty steps before we check if stuck, thus to clear the agent buffer
        if (!doneStartingOut && iterations++ < POINTS_TO_CHECK){
            return false;
        }else{
            iterations = 0;
            doneStartingOut = true;
        }
                
        Float average = va.averageDistanceLastXPoints(POINTS_TO_CHECK);

        log.log(Level.INFO, "average of last fifty is {0} comparing to {1} ", new Float[]{average, va.getSpeed() * 2});    
        
        if(average != null && average < va.getSpeed() * 2){
            
           return true;
           
        }
        
        return false;
        
        
    }
    
}
