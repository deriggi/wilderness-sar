/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Johnny
 */
public class SimId {
    private static Random r = new Random();
    private static char[] letters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    
    public static void main(String[] args){
        ArrayList<String> list=  new ArrayList<String>();
        int i = 0;
        while (i++ < 10000){
            String id = SimId.getNewSimId();
            
            if(list.contains(id)){
                System.out.println("dupe!");
            }
            
            list.add(id);
            System.out.println(id);
        }
        
        
                
                
    }
    
    
    private static  String getNewSimId(){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while(i++ < 6){
            if(Math.random() < 0.5f){
                
                sb.append(randomDigit());
                
            }else{
                
                sb.append(randomChar());
                
            }
        }
        return sb.toString();
    }
    
    private static int randomDigit(){
        return r.nextInt(9);
    }
    
    private static char randomChar(){
        return letters[r.nextInt(25)];
    }
    
}
