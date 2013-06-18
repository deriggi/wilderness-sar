/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;


import com.google.gson.Gson;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import geomutils.GeomBuilder;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import raster.AsciiRasterIntersector;
import raster.domain.GridCell;
import webdomain.ClimateResult;

/**
 *
 * @author Johnny
 */


@Path("/userdefined")
public class UserDefined {
    @Context ServletContext context;
    private static final Logger log = Logger.getLogger(UserDefined.class.getCanonicalName());
    
    @POST
    //@Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String intersectWithRaster(@FormParam("pointslist") String points){
        System.out.println("received " + points);
       
        double[][] pointsarray  = GeomBuilder.convertPointStringToArray(points);
        List<Point> pointlist = GeomBuilder.convertArrayToGeometry(pointsarray);
        Polygon convertedpolygon = GeomBuilder.createPolygonFromCoordList(pointlist);
        
        
//        String dataPath = "C:/cru/cru_ts_3_10_01.1901.2009.raster_ascii.pre/pre/cru_ts_3_10_01.1901.2009.pre_2009_12.asc";
        
        
        
        String dataPath = "/home/ec2-user/data/cru_ts_3_10.1901.2009.pre_2009_12.asc"; 
        
        Set<GridCell> cells = AsciiRasterIntersector.getGridCells(dataPath);
//        Set<Point> centroids = AsciiRasterIntersector.getIntersectingCellCentroids(cells, convertedpolygon);
        ClimateResult climateResult = AsciiRasterIntersector.doIntersection(cells, convertedpolygon);
        
        String outputDirectory = new File(context.getRealPath("/webappoutput/" + "somename")).getAbsolutePath() + "/";
        StringBuilder sb = new StringBuilder();
        sb.append(outputDirectory);
        sb.append(System.getProperty("line.separator"));
        sb.append("area : " );
        sb.append(convertedpolygon.getArea());
        sb.append(System.getProperty("line.separator"));
        sb.append("roids : ");
        
//        StringBuilder gsonBuilder = new StringBuilder();
        
        Gson gson = new Gson();
        
        log.log(Level.INFO,sb.toString());
        
//        sb.append(cells.size());
        
        
        
//        GeomBuilder.createPointFromCoords(longitude, latitude);
        
        return gson.toJson(climateResult);
        
    }
}
