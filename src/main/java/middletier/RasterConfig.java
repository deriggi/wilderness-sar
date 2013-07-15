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
//    BIG("C:\\gis data\\terrain\\grdn41w117_13\\grdn41w117_13.txt", -116.510f, 40.50f),
    BIG("C:\\gis data\\terrain\\grdn41w117_13\\grdn41w117_13.txt", -116.510f, 40.50f, 40.25f),
    SMALL("C:\\gis data\\terrain\\grdn41w117_13\\grdn41w117_13.txt", -116.510f, 40.1f,40.0f );
    
    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getPath() {
        return path;
    }
    
    public float getLowerLat() {
        return lowerLat;
    }

    //"C:\\gis data\\terrain\\grdn41w117_13.txt", -116.510f, 40.50f
    
    private String path; 
    private float lon;
    private float lat;
    private float lowerLat;

    
    RasterConfig(String path, float lon, float lat, float lowerLat){
        this.path = path;
        this.lon = lon;
        this.lat = lat;
        this.lowerLat = lowerLat;
    }
    
    
    
}
