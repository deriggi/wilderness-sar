/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.observer;

import java.util.Stack;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class ClearStackExitObserver implements DirectionUpdaterObserver{

    @Override
    public void notifyOfStateChange(VectorAgent va) {
        Stack<float[]> stack = va.getStackedPosition();
        if(stack != null){
            stack.clear();
        }
    }
    
}
