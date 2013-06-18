/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster;

import raster.domain.GridCell;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import statsutils.StatsAverager;
import webdomain.ClimateResult;

/**
 *
 * @author Johnny
 */
public class AsciiRasterIntersector {
    
    private static final Logger log = Logger.getLogger(AsciiRasterIntersector.class.getName());
    
//    public static void handleUserGeometry(Geometry userGeometry){
//        Set<GridCell> cells = getGridCells(null);
//        StatsAverager averager = doIntersection(cells,userGeometry);
//        
//    }
    
    // might be good to do this at startup time
    public static Set<GridCell> getGridCells(String cruPath) {
        CollectGeometryAsciiAction caa = new CollectGeometryAsciiAction();
        try {
            long t0 = Calendar.getInstance().getTimeInMillis();
            new AsciiParser(caa).parseAsciiFile(new FileInputStream(new File(cruPath)));
            long t1 = Calendar.getInstance().getTimeInMillis();
            log.log(Level.INFO, "collecting cells took :  {0} seconds", (t1 - t0) / 1000.0);
            log.log(Level.INFO, "have {0}", caa.getSize());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return caa.getGridCells();
    }
    
    public static Set<Point> getIntersectingCellCentroids(Set<GridCell> cells, Geometry userGeometry){
        Set<Point> centroids = new HashSet<Point>();
        for(GridCell cell : cells){
            if(userGeometry.intersects(cell.getPolygon())){
                centroids.add(cell.getPolygon().getCentroid());
            }
        }
        
        return centroids;
    }
    
    //  return a model with everything needed to create output  files. 
    //      1) grid cell geoms
    //      2) geom wide average, max, min, 10, 50, 90, iqr 
    public static ClimateResult doIntersection(Set<GridCell> cells, Geometry userGeometry){
        StatsAverager statsAverager = new StatsAverager();
        Set<Point> centroids = new HashSet<Point>();
        for(GridCell cell : cells){
            if(userGeometry.intersects(cell.getPolygon())){
//                System.out.println("cell value " + cell.getValue());
                statsAverager.update(cell.getValue());
                centroids.add(cell.getPolygon().getCentroid());
            }
        }
        
        // build climate result
        ClimateResult climateResult = new ClimateResult();
        climateResult.setPoints(centroids);
        climateResult.setMax(statsAverager.getMax());
        climateResult.setMin(statsAverager.getMin());
        climateResult.setMin(statsAverager.getAvg());
        
        return climateResult;
    }
    
}
