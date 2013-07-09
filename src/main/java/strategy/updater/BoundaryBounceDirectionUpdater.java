/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class BoundaryBounceDirectionUpdater extends SkelatalDirectionUpdater {
    
    private static final Logger log = Logger.getLogger(BoundaryBounceDirectionUpdater.class.getName());

    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        BorderChecker bc = new BorderChecker();

        float[] loc = ownerAgent.getLocation();

        if (Math.abs(loc[0] - bc.getWestEnd()) < bc.getThreshold()) {

            // punch it east
            dxDy[0] += ownerAgent.getSpeed();
            log.info("punching east");

        } else if (Math.abs(loc[0] - bc.getEastEnd()) < bc.getThreshold()) {

            // puch it west
            dxDy[0] -= ownerAgent.getSpeed();
            log.info("punching west");

        }

        if (Math.abs(loc[1] - bc.getSouthEnd()) < bc.getThreshold()) {

            // punch it north
            dxDy[1] -= ownerAgent.getSpeed();
            log.info("punching north");

        } else if (Math.abs(loc[1] - bc.getNorthEnd()) < bc.getThreshold()) {

            // punch it south
            dxDy[1] += ownerAgent.getSpeed();
            log.info("punching south");

        }



    }

    private class BorderChecker {

        private Raster2D raster = null;
        private int eastEnd;
        private int westEnd;
        private int northEnd;
        private int southEnd;
        private int threshold;

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

        public BorderChecker() {
            raster = RasterLoader.get(RasterConfig.BIG).getData();
            eastEnd = raster.countColumns() - 1;
            westEnd = 0;
            northEnd = 0;
            southEnd = raster.countRows() - 1;
            threshold = 30;
        }
    }
}
