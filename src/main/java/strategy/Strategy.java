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
    
    public void calculateNextMove(VectorAgent ownerAgent);
    
    
    public void addDirectionUpdater(DirectionUpdater updater);
    
    
}
