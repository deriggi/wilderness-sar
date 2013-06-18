/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import middletier.RasterConfig;
import middletier.RasterLoader;

/**
 * Web application lifecycle listener.
 * @author Johnny
 */
public class StartupListener implements ServletContextListener {
    private static final Logger log = Logger.getLogger(StartupListener.class.getName());
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.log(Level.INFO, "loading raster data...");
        RasterLoader.get(RasterConfig.BIG);
    }

    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.log(Level.INFO, "shutting down...");
    }
}
