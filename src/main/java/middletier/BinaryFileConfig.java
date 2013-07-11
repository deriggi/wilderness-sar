/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

/**
 *
 * @author Johnny
 */
public enum BinaryFileConfig {
//    myubuntu OUT_PATH("/home/john/javaplay/wisar/terraindata/grdn41w117_13.bte");
     OUT_PATH("C:\\Users\\Johnny\\binaryterrain\\out.bte"); 
//     OUT_PATH("/usr/share/terraindata/grdn41w117_13.bte"); 
    private String outPath= null;
    
    BinaryFileConfig(String path){
        this.outPath = path;
    }
    public String getPath(){
        return this.outPath;
    }
    
}
