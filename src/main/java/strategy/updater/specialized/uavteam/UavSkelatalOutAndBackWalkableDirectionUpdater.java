/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.specialized.uavteam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;
import raster.domain.agent.SkelatalAgent;
import strategy.DirectionUpdater;
import strategy.updater.Direction;
import strategy.updater.SkelatalDirectionUpdater;

/**
 *
 * @author Johnny
 */
public abstract class UavSkelatalOutAndBackWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(UavSkelatalOutAndBackWalkableDirectionUpdater.class.getName());
    private final int initialSteps = 50;
    private boolean registered = false;

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

    private class MemoryData implements Comparable<MemoryData> {

        public MemoryData(String name, float data) {
            this.name = name;
            this.data = data;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MemoryData other = (MemoryData) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            if (Float.floatToIntBits(this.data) != Float.floatToIntBits(other.data)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 97 * hash + Float.floatToIntBits(this.data);
            return hash;
        }
        private String name;
        private float data;

        public float getData() {
            return data;
        }

        public void setData(float data) {
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public int compareTo(MemoryData o) {
            if (o == null) {
                return 1;
            }
            if (o.equals(this)) {
                return 0;
            }

            if (o.getData() > getData()) {
                return -1;
            }

            if (o.getData() < getData()) {
                return 1;
            }
            return 0;
        }
    }

    private List<MemoryData> toMemoryData(HashMap<String, Float> mem) {

        Set<String> keys = mem.keySet();
        List<MemoryData> memoryData = new ArrayList<MemoryData>();

        for (String s : keys) {
            memoryData.add(new MemoryData(s, mem.get(s)));
        }

        Collections.sort(memoryData);

        return memoryData;

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

            // repeated code!
            List<MemoryData> memCache = toMemoryData(ownerAgent.getMemory());
            for(MemoryData memData : memCache){
                
                log.info(memData.getName() + "  " + memData.getData());
            
            }
//            for (int i = memCache.size() - 1; i > 0; i--) {
//                log.log(Level.INFO, " the order of goodness is {0} ", memCache.get(i).getName());
//            }


            return;
        } else if (locs.isEmpty()) {

            log.info("returned home but still don't have complete info from other agents");

            // repeated code!
            List<MemoryData> memCache = toMemoryData(ownerAgent.getMemory());
            for(MemoryData memData : memCache){
                log.info(memData.getName() + "  " + memData.getData());
            }


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

        for (Float i : totalVisible) {
            sum += i;
        }

        return sum / totalVisible.size();
    }
}
