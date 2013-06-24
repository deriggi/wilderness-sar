/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

/**
 *
 * @author Johnny
 */
public class IdLoc {
    private int id;
    private double[] location;
    private Boolean foundOthers = null;

    public void setFoundOthers(boolean found){
        foundOthers = found;
    }
    public Boolean getFoundOthers(){
        return foundOthers;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }
    
    
}
