/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import java.util.HashMap;
import java.util.List;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class SimService {
    
    private static SimService sim = null;
    
    private int simId = 0;
    private HashMap<Integer, List<SkelatalAgent>> agents = new HashMap<Integer, List<SkelatalAgent>>();
    
    private  int getNextId(){
        return simId++;
    }
    
//    private void addAgent()
    
    public static synchronized SimService get(){
        
        if(sim == null){
            sim = new SimService();
        }
        
        return sim;
        
    }
    
}
