/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class BacktrackDirectionUpdater extends SkelatalDirectionUpdater {

    private final static Logger log = Logger.getLogger(BacktrackDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "Backtrack";
    }
    private Stack<float[]> myPoints = null;
    
    public Stack<float[]> getMyPoints() {
        return myPoints;
    }
    private String key = null;
    public BacktrackDirectionUpdater(String key){
        this.key = key;
    }

    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        log.info("runnignn backtrack");
        // initialize
        if (myPoints == null){
            myPoints = ownerAgent.getStackedPosition(key);
            if(myPoints == null){
                log.warning("USING MASTER stack");
                myPoints =new Stack<float[]>();
                myPoints.addAll(ownerAgent.getMasterStack());
            }
        }
        
        
        // pop and rock
        if ( myPoints.size() > 0) {
            log.log(Level.INFO, "running backtrack stack size is {0} ", myPoints.size());

            float[] destination = myPoints.pop();

            // set velocity equal to the one that would take me to this way point
            float dx = destination[0] - ownerAgent.getLocation()[0];
            float dy = destination[1] - ownerAgent.getLocation()[1];

            ownerAgent.setVelocityVector(new double[]{dx, dy});
        }else{
            log.info("my stack is size zero so not doin nuthin'");
        }

    }
}
