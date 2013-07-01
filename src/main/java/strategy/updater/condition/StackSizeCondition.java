/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public class StackSizeCondition implements StateCondition{

    private int maxSize = 0;
    public StackSizeCondition(int size){
        this.maxSize = size;
    }
    
    @Override
    public boolean checkState(VectorAgent va) {
        if(va.getMasterStack().size() >= maxSize){
            return true;
        }
        return false;
    }

    @Override
    public DirectionUpdater getNextState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setNextState(DirectionUpdater du) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
