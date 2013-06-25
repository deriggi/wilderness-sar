/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import geomutils.VectorUtils;
import java.rmi.server.Skeleton;
import raster.domain.agent.VectorAgent;
import strategy.updater.SkelatalDirectionUpdater;
import strategy.updater.condition.StateCondition;
import strategy.updater.message.UpdaterMessage;

/**
 *
 * @author Johnny
 */
public class BoundedEasternDirectionMaker extends  SkelatalDirectionUpdater {

    private int northBound;
    private int southBound;

    public void setSouthBound(int south) {
        this.southBound = south;
    }

    public void setNorthBound(int north) {
        this.northBound = north;
    }

//    @Override
    public void updateDirection(double[] dxDy, int[] location) {
        if (dxDy == null || dxDy.length <= 2) {
            return;
        }

        // make velocity east bound
        if (dxDy[0] < 0) {
            dxDy[0] *= -1;
        }

        // poke south. maybe make percentage of bound difference
        int amountTooNorth = (northBound + 2) - location[1];
        int amountTooSouth = location[1] - (southBound - 2);
        
        if ( amountTooNorth > 0 ) {
            dxDy[1] = 4 * amountTooNorth;
        }
        else if ( amountTooSouth  > 0) {
            dxDy[1] = -4 * amountTooSouth;
        }
        else{
            dxDy[0] += 4;
        }
        
        VectorUtils.limit(dxDy,4);
        
    }

    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectionUpdater getNextState() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCondition(StateCondition sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean switchIfConditionMet(VectorAgent vectorAgent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void getCondition(StateCondition sc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void notifyMe(UpdaterMessage message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateDirectionCaller(double[] velocity, VectorAgent va) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
