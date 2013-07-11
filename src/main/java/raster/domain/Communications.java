/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class Communications {
    
    private HashMap<String, ArrayList<HashMap<String,Float>>> messages = new HashMap<String, ArrayList<HashMap<String,Float>>>();
    private HashMap<String, ArrayList<SkelatalAgent>> observers = new HashMap<String, ArrayList<SkelatalAgent>>();
    
    private static final Logger log = Logger.getLogger(Communications.class.getName());
    
    /**
     * Become an observer on a chanel
     * @param key the chanel key
     * @param agent the observer agent
     */
    public void register(String key, SkelatalAgent agent){
        if(!messages.containsKey(key)){
            ArrayList<HashMap<String,Float>> listOfMessages = new ArrayList<HashMap<String,Float>>();
            messages.put(key, listOfMessages);
        }
        
        if(!observers.containsKey(key)){
            observers.put(key, new ArrayList<SkelatalAgent>());
        }
        
        observers.get(key).add(agent);
        
    }
        
    public void relayMessage(String channel, HashMap<String, Float> message){
        if(!observers.containsKey(channel)){
            log.log(Level.WARNING, "trying to relay message on non existing channel: {0} ", channel);
            return;
        }
        
        
        ArrayList<SkelatalAgent> obs = observers.get(channel);
        for(SkelatalAgent observer: obs){
//            observer.handleMessage(message)
        }
        
    }
    
}
