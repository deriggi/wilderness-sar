/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.uavteam;

import geomutils.VectorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.Communications;
import raster.domain.agent.SkelatalAgent;
import strategy.DirectionUpdater;
import strategy.updater.Direction;
import strategy.updater.MemoryData;
import strategy.updater.SkelatalDirectionUpdater;
import strategy.updater.conditionchecker.AlwaysTrueConditionChecker;
import strategy.updater.specialized.EasternWalkableDirectionUpdater;
import strategy.updater.specialized.NorthernWalkableDirectionUpdater;
import strategy.updater.specialized.SouthernWalkableDirectionUpdater;
import strategy.updater.specialized.WesternWalkableDirectionUpdater;

/**
 *
 * @author Johnny
 */
public abstract class UavSkelatalOutAndBackWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(UavSkelatalOutAndBackWalkableDirectionUpdater.class.getName());
    private final int initialSteps = 50;
    private boolean registered = false;
    private Direction direction = null;

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean reg) {
        this.registered = reg;
    }
    private final Stack<float[]> localStack = new Stack<float[]>();

    public Stack<float[]> getLocalStack() {
        return localStack;
    }
    private ArrayList<Float> totalVisible = new ArrayList<Float>();

    public ArrayList<Float> getVisibleCountList() {
        return totalVisible;
    }
    private OutOrBack outOrBack = OutOrBack.OUT;

    private boolean isStuckPenalty = false;

    public boolean isIsStuckPenalty() {
        return isStuckPenalty;
    }

    public void setIsStuckPenalty(boolean isStuckPenalty) {
        this.isStuckPenalty = isStuckPenalty;
    }
    
    public float distanceFromHomeConsideringStuckPenalty(SkelatalAgent ownerAgent){
        
        float distanceFromHome = (float) VectorUtils.distance(ownerAgent.getOrigin(), ownerAgent.getLocation());
        
        if(isIsStuckPenalty()){
            distanceFromHome = distanceFromHome/2.0f;
        }
        
        return distanceFromHome;
    }
    
    public void broadcastMessage(Direction direction, String comsChanel, SkelatalAgent ownerAgent){
        float distanceFromHome = distanceFromHomeConsideringStuckPenalty(ownerAgent);

        // build message
        HashMap<String, Float> message = new HashMap<String, Float>(1);
        message.put(direction.toString(), distanceFromHome);
        Communications.relayMessage(comsChanel, message);
    }
    
    public void checkForStuckOrDone(SkelatalAgent ownerAgent) {

        // if stuck or full then go back
        if (localStack.size() > initialSteps && ownerAgent.averageDistanceLastXPoints(initialSteps) < ownerAgent.getSpeed() * 2) {

            setOutOrBack(OutOrBack.BACK);
            log.info("setting mode to back because we are stuck");
            clearLocalCache();
            setIsStuckPenalty(true);
            broadcastMessage(getDirection(), SkelatalAgent.COMS, ownerAgent);
                

        } else if (localStack.size() > 200) {

            setOutOrBack(OutOrBack.BACK);
            log.info("setting mode to back beacause walked enough");
            clearLocalCache();
            broadcastMessage(getDirection(), SkelatalAgent.COMS, ownerAgent);
        }

    }

    private void clearLocalCache() {
        getVisibleCountList().clear();
    }

    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {

        checkForStuckOrDone(ownerAgent);

        if (isOutOrBack().equals(OutOrBack.OUT)) {

            doOutMode(dxDy, ownerAgent);

        } else {

            doBackMode(dxDy, ownerAgent);

        }

    }

    public OutOrBack isOutOrBack() {
        return outOrBack;
    }

    public void setOutOrBack(OutOrBack oob) {
        this.outOrBack = oob;
    }

    protected abstract void doOutMode(double[] dxDy, SkelatalAgent ownerAgent);
    private DirectionUpdater next = null;

    protected void setNextDirectionUpdater(DirectionUpdater du) {
        this.next = du;
    }

    protected DirectionUpdater getNextDirectionUpdater() {
        return next;
    }

    

    protected void doBackMode(double[] dxDy, SkelatalAgent ownerAgent) {
        Stack<float[]> locs = getLocalStack();

        if (locs == null) {
            log.warning("um, the uh stack is null");
            return;
        }

        // check memory array for north south east and west, then choose one
        if (locs.isEmpty() && ownerAgent.getMemory().containsKey(Direction.EAST.toString())
                && ownerAgent.getMemory().containsKey(Direction.NORTH.toString())
                && ownerAgent.getMemory().containsKey(Direction.SOUTH.toString())
                && ownerAgent.getMemory().containsKey(Direction.WEST.toString())) {

            chooseUpdater(ownerAgent.getMemory());
            
            return;
        } else if (locs.isEmpty()) {

            log.info("returned home but still don't have complete info from other agents so chillin");
            
            // so sit still
            dxDy[0] = 0;
            dxDy[1] = 0;

            return;
        }


        float[] destination = locs.pop();

        // set velocity equal to the one that would take me to this way point
        float dx = destination[0] - ownerAgent.getLocation()[0];
        float dy = destination[1] - ownerAgent.getLocation()[1];

        ownerAgent.setVelocityVector(new double[]{dx, dy});
    }
    
     /**
     * Based on memory let's choose the right direction
     * @param agentMemory 
     */
    public void chooseUpdater(HashMap<String, Float> agentMemory) {
        List<MemoryData> data = MemoryData.toMemoryData(agentMemory);
        if(data.isEmpty()){
            log.warning("problem: no  data from team memory");
            return;
        }
        
        DirectionUpdater du = null;
        String bestDirection = data.get(data.size()-1).getName();
        
        if(bestDirection.equals( Direction.NORTH.toString() )){
            
            du = new NorthernWalkableDirectionUpdater();
            
        }
        else if(bestDirection.equals( Direction.EAST.toString() ) ){
            
            du = new EasternWalkableDirectionUpdater();
            
        }
        else if ( bestDirection.equals( Direction.WEST.toString() )){
            
            du = new WesternWalkableDirectionUpdater();
            
        }
        else if ( bestDirection.equals( Direction.SOUTH.toString() )){
            
            du = new SouthernWalkableDirectionUpdater();
            
        }
        
        AlwaysTrueConditionChecker alwaysTrue = new AlwaysTrueConditionChecker();
        alwaysTrue.setNextState(du);
        setConditionChecker(alwaysTrue);
        
        log.log(Level.INFO, "team decided best route is {0} ", bestDirection);

    }

    public float averageFieldOfView() {
        if (totalVisible == null || totalVisible.isEmpty()) {
            return 0;
        }
        Float sum = 0.0f;

        for (Float i : totalVisible) {
            sum += i;
        }

        return sum / totalVisible.size();
    }
}
