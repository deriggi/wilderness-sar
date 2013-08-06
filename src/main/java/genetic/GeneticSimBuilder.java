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

    public static void main(String[] args) {
//        new GeneticSimBuilder().runGeneticAlgo();
        new GeneticSimBuilder().runCrossoverTest();
    }

    public void runCrossoverTest() {
        String a = "e_25_30.0_n_25_40.0_s_25_20.0_";
        String b = "w_31_10.0_w_31_30.0_n_31_10.0_n_31_20.0_n_31_30.0_n_31_40.0_s_31_20.0_s_31_30.0_";
        String c = "e_31_10.0_e_31_20.0_e_31_40.0_w_31_40.0_n_31_10.0_n_31_30.0_s_31_10.0_s_31_20.0_s_31_30.0_";
        String d = "e_31_10.0_n_31_40.0_s_31_30.0_";
        String e = "e_25_10.0_e_25_20.0_e_25_30.0_w_25_30.0_s_25_10.0_s_25_30.0_";

        crossOverAndTest(e, d);
        crossOverAndTest(e, c);
        crossOverAndTest(e, b);
        crossOverAndTest(e, a);

    }

    public void crossOverAndTest(String a, String b) {
        GeneticAgentMaker god = new GeneticAgentMaker();
        SimBuilder sb = new SimBuilder();
        if (!a.equals(b)) {
            List<List<DirectionUpdater>> babies = god.crossOver(a, b);
            String baby0 = GeneticAgentMaker.makeStringName(babies.get(0));
            String baby1 = GeneticAgentMaker.makeStringName(babies.get(1));

            HashMap<String, Float> result0 = sb.runGeneticVerboseAgent(baby0, babies.get(0), 2000, SimBuilder.BBox.SPOT_1);
            FileExportHelper.appendLineToFile(WisarPaths.AGENT_OUT + "offspringresult.csv", baby0 + "," + result0.get(baby0));
            
            HashMap<String, Float> result1 = sb.runGeneticVerboseAgent(baby1, babies.get(1), 2000, SimBuilder.BBox.SPOT_1);
            FileExportHelper.appendLineToFile(WisarPaths.AGENT_OUT + "offspringresult.csv", baby1 + "," + result1.get(baby1));
            

        }


    }

    public void runGeneticAlgo() {
        Set<List<DirectionUpdater>> population = new HashSet<List<DirectionUpdater>>();
        GeneticAgentMaker god = new GeneticAgentMaker();
        SimBuilder sb = new SimBuilder();

        // create 500 agents
        for (int i = 0; i < 65; i++) {
            population.add(god.makeRandomAgent());
        }

        for (List<DirectionUpdater> anOffspring : population) {
            String offspringCode = GeneticAgentMaker.makeStringName(anOffspring);
            HashMap<String, Float> result = sb.runGeneticVerboseAgent(offspringCode, anOffspring, 2000, SimBuilder.BBox.SPOT_1);
            FileExportHelper.appendLineToFile(WisarPaths.AGENT_OUT + "offspringresult.csv", offspringCode + "," + result.get(offspringCode));

        }



        // run 500 agents

        // extract results

        // rank agents

        // spit output
    }
}
