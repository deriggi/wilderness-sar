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
import raster.domain.Raster2D;
import raster.domain.agent.IdLoc;
import raster.domain.agent.SkelatalAgent;
import strategy.WanderStrategy;
import strategy.updater.condition.AlmostOutOfBoundsConditionChecker;
import strategy.updater.condition.NearOriginConditionChecker;

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
    
    public SkelatalAgent createAgent(float column, float row, float speed, FSMFactory.MachineName behaviour){
        stopSim = false;
        
        VectorAgent a = new VectorAgent();
        a.setSpeed(speed);
        a.setLocation(new float[]{column,row});
        a.setOrigin(new float[]{column,row});
        a.setId(getNextId());
        agents.put(a.getId(), a);
        
        // strategery
        WanderStrategy wanderStrat = new WanderStrategy();
        wanderStrat.setName(behaviour.toString());
        wanderStrat.setConditionChecker(new AlmostOutOfBoundsConditionChecker());
        wanderStrat.addAllDirectinoUpdaters( FSMFactory.getMachine(behaviour) );
        
        WanderStrategy goHomeStrategy = new WanderStrategy();
        goHomeStrategy.addAllDirectinoUpdaters(FSMFactory.getMachine(FSMFactory.MachineName.GO_TO_ORIGIN));
        goHomeStrategy.setConditionChecker(new NearOriginConditionChecker());
        goHomeStrategy.setName("GO_TO_ORIGIN");
        
        a.addMovementStrategy(wanderStrat);
        a.addMovementStrategy(goHomeStrategy);
        
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
    

    public HashMap<Double,VectorAgent> getAgentsWithinRange(float[] loc, int range, SkelatalAgent except){
        Set<Integer> keys = agents.keySet();
        HashMap<Double, VectorAgent> distanceAgentMap = new HashMap<Double, VectorAgent>();
        
        for(Integer i : keys){
            VectorAgent someAgent = agents.get(i);
            if(someAgent == except){
                continue;
            }
            double distance = VectorUtils.distance(loc, someAgent.getLocation());
            
            
            if(distance <= range){
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
