/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.Raster2D;
import strategy.Strategy;
import util.FileExportHelper;

/**
 *
 * @author Johnny
 */
public class VectorAgent extends SkelatalAgent {

    private static final Logger log = Logger.getLogger(VectorAgent.class.getName());
//    private Strategy movementStrategy = null;
    private List<Strategy> strategies = new ArrayList<Strategy>();
    private int strategyIndex = 0;
    // constants, 'hey man you should use enums'
    public static final float WALKABLE_SLOPE = 0.18f;
    public static final int SHORT_VIS_RANGE = 25;
    public static final int LONG_VIS_RANGE = 70;

    public Strategy getMovementStrategy() {
        return strategies.get(strategyIndex);
    }

    public void addMovementStrategy(Strategy movementStrategy) {
        strategies.add(movementStrategy);
    }

    /**
     * Do a smart strategy index update
     */
    private void incrementStrategyIndex() {
        int numberOfStrategies = strategies.size();
        if (strategyIndex + 1 >= numberOfStrategies) {
            strategyIndex = 0;
        } else {
            strategyIndex++;
        }
    }

    public VectorAgent() {
        setVelocityVector(new double[]{0.0, 0.0});

    }

    // rename to move?
    @Override
    public void wander() {

        // this shit is probably going to get shelved
        Strategy currentStrat = strategies.get(strategyIndex);
        currentStrat.calculateNextMove(this);
        updateStepsTaken();

        incrementMasterTimestep();
        // test strategy for condition for switching to next strategy
        if (currentStrat.getIsTimeToSwitch(this)) {
            log.log(Level.INFO, "swithing from {0} ", new Object[]{currentStrat.getName()});

            incrementStrategyIndex();
        }

        // all agents should do this super()?
        addToDotProductBuffer();
        setLastVelocity(getVelocityVector());
        pushLoc();
        
        foundOthers(getDetectionRange(), AgentName.LOST);
        
       
        

    }

    public void writeOut() {
        log.info("writing output file ");
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        double[] geoloc = raster.getLonLat(getLocation()[0], getLocation()[1]);
        FileExportHelper.appendLineToFile("C:/agentout/testout.csv", geoloc[0] + "," + geoloc[1]);
    }
    

    public static void main(String[] args) {
        VectorAgent va = new VectorAgent();
//        System.out.println(va.dotProduct(new double[]{-0.2, -4f}, new double[]{-0.26f, 4f}));

    }
}
