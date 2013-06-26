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
    private Integer timestep = null;

    public Integer getTimestep() {
        return timestep;
    }

    public void setTimestep(Integer timestep) {
        this.timestep = timestep;
    }

    public String getNameTag() {
        return nameTag;
    }

    public void setNameTag(String nameTag) {
        this.nameTag = nameTag;
    }
    private String nameTag = null;
    

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
