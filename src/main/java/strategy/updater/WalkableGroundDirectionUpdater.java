/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class WalkableGroundDirectionUpdater extends SkelatalDirectionUpdater {

    private static Logger log = Logger.getLogger(WalkableGroundDirectionUpdater.class.getName());

    /**
     * Go towards walkable ground plus momentum
     * @param dxDy
     * @param ownerAgent 
     */
    @Override
    public void updateDirection(double[] dxDy, VectorAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();
        int range = 12;

        ArrayList<ArrayList<SlopeDataCell>> hood = raster.getSlopeDataNeighborhood((int) loc[0], (int) loc[1], range);
        ArrayList<SlopeDataCell> pointsOfInterest = raster.getCellsLessThan(hood, raster.getCell((int)loc[0], (int)loc[1]), 0.20f);
        float max = raster.getCell(loc[0], loc[1]);

        
//        ArrayList<SlopeDataCell> pointsOfInterest = raster.getFlats(hood);
//        ArrayList<SlopeDataCell> pointsOfInterest = raster.getCellsLessThan(hood, max, 0.25f);
//        ArrayList<SlopeDataCell> pointsOfInterest = raster.getSlopeLessThan(hood, 0.30f);
        log.log(Level.INFO, "number of attractive cells {0}", pointsOfInterest.size());
        float[] acceleration = raster.calculateForcesAgainst(new int[]{(int)loc[0], (int)loc[1]}, pointsOfInterest);
//        analyzeHood(oneDCells, loc);
        log.log(Level.INFO, "dx {0}  dy {1} ", new Object[]{acceleration[0], acceleration[1]});
        dxDy[0] += acceleration[0];
        dxDy[1] += acceleration[1];



    }

    private void analyzeHood(List<SlopeDataCell> cells, float[] middle) {
        int northCount = 0;
        int southCount = 0;
        
        int eastCount = 0;
        int westCount = 0;
        
        for (SlopeDataCell cell : cells) {
            if (cell.getRow() > new Float(middle[1]).intValue()) {
                southCount++;
            } else if (cell.getRow() < new Float(middle[1]).intValue()) {
                northCount++;
            }
            
            if(cell.getColumn() > new Float(middle[0]).intValue()){
                eastCount++;
            }else if(cell.getColumn() < new Float(middle[0]).intValue()){
                westCount++;
            }
        }
        
        log.log(Level.INFO, "cells north {0} cells south {1} cells east{2} cells west {3} ", new Object[]{northCount, southCount, eastCount, westCount});


    }
}
