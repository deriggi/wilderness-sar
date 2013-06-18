/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package directionupdate;

import strategy.DirectionUpdater;

/**
 *
 * @author Johnny
 */
public class Switch {
    private DirectionUpdater old;
    private DirectionUpdater newOne;

    public DirectionUpdater getNewOne() {
        return newOne;
    }

    public void setNewOne(DirectionUpdater newOne) {
        this.newOne = newOne;
    }

    public DirectionUpdater getOld() {
        return old;
    }

    public void setOld(DirectionUpdater old) {
        this.old = old;
    }
    
    public Switch (DirectionUpdater old, DirectionUpdater newOne){
        this.old = old;
        this.newOne= newOne;
    }
    

}
