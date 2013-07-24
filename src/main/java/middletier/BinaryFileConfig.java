/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import util.WisarPaths;

/**
 *
 * @author Johnny
 */
public enum BinaryFileConfig {
//    myubuntu OUT_PATH("/home/john/javaplay/wisar/terraindata/grdn41w117_13.bte");
     OUT_PATH(WisarPaths.BINARY_TERRAIN); 
//     OUT_PATH("/usr/share/terraindata/grdn41w117_13.bte"); 
    private String outPath= null;
    
    BinaryFileConfig(String path){
        this.outPath = path;
    }
    
   
    public void setPath(String path){
        this.outPath = path;
    }
    public String getPath(){
        return this.outPath;
    }
    
}
