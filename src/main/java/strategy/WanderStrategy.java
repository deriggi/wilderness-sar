/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import directionupdate.Switch;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class WanderStrategy implements Strategy {

    private static final Logger log = Logger.getLogger(WanderStrategy.class.getName());
//    private VectorAgent ownerAgent = null;
    // throttles the amount wandering at each step
    private List<DirectionUpdater> updaters = new ArrayList<DirectionUpdater>();
    private final float DEFAULT_WANDER = 5.0f;
    private float denom = DEFAULT_WANDER;
    private final float PI_FLOAT = (float) Math.PI;
    private float wanderingNess = (float) (PI_FLOAT / denom);
    private float increment = 0.5f;

    // increase the wandering by an increment 
    public void increaseWander() {
        increaseWander(1);
    }

    public void resetWander() {
        denom = DEFAULT_WANDER;
    }

    public void increaseWander(int scale) {
        if (denom < increment) {
            denom = increment;
            return;
        }
        denom -= scale * increment;
    }

    public void decreaseWander() {
        decreaseWander(1);
    }

    public void decreaseWander(int scale) {
        if (denom > 2 * PI_FLOAT) {
            denom = 2 * PI_FLOAT;
            return;
        }
        denom += scale * increment;
    }

    private void replaceIfNotNull(DirectionUpdater oldUpdater, DirectionUpdater newUpdater) {
        if (newUpdater == null) {
            return;
        }
        int index = updaters.indexOf(oldUpdater);
        updaters.remove(oldUpdater);
        updaters.add(index, newUpdater);
    }
    

    @Override
    public void calculateNextMove(VectorAgent ownerAgent) {

        List<Switch> switches = new ArrayList<Switch>();
        
        // apply the force, then steer by a little
        for (DirectionUpdater updater : updaters) {
            
            // apply update to agent velocity
            updater.updateDirectionCaller(ownerAgent.getVelocityVector(), ownerAgent);
            log.log(Level.INFO, "running {0}", updater.toString());
            
            // if there is a condition has been met
            if(updater.switchIfConditionMet(ownerAgent)){
                switches.add(new Switch(updater, updater.getNextState()));
            }
            
            // does update if needed
//            DirectionUpdater newState = updater.getNextState();
//            replaceIfNotNull(updater, newState);
        }
        
        // swap out accelerators if need be
        for(Switch switchie: switches){
            replaceIfNotNull(switchie.getOld(), switchie.getNewOne());
        }
        
        
//        double amountToSteer = wanderingNess * getALittle();
//        double[] desiredVector = ownerAgent.steer(amountToSteer);
        
        // probably just make an add thing
        ownerAgent.steer(ownerAgent.getVelocityVector(), 1);

    }

   

    @Override
    public void addDirectionUpdater(DirectionUpdater updater) {
        this.updaters.add(updater);
    }
    
    public void addAllDirectinoUpdaters(List<DirectionUpdater> updadters){
        updaters.addAll(updadters);
    }
}
