/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

import geomutils.VectorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.AgentService;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import strategy.updater.Direction;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalAgent {

    private static final Logger log = Logger.getLogger(SkelatalAgent.class.getName());
    public static final String COMS = "coms";
    public static final int MAX_STACK_SIZE = 200;
    public static final int MINI_STACK_SIZE = 400;
    private float[] origin = new float[2];
    private double[] velocity = new double[2];
    private float speed = 4;
    private String simId = null;
    private int detectionRange = 20;
    private float[] location = new float[2];
    private Stack<float[]> masterStack = new Stack<float[]>();
    private int masterTimestepsTaken = 0;
    private Integer id;
    private AgentName nameTag;
    private float considerAFieldChance = 0.05f;
    private HashMap<String, Stack<short[]>> mapOfStacks = new HashMap<String, Stack<short[]>>();
    private int stepsTaken = 0;
    private Direction eastWestIntention = null;
    private Direction northSouthIntention = null;
    private HashMap<String, Float> memory = new HashMap<String, Float>();
    private int delay = 0;
    private boolean stuck = false;
    private Direction direction = null;
    private BorderChecker borderChecker = new BorderChecker();
    
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isStuck() {
        return stuck;
    }

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
    }

    public boolean doDelay() {
        if (delay > 0) {
            delay--;
            return true;
        }

        return false;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return this.delay;
    }

    public String getSimId() {
        return simId;
    }

    public void setSimId(String simId) {
        this.simId = simId;
    }

    public void handleMessage(HashMap<String, Float> message) {
        if (message == null || message.isEmpty()) {
            return;
        }
        Set<String> keys = message.keySet();
        for (String key : keys) {
            memory.put(key, message.get(key));
        }


    }

    public HashMap<String, Float> getMemory() {
        return memory;
    }

    public Direction getEastWestIntention() {
        return eastWestIntention;
    }

    public void setEastWestIntention(Direction eastWestIntention) {
        this.eastWestIntention = eastWestIntention;
    }

    public Direction getNorthSouthIntention() {
        return northSouthIntention;
    }

    public void setNorthSouthIntention(Direction northSouthIntention) {
        this.northSouthIntention = northSouthIntention;
    }

    public int getStepsTaken() {
        return this.stepsTaken;
    }

    public void setStepsTaken(int count) {
        this.stepsTaken = count;
    }

    public boolean isNextStepOutOfBounds() {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = getLocation();

        double[] v = getVelocityVector();

        double[] sum = VectorUtils.add(loc, VectorUtils.multiplyDonTouch(v, getSpeed()));


        boolean isInBounds = raster.isInBounds(sum);

        return !isInBounds;

    }

    public void updateStepsTaken() {
        stepsTaken++;
    }

    public void incrementMasterTimestep() {
        masterTimestepsTaken++;
    }

    public void pushLoc(String key) {
        if (key == null || !mapOfStacks.containsKey(key)) {
            return;
        }

        Stack<short[]> someStack = mapOfStacks.get(key);
        if (someStack.size() > MINI_STACK_SIZE) {
            someStack.remove(0);
        }

        float[] loc = getLocation();
        short[] shortLoc = new short[]{(short) loc[0], (short) loc[1]};

        someStack.push(shortLoc);

    }

    public void registerStack(String key) {
        if (key == null) {
            return;
        }

        if (mapOfStacks.containsKey(key)) {
            log.log(Level.INFO, "already has key  {0} so not registering ", key);
            return;
        }
        log.log(Level.INFO, "registering stack for key {0}", key);
        mapOfStacks.put(key, new Stack<short[]>());
        log.log(Level.INFO, "stacks count is now {0}", mapOfStacks.size());
    }

    public Stack<short[]> getStackedPosition(String key) {
        return mapOfStacks.get(key);
    }

    public float getConsiderAFieldChance() {
        return considerAFieldChance;
    }

    public void setConsiderAFieldChance(float considerAFieldChance) {
        this.considerAFieldChance = considerAFieldChance;
    }

    public int getDetectionRange() {
        return detectionRange;
    }

    public AgentName getNameTag() {
        return nameTag;
    }

    public void setNameTag(AgentName nameTag) {
        this.nameTag = nameTag;
    }

    public double[] getLastVelocity() {
        return lastVelocity;
    }

    public void setLastVelocity(double[] lastVelocity) {
        this.lastVelocity = new double[]{lastVelocity[0], lastVelocity[1]};
    }

    public Stack<float[]> getMasterStack() {
        return masterStack;
    }

    public void clearLastVelocity() {
        lastVelocity = null;
    }
    private double[] lastVelocity = null;

    public double[] steer(double radians) {
        double newBearing = getBearing() + radians;
        double dx = speed * Math.cos(newBearing);
        double dy = speed * Math.sin(newBearing);

        return new double[]{dx, dy};


    }

    public boolean isAgitated() {
        int size = getDotProductBuffer().size();
        float dpAverage = getDotProductBufferAverage();
        if (size >= 39 && dpAverage < -0.72f) {
            log.info("owner is agitated!");

            return true;
        }

        return false;

    }

    public int getMasterTimestepsTaken() {
        return masterTimestepsTaken;
    }

    public abstract void wander();

    public double getLongitude() {
        return getGeoLocation()[0];
    }

    public double getLatitude() {
        return getGeoLocation()[1];
    }

    public double[] getGeoLocation() {
        return RasterLoader.get(RasterConfig.BIG).getData().getLonLat(getLocation()[0], getLocation()[1]);
    }

    public float[] getOrigin() {
        return origin;
    }

    public void setOrigin(float[] origin) {
        this.origin = origin;
    }

    /**
     * -1 is highly agitated and 1 is not agitated
     * @return 
     */
    public float getDotProductBufferAverage() {
        if (dotProductBuffer == null || dotProductBuffer.isEmpty()) {
            return 0;
        }

        Float sum = 0.0f;
        for (Float f : dotProductBuffer) {
            sum += f;
        }
        log.log(Level.FINE, "dividing {0} by {1} ", new Float[]{sum, (float) dotProductBuffer.size()});
        return sum / dotProductBuffer.size();
    }

    public void pushLoc() {
        if (masterStack.size() > MAX_STACK_SIZE) {
            masterStack.remove(0);
        }

        masterStack.push(getLocation());


    }

    public void addToDotProductBuffer() {
        if (getLastVelocity() != null && VectorUtils.magnitude(getLastVelocity()) != 0 && VectorUtils.magnitude(getVelocityVector()) != 0) {

            Float dotProduct = new Float(dotProduct(getLastVelocity(), getVelocityVector()));
            if (dotProductBuffer.size() >= 40) {
                dotProductBuffer.remove(0);
            }
            dotProductBuffer.add(dotProduct);
        }
    }
    private ArrayList<Float> dotProductBuffer = new ArrayList<Float>();

    public ArrayList<Float> getDotProductBuffer() {
        return dotProductBuffer;
    }

    public Float averageDistanceLastXPoints(int x) {
        if (masterStack == null || masterStack.isEmpty() || x > masterStack.size()) {
            return null;
        }
        float[] positinToMeasureAgainst = masterStack.get(masterStack.size() - 1);


        int positionToStart = masterStack.size() - x;
        float sum = 0.0f;
        int i = 0;
        for (i = positionToStart; i < masterStack.size(); i++) {
            sum += VectorUtils.distance(masterStack.get(i), positinToMeasureAgainst);
        }

        float average = sum / (i - positionToStart - 1);

        return average;

    }

    public double dotProduct(double[] v1, double[] v2) {
        double v1magnitude = Math.sqrt(Math.pow(v1[0], 2) + Math.pow(v1[1], 2));
        double v2magnitude = Math.sqrt(Math.pow(v2[0], 2) + Math.pow(v2[1], 2));

        double[] v1Unit = {v1[0] / v1magnitude, v1[1] / v1magnitude};
        double[] v2Unit = {v2[0] / v2magnitude, v2[1] / v2magnitude};

        double dotProduct = 0;
        dotProduct += v1Unit[0] * v2Unit[0];
        dotProduct += v1Unit[1] * v2Unit[1];

        return dotProduct;
    }

    public double dotProduct(double[][] vectorOne, double[][] vectorTwo) {
        double xa1 = vectorOne[0][0];
        double xa2 = vectorOne[1][0];

        double ya1 = vectorOne[0][1];
        double ya2 = vectorOne[1][1];

        double xb1 = vectorTwo[0][0];
        double xb2 = vectorTwo[1][0];

        double yb1 = vectorTwo[0][1];
        double yb2 = vectorTwo[1][1];

        double v1[] = {xa2 - xa1, ya2 - ya1};
        double v2[] = {xb2 - xb1, yb2 - yb1};

        return dotProduct(v1, v2);
    }

    public double[] getVelocityVector() {

        return velocity;
    }

    public void setVelocityVector(double[] aVelocityVector) {
        this.velocity = aVelocityVector;
    }

    public Direction getGeneralDirection() {


        if (velocity[1] < 0 && Math.abs(velocity[1]) > Math.abs(velocity[0])) {
            return Direction.NORTH;
        }

        if (velocity[1] > 0 && Math.abs(velocity[1]) > Math.abs(velocity[0])) {
            return Direction.SOUTH;
        }

        if (velocity[0] < 0 && Math.abs(velocity[0]) > Math.abs(velocity[1])) {
            return Direction.WEST;
        }

        if (velocity[0] > 0 && Math.abs(velocity[0]) > Math.abs(velocity[1])) {
            return Direction.EAST;
        }

        log.log(Level.INFO, "oddly, there is no defined direction for {0}, {1}", velocity);
        return null;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = Math.abs(speed);
    }

    public double getBearing() {

        return Math.atan2(velocity[1], velocity[0]);

    }

    public void steer(double[] desired) {
        steer(desired, 1.0f);
    }

    public void steer(double[] desired, float weight) {


        double dx = desired[0];
        double dy = desired[1];

        double[] steerVector = new double[2];

        steerVector[0] = (dx - velocity[0]);
        steerVector[1] = (dy - velocity[1]);

        velocity[0] += steerVector[0];
        velocity[1] += steerVector[1];
        double magnitude = 1;
        velocity[0] = 1 * velocity[0] / magnitude;
        velocity[1] = 1 * velocity[1] / magnitude;

        VectorUtils.limit(velocity, getSpeed());


        float newX = (float) velocity[0] * weight + location[0];
        float newY = (float) velocity[1] * weight + location[1];

        setLocation(new float[]{newX, newY});
    }

    public float[] getLocation() {
        return location;
    }

    public void setLocation(float[] location) {
        this.location = location;
    }

    public Integer getId() {
        return id;
    }

    public IdLoc toIdLoc() {
        IdLoc idLoc = new IdLoc();
        idLoc.setSimId(getSimId());
        idLoc.setId(getId());
        idLoc.setFoundOthers(foundOthers(getDetectionRange(), AgentName.LOST));
        idLoc.setNameTag(getNameTag().toString());
        return idLoc;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean foundOthers(int range, AgentName agentName) {
        int numberOfDetectedAgents = detect(range, agentName).size();
        return numberOfDetectedAgents > 0;
    }

    public Collection<SkelatalAgent> detect(int range, AgentName name) {

        HashMap<Double, SkelatalAgent> distanceAgentMap = AgentService.get().getAgentsWithinRange(getLocation(), range, this, name);
        return distanceAgentMap.values();

    }
    
    public boolean isWithinRangeOfBorder(int range){
        return borderChecker.isBorderWithinRange(this, range);
    }

    private class BorderChecker {

        private Raster2D raster = raster = RasterLoader.get(RasterConfig.BIG).getData();
        private int eastEnd = raster.countColumns() - 1;
        private int westEnd = 0;
        private int northEnd = 0;
        private int southEnd = raster.countRows() - 1;

        public int getEastEnd() {
            return eastEnd;
        }

        public int getNorthEnd() {
            return northEnd;
        }

        public int getSouthEnd() {
            return southEnd;
        }

        public int getWestEnd() {
            return westEnd;
        }

        public boolean isBorderWithinRange(SkelatalAgent agent, int range) {

            float[] loc = agent.getLocation();

            if (Math.abs(loc[0] - getWestEnd()) < range) {

                return true;

            } else if (Math.abs(loc[0] - getEastEnd()) < range) {

                return true;

            }

            if (Math.abs(loc[1] - getSouthEnd()) < range) {

                return true;

            } else if (Math.abs(loc[1] - getNorthEnd()) < range) {

                return true;

            }

            return false;
        }
    }
}
