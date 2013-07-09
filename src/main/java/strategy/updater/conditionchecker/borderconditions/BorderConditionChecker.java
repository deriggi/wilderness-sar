/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.conditionchecker.borderconditions;

import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import strategy.updater.conditionchecker.SkelatalUpdaterConditionChecker;

/**
 *
 * @author Johnny
 */
public abstract class BorderConditionChecker extends SkelatalUpdaterConditionChecker {

    private Raster2D raster = null;
    private int eastEnd ;
    private int westEnd ;
    private int northEnd ;
    private int southEnd ;
    private int threshold ;

    public int getEastEnd() {
        return eastEnd;
    }

    public int getNorthEnd() {
        return northEnd;
    }

    public int getSouthEnd() {
        return southEnd;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getWestEnd() {
        return westEnd;
    }

    public BorderConditionChecker() {
        raster = RasterLoader.get(RasterConfig.BIG).getData();
        eastEnd = raster.countColumns() - 1;
        westEnd = 0;
        northEnd = 0;
        southEnd = raster.countRows() - 1;
        threshold = 30;
    }
}
