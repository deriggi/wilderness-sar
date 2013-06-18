/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Date;

/**
 *
 * @author Johnny
 */
public class TimerUtil {
    
    public static long getTime(){
        return new Date().getTime();
    }
    
    public static float seconds(long t1, long t0){
        return (t1 - t0)/1000.0f;
        
    }
    
}
