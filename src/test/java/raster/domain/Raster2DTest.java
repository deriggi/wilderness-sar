///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package raster.domain;
//
//import java.util.ArrayList;
//import middletier.RasterConfig;
//import middletier.RasterLoader;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
///**
// *
// * @author Johnny
// */
//public class Raster2DTest {
//
//    private RasterLoader loader = null;
//
//    public Raster2DTest() {
//        this.loader = RasterLoader.get(RasterConfig.SMALL);
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Test
//    public void testGetMaxX() {
//        Raster2D raster = this.loader.getData();
//        double lon = raster.getMaxX();
//        double lat = raster.getMaxY();
//
//        int[] position = raster.getPosition(lon, lat);
//        System.out.println("start " + position[0] + " " + position[1]);
//
//        double[] lonlat = raster.getLonLat(position[0], position[1]);
//        int[] derivedPosition = raster.getPosition(lonlat[0], lonlat[1]);
//        System.out.println("derived " + derivedPosition[0] + " " + derivedPosition[1]);
//
//        assertTrue(position[0] == derivedPosition[0] && (position[1] == derivedPosition[1]));
//    }
//
//    @Test
//    public void testGetCellSize() {
//    }
//
//    @Test
//    public void testGetData() {
//    }
//
//    @Test
//    public void testGetMaxData() {
//    }
//
//    @Test
//    public void testGetCell() {
//    }
//
//    @Test
//    public void testPrintNeighbourhood() {
//    }
//
//    @Test
//    public void testGetHighestFlattest() {
//    }
//
////    @Test
////    public void testGetSlopeDataNeighborhood() {
////        Raster2D raster = this.loader.getData();
////        double lon = raster.getMaxX();
////        double lat = raster.getMaxY();
////        int[]max = raster.getPosition(lon, lat);
////        
////        ArrayList<ArrayList<SlopeDataCell>> nayb = raster.getSlopeDataNeighborhood(max[0], max[1]);
////        for(ArrayList<SlopeDataCell> row: nayb){
////            for(SlopeDataCell cell : row){
////                double[] lonlat = raster.getLonLat(cell.getColumn(), cell.getRow());
////                System.out.println(lonlat[0] + " " + lonlat[1] + " " + cell.getColumn() + " " + cell.getRow() + " "  +  cell.getData() + " " + cell.getSlope());
////            }
////        }
////        System.out.println();
////        System.out.println("============================");
////        System.out.println();
////        
////        ArrayList<SlopeDataCell> highestFlattest = raster.getHighestFlattest(nayb);
////        for(SlopeDataCell slell: highestFlattest){
////            System.out.println(slell.getRow() + " " +slell.getColumn() + " " +  slell.getData() + " " + slell.getSlope());
////        }
////        
////        
////    }
//    @Test
//    public void testGetBBoxLatLon() {
//
//        Raster2D raster = this.loader.getData();
//        double lon = raster.getMaxX();
//        double lat = raster.getMaxY();
//        int[] max = raster.getPosition(lon, lat);
//
//        ArrayList<ArrayList<SlopeDataCell>> nayb = raster.getSlopeDataNeighborhood(max[0], max[1], 2);
//        double[][] bbox = raster.getBBoxLatLon(nayb);
//        System.out.println("bounding box: ");
//        System.out.println();
//        
//        for(double[] coords: bbox){
//            System.out.println(coords[0] + " " + coords[1]);
//        }
//        
//        double[] nwlonlat = raster.getLonLat(nayb.get(0).get(0).getColumn(), nayb.get(0).get(0).getRow());
//        assert(nwlonlat[0] == bbox[0][0]);
//        System.out.println("comparing " + (nwlonlat[1]+raster.getCellSize())+  " " + bbox[0][1]);
//        
//        assert(nwlonlat[1]+raster.getCellSize() == bbox[0][1]);
//                
//        System.out.println();
//        
//    }
//
//    @Test
//    public void testGetSlopeDataNeighborhoodRange() {
//        Raster2D raster = this.loader.getData();
//        double lon = raster.getMaxX();
//        double lat = raster.getMaxY();
//        int[] max = raster.getPosition(lon, lat);
//
//        System.out.println();
//        System.out.println("the hood:");
//        System.out.println("============================");
//        System.out.println();
//        ArrayList<ArrayList<SlopeDataCell>> nayb = raster.getSlopeDataNeighborhood(max[0], max[1], 2);
//        for (ArrayList<SlopeDataCell> row : nayb) {
//            for (SlopeDataCell cell : row) {
//                double[] lonlat = raster.getLonLat(cell.getColumn(), cell.getRow());
//                System.out.println(lonlat[0] + " " + lonlat[1] + " " + cell.getColumn() + " " + cell.getRow() + " " + cell.getData() + " " + cell.getSlope());
//            }
//        }
//        System.out.println();
//        System.out.println("high flats:");
//        System.out.println("============================");
//        System.out.println();
//
//        ArrayList<SlopeDataCell> highestFlattest = raster.getHighestFlattest(nayb);
//        for (SlopeDataCell slell : highestFlattest) {
//            System.out.println(slell.getRow() + " " + slell.getColumn() + " " + slell.getData() + " " + slell.getSlope());
//        }
//
//
//    }
//
//    @Test
//    public void testGetLonLat() {
//    }
//
//    @Test
//    public void testGetPosition() {
//    }
//
//    @Test
//    public void testCalculateSlope() {
//    }
//}
