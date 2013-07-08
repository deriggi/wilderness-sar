/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

import geomutils.VectorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.AgentService;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalAgent {

    private static Logger log = Logger.getLogger(SkelatalAgent.class.getName());
    public static final int MAX_STACK_SIZE = 200;
    private float[] origin = new float[2];
    private double[] velocity = new double[2];
    private float speed = 4;
    private int detectionRange = (int) speed * 2;
    private float[] location = new float[2];
    private Stack<float[]> masterStack = new Stack<float[]>();
    private int masterTimestepsTaken = 0;
    private Integer id;
    private String nameTag;
    private float considerAFieldChance = 0.10f;
    private HashMap<String, Stack<float[]>> mapOfStacks = new HashMap<String, Stack<float[]>>();
    private int stepsTaken = 0;

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

        Stack<float[]> someStack = mapOfStacks.get(key);
        if (someStack.size() > MAX_STACK_SIZE) {
            someStack.remove(0);
        }

        someStack.push(getLocation());

    }

    public void registerStack(String key) {
        if (mapOfStacks.containsKey(key)) {
            log.log(Level.INFO, "already has key  {0} so not registering ", key);
            return;
        }
        log.log(Level.INFO, "registering stack for key {0}", key);
        mapOfStacks.put(key, new Stack<float[]>());
        log.log(Level.INFO, "stacks count is now {0}", mapOfStacks.size());
    }

    public Stack<float[]> getStackedPosition(String key) {
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

    public String getNameTag() {
        return nameTag;
    }

    public void setNameTag(String nameTag) {
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
        log.log(Level.INFO, "dividing {0} by {1} ", new Float[]{sum, (float) dotProductBuffer.size()});
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
        idLoc.setId(getId());
        idLoc.setFoundOthers(foundOthers(detectionRange));
        idLoc.setNameTag(getNameTag());
        return idLoc;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean foundOthers(int range) {
        int numberOfDetectedAgents = detect(range).size();

        return numberOfDetectedAgents > 0;
    }

    public Collection<VectorAgent> detect(int range) {

        HashMap<Double, VectorAgent> distanceAgentMap = AgentService.get().getAgentsWithinRange(getLocation(), range, this);
        return distanceAgentMap.values();

    }
}
