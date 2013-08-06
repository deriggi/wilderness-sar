/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import middletier.AgentService;
import middletier.SimBuilder;
import strategy.DirectionUpdater;
import util.FileExportHelper;
import util.WisarPaths;

/**
 *
 * @author Johnny
 */
public class GeneticSimBuilder {
    
    public static void main(String[] args){
        new GeneticSimBuilder().runGeneticAlgo();
    }
    public void runGeneticAlgo(){
        Set<List<DirectionUpdater>> population = new HashSet<List<DirectionUpdater>>();
        GeneticAgentMaker god = new GeneticAgentMaker();
        SimBuilder sb = new SimBuilder();
        
        // create 500 agents
        for(int i = 0;i  < 65; i++){
            population.add(god.makeRandomAgent());
        }
        
        for(List<DirectionUpdater> anOffspring: population){
            String offspringCode = GeneticAgentMaker.makeStringName(anOffspring);
            HashMap<String, Float> result = sb.runGeneticVerboseAgent(offspringCode, anOffspring, 2000, SimBuilder.BBox.SPOT_1);
            FileExportHelper.appendLineToFile(WisarPaths.AGENT_OUT + "offspringresult.csv", offspringCode +"," + result.get(offspringCode) );
            
        }
        
       
        
        // run 500 agents
        
        // extract results
        
        // rank agents
        
        // spit output
    }
    
}
