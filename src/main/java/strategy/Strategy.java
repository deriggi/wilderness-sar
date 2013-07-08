/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy;

import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public interface Strategy {
    
    public Integer getTimestep();
    public void calculateNextMove(SkelatalAgent ownerAgent);
    public boolean getIsTimeToSwitch(SkelatalAgent ownerAgent);
    public void setName(String name);
    public String getName();
    public void addDirectionUpdater(DirectionUpdater updater);
    
    
}
