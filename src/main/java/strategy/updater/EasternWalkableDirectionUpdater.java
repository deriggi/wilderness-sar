/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import strategy.updater.specialized.WesternWalkableDirectionUpdater;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;
import strategy.updater.conditionchecker.AlwaysTrueConditionChecker;

/**
 *
 * @author Johnny
 */
public class EasternWalkableDirectionUpdater extends SkelatalDirectionUpdater {
    
    private static final Logger log = Logger.getLogger(EasternWalkableDirectionUpdater.class.getName());
    
    @Override
    public String toString() {
        return "east walkable";        
    }
    private float minSlope = .20f;
    
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], 10);

//        raster.getEasternCutoutCells(visibleCells, (int)loc[0], (int)loc[1], 8);
        raster.getEasternCells(visibleCells, (int) loc[0], (int) loc[1]);
        visibleCells = raster.getSlopeLessThan1D(visibleCells, minSlope);
        
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);
        
        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];
        
        
        
        if (Math.abs(acceleration[0]) == 0 && Math.abs(acceleration[1]) == 0) {
            
//            log.log(Level.WARNING, "acceleration {0} {1} ", new Object[]{(int) acceleration[0], (int) acceleration[1]});
//            
//            acceleration = expand(loc, raster);
//            dxDy[0] = acceleration[0];
//            dxDy[1] = acceleration[1];
//            
//            log.log(Level.WARNING, "expanding view acceleration {0} {1} ", new Object[]{(int) acceleration[0], (int) acceleration[1]});
//            
//            
//            if (Math.abs(acceleration[0]) == 0 && Math.abs(acceleration[1]) == 0) {
//                
                log.log(Level.WARNING, "acceleration {0} {1} ", new Object[]{(int) acceleration[0], (int) acceleration[1]});
                log.log(Level.WARNING, "attempting switch to west ");
                
                AlwaysTrueConditionChecker truthiness = new AlwaysTrueConditionChecker();
                truthiness.setNextState(new WesternWalkableDirectionUpdater());
                setConditionChecker(truthiness);
                
//            }            
            
            
        }
    }
    
    private float[] expand(float[] loc, Raster2D raster) {
        ArrayList<SlopeDataCell> visibleCells2 = raster.getVisibleCells((int) loc[0], (int) loc[1], 10);
        raster.getEasternCutoutCells(visibleCells2, (int) loc[0], (int) loc[1], 8);
        visibleCells2 = raster.getSlopeLessThan1D(visibleCells2, minSlope);
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells2);
        return acceleration;
    }
}
