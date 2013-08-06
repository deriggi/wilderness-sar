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

    public static String makeStringName(List<DirectionUpdater> offspring) {
        StringBuilder sb = new StringBuilder();
        for (DirectionUpdater du : offspring) {
            sb.append(du.toString());
            sb.append("_");
        }

        return sb.toString();

    }

    public static void main(String[] args) {
        GeneticAgentMaker gm = new GeneticAgentMaker();
        
//        List<List<String>> elements = gm.decodeName("e_25_20.0_e_25_30.0_e_25_40.0_w_25_30.0_w_25_40.0_n_25_40.0_s_25_20.0_s_25_30.0_s_25_40.0_");
//        boolean[] dna = gm.binaryStringFromNameElements(elements);
//        for(boolean b: dna){
//            System.out.println(b);
//        }
        boolean[] b =  new boolean[] {true,true,true,true,true,true};
        boolean[] c =  new boolean[] {false,false,false,false,false,false};
        
        gm.crossover(b,c);
        
        
//        int n = 10;
//        int i = 0;
//        while (i++ < n) {
//            List<DirectionUpdater> agent = gm.makeRandomAgent();
//            for(DirectionUpdater du: agent){
//                System.out.println(du.toString());
//            }
//            System.out.println();
//            System.out.println("============");
//            System.out.println();
//            
//        }
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

    private int getIndexFromCharacteristic(List<String> threeElements) {
        int index = 0;

        String first = threeElements.get(0);
        String third = threeElements.get(2);

        if (first.equals("e")) {
            index += 0;
        } else if (first.equals("w")) {
            index += 4;
        } else if (first.equals("n")) {
            index += 8;
        } else if (first.equals("s")) {
            index += 12;
        }

        int thirdInt = ((int)Float.parseFloat(third) / 10) - 1;
        System.out.println("thirdint" + thirdInt);
        index += thirdInt;
        return index;
    }
    
    private boolean hasLongViewShedGene(List<String> threeElements){
        if(threeElements.get(1).equals("31")){
            return true;
        }
        return false;
    }

    private boolean[] binaryStringFromNameElements(List<List<String>> nameElements) {
        boolean[] dna = new boolean[17];

        for (List<String> characteristic : nameElements) {
            dna[getIndexFromCharacteristic(characteristic)] = true;
        }
        dna[16] = hasLongViewShedGene(nameElements.get(0));
        
        return dna;
    }

    
    private List<List<String>> decodeName(String code) {
        List<List<String>> listOfCodes = new ArrayList<List<String>>();
        if (code == null) {
            return listOfCodes;
        }


        String[] parts = code.split("_");


        List<String> three = new ArrayList<String>();
        for (int i = 0; i < parts.length; i++) {


            if (i % 3 == 0) {
                if (!three.isEmpty()) {
                    listOfCodes.add(three);
                }
                three = new ArrayList<String>();
            }
            three.add(parts[i]);

        }
        listOfCodes.add(three);
        return listOfCodes;
    }
    
    private void crossover(boolean[] dnaOne, boolean[] dnaTwo){
        double crossOverRate = Math.random();
        int fromIndex = (int)(dnaOne.length*crossOverRate);
        System.out.println("crossover inndex is " + fromIndex);
        
        // baby one is getting first part of dnaone, second part of dna two
        boolean[] babyOne = new boolean[dnaOne.length];
        System.arraycopy(dnaOne, 0, babyOne, 0, fromIndex);
        System.arraycopy(dnaTwo, fromIndex, babyOne, fromIndex, dnaTwo.length - fromIndex);
        
        // baby two is getting first part of dnatwo, second party of dna one
        boolean[] babyTwo = new boolean[dnaOne.length];
        System.arraycopy(dnaTwo, 0, babyTwo, 0, fromIndex);
        System.arraycopy(dnaOne, fromIndex, babyTwo, fromIndex, dnaOne.length - fromIndex);
        
        
        for(boolean a : babyOne){
            System.out.println(a);
        }
        System.out.println();
        
        
        for(boolean b : babyTwo){
            System.out.println(b);
        }
    }
}
