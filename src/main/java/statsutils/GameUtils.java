/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statsutils;

/**
 *
 * @author Johnny
 */
public class GameUtils {
    
    
    public static boolean percentChanceTrue(float chance) {
        
        double r = Math.random();
        
        
        if (r < chance) {
            
            return true;
            
        }
        
        return false;
        
    }
    
}
