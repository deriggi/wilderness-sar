/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.message;

/**
 *
 * @author Johnny
 */
public abstract class SkelatalMessage implements UpdaterMessage {
    
    private String from;
    private String stage;

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    
}
