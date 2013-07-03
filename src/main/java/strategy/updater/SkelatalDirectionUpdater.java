/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import strategy.updater.notificationhandler.UpdaterNotificationHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.agent.VectorAgent;
import strategy.DirectionUpdater;
import strategy.updater.conditionchecker.UpdaterConditionChecker;
import strategy.updater.message.FinishMessage;
import strategy.updater.message.StartMessage;
import strategy.updater.message.UpdaterMessage;
import strategy.updater.observer.DirectionUpdaterObserver;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalDirectionUpdater implements DirectionUpdater {

    private static Logger log = Logger.getLogger(SkelatalDirectionUpdater.class.getName());
    private DirectionUpdater nextUpdater = null;
    private UpdaterConditionChecker conditionChecker = null;
    private List<UpdaterNotificationHandler> updaterNotificationHandlers = new ArrayList<UpdaterNotificationHandler>();

    @Override
    public void addUpdaterNotificationHandler(UpdaterNotificationHandler handler) {
        updaterNotificationHandlers.add(handler);
    }

    @Override
    public UpdaterConditionChecker getConditionChecker() {
        return conditionChecker;
    }

    @Override
    public void setConditionChecker(UpdaterConditionChecker conditionChecker) {
        this.conditionChecker = conditionChecker;
    }
    private List<DirectionUpdaterObserver> observers = null;
    private List<DirectionUpdater> updaterObservers = null;
    private boolean started = false;
    private boolean isDisabled = false;

    public void addUpdaterListener(DirectionUpdater updater) {
        if (updaterObservers == null) {
            updaterObservers = new ArrayList<DirectionUpdater>();
        }
        updaterObservers.add(updater);

    }

    @Override
    public void setDisabled(boolean b) {
        if (b == false) {
            log.info("someone is setting to enabled");
        }
        isDisabled = b;
    }

    @Override
    public boolean isDisabled() {
        return isDisabled;
    }

    @Override
    public void updateDirectionCaller(double velocity[], VectorAgent agent) {
        if (!started) {
            notifyStartObservers();
            started = true;
        }
        updateDirection(velocity, agent);

    }

    public void notifyStartObservers() {
        StartMessage start = new StartMessage();
        start.setFrom(this.toString());

        if (updaterObservers != null) {
            for (DirectionUpdater updater : updaterObservers) {
                updater.notifyMe(start);
            }
        }
    }

    private void notifyExitObservers(VectorAgent ownerAgent) {
        if (observers != null) {
            for (DirectionUpdaterObserver obs : observers) {
                obs.notifyOfStateChange(ownerAgent);
            }
        }

        FinishMessage finish = new FinishMessage();
        finish.setFrom(this.toString());

        if (updaterObservers != null) {
            for (DirectionUpdater updater : updaterObservers) {
                updater.notifyMe(finish);
            }
        }
    }

    public void addExitObserver(DirectionUpdaterObserver observer) {
        if (observers == null) {
            observers = new ArrayList<DirectionUpdaterObserver>();
        }
        observers.add(observer);
    }

    @Override
    public DirectionUpdater getNextState() {
        DirectionUpdater nexty = this.nextUpdater;
        this.nextUpdater = null;

        return nexty;
    }

    /**
     * Checks if the condition is met then switches to the next tactic
     * @param ownerAgent 
     */
    @Override
    public boolean switchIfConditionMet(VectorAgent ownerAgent) {
        boolean isConditionMet = false;
        if (getConditionChecker() != null && this.conditionChecker.checkCondition(ownerAgent)) {
            this.nextUpdater = getConditionChecker().getNextState();
            isConditionMet = true;
            notifyExitObservers(ownerAgent);
            started = false;
        }
        return isConditionMet;
    }

    @Override
    public void notifyMe(UpdaterMessage message) {

        for (UpdaterNotificationHandler handler : updaterNotificationHandlers) {
            handler.handleNotification(this, message);
        }

    }

    //======
    // north
    // ======
    public int getNorthVisibleCount(Raster2D raster, float[] loc, int visibilityRadius, float minSlope) {
        int northCount = getNorthVisibleCells(raster, loc, visibilityRadius, minSlope).size();
        log.log(Level.INFO, "north count is {0}", new Object[]{northCount});

        return northCount;
    }

    public ArrayList<SlopeDataCell> getNorthVisibleCells(Raster2D raster, float[] loc, int visibilityRadius, float minSlope) {
        return raster.getSlopeLessThan1D(raster.getNorthernCells(raster.getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius), (int) loc[0], (int) loc[1]), minSlope);
    }

    //=====
    // south
    //======
    public int getSouthVisibleCount(Raster2D raster, float[] loc, int visibilityRadius, float minSlope) {
        int southCount = getSouthVisibleCells(raster, loc, visibilityRadius, minSlope).size();
        log.log(Level.INFO, "south count is {0} ", new Object[]{southCount});
        return southCount;
    }

    public ArrayList<SlopeDataCell> getSouthVisibleCells(Raster2D raster, float[] loc, int visibilityRadius, float minSlope) {
        return raster.getSlopeLessThan1D(raster.getSouthernCells(raster.getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius), (int) loc[0], (int) loc[1]), minSlope);
    }

    // =======
    // west
    // =======
    public int getWestVisibleCount(Raster2D raster, float[] loc, int visibilityRadius, float minSlope) {
        int westCount = getWestVisibleCells(raster, loc, visibilityRadius, minSlope).size();
        log.log(Level.INFO, "east count is {0} ", new Object[]{westCount});
        return westCount;
    }

    public ArrayList<SlopeDataCell> getWestVisibleCells(Raster2D raster, float[] loc, int visibilityRadius, float minSlope) {
        return raster.getSlopeLessThan1D(raster.getWesternCells(raster.getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius), (int) loc[0], (int) loc[1]), minSlope);
    }

    //=======
    // east
    //======
    public int getEastVisibleCount(Raster2D raster, float[] loc, int visibilityRadius, float minSlope) {
        int eastCount = getEastVisibleCells(raster, loc, visibilityRadius, minSlope).size();
        log.log(Level.INFO, "east count is {0} ", new Object[]{eastCount});
        return eastCount;
    }

    public ArrayList<SlopeDataCell> getEastVisibleCells(Raster2D raster, float[] loc, int visibilityRadius, float minSlope) {
        return raster.getSlopeLessThan1D(raster.getEasternCells(raster.getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius), (int) loc[0], (int) loc[1]), minSlope);
    }
}
