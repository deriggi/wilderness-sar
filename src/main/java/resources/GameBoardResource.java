/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import middletier.RasterConfig;
import middletier.RasterLoader;
import middletier.TempCache;
import raster.domain.ForceCalculator;
import raster.domain.Raster2D;
import raster.domain.SlopeDataCell;
import raster.domain.VectorHolder;
import util.GsonGetter;
import webdomain.BoardFeatures;
import webdomain.Neighborhood;
import webdomain.RadialSearchResult;

/**
 *
 * @author Johnny
 */
@Path("/board")
public class GameBoardResource {
//    @Context ServletContext context;

    private static final Logger log = Logger.getLogger(GameBoardResource.class.getCanonicalName());

    @GET
    @Path("/viewshed/{lon: \\-?[0-9]{1,3}\\.[0-9]+}/{lat: \\-?[0-9]{1,3}\\.[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String viewshed(@PathParam("lon") double lon, @PathParam("lat") double lat) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        int[] centerPoint = raster.getPosition(lon, lat);
        int radius = 40;

//        ArrayList<SlopeDataCell> cells = raster.calculateViewShed(centerPoint[0], centerPoint[1], radius);
        

//        int cellIndex = 0;
//        for (SlopeDataCell cell : cells) {
//            pois[cellIndex] = raster.getLonLat(cell.getColumn(), cell.getRow());
//            cellIndex++;
//        }
//
//        ArrayList<ArrayList<SlopeDataCell>> smallbox = raster.getSlopeDataNeighborhood(centerPoint[0], centerPoint[1], radius);
//
//        double[][] bbox = raster.getBBoxLatLon(smallbox);
//
//        Neighborhood nayb = new Neighborhood(bbox,pois, null);
        log.info("viewsed request");
        double[][][] pois = raster.calculateViewShed(centerPoint[0], centerPoint[1], radius);
        log.info("viewshed response");
        return GsonGetter.get().toJson(pois);

    }

    @GET
    @Path("/radialsearch/{lon: \\-?[0-9]{1,3}\\.[0-9]+}/{lat: \\-?[0-9]{1,3}\\.[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String radialSearch(@PathParam("lon") double lon, @PathParam("lat") double lat) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        int[] centerPoint = raster.getPosition(lon, lat);
        int[][] perimeterPoints = raster.getPerimiterCoordinates(centerPoint[0], centerPoint[1], 6);
        ForceCalculator fc = new ForceCalculator();
        Float lastDataChange = null;
        RadialSearchResult radialSearchResult = new RadialSearchResult();
        double[] winnerPerimeterPoint = new double[2];
        float differences = 0.0f;
        double[][] theLine = new double[2][2];

        // go through each line, then figure out which of 
        for (int[] perimeterPoint : perimeterPoints) {
            // int[] perimeterPoint = perimeterPoints[0];

            theLine[0][0] = (double) centerPoint[0];
            theLine[0][1] = (double) centerPoint[1];

            theLine[1][0] = (double) perimeterPoint[0];
            theLine[1][1] = (double) perimeterPoint[1];

            float originCellValue = raster.getCell(centerPoint[0], centerPoint[1]);
            differences = 0;
            int k = 0;
            while (k < 6) {
                double[] offSet = fc.getOffset(theLine, k + 1);
                // centerPoint[0] + offSet[0];
                float celly = raster.getCell((int) Math.floor(centerPoint[0] + offSet[0]), (int) Math.floor(centerPoint[1] + offSet[1]));
                differences += (celly - originCellValue);
                k++;
            }
            // done going through the perimeter points so find out which has
            // minimum elevation change
            if (lastDataChange == null) {
                lastDataChange = Math.abs(differences);
                winnerPerimeterPoint[0] = theLine[1][0];
                winnerPerimeterPoint[1] = theLine[1][1];
            } else if (Math.abs(differences) < lastDataChange) {
                // found a new minimum!
                lastDataChange = Math.abs(differences);
                winnerPerimeterPoint[0] = theLine[1][0];
                winnerPerimeterPoint[1] = theLine[1][1];
            }

        }


        System.out.println("the differences in the path " + differences);
        System.out.println("current minimum is " + lastDataChange);

        ArrayList<ArrayList<SlopeDataCell>> smallbox = raster.getSlopeDataNeighborhood(centerPoint[0], centerPoint[1], 6);

        double[][] bbox = raster.getBBoxLatLon(smallbox);
        double[][] lonLatSearchLine = new double[2][2];


        lonLatSearchLine[0][0] = lon;
        lonLatSearchLine[0][1] = lat;

        lonLatSearchLine[1][0] = lon + (winnerPerimeterPoint[0] - centerPoint[0]) * raster.getCellSize();
        lonLatSearchLine[1][1] = lat - (winnerPerimeterPoint[1] - centerPoint[1]) * raster.getCellSize();

//        RadialSearchResult radialSearch = new RadialSearchResult(bbox, null, lonLatSearchLine);
        Neighborhood radialSearch = new Neighborhood(bbox, null, lonLatSearchLine);


        return GsonGetter.get().toJson(radialSearch);
//        }
    }

    /**
     * Not sure about the long term logistics of this one. 
     * The idea was to determine a direction of a type of terrain but I don't
     * the method for determining the direction wasnt so great.
     * @param lon
     * @param lat
     * @param direction
     * @return 
     */
    @GET
    @Path("/testfor/{lon: \\-?[0-9]{1,3}\\.[0-9]+}/{lat: \\-?[0-9]{1,3}\\.[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String testForShit(@PathParam("lon") double lon, @PathParam("lat") double lat, @PathParam("dir") String direction) {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        int[] max = raster.getPosition(lon, lat);

        ArrayList<ArrayList<SlopeDataCell>> nayb = raster.getSlopeDataNeighborhood(max[0], max[1], 6);
//        ArrayList<ArrayList<SlopeDataCell>> east = raster.getEasternNeighborhoodPortion(nayb, .6f);

        double[][] bbox = raster.getBBoxLatLon(nayb);


        ArrayList<SlopeDataCell> pointsOfInterest = raster.getFlats(nayb);
        ForceCalculator fc = new ForceCalculator();
        VectorHolder holder = null;
//        double[][][] severalLines = new double[4][2][2];
        double[][] mostVector = new double[2][2];
        double[][] columnRowVectorResult = new double[2][2];
        double maxDistance = 0;

        pointsOfInterest = raster.mask(nayb, pointsOfInterest, 0);
        holder = fc.calculateAttractiveForceVector(max[0], max[1], nayb, pointsOfInterest, 6);
        if (holder.calcColumnVectorLength() > maxDistance) {
            maxDistance = holder.calcColumnVectorLength();
            mostVector = holder.getLonLatVector();
            columnRowVectorResult = holder.getColumnRowVector();
        }

        pointsOfInterest = raster.mask(nayb, pointsOfInterest, 1);
        holder = fc.calculateAttractiveForceVector(max[0], max[1], nayb, pointsOfInterest, 6);
        if (holder.calcColumnVectorLength() > maxDistance) {
            maxDistance = holder.calcColumnVectorLength();
            mostVector = holder.getLonLatVector();
            columnRowVectorResult = holder.getColumnRowVector();
        }

        pointsOfInterest = raster.mask(nayb, pointsOfInterest, 2);
        holder = fc.calculateAttractiveForceVector(max[0], max[1], nayb, pointsOfInterest, 6);
        if (holder.calcColumnVectorLength() > maxDistance) {
            maxDistance = holder.calcColumnVectorLength();
            mostVector = holder.getLonLatVector();
            columnRowVectorResult = holder.getColumnRowVector();
        }

        pointsOfInterest = raster.mask(nayb, pointsOfInterest, 3);
        holder = fc.calculateAttractiveForceVector(max[0], max[1], nayb, pointsOfInterest, 6);
        if (holder.calcColumnVectorLength() > maxDistance) {
            maxDistance = holder.calcColumnVectorLength();
            mostVector = holder.getLonLatVector();
            columnRowVectorResult = holder.getColumnRowVector();
        }
        double[][] pois = new double[pointsOfInterest.size()][2];
        for (int i = 0; i < pointsOfInterest.size(); i++) {
            pois[i] = raster.getLonLat(pointsOfInterest.get(i).getColumn(), pointsOfInterest.get(i).getRow());
        }
        double[] nextMove = fc.getOffset(columnRowVectorResult, 4);
        double[] lonLatNextMove = new double[2];

        lonLatNextMove[0] = mostVector[0][0] + nextMove[0] * raster.getCellSize();
        lonLatNextMove[1] = mostVector[0][1] - nextMove[1] * raster.getCellSize();

        double[][] lastVector = TempCache.get().getLastVector();
        if (lastVector != null) {
            double dotProduct = fc.dotProduct(columnRowVectorResult, lastVector);
            System.out.println("============");
            System.out.println("our most walkable path dp with last path");
            System.out.println(dotProduct);
            System.out.println();
        }

        Neighborhood webnayb = new Neighborhood(bbox, pois, mostVector);
        webnayb.setNextMove(lonLatNextMove);
        return GsonGetter.get().toJson(webnayb);

    }

    @GET
    @Path("/agentrun/{lon: \\-?[0-9]{1,3}\\.[0-9]+}/{lat: \\-?[0-9]{1,3}\\.[0-9]+}/{dir}")
    @Produces(MediaType.APPLICATION_JSON)
    public String runAgents(@PathParam("lon") double lon, @PathParam("lat") double lat, @PathParam("dir") String direction) {
        int runs = 80;
        int i = 0;

        ArrayList<Neighborhood> hoods = new ArrayList<Neighborhood>();
        while (i++ < runs) {
            Neighborhood hood = iterateOnce(lon, lat, direction);
            // we flip them so we have to chnge up
            lon = hood.getNextMove()[1];
            lat = hood.getNextMove()[0];
            
            hood.setRidgePoints(null);
            hood.setBbox(null);
            hoods.add(hood);
        }
        return GsonGetter.get().toJson(hoods);

    }

    @GET
    @Path("/highflats/{lon: \\-?[0-9]{1,3}\\.[0-9]+}/{lat: \\-?[0-9]{1,3}\\.[0-9]+}/{dir}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getHighFlats(@PathParam("lon") double lon, @PathParam("lat") double lat, @PathParam("dir") String direction) {

        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        int[] max = raster.getPosition(lon, lat);

        ArrayList<ArrayList<SlopeDataCell>> nayb = raster.getSlopeDataNeighborhood(max[0], max[1], 6);
//        ArrayList<ArrayList<SlopeDataCell>> east = raster.getEasternNeighborhoodPortion(nayb, .6f);
        ArrayList<SlopeDataCell> pointsOfInterest = null;
        double[][] bbox = raster.getBBoxLatLon(nayb);

        if (direction.equals("highflat")) {
            pointsOfInterest = raster.getHighestFlattest(nayb);
        } else if (direction.equals("underflat")) {
            pointsOfInterest = raster.getCellsLessThan(nayb, raster.getCell(max[0], max[1]), 0.20f);
        } else if (direction.equals("overflat")) {
            pointsOfInterest = raster.getCellsMoreThan(nayb, raster.getCell(max[0], max[1]), 0.20f);
        } else if (direction.equals("range")) {
            pointsOfInterest = raster.getCellsWithinRange(nayb, raster.getCell(max[0], max[1]), 0.15f);
        } else if (direction.equals("flat")) {
            pointsOfInterest = raster.getFlats(nayb);
        } else if (direction.equals("high")) {
            pointsOfInterest = raster.getHighs(nayb);
        }

        ForceCalculator fc = new ForceCalculator();
        VectorHolder holder = fc.calculateAttractiveForceVector(max[0], max[1], nayb, pointsOfInterest, 6);

        double[][] forceVector = holder.getLonLatVector();
        double[][] lastVector = TempCache.get().getLastVector();
        if (lastVector != null) {
            fc.dotProduct(holder.getColumnRowVector(), lastVector);
        }
        TempCache.get().setLastVector(holder.getColumnRowVector());

        double[][] ridge = new double[pointsOfInterest.size()][2];
        for (int i = 0; i < pointsOfInterest.size(); i++) {
            ridge[i] = raster.getLonLat(pointsOfInterest.get(i).getColumn(), pointsOfInterest.get(i).getRow());
        }

        double[] nextMove = fc.getOffset(holder.getColumnRowVector(), 4);
        double[] lonLatNextMove = new double[2];

        lonLatNextMove[0] = forceVector[0][0] + nextMove[0] * raster.getCellSize();
        lonLatNextMove[1] = forceVector[0][1] - nextMove[1] * raster.getCellSize();

//        nextMove =  raster.getCellSize()*(max[0] + nextMove[0]) , raster.getCellSize()*(max[1] + nextMove[1]));

        Neighborhood hood = new Neighborhood(bbox, ridge, forceVector);
        hood.setNextMove(lonLatNextMove);

        return GsonGetter.get().toJson(hood);
    }

    private Neighborhood iterateOnce(double lon, double lat, String direction) {
        System.out.println("lon lat " + lon + " " + lat);
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        int[] max = raster.getPosition(lon, lat);
        System.out.println("column row position " + max[0] + " " + max[1]);

        ArrayList<ArrayList<SlopeDataCell>> nayb = raster.getSlopeDataNeighborhood(max[0], max[1], 6);
//        ArrayList<ArrayList<SlopeDataCell>> east = raster.getEasternNeighborhoodPortion(nayb, .6f);
        ArrayList<SlopeDataCell> pointsOfInterest = null;
        double[][] bbox = raster.getBBoxLatLon(nayb);

        if (direction.equals("highflat")) {
            pointsOfInterest = raster.getHighestFlattest(nayb);
        } else if (direction.equals("underflat")) {
            pointsOfInterest = raster.getCellsLessThan(nayb, raster.getCell(max[0], max[1]), 0.20f);
        } else if (direction.equals("overflat")) {
            pointsOfInterest = raster.getCellsMoreThan(nayb, raster.getCell(max[0], max[1]), 0.20f);
        } else if (direction.equals("range")) {
            pointsOfInterest = raster.getCellsWithinRange(nayb, raster.getCell(max[0], max[1]), 0.15f);
        } else if (direction.equals("flat")) {
            pointsOfInterest = raster.getFlats(nayb);
        } else if (direction.equals("high")) {
            pointsOfInterest = raster.getHighs(nayb);
        }

        ForceCalculator fc = new ForceCalculator();
        VectorHolder holder = fc.calculateAttractiveForceVector(max[0], max[1], nayb, pointsOfInterest, 6);

        double[][] forceVector = holder.getLonLatVector();
        double[][] lastVector = TempCache.get().getLastVector();
        if (lastVector != null) {
            fc.dotProduct(holder.getColumnRowVector(), lastVector);
        }
        TempCache.get().setLastVector(holder.getColumnRowVector());

        double[][] ridge = new double[pointsOfInterest.size()][2];
        for (int i = 0; i < pointsOfInterest.size(); i++) {
            ridge[i] = raster.getLonLat(pointsOfInterest.get(i).getColumn(), pointsOfInterest.get(i).getRow());
        }

        double[] nextMove = fc.getOffset(holder.getColumnRowVector(), 4);
        double[] lonLatNextMove = new double[2];

        lonLatNextMove[0] = forceVector[0][0] + nextMove[0] * raster.getCellSize();
        lonLatNextMove[1] = forceVector[0][1] - nextMove[1] * raster.getCellSize();

//        nextMove =  raster.getCellSize()*(max[0] + nextMove[0]) , raster.getCellSize()*(max[1] + nextMove[1]));

        Neighborhood hood = new Neighborhood(bbox, ridge, forceVector);
        hood.setNextMove(lonLatNextMove);

        return hood;
    }

    @GET
    @Path("/maxhood")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMaxNeighborhood() {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        double lon = raster.getMaxX();
        double lat = raster.getMaxY();
//        int[] max = raster.getPosition(-116.86111111115494, 40.40314814814548);
        int[] max = raster.getPosition(lon, lat);

        ArrayList<ArrayList<SlopeDataCell>> nayb = raster.getSlopeDataNeighborhood(max[0], max[1], 6);
        //ArrayList<ArrayList<SlopeDataCell>> east = raster.getEasternNeighborhoodPortion(nayb, .6f);

        double[][] bbox = raster.getBBoxLatLon(nayb);
        ArrayList<SlopeDataCell> highFlats = raster.getHighestFlattest(nayb);


        double[][] ridge = new double[highFlats.size()][2];
        for (int i = 0; i < highFlats.size(); i++) {
            ridge[i] = raster.getLonLat(highFlats.get(i).getColumn(), highFlats.get(i).getRow());
        }


        Neighborhood hood = new Neighborhood(bbox, ridge, null);

        Gson gson = new Gson();
        return gson.toJson(hood);

    }

    @POST
    //@Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public String getData(@FormParam("pointslist") String points) {

        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();

        double nwlon = raster.getNwX();
        double nwlat = raster.getNwY();

        double selon = raster.getSeX();
        double selat = raster.getSeY();

        double maxlon = raster.getMaxX();
        double maxlat = raster.getMaxY();

        double minlon = raster.getMinX();
        double minlat = raster.getMinY();

        BoardFeatures board = new BoardFeatures();
        board.setMaxlat(maxlat);
        board.setMaxlon(maxlon);

        board.setMinlat(minlat);
        board.setMinlon(minlon);

        board.setNwlat(nwlat);
        board.setNwlon(nwlon);

        board.setSelat(selat);
        board.setSelon(selon);

        Gson gson = new Gson();


        return gson.toJson(board);

    }
}
