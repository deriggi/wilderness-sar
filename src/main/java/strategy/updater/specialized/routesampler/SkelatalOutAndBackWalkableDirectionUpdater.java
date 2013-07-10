/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.routesampler;

import java.util.ArrayList;
import java.util.Stack;
import java.util.logging.Logger;
import raster.domain.agent.SkelatalAgent;
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
    private final Stack<float[]> localStack = new Stack<float[]>();

    public Stack<float[]> getLocalStack() {
        return localStack;
    }
    private ArrayList<Integer> totalVisible = new ArrayList<Integer>();

    public ArrayList<Integer> getVisibleCountList() {
        return totalVisible;
    }
    private OutOrBack outOrBack = OutOrBack.OUT;

    public void checkForStuckOrDone(SkelatalAgent ownerAgent) {

        // if stuck or full then go back
        if (localStack.size() > initialSteps && ownerAgent.averageDistanceLastXPoints(initialSteps) < ownerAgent.getSpeed() * 2) {

            setOutOrBack(OutOrBack.BACK);
            log.info("setting mode to back because we are stuck");
            clearLocalCache();

        } else if (localStack.size() > 200) {

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

    public float averageFieldOfView() {
        if (totalVisible == null || totalVisible.isEmpty()) {
            return 0;
        }
        Float sum = 0.0f;

        for (Integer i : totalVisible) {
            sum += i;
        }

        return sum / totalVisible.size();
    }
}
