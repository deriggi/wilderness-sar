/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.message;

/**
 *
 * @author Johnny
 */
public interface UpdaterMessage {

    public String getStage();

    public void setStage(String stage);

    public String getFrom();

    public void setFrom(String from);
}
