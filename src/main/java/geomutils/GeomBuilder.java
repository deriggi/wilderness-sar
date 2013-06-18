/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geomutils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 *
 * @author Johnny
 */
public class GeomBuilder {
    
    private final static Logger log = Logger.getLogger(GeomBuilder.class.getName());
    
      
    public static Polygon createGridCellFromLowerLeftPoint(double longitude, double latitude, double cellSize) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

        Coordinate[] coords =
                new Coordinate[]{
                new Coordinate(longitude, latitude),
                new Coordinate(longitude, latitude + cellSize),
                new Coordinate(longitude + cellSize, latitude + cellSize),
                new Coordinate(longitude + cellSize, latitude),
                new Coordinate(longitude, latitude)};

        
        LinearRing ring = geometryFactory.createLinearRing(coords);
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        return polygon;
    }
    
    
    public static Polygon createPolygonFromCoordList(List<Point> points) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Coordinate[] coords = new Coordinate[points.size()];
        int i = 0;

        for(Point p : points){
            coords[i++] = new Coordinate(p.getX(), p.getY());
        }
        
        LinearRing ring = geometryFactory.createLinearRing(coords);
        Polygon polygon = geometryFactory.createPolygon(ring, null);
        return polygon;
    }
    
    public static double[][] convertPointStringToArray(String commaSeparated) {
        String[] coordPairs = commaSeparated.split("\\|");
        
        System.out.println(commaSeparated);
        System.out.println("found " + coordPairs.length + " from coordPairs " );
        
        double[][] coordinates = new double[coordPairs.length][2];

        int i = 0;
        for (String s : coordPairs) {
            String[] coord = s.split("\\,");
            if (coord == null || coord.length != 2) {
                log.warning("bad coord in coord string");
            } else {
                try {
                    coordinates[i][0] = Double.parseDouble(coord[0]);
                    coordinates[i][1] = Double.parseDouble(coord[1]);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
            i++;
        }
        return coordinates;
    }

    public static double[][][] geomToLeafletObject(ArrayList<Polygon> geom){
        
        
        System.out.println("=====================");
        System.out.println("after union the number of geometries in this guy are " + geom.size());
        int geomCount = geom.size();
        int i = 0;
        Coordinate[] coords=  null;
        // 3 dimensional. 1) Each geom > 2) has list of coords > 3) each list item is 2 coords
        double[][][] justTheCoords = new double[geomCount][][];
        while (i < geomCount){
            Geometry internalGeom = geom.get(i);
            coords = internalGeom.getCoordinates();
            justTheCoords[i] = new double[coords.length][2];
            for(int j = 0; j < coords.length; j++){
                justTheCoords[i][j][0] = coords[j].x;
                justTheCoords[i][j][1] = coords[j].y;
            }
            i++;
        }
        
        return justTheCoords;
    }
    
    public static List<Point> convertArrayToGeometry(double[][] points){
        List<Point> pointList = new ArrayList<Point>();
        if (points == null){
            return pointList;
        }
        
        for(double[] coord : points){
            Point p = GeomBuilder.createPointFromCoords(coord[0], coord[1]);
            if(p != null){
                pointList.add(p);
            }
            
        }
        
        return pointList;
    }
    
    public static Point createPointFromCoords(double longitude, double latitude) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Point point = geometryFactory.createPoint((new Coordinate(longitude,latitude)));
        
        return point;
    }
    
}
