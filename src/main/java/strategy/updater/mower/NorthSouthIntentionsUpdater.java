/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.mower;

import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import raster.domain.agent.SkelatalAgent;
import strategy.updater.Direction;
import strategy.updater.SkelatalDirectionUpdater;

/**
 *
 * @author Johnny
 */
public class NorthSouthIntentionsUpdater extends SkelatalDirectionUpdater {

    private static final Logger log = Logger.getLogger(NorthSouthIntentionsUpdater.class.getName());

    @Override
    public String toString() {
        return "mowin";
    }

    /**
     *  west edge? go south, east edge? go south, south edge
     * then mow north. Should probably use separate ones for north and south
     * @param dxDy
     * @param ownerAgent 
     */
    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] loc = ownerAgent.getLocation();

        int northEnd = 0;
        int southEnd = raster.countRows() - 1;
        int threshold = 30;
        
        
        if (ownerAgent.getNorthSouthIntention() == null) {
            int toNorth = (int) loc[1];
            int toSouth = (int) Math.abs(loc[1] - southEnd);

            if (toNorth > toSouth) {
                ownerAgent.setNorthSouthIntention(Direction.NORTH);
                log.info("intending to go north ");
            } else {
                ownerAgent.setNorthSouthIntention(Direction.SOUTH);
                log.info("intending to go south");
            }

        }
        if (Math.abs(loc[1] - northEnd) < threshold) {

            log.info("setting direction to south because we are at the north end");
            ownerAgent.setNorthSouthIntention(Direction.SOUTH);

        } else if (Math.abs(loc[1] - southEnd) < threshold) {

            log.info("setting direction to north because we are at the south end");
            ownerAgent.setNorthSouthIntention(Direction.NORTH);

        }

    }
}
