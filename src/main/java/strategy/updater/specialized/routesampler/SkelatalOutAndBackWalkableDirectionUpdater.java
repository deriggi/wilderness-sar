/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.routesampler;

import geomutils.VectorUtils;
import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.SkelatalAgent;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;
import strategy.updater.Direction;
import strategy.updater.SkelatalDirectionUpdater;
import strategy.updater.conditionchecker.AlwaysTrueConditionChecker;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalOutAndBackWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(SkelatalOutAndBackWalkableDirectionUpdater.class.getName());
    private final int initialSteps = 50;
    public static final int MAX_ROUTE_SAMPLE_DISTANCE = 200;
    private final Stack<float[]> localStack = new Stack<float[]>();

    private boolean addStuckPenalty = false;
    
    public Stack<float[]> getLocalStack() {
        return localStack;
    }
    private ArrayList<Float> totalVisible = new ArrayList<Float>();

    public ArrayList<Float> getVisibleCountList() {
        return totalVisible;
    }
    private OutOrBack outOrBack = OutOrBack.OUT;

    public void checkForStuckOrDone(SkelatalAgent ownerAgent) {

        // if stuck or full then go back
        if (localStack.size() > initialSteps && ownerAgent.averageDistanceLastXPoints(initialSteps) < ownerAgent.getSpeed() * 2) {

            setOutOrBack(OutOrBack.BACK);
            log.info("setting mode to back and adding stuck penalty because we are stuck");
            clearLocalCache();
            addStuckPenalty = true;

        } else if (localStack.size() > MAX_ROUTE_SAMPLE_DISTANCE) {

            setOutOrBack(OutOrBack.BACK);
            log.info("setting mode to back beacause walked enough");
            clearLocalCache();
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

            // clear that shit
//            ownerAgent.getMemory().remove(Direction.EAST.toString());
            log.info("done so choosing a direction after route sampling!");
            //@TODO do choose a direction
            log.log(Level.INFO, "East value is {0} ", ownerAgent.getMemory().get(Direction.EAST.toString()));
            log.log(Level.INFO, "West value is {0} ", ownerAgent.getMemory().get(Direction.WEST.toString()));
            log.log(Level.INFO, "South value is {0} ", ownerAgent.getMemory().get(Direction.SOUTH.toString()));
            log.log(Level.INFO, "North value is {0} ", ownerAgent.getMemory().get(Direction.NORTH.toString()));

            return;
        } else if (locs.isEmpty()) {

            //  done poppin so go to next strategy
            AlwaysTrueConditionChecker alwaysTrue = new AlwaysTrueConditionChecker();
            alwaysTrue.setNextState(next);
            setConditionChecker(alwaysTrue);
            log.info("switching to next route");
            return;
        }


        float[] destination = locs.pop();

        // set velocity equal to the one that would take me to this way point
        float dx = destination[0] - ownerAgent.getLocation()[0];
        float dy = destination[1] - ownerAgent.getLocation()[1];

        ownerAgent.setVelocityVector(new double[]{dx, dy});

    }

    public void goTowardsVisibleCells(ArrayList<SlopeDataCell> visibleCells, Raster2D raster, float[] loc, double dxDy[]) {
        visibleCells = raster.getSlopeLessThan1D(visibleCells, VectorAgent.WALKABLE_SLOPE);


        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);
        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];
    }

    /**
     * Considers both the distance traveled and the 
     * @param ownerAgent
     * @param visibleCells
     * @return 
     */
    public float calculateRouteQuality(SkelatalAgent ownerAgent, ArrayList<SlopeDataCell> visibleCells) {
        double distanceFromHome = VectorUtils.distance(ownerAgent.getOrigin(), ownerAgent.getLocation());
        int stackSize = getLocalStack().size();
        double distancePortion = 0;
        
        if (stackSize > 0) {
            distancePortion = distanceFromHome / ( getLocalStack().size() * ownerAgent.getSpeed() );
            log.log(Level.INFO, "distance from home {0} out of {1} ", new Float[]{(float)distanceFromHome, (float)( getLocalStack().size() * ownerAgent.getSpeed() ) });
            log.log(Level.INFO, "distance portion is {0} ", distancePortion);
        }
        
        float denom = (float) (10 * VectorAgent.SHORT_VIS_RANGE * VectorAgent.SHORT_VIS_RANGE)/2.0f;
        int num = visibleCells.size();
        float visiblePortion = (float) (num / denom);
        float routeQuality = (float) (distancePortion + visiblePortion);
        log.log(Level.INFO, "num and denom are  {0} {1}", new Float[]{(float)num, denom});
        return routeQuality;

    }

    public float averageFieldOfView() {
        if (totalVisible == null || totalVisible.isEmpty()) {
            return 0;
        }
        Float sum = 0.0f;

        for (Float i : totalVisible) {
            sum += i;
        }
        float avg = sum / totalVisible.size();
        
        if(addStuckPenalty){
            avg = avg/2.0f;
        }
        return avg;

        
    }
}
