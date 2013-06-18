/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

/**
 *
 * @author Johnny
 */
public enum RasterConfig {
    BIG("C:\\gis data\\terrain\\grdn41w117_13\\grdn41w117_13.txt", -116.510f, 40.50f),
    SMALL("C:\\gis data\\terrain\\grdn41w117_13\\grdn41w117_13.txt", -116.0f, 40.1f);
    
    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getPath() {
        return path;
    }

    //"C:\\gis data\\terrain\\grdn41w117_13.txt", -116.510f, 40.50f
    
    private String path; 
    private float lon;
    private float lat;
    RasterConfig(String path, float lon, float lat){
        this.path = path;
        this.lon = lon;
        this.lat = lat;
    }
    
    
    
}
