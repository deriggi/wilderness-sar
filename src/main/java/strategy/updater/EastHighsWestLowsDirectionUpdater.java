package strategy.updater;

import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.agent.SkelatalAgent;

public class EastHighsWestLowsDirectionUpdater extends SkelatalDirectionUpdater {

    private final static Logger log = Logger.getLogger(EastHighsWestLowsDirectionUpdater.class.getName());

    @Override
    public String toString() {
        return "EastHighsWestLows";
    }
    private HighGroundDirectionUpdater highGroundUpdater = new HighGroundDirectionUpdater();
    private LowerGroundDirectionUpdater lowGroundUpdater = new LowerGroundDirectionUpdater();
    

    @Override
    public void updateDirection(double[] dxDy, SkelatalAgent ownerAgent) {
        if (isDisabled()) {
            log.info("disabled so not updating direction");
            return;
        }
        log.log(Level.INFO, "enabled");
        if (dxDy == null || dxDy.length != 2) {
            return;
        }

        // if eastbound then go high
        if (dxDy[0] > 0) {

            highGroundUpdater.updateDirection(dxDy, ownerAgent);
            if (dxDy[0] < 0) {
                dxDy[0] *= -1;
            }

        } else {

            lowGroundUpdater.updateDirection(dxDy, ownerAgent);
            if (dxDy[0] > 0) {
                dxDy[0] *= -1;
            }
        }
    }

//    @Override
//    public void notifyMe(UpdaterMessage message) {
//        log.log(Level.INFO, "notified! {0} {1}", new Object[]{message.getStage(), message.getFrom()});
//        
//        if (message.getStage().equals("START") && message.getFrom().equals("South")) {
//            setDisabled(true);
//            log.log(Level.INFO, "set to disabled! {0}", isDisabled());
//        }else if (message.getStage().equals("END") && message.getFrom().equals("South")) {
//            setDisabled(false);
//            log.log(Level.INFO, "set to enabled!");
//        }
//
//
//    }
}
