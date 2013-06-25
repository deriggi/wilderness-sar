/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.logging.Logger;
import raster.domain.agent.VectorAgent;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class WanderDirectionUpdater extends SkelatalDirectionUpdater {

    private final static Logger log = Logger.getLogger(WanderDirectionUpdater.class.getName());
    
    private final float DEFAULT_WANDER = 5.0f;
    private float denom = DEFAULT_WANDER;
    private final float PI_FLOAT = (float) Math.PI;
    private float wanderingNess = (float) (PI_FLOAT / denom);

    @Override
    public String toString() {
        return "Wander";
    }


    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        if (dxDy == null || dxDy.length != 2) {
            return;
        }

        double amountToSteer = wanderingNess * getALittle();
        double[] desiredVector = ownerAgent.steer(amountToSteer);
        dxDy[0] += desiredVector[0];
        dxDy[1] += desiredVector[1];
        
        ownerAgent.setVelocityVector(desiredVector);


    }


    private double getALittle() {
        return Math.random() - Math.random();
    }

    @Override
    public void notifyMe(UpdaterMessage message) {
    }
}
