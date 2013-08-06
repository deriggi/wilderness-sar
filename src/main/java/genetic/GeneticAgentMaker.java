/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public class GeneticAgentMaker {

    private static final int DNA_LENGTH = 17;

    public List<DirectionUpdater> makeRandomAgent() {

        boolean[] dna = makeRandomArray(DNA_LENGTH);
        ArrayList<DirectionUpdater> list = decode(dna);

        return list;

    }

    public static String makeStringName(List<DirectionUpdater> offspring){
        StringBuilder sb = new StringBuilder();
        for(DirectionUpdater du : offspring){
            sb.append(du.toString());
            sb.append("_");
        }
        
        return sb.toString();
        
    }
    
    
    public static void main(String[] args) {
        GeneticAgentMaker gm = new GeneticAgentMaker();

        int n = 10;
        int i = 0;
        while (i++ < n) {
            List<DirectionUpdater> agent = gm.makeRandomAgent();
            for(DirectionUpdater du: agent){
                System.out.println(du.toString());
            }
            System.out.println();
            System.out.println("============");
            System.out.println();
            
        }
    }

    private boolean[] makeRandomArray(int size) {
        boolean[] dna = new boolean[size];
        Random rand = new Random();

        for (int i = 0; i < size; i++) {
            dna[i] = rand.nextBoolean();
        }
        return dna;


    }
    
    

    private ArrayList<DirectionUpdater> decode(boolean[] dna) {
        int vis = VectorAgent.SHORT_VIS_RANGE;

        if (dna[16]) {
            vis = 31;
        }
//        vis = 10;
        
        ArrayList<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();

        if (dna[0]) {
            updaters.add(new EasternWalkableDirectionUpdater(vis, 10, 0));
        }
        if (dna[1]) {
            updaters.add(new EasternWalkableDirectionUpdater(vis, 20, 1));
        }
        if (dna[2]) {
            updaters.add(new EasternWalkableDirectionUpdater(vis, 30, 2));
        }
        if (dna[3]) {
            updaters.add(new EasternWalkableDirectionUpdater(vis, 40, 3));
        }

        if (dna[4]) {
            updaters.add(new WesternWalkableDirectionUpdater(vis, 10, 4));
        }
        if (dna[5]) {
            updaters.add(new WesternWalkableDirectionUpdater(vis, 20, 5));
        }
        if (dna[6]) {
            updaters.add(new WesternWalkableDirectionUpdater(vis, 30, 6));
        }
        if (dna[7]) {
            updaters.add(new WesternWalkableDirectionUpdater(vis, 40, 7));
        }

        if (dna[8]) {
            updaters.add(new NorthernWalkableDirectionUpdater(vis, 10, 8));
        }
        if (dna[9]) {
            updaters.add(new NorthernWalkableDirectionUpdater(vis, 20, 9));
        }
        if (dna[10]) {
            updaters.add(new NorthernWalkableDirectionUpdater(vis, 30, 10));
        }
        if (dna[11]) {
            updaters.add(new NorthernWalkableDirectionUpdater(vis, 40, 11));
        }

        if (dna[12]) {
            updaters.add(new SouthernWalkableDirectionUpdater(vis, 10, 12));
        }
        if (dna[13]) {
            updaters.add(new SouthernWalkableDirectionUpdater(vis, 20, 13));
        }
        if (dna[14]) {
            updaters.add(new SouthernWalkableDirectionUpdater(vis, 30, 14));
        }
        if (dna[15]) {
            updaters.add(new SouthernWalkableDirectionUpdater(vis, 40, 15));
        }
        return updaters;

    }
}
