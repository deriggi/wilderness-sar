/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import raster.domain.agent.VectorAgent;

/**
 * 1) wandering
2) wandering avoiding steep slope
3) direction wandering
4) direction wandering avoiding steep slope
5) wandering avoiding steep slope maximizing viewshed
6) direction sampling
	a) pick starting point
	b) wander directionally
	c) backtrack
directional wander strategy will have direction, and may have bounds
 *
 * @author Johnny
 */
public class DirectionalWanderStrategy implements Strategy {

    private VectorAgent ownerAgent = null;
    // throttles the amount wandering at each step
    private final float DEFAULT_WANDER = 6.0f;
    private float denom = 6.0f;
    
    private final float PI_FLOAT = (float)Math.PI;
    private float wanderingNess = (float)( PI_FLOAT / denom );
    private float increment = 0.5f;
    
    private int upperBound;
    private int lowerBound;
    
    private DirectionUpdater directionMaker = null;

    public DirectionUpdater getDirectionMaker() {
        return directionMaker;
    }

    public void setDirectionMaker(DirectionUpdater directionMaker) {
        this.directionMaker = directionMaker;
    }
    // increase the wandering by an increment 
    public void increaseWander(){
        increaseWander(1);   
    }
    
    public void resetWander(){
        denom = DEFAULT_WANDER;
    }
    
    public void increaseWander(int scale){
        if(denom < increment){
            denom = increment;
            return;
        }
        denom -= scale*increment;   
    }
    
    public void decreaseWander(){
        decreaseWander(1);
    }
    
    public void decreaseWander(int scale){
        if( denom > 2*PI_FLOAT ){
            denom = 2*PI_FLOAT;
            return;
        }
        denom += scale*increment;
    }
         
    
//    @Override
//    public void calculateNextMove() {
//        
//        // wander a bit
//        double amountToSteer = wanderingNess * getALittle();
//        double[] acceleration = ownerAgent.steer(amountToSteer);
//        
//        directionMaker.updateDirection(acceleration);
//        // update against bounds
//        
//        ownerAgent.steer( acceleration );
//    }

    /**
     * Generates a decimal that is usually close to zero. 
     * Even chance of being negative
     * 
     * @return the small fraction
     */
    private double getALittle() {
        return Math.random() - Math.random();
    }

    @Override
    public void calculateNextMove(VectorAgent ownerAgent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addDirectionUpdater(DirectionUpdater updater) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Integer getTimestep() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean getIsTimeToSwitch(VectorAgent ownerAgent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
