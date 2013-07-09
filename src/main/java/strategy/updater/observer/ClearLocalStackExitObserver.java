/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.observer;

import java.util.Stack;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class ClearLocalStackExitObserver implements DirectionUpdaterObserver {

    private String key = null;

    public ClearLocalStackExitObserver(String key) {
        this.key = key;
    }

    @Override
    public void notifyOfStateChange(SkelatalAgent va) {
        if (key != null && va.getStackedPosition(key) != null) {
            Stack<short[]> stack = va.getStackedPosition(key);
            stack.clear();
        }
    }
}
