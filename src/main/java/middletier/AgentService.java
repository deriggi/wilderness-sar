/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import raster.domain.agent.FSMFactory;
import raster.domain.agent.VectorAgent;
import geomutils.VectorUtils;
import java.util.List;
import java.util.logging.Level;
import raster.domain.Raster2D;
import raster.domain.agent.AgentName;
import raster.domain.agent.IdLoc;
import raster.domain.agent.SkelatalAgent;
import strategy.WanderStrategy;
import util.FileExportHelper;

/**
 *
 * @author Johnny
 */
public class AgentService {

    private static AgentService service = new AgentService();
//    private HashMap<Integer, VectorAgent> agents = new HashMap<Integer, VectorAgent>();
    private HashMap<String, List<SkelatalAgent>> agents = new HashMap<String, List<SkelatalAgent>>();
    private int nextId = 1;
    private static final Logger log = Logger.getLogger(AgentService.class.getName());
    private boolean stopSim = false;

    public void setStopSim(boolean stop) {
        this.stopSim = stop;
    }

    private int getNextId() {
        return nextId++;
    }

    public void clearAgents(String simId) {
        if (!agents.containsKey(simId)) {
            log.log(Level.WARNING, "no agents for sim {0}", simId);
            return;
        }
        agents.get(simId).clear();
        agents.remove(simId);

        log.log(Level.INFO, "agents cleared from {0} sims count is {1}", new String[]{simId, Integer.toString(agents.size())});

    }

    private void exportAgentStates(List<IdLoc> states) {

        if (states == null || states.isEmpty()) {
            log.warning("trying to export empty or null states");
            return;
        }

        String simId = states.get(0).getSimId();
        StringBuilder sb = new StringBuilder();

        for (IdLoc state : states) {
            sb.append(state.getSimId());
            sb.append(FileExportHelper.COMMA);

            sb.append(state.getNameTag());
            sb.append(FileExportHelper.COMMA);

            sb.append(state.getId());
            sb.append(FileExportHelper.COMMA);

            sb.append(state.getLocation()[0]);
            sb.append(FileExportHelper.COMMA);

            sb.append(state.getLocation()[1]);
            sb.append(FileExportHelper.LINE_SEPARATOR);

        }

        FileExportHelper.appendBatchToFile("C:\\agentout\\" + simId + ".csv", sb.toString());

    }
    

    public ArrayList<IdLoc> runUntilFound(String simId) {
//        if (agents.get(simId) == null || agents.get(simId).size() < 2) {
//
//            log.warning("not enough agents to run sim");
//            return new ArrayList<IdLoc>(0);
//
//        }

        boolean found = false;
        ArrayList<IdLoc> states = null;
        ArrayList<IdLoc> buffer = new ArrayList<IdLoc>(200);

        while (!found && !stopSim) {

            states = runAgents(simId);
            for (IdLoc state : states) {
                if (state.getFoundOthers()) {
                    found = true;
                }
            }
            buffer.addAll(states);

            if (buffer.size() == 200) {
                exportAgentStates(buffer);
                buffer.clear();
            }

        }
        if (buffer.size() > 0) {
            exportAgentStates(buffer);
            buffer.clear();
        }
        clearAgents(simId);

        return states;
    }

    public ArrayList<IdLoc> runFor(String simId, int count) {
//        if (agents.get(simId) == null || agents.get(simId).size() < 2) {
//
//            log.warning("not enough agents to run sim");
//            return new ArrayList<IdLoc>(0);
//
//        }

        boolean found = false;
        ArrayList<IdLoc> states = null;
        ArrayList<IdLoc> buffer = new ArrayList<IdLoc>(200);
        int x = 0;
        while (x++ <count && !stopSim) {

            states = runAgents(simId);
            for (IdLoc state : states) {
                if (state.getFoundOthers()) {
                    found = true;
                }
            }
            buffer.addAll(states);

            if (buffer.size() == 200) {
                exportAgentStates(buffer);
                buffer.clear();
            }

        }
        if (buffer.size() > 0) {
            exportAgentStates(buffer);
            buffer.clear();
        }
        clearAgents(simId);

        return states;
    }
    
    
    public ArrayList<IdLoc> runUntilFound(String simId, int every) {
        if (agents.get(simId) == null || agents.get(simId).size() < 2) {

            log.warning("not enough agents to run sim");
            return new ArrayList<IdLoc>(0);

        }

        boolean found = false;
        ArrayList<IdLoc> states = null;
        ArrayList<IdLoc> buffer = new ArrayList<IdLoc>(200);

        int i = 0;
        while (!found && !stopSim) {
            if (i++%every == 0) {
                states = runAgents(simId);
                for (IdLoc state : states) {
                    if (state.getFoundOthers()) {
                        found = true;
                    }
                }
                buffer.addAll(states);

                if (buffer.size() == 200) {
                    exportAgentStates(buffer);
                    buffer.clear();
                }
                
            }
        }



        if (buffer.size() > 0) {
            exportAgentStates(buffer);
            buffer.clear();
        }
        clearAgents(simId);

        return states;
    }

    public ArrayList<IdLoc> runAgents(String simId) {

        List<SkelatalAgent> localAgents = getAllAgents(simId);

        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        ArrayList<IdLoc> agentStates = new ArrayList<IdLoc>();

        for (SkelatalAgent a : localAgents) {


            a.wander();
            double[] lonLat = raster.getLonLat(a.getLocation()[0], a.getLocation()[1]);
            IdLoc idLoc = a.toIdLoc();
            idLoc.setLocation(lonLat);
            agentStates.add(idLoc);
            idLoc.setTimestep(a.getMasterTimestepsTaken());


        }

        return agentStates;

    }

    public SkelatalAgent createAgent(float column, float row, float speed, FSMFactory.MachineName behaviour, String simId) {
        stopSim = false;

        VectorAgent a = new VectorAgent();
        a.setSpeed(speed);
        a.setLocation(new float[]{column, row});
        a.setOrigin(new float[]{column, row});
        a.setId(getNextId());
        a.setSimId(simId);

        if (!agents.containsKey(simId)) {
            agents.put(simId, new ArrayList<SkelatalAgent>());
        }
        agents.get(simId).add(a);
        log.log(Level.INFO, "{0} agents in sim {1}", new String[]{Integer.toString(agents.get(simId).size()), simId});

        // strategery
        WanderStrategy wanderStrat = new WanderStrategy();
        wanderStrat.setName(behaviour.toString());
//        wanderStrat.setConditionChecker(new AlmostOutOfBoundsConditionChecker());
        wanderStrat.addAllDirectinoUpdaters(FSMFactory.getMachine(behaviour));

//        WanderStrategy goHomeStrategy = new WanderStrategy();
//        goHomeStrategy.addAllDirectinoUpdaters(FSMFactory.getMachine(FSMFactory.MachineName.GO_TO_ORIGIN));
//        goHomeStrategy.setConditionChecker(new NearOriginConditionChecker());
//        goHomeStrategy.setName("GO_TO_ORIGIN");

        a.addMovementStrategy(wanderStrat);
//        a.addMovementStrategy(goHomeStrategy);

        return a;
    }

    public HashMap<Double, SkelatalAgent> getAgentsWithinRange(float[] loc, int range, SkelatalAgent except, AgentName name) {
        String simId = except.getSimId();
        HashMap<Double, SkelatalAgent> distanceAgentMap = new HashMap<Double, SkelatalAgent>();
        if (!agents.containsKey(simId)) {
            log.warning(" no agents with that simid sucka!");
            return distanceAgentMap;
        }

        List<SkelatalAgent> otherAgents = agents.get(simId);


        for (SkelatalAgent someAgent : otherAgents) {
            if (someAgent.equals(except)) {
                continue;
            }

            double distance = VectorUtils.distance(loc, someAgent.getLocation());


            if (distance <= range && someAgent.getNameTag().equals(name)) {
                distanceAgentMap.put(distance, someAgent);
            }
        }

        return distanceAgentMap;
    }

    public static AgentService get() {

        return service;
    }

    public List<SkelatalAgent> getAllAgents(String simId) {
        if (!agents.containsKey(simId)) {
            log.log(Level.INFO, "sim id does not exist {0} ", simId);
        }

        return agents.get(simId);
    }
}
