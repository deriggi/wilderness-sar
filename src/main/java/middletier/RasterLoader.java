/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.Raster2D;
import util.TimerUtil;

/**
 *
 * @author Johnny
 */
public class RasterLoader {

    private static final Logger log = Logger.getLogger(RasterLoader.class.getName());
    private static RasterLoader loader = null;
    private Raster2D data = null;

    public Raster2D getData() {
        return data;
    }

    public void setData(Raster2D data) {
        this.data = data;
    }

    private void log(String template, Object data) {

        log.log(Level.INFO, template, data);

    }

    public synchronized static RasterLoader get(RasterConfig config) {
        if (loader == null) {
            loader = new RasterLoader();
            long t0 = new Date().getTime();
            loader.load(config.getPath(), config.getLon(), config.getLat());

            long t1 = new Date().getTime();
            loader.log("load time {0} ", (t1 - t0) / 1000.0f);
            loader.log("rows ,  columns: {0}  ", loader.getData().getData().size() + " , " + loader.getData().getData().get(0).size());
            loader.log("nwx {0} ", loader.getData().getNwX());
            loader.log("nwy {0} ", loader.getData().getNwY());
            loader.log("cellsize {0} ", loader.getData().getCellSize());
        }
        return loader;
    }

    private void load(String path, float upperLonBound, float upperLatBound) {
        FileInputStream fis = null;
        try {
            //========================
//            CollectGeometryAsciiAction ca = new CollectGeometryAsciiAction();
//            ca.setUpperXBound(upperLonBound);
//            ca.setUpperYBound(upperLatBound);
//            fis = null;
//            AsciiParser2 ap = new AsciiParser2(ca);
//            fis = new FileInputStream(path);
//            long t0 = TimerUtil.getTime();
//            ap.parseAsciiFile(fis);
//            long t1 = TimerUtil.getTime();
//            log.log(Level.INFO, " to load from an ascii file {0}" , TimerUtil.seconds(t1, t0));
//            setData(ca.getRaster2D());
            //getData().export(BinaryFileConfig.OUT_PATH.getPath());
            //========================================

            long t0 = TimerUtil.getTime();
            Raster2D raster = Raster2D.importIt(BinaryFileConfig.OUT_PATH.getPath());
            long t1 = TimerUtil.getTime();

            log.log(Level.INFO, "time to load binary terrain {0}", TimerUtil.seconds(t1, t0));
            setData(raster);
            // =======================


            //            log.info(ca.getRaster2D().getData().size());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RasterLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RasterLoader.class.getName()).log(Level.SEVERE, null, ex);

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(RasterLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }


    }

    public static void main(String[] args) {
        RasterLoader.get(RasterConfig.BIG).getData();
    }
}
