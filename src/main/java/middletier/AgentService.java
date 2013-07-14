/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import raster.domain.agent.FSMFactory;
import raster.domain.agent.VectorAgent;
import geomutils.VectorUtils;
import java.util.List;
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
    private HashMap<Integer, VectorAgent> agents = new HashMap<Integer, VectorAgent>();
    private int nextId = 1;
    private static final Logger log = Logger.getLogger(AgentService.class.getName());
    
    private boolean stopSim = false;
    public void setStopSim(boolean stop){
        this.stopSim = stop;
    }
    
    private int getNextId() {
        return nextId++;
    }

    public void clearAgents(){
        
        agents.clear();
        log.info("agents cleared from sim");
    }
    
    
    private void exportAgentStates(List<IdLoc> states){
        
        if(states == null || states.isEmpty()){
            log.warning("trying to export empty or null states");
            return;
        }
        
        String simId  = states.get(0).getSimId();
        StringBuilder sb = new StringBuilder();
        
        for(IdLoc state : states){
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
        
        FileExportHelper.appendToFile(simId + ".csv", sb.toString());
        
    }
    
    
    public ArrayList<IdLoc> runUntilFound(){
        boolean found = false;
        ArrayList<IdLoc> states = null;
        while(!found && !stopSim){
            states = runAgents();
            for(IdLoc state : states){
                if(state.getFoundOthers()){
                    found = true;
                }
            }
        }
        agents.clear();
        
        return states;
    }
    
    
    public ArrayList<IdLoc> runAgents(){
        
        Collection<VectorAgent> localAgents = getAllAgents();
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        ArrayList<IdLoc> agentStates = new ArrayList<IdLoc>();
        
        for (VectorAgent a : localAgents) {

            a.wander();
            double[] lonLat = raster.getLonLat(a.getLocation()[0], a.getLocation()[1]);
            IdLoc idLoc = a.toIdLoc();
            idLoc.setLocation( lonLat );
            agentStates.add( idLoc );
            idLoc.setTimestep(a.getMasterTimestepsTaken());
            

        }
        
        return agentStates;
        
    }
    
    public SkelatalAgent createAgent(float column, float row, float speed, FSMFactory.MachineName behaviour, String simId){
        stopSim = false;
        
        VectorAgent a = new VectorAgent();
        a.setSpeed(speed);
        a.setLocation(new float[]{column,row});
        a.setOrigin(new float[]{column,row});
        a.setId(getNextId());
        a.setSimId(simId);
        agents.put(a.getId(), a);
        
        // strategery
        WanderStrategy wanderStrat = new WanderStrategy();
        wanderStrat.setName(behaviour.toString());
//        wanderStrat.setConditionChecker(new AlmostOutOfBoundsConditionChecker());
        wanderStrat.addAllDirectinoUpdaters( FSMFactory.getMachine(behaviour) );
        
//        WanderStrategy goHomeStrategy = new WanderStrategy();
//        goHomeStrategy.addAllDirectinoUpdaters(FSMFactory.getMachine(FSMFactory.MachineName.GO_TO_ORIGIN));
//        goHomeStrategy.setConditionChecker(new NearOriginConditionChecker());
//        goHomeStrategy.setName("GO_TO_ORIGIN");
        
        a.addMovementStrategy(wanderStrat);
//        a.addMovementStrategy(goHomeStrategy);
        
        return a;
    }
    
    public VectorAgent createUAVAgent(float column, float row){
        VectorAgent a = new VectorAgent();
        a.setSpeed(4);
        a.setLocation(new float[]{column,row});
        a.setOrigin(new float[]{column,row});
        a.setId(getNextId());
        agents.put(a.getId(), a);
        
        // strategery
        WanderStrategy wanderStrat = new WanderStrategy();
        wanderStrat.addAllDirectinoUpdaters(FSMFactory.getMachine(FSMFactory.MachineName.EAST_WEST_LAWN_MOWER));
//        WanderDirectionUpdater wanderUpdater = new WanderDirectionUpdater();
//        wanderStrat.addDirectionUpdater(wanderUpdater);
        a.addMovementStrategy(wanderStrat);
        
        return a;
    }
    

    public HashMap<Double,VectorAgent> getAgentsWithinRange(float[] loc, int range, SkelatalAgent except, AgentName name){
        Set<Integer> keys = agents.keySet();
        HashMap<Double, VectorAgent> distanceAgentMap = new HashMap<Double, VectorAgent>();
        
        for(Integer i : keys){
            VectorAgent someAgent = agents.get(i);
            if(someAgent == except){
                continue;
            }
            double distance = VectorUtils.distance(loc, someAgent.getLocation());
            
            
            if(distance <= range && someAgent.getNameTag().equals(name)){
                distanceAgentMap.put(distance, someAgent);
            }
        }

        return distanceAgentMap;
    }

    public HashMap<Double,VectorAgent> getAgentsWithinRange(float[] loc, int range){

        Set<Integer> keys = agents.keySet();
        HashMap<Double, VectorAgent> distanceAgentMap = new HashMap<Double, VectorAgent>();
        
        for(Integer i : keys){
            VectorAgent someAgent = agents.get(i);
            double distance = VectorUtils.distance(loc, someAgent.getLocation());
            
            if(distance <= range){
                distanceAgentMap.put(distance, someAgent);
            }

        }

        return distanceAgentMap;

    }
    

    public static AgentService get() {

        return service;
    }

    public VectorAgent getAgent(int id) {
        return agents.get(id);
    }

    public Collection<VectorAgent> getAllAgents() {
        return agents.values();
    }

    private void load() {
        agents.put(getNextId(), new VectorAgent());
    }
}
