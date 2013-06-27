/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public interface Strategy {
    
    public Integer getTimestep();
    public void calculateNextMove(VectorAgent ownerAgent);
    public boolean getIsTimeToSwitch(VectorAgent ownerAgent);
    public void setName(String name);
    public String getName();
    public void addDirectionUpdater(DirectionUpdater updater);
    
    
}
