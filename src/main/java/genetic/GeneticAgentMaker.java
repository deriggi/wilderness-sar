/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public class GeneticAgentMaker {
    private static final int geneStringLength = 18;
    private static final int[] visibilityRangeGenes = new int[]{10,20, 30};
    
    public List<DirectionUpdater> makeRandomAgent(){
        DirectionUpdater[] geneCode = new DirectionUpdater[18];
        
        ArrayList<DirectionUpdater> list = new ArrayList<DirectionUpdater>();
        
        
        ArrayList<DirectionUpdater> updater = new ArrayList<DirectionUpdater>();
        
        
    }
    
    private DirectionUpdater decode(boolean[] dna){
        int vis = VectorAgent.SHORT_VIS_RANGE;
        
        if(dna[17]){
            vis = VectorAgent.LONG_VIS_RANGE;
        }
                
        
        if(dna[0]){
            return new EasternWalkableDirectionUpdater(vis,10);
        }
        if(dna[1]){
            return new EasternWalkableDirectionUpdater(vis,20);
        }
        if(dna[2]){
            return new EasternWalkableDirectionUpdater(vis,30);
        }
        if(dna[3]){
            return new EasternWalkableDirectionUpdater(vis,40);
        }
        
        if(dna[4]){
            return new WesternWalkableDirectionUpdater(vis,10);
        }
        if(dna[5]){
            return new WesternWalkableDirectionUpdater(vis,20);
        }
        if(dna[6]){
            return new WesternWalkableDirectionUpdater(vis,30);
        }
        if(dna[7]){
            return new WesternWalkableDirectionUpdater(vis,40);
        }
        
        if(dna[8]){
            return new NorthernWalkableDirectionUpdater(vis,10);
        }
        if(dna[9]){
            return new NorthernWalkableDirectionUpdater(vis,20);
        }
        if(dna[10]){
            return new NorthernWalkableDirectionUpdater(vis,30);
        }
        if(dna[11]){
            return new NorthernWalkableDirectionUpdater(vis,40);
        }
        
        if(dna[12]){
            return new SouthernWalkableDirectionUpdater(vis,10);
        }
        if(dna[13]){
            return new SouthernWalkableDirectionUpdater(vis,20);
        }
        if(dna[14]){
            return new SouthernWalkableDirectionUpdater(vis,30);
        }
        if(dna[15]){
            return new SouthernWalkableDirectionUpdater(vis,40);
        }
        return null;
        
    }
    
}
