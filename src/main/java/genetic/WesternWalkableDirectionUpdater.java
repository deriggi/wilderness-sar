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
public class WesternWalkableDirectionUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(WesternWalkableDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "w_" + visrange + "_" +chanceHappening;
    }
    
    private int visrange = 10;
    private float chanceHappening = 0.5f;
    private int dnaSequence = 0;
    
    public WesternWalkableDirectionUpdater(int visrange, float chanceHappening, int dnaSequence){
        this.visrange = visrange;
        this.chanceHappening = chanceHappening;
        this.dnaSequence = dnaSequence;
    }
    

    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        if(!GameUtils.percentChanceTrue(chanceHappening)){
            return;
        }
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();

        ArrayList<SlopeDataCell> visibleCells = raster.getVisibleCells((int) loc[0], (int) loc[1], visrange);
//        raster.getWesternCutoutCells(visibleCells, (int)loc[0], (int)loc[1], 8);
        raster.getWesternCells(visibleCells, (int) loc[0], (int) loc[1]);
        visibleCells = raster.getSlopeLessThan1D(visibleCells, VectorAgent.WALKABLE_SLOPE);

        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int) loc[0], (int) loc[1]}, visibleCells);


        // if acc is zero


        dxDy[0] += acceleration[0];
        dxDy[1] += acceleration[1];

//        if (Math.abs(acceleration[0]) == 0 && Math.abs(acceleration[1]) == 0) {
//
//
//                log.log(Level.WARNING, "acceleration {0} {1} ", new Object[]{(int) acceleration[0], (int) acceleration[1]});
//                log.log(Level.WARNING, "attempting switch to east ");
//
//                AlwaysTrueConditionChecker truthiness = new AlwaysTrueConditionChecker();
//                truthiness.setNextState(new EasternWalkableDirectionUpdater());
//                setConditionChecker(truthiness);
//
////            }
//        }

    }

}
