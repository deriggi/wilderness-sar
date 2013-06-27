/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

import geomutils.VectorUtils;
import java.util.Stack;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import strategy.Strategy;
import middletier.AgentService;
import raster.domain.Raster2D;

/**
 *
 * @author Johnny
 */
public class VectorAgent {

    private static final Logger log = Logger.getLogger(VectorAgent.class.getName());
    
    private Integer id = null;
    private float[] origin = new float[2];
//    private Strategy movementStrategy = null;
    private List<Strategy> strategies = new ArrayList<Strategy>();
    private int strategyIndex = 0;
    private Stack<float[]> stackedPositions = new Stack<float[]>();
    private int simpleDetectionRange = 20;
    private int stepsTaken = 0;
    private int masterTimestepsTaken = 0;

    public int getMasterTimestepsTaken() {
        return masterTimestepsTaken;
    }
    private String nameTag;
    

    public String getNameTag() {
        return nameTag;
    }

    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
    }
    
    

    public int getStepsTaken(){
        return this.stepsTaken;
    }

    public void setStepsTaken(int count){
        this.stepsTaken = count;
    }

    public void setSimpleDetectionRange(int detectionRange){
        this.simpleDetectionRange = detectionRange;
    }

    private void updateStepsTaken(){
        stepsTaken++;
    }

    public int getSimpleDetectionRange(){
        return simpleDetectionRange;
    }

    public Strategy getMovementStrategy(){
        return strategies.get(strategyIndex);
    }
    
    public void addMovementStrategy(Strategy movementStrategy) {
        strategies.add(movementStrategy);
    }
    
    
    
    /**
     * Do a smart strategy index update
     */
    public void incrementStrategyIndex(){
        int numberOfStrategies = strategies.size();
        if(strategyIndex+1 >= numberOfStrategies){
            strategyIndex = 0;
        }else{
            strategyIndex++;
        }
    }
    
    public boolean isNextStepOutOfBounds(){
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = getLocation();
        
        double[] v = getVelocityVector();
        
        double[] sum = VectorUtils.add(loc, VectorUtils.multiplyDonTouch(v, 4));
        
        
        boolean isInBounds = raster.isInBounds(sum);
        
        return !isInBounds;
        
    }
    
    public float[] getOrigin() {
        return origin;
    }

    public void pushLoc() {
        if (stackedPositions == null) {
            stackedPositions = new Stack<float[]>();
        }
        if(stackedPositions.size() > 1000){
            log.severe("stack is growing too large, ignoring");
            return;
        }
        stackedPositions.add(this.getLocation());
    }

    public Stack<float[]> getStackedPosition() {
        return stackedPositions;
    }

    public double getLongitude() {
        return getGeoLocation()[0];
    }

    public double getLatitude() {
        return getGeoLocation()[1];
    }

    public double[] getGeoLocation() {
        return RasterLoader.get(RasterConfig.BIG).getData().getLonLat(getLocation()[0], getLocation()[1]);
    }

    public void setOrigin(float[] origin) {
        this.origin = origin;
    }

    public Integer getId() {
        return id;
    }

    public VectorAgent() {
        velocity = new double[]{0.0, 0.0};

    }

    public IdLoc toIdLoc() {
        IdLoc idLoc = new IdLoc();
        idLoc.setId(getId());
        idLoc.setFoundOthers(foundOthers(getSimpleDetectionRange()));
        idLoc.setNameTag(getNameTag());
        return idLoc;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public boolean foundOthers(int range){
        int numberOfDetectedAgents = detect(range).size();
        
        return numberOfDetectedAgents > 0;
    }


    public Collection<VectorAgent> detect(int range) {
        
        HashMap<Double, VectorAgent> distanceAgentMap = AgentService.get().getAgentsWithinRange(getLocation(), range, this);
        return distanceAgentMap.values();

    }

    // rename to move?
    public void wander() {
        Strategy currentStrat = strategies.get(strategyIndex);
        
        currentStrat.calculateNextMove(this);
        updateStepsTaken();
        masterTimestepsTaken++;
        // test strategy for condition for switching to next strategy
        if(currentStrat.getIsTimeToSwitch(this)){
            log.log(Level.INFO, "swithing from {0} ", new Object[]{currentStrat.getName()});
            
            incrementStrategyIndex();
        }
    }

    // radians
    private double[] velocity = new double[2];
    // cells per whatever
    private float speed = 4;

    public float[] getLocation() {
        return location;
    }

    public void setLocation(float[] location) {
        this.location = location;
    }
    // column, row
    private float[] location = new float[2];

//    public 
    public double getBearing() {
//        if(velocity[0] == 0){
//            
//        }

        return Math.atan2(velocity[1], velocity[0]);
    }

    public void steer(double[] desired) {
        steer(desired, 1.0f);
    }

    // instead of towards, this should be dx dy
    public void steer(double[] desired, float weight) {


        double dx = desired[0];
        double dy = desired[1];

        //==== if too high add to dy
        /**if(getLocation()[1] < getOrigin()[1] - 4){
        dy = 2;
        }
        
        if(getLocation()[1] > getOrigin()[1] + 4){
        dy = -2;
        }
        
        if(Math.abs(getLocation()[1] - getOrigin()[1]) < 2 ){
        dx += 2;
        }**/
        //=====
//        double magnitude = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
//        dx = dx/magnitude;
//        dy = dy/magnitude;
        double[] steerVector = new double[2];

        steerVector[0] = (dx - velocity[0]);
        steerVector[1] = (dy - velocity[1]);

        velocity[0] += steerVector[0];
        velocity[1] += steerVector[1];
        double magnitude = 1;// Math.sqrt(Math.pow(velocity[0], 2) + Math.pow(velocity[1], 2));
        velocity[0] = 1 * velocity[0] / magnitude;
        velocity[1] = 1 * velocity[1] / magnitude;

        VectorUtils.limit(velocity, getSpeed());

//        double magnitude = Math.sqrt(Math.pow(velocity[0], 2) + Math.pow(velocity[1], 2));

        // unitize
//        velocity[0] = 1*(velocity[0]/magnitude);
//        velocity[1] = 1*(velocity[1]/magnitude);

        float newX = (float) velocity[0] * weight + location[0];
        float newY = (float) velocity[1] * weight + location[1];

        setLocation(new float[]{newX, newY});
    }

    public double[] steer(double radians) {
        double newBearing = getBearing() + radians;
        double dx = speed * Math.cos(newBearing);
        double dy = speed * Math.sin(newBearing);
        // east
//        if (dx < 0){
//            dx = 1.5;
//        }

        return new double[]{dx, dy};

//        steer ( new double[]{ dx, dy } ) ;

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

        double v1magnitude = Math.sqrt(Math.pow(v1[0], 2) + Math.pow(v1[1], 2));
        double v2magnitude = Math.sqrt(Math.pow(v2[0], 2) + Math.pow(v2[1], 2));

        double[] v1Unit = {v1[0] / v1magnitude, v1[1] / v1magnitude};
        double[] v2Unit = {v2[0] / v2magnitude, v2[1] / v2magnitude};

        double dotProduct = 0;
        dotProduct += v1Unit[0] * v2Unit[0];
        dotProduct += v1Unit[1] * v2Unit[1];

        return dotProduct;

    }

//    public void steerRando() {
//        int[] currentPosition = getLocation();
//        double[] south = new double[2];
//        south[0] = currentPosition[0];
//        south[1] = currentPosition[1] + (4 * Math.random() - 2);
//
//        steer(south);
//
//    }
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
}
