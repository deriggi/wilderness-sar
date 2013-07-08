/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class IsAgitatedConditionChecker extends  SkelatalUpdaterConditionChecker {

    private static final Logger log = Logger.getLogger(IsAgitatedConditionChecker.class.getName());
    
    @Override
    public boolean checkCondition(SkelatalAgent va) {
       float dpAverage = va.getDotProductBufferAverage();
       ArrayList<Float> buffer = va.getDotProductBuffer();
       int size = 0;
       if(buffer != null){
           size = buffer.size();
       }
       
       log.log(Level.INFO, "size and average {0}  {1} ", new Object[] {size, dpAverage } );
//       printBuffer(buffer);
       if(size >= 39  && dpAverage < -0.72f){
           return true;
       }
       return false;
    }
    
    private void printBuffer(ArrayList<Float> buffer){
        System.out.println("=================");
        System.out.println(" buffer ");
        
        for(Float f: buffer){
            System.out.println(f);
        }
        System.out.println("=================");
        System.out.println();
        
    }
    
}
