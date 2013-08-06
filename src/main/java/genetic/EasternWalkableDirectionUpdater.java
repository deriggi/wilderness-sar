/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package genetic;

import java.util.ArrayList;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.SkelatalAgent;
import raster.domain.agent.VectorAgent;
import statsutils.GameUtils;
import strategy.updater.SkelatalDirectionUpdater;

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
    
    // 16 for a moderately healty walker
    private int visrange = 10;
    private float chanceHappening = 0.5f;
    
    public EasternWalkableDirectionUpdater(int visrange, float chanceOfHappening){
        this.visrange = visrange;
        this.chanceHappening = chanceOfHappening;
    }
    
    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        if(!GameUtils.percentChanceTrue(chanceHappening)){
            return;
        }
        
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], visrange);

//        raster.getEasternCutoutCells(visibleCells, (int)loc[0], (int)loc[1], 8);
        raster.getEasternCells(visibleCells, (int) loc[0], (int) loc[1]);
        visibleCells = raster.getSlopeLessThan1D(visibleCells, VectorAgent.WALKABLE_SLOPE);
        
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);
        
        dxDy[0] = acceleration[0];
        dxDy[1] = acceleration[1];
        
        
        
    }
    
}
