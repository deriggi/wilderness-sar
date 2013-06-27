/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.Stack;
import raster.domain.agent.VectorAgent;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class BacktrackDirectionUpdater extends SkelatalDirectionUpdater {
    
    @Override
    public String toString(){
        return "Backtrack";
    }

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Stack<float[]> points = ownerAgent.getStackedPosition();

        if (points != null && points.size() > 0) {
            
            float[] destination = points.pop();

            // set velocity equal to the one that would take me to this way point
            float dx = destination[0] - ownerAgent.getLocation()[0];
            float dy = destination[1] - ownerAgent.getLocation()[1];

            ownerAgent.setVelocityVector(new double[]{dx, dy});
        }
        
        // implement a place for an observer in skeltaldu so things can react to state changes
    }

    @Override
    public void notifyMe(UpdaterMessage message) {
    }
}
