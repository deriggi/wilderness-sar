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
        if (Math.random() < chance) {
            return true;
        }
        return false;
    }
    
}
