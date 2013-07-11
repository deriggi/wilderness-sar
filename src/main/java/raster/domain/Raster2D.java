/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

import com.vividsolutions.jts.geom.Polygon;
import geomutils.GeomBuilder;
import geomutils.VectorUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.RowPolyMapCount;

/**
 *
 * @author Johnny
 */
public class Raster2D {

    private static final Logger log = Logger.getLogger(Raster2D.class.getName());
    private ArrayList<ArrayList<Float>> data;
    private double cellSize = 0;
    private double nwY = 0;
    private double nwX = 0;
    private double seY = 0;
    private double seX = 0;

    public double getSeX() {
        return seX;
    }

    public void setSeX(double seX) {
        this.seX = seX;
    }

    public double getSeY() {
        return seY;
    }

    public void setSeY(double seY) {
        this.seY = seY;
    }
    private double maxX = 0, maxY = 0, minY = 0, minX = 0;

    public Double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMinX() {
        return this.minX;
    }

    public double getMinY() {
        return this.minY;
    }

    public static void closeBinaryFile(RandomAccessFile raf) {
        if (raf == null) {
            return;
        }
        try {
            raf.close();
        } catch (IOException ex) {
            log.severe("could not close file");
        }

    }

    public void export(String outputPath) {
        RandomAccessFile raf = openBinaryFile(outputPath, "rw");
        writeDouble(raf, getNwX());
        writeDouble(raf, getNwY());
        writeDouble(raf, getCellSize());

        int rows = getData().size();
        log.log(Level.INFO, "writing rows of {0}", rows);

        int columns = getData().get(0).size();
        log.log(Level.INFO, "writing rows of {0}", columns);

        writeInt(raf, columns);
        writeInt(raf, rows);

        ArrayList<ArrayList<Float>> theData = getData();
        for (ArrayList<Float> row : theData) {
            for (Float f : row) {
                writeFloat(raf, f);
            }
        }

        closeBinaryFile(raf);
    }

    private double doubleFromBytes(byte[] raw) {
//        return doubleFromBytes(raw, 0);
        double val = ByteBuffer.wrap(raw).getDouble();
        return val;
    }

    private double doubleFromBytes(byte[] raw, int startIndex) {
        return doubleFromBytes(Arrays.copyOfRange(raw, startIndex, startIndex + 8));

//        int i = startIndex;
//        long asLong =
//                (raw[i + 7] & 0xFF)
//                | ((raw[i + 6] & 0xFF) << 8)
//                | ((raw[i + 5] & 0xFF) << 16)
//                | ((raw[i + 4] & 0xFF) << 24)
//                | ((raw[i + 3] & 0xFF) << 32)
//                | ((raw[i + 2] & 0xFF) << 40)
//                | ((raw[i + 1] & 0xFF) << 48)
//                | ((raw[i] & 0xFF) << 56);
//        return Double.longBitsToDouble(asLong);
    }

    private float floatFromBytes(byte[] raw) {
        return floatFromBytes(raw, 0);
    }

    public int countColumns(){
        return getData().get(0).size();
    }
    public int countRows(){
        return getData().size();
    }
    
    public boolean isInBounds(double[] somePoint) {
        if (somePoint == null || somePoint.length != 2) {
            return false;
        }

        if (somePoint[0] < 0 || somePoint[1] < 0) {
            return false;
        }

        if (somePoint[0] > getData().get(0).size() - 1) {
            return false;
        }

        if (somePoint[1] > getData().size() - 1) {
            return false;
        }

        return true;
    }

    private static float floatFromBytes(byte[] raw, int startIndex) {
        int i = startIndex;
        int asInt = (raw[i + 3] & 0xFF)
                | ((raw[i + 2] & 0xFF) << 8)
                | ((raw[i + 1] & 0xFF) << 16)
                | ((raw[i] & 0xFF) << 24);
        return Float.intBitsToFloat(asInt);
    }

    public float[] calculateForcesAgainst(float[] origin, float[] attractivePoint) {
        float dx = 0, dy = 0;

        dx += attractivePoint[0] - origin[0];
        dy += attractivePoint[1] - origin[1];

        float[] forceVector = new float[]{dx, dy};
        return forceVector;
    }

//    public float[] calculateForcesAgainst(float[] origin, ArrayList<SlopeDataCell> pointsOfInterest) {
//        float dx = 0, dy = 0;
//        for (SlopeDataCell point : pointsOfInterest) {
//            dx += point.getColumn() - origin[0];
//            dy += point.getRow() - origin[1];
//        }
//        float[] forceVector = new float[]{dx, dy};
//        return forceVector;
//    }
    
    public float[] calculateForcesAgainst(int[] origin, ArrayList<SlopeDataCell> pointsOfInterest) {
        float dx = 0, dy = 0;
        for (SlopeDataCell point : pointsOfInterest) {
            dx += point.getColumn() - origin[0];
            dy += point.getRow() - origin[1];
        }
        float[] forceVector = new float[]{dx, dy};
        return forceVector;
    }

    public static Raster2D importIt(String inputPath) throws IOException {
        Raster2D raster = null;

        RandomAccessFile raf = openBinaryFile(inputPath, "r");
        double nwx = raf.readDouble();


        double nwy = raf.readDouble();


        double cellySize = raf.readDouble();


        int columns = raf.readInt();

        int rows = raf.readInt();

        log.log(Level.INFO, "the pointer after reading the header is {0}", raf.getFilePointer());
        ArrayList<ArrayList<Float>> theData = new ArrayList<ArrayList<Float>>();


        byte[] rawInput = new byte[(int) raf.length() - (32)];
        log.log(Level.INFO, "nwx from binary header: {0} ", nwx);
        log.log(Level.INFO, "nwy from binary header: {0} ", nwy);
        log.log(Level.INFO, "celly from binary header: {0} ", cellySize);
        log.log(Level.INFO, "rows from binary header: {0} ", rows);
        log.log(Level.INFO, "columns from binary header: {0} ", columns);
        log.log(Level.INFO, "the length of this thing {0} ", raf.length());
        log.log(Level.INFO, "made an array of size {0} ", (int) raf.length() - (41));

//        raf.skipBytes(40);
        raf.readFully(rawInput);

        // ready to read data
        int allIndex = 0;
        for (int i = 0; i < rows; i++) {
            ArrayList<Float> row = new ArrayList<Float>();
            for (int j = 0; j < columns && allIndex < rawInput.length; j++) {
//                row.add(raf.readFloat());
                row.add(floatFromBytes(rawInput, allIndex));
                allIndex += 4;
            }
            theData.add(row);
        }
        closeBinaryFile(raf);
        raster = new Raster2D(theData, cellySize, nwy, nwx);

        return raster;
    }

    private static RandomAccessFile openBinaryFile(String outputPath, String options) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(outputPath, options);

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        }
        return raf;

    }

    private void writeFloat(RandomAccessFile raf, float someFloat) {
        try {
            raf.writeFloat(someFloat);
        } catch (IOException ex) {
            log.severe("writing float didn't work");
        }
    }

    private void writeDouble(RandomAccessFile raf, double someDouble) {
        try {
            raf.writeDouble(someDouble);
        } catch (IOException ex) {
            log.severe("writing double didn't work");
        }
    }

    private void writeInt(RandomAccessFile raf, int someInt) {
        try {
            raf.writeInt(someInt);
        } catch (IOException ex) {
            log.severe("writing double didn't work");
        }
    }

    public Double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }
    private Float maxData = null;

    public Raster2D(ArrayList<ArrayList<Float>> data, double cellSize, double nwY, double nwX) {
        this.data = data;
        this.nwY = nwY;
        this.nwX = nwX;
        this.cellSize = cellSize;
        calculateSePoint();
        calculateExtremes();
    }

    public double getCellSize() {
        return cellSize;
    }

    public ArrayList<ArrayList<Float>> getData() {
        return data;
    }

    public double getNwX() {
        return nwX;
    }

    public double getNwY() {
        return nwY;
    }

    public Float getMaxData() {
        if (maxData == null) {
            maxData = calculateExtremes();
        }
        return maxData;
    }
    
    public ArrayList<SlopeDataCell>  getVisibleCells(int column, int row, int radius) {
//        return oneDirectionViewshed(column, row, radius);

        float elevationOrigin = getCell(column, row) + 6;

        int[][] perimeterPoints = getPerimiterCoordinates(column, row, radius);
        double[][] theLine = new double[2][2];
        ForceCalculator fc = new ForceCalculator();
        double[] offset = null;
        int distance = 0;
        float elevation = 0;
        ArrayList<SlopeDataCell> visibleCells = new ArrayList<SlopeDataCell>();

        for (int[] perimeterPoint : perimeterPoints) {
            // make line from center to perimeter
            theLine[0][0] = column;
            theLine[0][1] = row;

            theLine[1][0] = perimeterPoint[0];
            theLine[1][1] = perimeterPoint[1];
            distance = 1;
            int maxDistance = (int) (VectorUtils.distance(theLine[0], theLine[1]))+1;
            float biggestSlopeSeen = -999999;
            float slope = 0, dx = 0, dy = 0;
            SlopeDataCell visibleCell = null;

            while (distance < maxDistance) {
                offset = fc.getOffset(theLine, distance);
                elevation = getCell((int) Math.floor(column + offset[0]), (int) Math.floor(row + offset[1]));
                dx = distance * 7;
//                double aSquared = Math.pow(column + offset[1]  - column, 2);
//                double aSquared = Math.pow(column + offset[1]  - column, 2);
//                dx = Math.sqrt(  );
                dy = elevation - elevationOrigin;
                slope = dy / dx;
                if (slope > biggestSlopeSeen) {
                    biggestSlopeSeen = slope;
                    visibleCell = getSlopeDataCell((int) Math.floor(column + offset[0]), (int) Math.floor(row + offset[1]));
                    if (!visibleCells.contains(visibleCell)) {
                        visibleCells.add(visibleCell);
                    }

                }
//                distance += 1.1;
                distance += 1;
            }
        }
        return visibleCells;
    }
    
    public ArrayList<SlopeDataCell> getEasternCells(ArrayList<SlopeDataCell> cells, int column, int row){
        Iterator<SlopeDataCell> iterator = cells.iterator();
        while (iterator.hasNext()){
            SlopeDataCell cell = iterator.next();
            if(cell.getColumn() <= column - 1){
                iterator.remove();
            }
        }
        return cells;
    }
    
    
    public ArrayList<SlopeDataCell> getNorthernCells(ArrayList<SlopeDataCell> cells, int column, int row){
        Iterator<SlopeDataCell> iterator = cells.iterator();
        while (iterator.hasNext()){
            SlopeDataCell cell = iterator.next();
            if(cell.getRow() >= row + 1){
                iterator.remove();
            }
        }
        return cells;
    }
    
    public ArrayList<SlopeDataCell> getSouthernCells(ArrayList<SlopeDataCell> cells, int column, int row){
        Iterator<SlopeDataCell> iterator = cells.iterator();
        while (iterator.hasNext()){
            SlopeDataCell cell = iterator.next();
            if(cell.getRow() <= row - 1){
                iterator.remove();
            }
        }
        return cells;
    }
    
    public ArrayList<SlopeDataCell> getEasternCutoutCells(ArrayList<SlopeDataCell> cells, int column, int row, int heightOfCutout ){
        
        Iterator<SlopeDataCell> iterator = cells.iterator();
        
        while (iterator.hasNext()){
            SlopeDataCell cell = iterator.next();
            if(cell.getColumn() <= column - 1 && cell.getRow() > row - heightOfCutout && cell.getRow() < row + heightOfCutout){
                iterator.remove();
            }
            else if(cell.getColumn() <= column - 1){
                iterator.remove();
            }
        }
        
        return cells;
        
    }
    
    
    public ArrayList<SlopeDataCell> getWesternCutoutCells(ArrayList<SlopeDataCell> cells, int column, int row, int heightOfCutout ){
        
        Iterator<SlopeDataCell> iterator = cells.iterator();
        
        while (iterator.hasNext()){
            SlopeDataCell cell = iterator.next();
            if(cell.getColumn() <= column + 1 && cell.getRow() > row - heightOfCutout && cell.getRow() < row + heightOfCutout){
                iterator.remove();
            }
            else if(cell.getColumn() >= column - 5){
                iterator.remove();
            }
        }
        
        return cells;
        
    }
    
    public ArrayList<SlopeDataCell> getWesternCells(ArrayList<SlopeDataCell> cells, int column, int row){
        Iterator<SlopeDataCell> iterator = cells.iterator();
        while (iterator.hasNext()){
            SlopeDataCell cell = iterator.next();
            if(cell.getColumn() >= column + 1){
                iterator.remove();
            }
        }
        return cells;
    }
        

    public double[][][] calculateViewShed(int column, int row, int radius) {

        ArrayList<SlopeDataCell> visibleCells = getVisibleCells(column, row, radius);

        double[][][] visibleGons = convertMapToCoords(myJoin(visibleCells));


        return visibleGons;
    }

    public double[][][] convertMapToCoords(RowPolyMapCount rowPolyMapCount) {
        HashMap<Integer, ArrayList<ArrayList<Integer>>> rowPolyMap = rowPolyMapCount.getRowPolysMap();

        // eachrow has 
//        ArrayList<WisarPolygon> visibleGons = new ArrayList<WisarPolygon>();
        double[][][] polys = new double[rowPolyMapCount.getPolyCount()][][];

        int polyCount = 0;
        Set<Integer> rows = rowPolyMap.keySet();
        for (Integer row : rows) {
            ArrayList<ArrayList<Integer>> rowOfPolys = rowPolyMap.get(row);
            for (ArrayList<Integer> poly : rowOfPolys) {

                double[] swPoint = getLonLat(poly.get(0), row);
                double[] nwPoint = new double[2];

                nwPoint[0] = swPoint[0];
                nwPoint[1] = swPoint[1] + getCellSize();

                double[] sePoint = getLonLat(poly.get(1), row);
                sePoint[0] += getCellSize();

                double[] nePoint = new double[2];
                nePoint[0] = sePoint[0];
                nePoint[1] = sePoint[1] + getCellSize();

                polys[polyCount] = new double[4][];
                polys[polyCount][0] = swPoint;
                polys[polyCount][1] = nwPoint;
                polys[polyCount][2] = nePoint;
                polys[polyCount][3] = sePoint;
                polyCount++;

//                WisarPolygon visibleGon = new WisarPolygon();
//                visibleGon.addCoord(swPoint);
//                visibleGon.addCoord(nwPoint);
//                visibleGon.addCoord(nePoint);
//                visibleGon.addCoord(sePoint);
//                visibleGons.add(visibleGon);
            }
        }

        return polys;

    }

    public RowPolyMapCount myJoin(ArrayList<SlopeDataCell> cells) {

        // fuck this, put this into a 2d array and then iterate through
        ArrayList<SlopeDataCell> rowOrder = new ArrayList<SlopeDataCell>(cells);
        ArrayList<SlopeDataCell> columnOrder = new ArrayList<SlopeDataCell>(cells);

        // sort
        Collections.sort(rowOrder, new RowComparator());
        Collections.sort(columnOrder, new ColumnComparator());

        // get max mins
        int maxRow = rowOrder.get(rowOrder.size() - 1).getRow();
        int minRow = rowOrder.get(0).getRow();

        int maxColumn = columnOrder.get(columnOrder.size() - 1).getColumn();
        int minColumn = columnOrder.get(0).getColumn();


        // build mini raster
        SlopeDataCell[][] miniRaster = new SlopeDataCell[maxRow - minRow + 1][maxColumn - minColumn + 1];

        // populate miniraster
        for (SlopeDataCell cell : cells) {

            miniRaster[cell.getRow() - minRow][cell.getColumn() - minColumn] = cell;
        }

        /// part two, joining row data, this kinda sucks
        int polyCount = 0;
        ArrayList<ArrayList<Integer>> rowOfPolys = null; //new ArrayList<ArrayList<Integer>>();
        HashMap<Integer, ArrayList<ArrayList<Integer>>> rowPolysMap = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
        for (SlopeDataCell[] row : miniRaster) {
            Integer start = null, end = null;
            SlopeDataCell lastCell = null;
            for (SlopeDataCell celly : row) {


                if (celly != null && start == null) {
                    start = new Integer(celly.getColumn());
                } else if (celly == null && start != null && end == null) {

                    end = new Integer(lastCell.getColumn());
                    ArrayList<Integer> poly = new ArrayList<Integer>();
                    poly.add(start);
                    poly.add(end);
                    polyCount++;

                    // get row, or add if new
                    if (!rowPolysMap.containsKey(lastCell.getRow())) {
                        rowPolysMap.put(lastCell.getRow(), new ArrayList<ArrayList<Integer>>());
                    }
                    rowOfPolys = rowPolysMap.get(lastCell.getRow());

                    rowOfPolys.add(poly);
                    rowPolysMap.put(lastCell.getRow(), rowOfPolys);
                    start = null;
                    end = null;

                }
                lastCell = celly;
            }
        }

        return new RowPolyMapCount(rowPolysMap, polyCount);

    }

    /**
     * Given the raster origin and velocity vector, what is the next point one velocity vector magnitude away
     * @param origin
     * @param velocityVector
     * @param cellSize
     * @return 
     */
    public double[] getNextLatLon(int[] origin, double velocityVector[]) {
        double[] lonLat = getLonLat(origin[0], origin[1]);
        lonLat[0] += velocityVector[0] * getCellSize();
        lonLat[1] -= velocityVector[1] * getCellSize();
        return lonLat;
    }

    public void getAllNorthernCells(SlopeDataCell startPoint, ArrayList<SlopeDataCell> allCells) {
    }

    public SlopeDataCell getACellNorth(SlopeDataCell aCellSouth) {
        return new SlopeDataCell(0, 0, aCellSouth.getColumn(), aCellSouth.getRow() + 1);
    }

    public SlopeDataCell getACellSouth(SlopeDataCell aCellNorth) {
        return new SlopeDataCell(0, 0, aCellNorth.getColumn(), aCellNorth.getRow() - 1);
    }

    public ArrayList<Polygon> collectGeometries(ArrayList<SlopeDataCell> cells) {


        ArrayList<Polygon> individualGons = new ArrayList<Polygon>();

        for (SlopeDataCell cell : cells) {

            double[] lonLat = getLonLat(cell.getColumn(), cell.getRow());
            Polygon cellGon = GeomBuilder.createGridCellFromLowerLeftPoint(lonLat[0], lonLat[1], getCellSize());

            individualGons.add(cellGon);

        }
        return individualGons;

//        GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);
//        GeometryCollection geomCollection = (GeometryCollection) factory.buildGeometry(individualGons);
//
//        return geomCollection.buffer(0);
    }

    private ArrayList<SlopeDataCell> oneDirectionViewshed(int column, int row, int radius) {
        float elevationOrigin = getCell(column, row) + 6;
        float elevationHere = 0;
        float dx = 0;
        float dy = 0;
        float slope = 0;
        float biggestSlopeSeen = -9999999;

        ArrayList<SlopeDataCell> cells = new ArrayList<SlopeDataCell>();

        // walk thirty colums to the right
        for (int i = row + 10; i < row + radius; i++) {
            elevationHere = getCell(column, i);
            System.out.println("elevation here: " + elevationHere);
            dx = (i - row) * 8;
            dy = elevationHere - elevationOrigin;
            slope = dy / dx;
            System.out.println("comparing " + slope + " to " + biggestSlopeSeen);
            if (slope > biggestSlopeSeen) {
                // visible
                biggestSlopeSeen = slope;
                cells.add(getSlopeDataCell(column, i));
                System.out.println(" slope: " + slope + " is deemed visible ");
            }

        }
        return cells;
    }

    private void calculateSePoint() {
        ArrayList<ArrayList<Float>> cells = getData();

        // last row
        log.log(Level.INFO, "row count is {0}", cells.size());
        int lastRow = (cells.size() - 1);

        // last column
        int lastColumn = cells.get(lastRow).size() - 1;

        setSeX(nwX + lastColumn * this.cellSize);
        setSeY(nwY - lastRow * this.cellSize);


    }

    private void log(String msgs, Object data) {
        log.log(Level.INFO, msgs, data);
    }

    public float getCell(float column, float row) {
        return getCell(new Float(column).intValue(), new Float(row).intValue());
    }

    public float getCell(int column, int row) {
        ArrayList<ArrayList<Float>> cells = getData();
        return cells.get(row).get(column);
    }

    public SlopeDataCell getSlopeDataCell(int column, int row) {
        SlopeDataCell slopeDataCell = new SlopeDataCell(calculateSlope(column, row), (double) getCell(column, row), column, row);
        return slopeDataCell;
    }

    /**
     * Just get the coordinates for the 
     * @param columnCenter
     * @param rowCenter
     * @param radius
     * @return 
     */
    public int[][] getPerimiterCoordinates(int columnCenter, int rowCenter, int radius) {
        int[][] perimiterPoints = new int[8 * radius][2];

        int coordinateIndex = 0;
        int firstRow = rowCenter - radius;
        int lastRow = rowCenter + radius;


        for (int j = columnCenter - radius; j < columnCenter + radius + 1; j++) {

            perimiterPoints[coordinateIndex][0] = j;
            perimiterPoints[coordinateIndex][1] = firstRow;
            coordinateIndex++;

            perimiterPoints[coordinateIndex][0] = j;
            perimiterPoints[coordinateIndex][1] = lastRow;
            coordinateIndex++;


        }

        // we do just the inner of this guy so we don't duplicate the corners
        int firstColumn = columnCenter - radius;
        int lastColumn = columnCenter + radius;
        for (int i = rowCenter - radius + 1; i < rowCenter + radius; i++) {

            perimiterPoints[coordinateIndex][0] = firstColumn;
            perimiterPoints[coordinateIndex][1] = i;
            coordinateIndex++;

            perimiterPoints[coordinateIndex][0] = lastColumn;
            perimiterPoints[coordinateIndex][1] = i;
            coordinateIndex++;
        }

        return perimiterPoints;

    }

    public double[][] getBBoxLatLon(ArrayList<ArrayList<SlopeDataCell>> theHood) {

        double[][] bbox = new double[4][2];
        ArrayList<SlopeDataCell> firstRow = theHood.get(0);
        ArrayList<SlopeDataCell> lastRow = theHood.get(theHood.size() - 1);

        int firstRowColumn = firstRow.get(0).getColumn();
        int firstRowRow = firstRow.get(0).getRow();

        int lastRowColumn = lastRow.get(lastRow.size() - 1).getColumn();
        int lastRowRow = lastRow.get(lastRow.size() - 1).getRow();

        bbox[0] = getLonLat(firstRowColumn, firstRowRow);
        bbox[0][1] += getCellSize();

        bbox[1] = getLonLat(lastRowColumn, firstRowRow);
        bbox[1][0] += getCellSize();
        bbox[1][1] += getCellSize();

        bbox[2] = getLonLat(lastRowColumn, lastRowRow);
        bbox[2][0] += getCellSize();

        bbox[3] = getLonLat(firstRowColumn, lastRowRow);

        return bbox;


    }

    public ArrayList<SlopeDataCell> getHighestFlattest(ArrayList<ArrayList<SlopeDataCell>> theHood, ArrayList<SlopeDataCell> excludes) {
        // exclude from the hood and then call the main daddy
        for (ArrayList<SlopeDataCell> row : theHood) {
            for (SlopeDataCell badCell : excludes) {
                if (row.contains(badCell)) {
                    row.remove(badCell);
                }
            }
        }
        return getHighestFlattest(theHood);
    }

    public ArrayList<SlopeDataCell> getCellsMoreThan(ArrayList<ArrayList<SlopeDataCell>> theHood, double min, float percentOver) {
        ArrayList<SlopeDataCell> slopeList = new ArrayList<SlopeDataCell>();

        for (ArrayList<SlopeDataCell> row : theHood) {
            for (SlopeDataCell cell : row) {
                slopeList.add(cell);
            }
        }
        Collections.sort(slopeList, new SlopeComparator());

        // bottom range
        double max = min * (1 + percentOver);
        ArrayList<SlopeDataCell> withinRange = new ArrayList<SlopeDataCell>();


        // get cells within range
        for (ArrayList<SlopeDataCell> row : theHood) {
            for (SlopeDataCell cell : row) {
                int slopeIndex = slopeList.indexOf(cell);
                double cellData = cell.getData();
                if (cellData >= min && cellData <= max && slopeIndex <= (int) Math.floor((slopeList.size() - 1) * 0.25)) {
                    withinRange.add(cell);
                }

            }
        }

        return withinRange;
    }

    public ArrayList<SlopeDataCell> getCellsWithinRange(ArrayList<ArrayList<SlopeDataCell>> theHood, double max, float percentRadius) {
        ArrayList<SlopeDataCell> lessThan = getCellsLessThan(theHood, max, percentRadius);
        ArrayList<SlopeDataCell> moreThan = getCellsMoreThan(theHood, max, percentRadius);

        ArrayList<SlopeDataCell> all = new ArrayList<SlopeDataCell>();
        all.addAll(lessThan);
        all.addAll(moreThan);

        return all;
    }

    public ArrayList<SlopeDataCell> getCellsLessThan(ArrayList<ArrayList<SlopeDataCell>> theHood, double max, float percentUnder) {
        ArrayList<SlopeDataCell> slopeList = new ArrayList<SlopeDataCell>();

        for (ArrayList<SlopeDataCell> row : theHood) {
            for (SlopeDataCell cell : row) {
                slopeList.add(cell);
            }
        }
        Collections.sort(slopeList, new SlopeComparator());

        // bottom range
        double min = max * (1 - percentUnder);
        ArrayList<SlopeDataCell> withinRange = new ArrayList<SlopeDataCell>();


        // get cells within range
        for (ArrayList<SlopeDataCell> row : theHood) {
            for (SlopeDataCell cell : row) {
                int slopeIndex = slopeList.indexOf(cell);
                double cellData = cell.getData();
                if (cellData >= min && cellData <= max && slopeIndex <= (int) Math.floor((slopeList.size() - 1) * 0.25)) {
                    withinRange.add(cell);
                }

            }
        }

        return withinRange;
    }

    public ArrayList<SlopeDataCell> as1DimensionalList(ArrayList<ArrayList<SlopeDataCell>> theHood) {
        
        ArrayList<SlopeDataCell> slopeList = new ArrayList<SlopeDataCell>();

        for (ArrayList<SlopeDataCell> row : theHood) {
            slopeList.addAll(row);
        }
        
        return slopeList;
    }
    
    public ArrayList<SlopeDataCell> getSlopeLessThan1D(ArrayList<SlopeDataCell> theHood, float maxSlope) {


        ArrayList<SlopeDataCell> withinRange = new ArrayList<SlopeDataCell>();
        for (SlopeDataCell cell : theHood) {
            if (cell.getSlope() < maxSlope) {
                withinRange.add(cell);
            }
        }


        return withinRange;
    }

    public ArrayList<SlopeDataCell> getSlopeLessThan(ArrayList<ArrayList<SlopeDataCell>> theHood, float maxSlope) {
        ArrayList<SlopeDataCell> slopeList = new ArrayList<SlopeDataCell>();

        for (ArrayList<SlopeDataCell> row : theHood) {
            slopeList.addAll(row);
        }


        ArrayList<SlopeDataCell> withinRange = new ArrayList<SlopeDataCell>();
        for (SlopeDataCell cell : slopeList) {
            if (cell.getSlope() < maxSlope) {
                withinRange.add(cell);
            }
        }


        return withinRange;
    }

    public ArrayList<SlopeDataCell> getFlats(ArrayList<ArrayList<SlopeDataCell>> theHood) {
        ArrayList<SlopeDataCell> slopeList = new ArrayList<SlopeDataCell>();

        for (ArrayList<SlopeDataCell> row : theHood) {
            slopeList.addAll(row);
        }
        Collections.sort(slopeList, new SlopeComparator());
        ArrayList<SlopeDataCell> withinRange = new ArrayList<SlopeDataCell>();


        // get cells within range
        for (ArrayList<SlopeDataCell> row : theHood) {
            for (SlopeDataCell cell : row) {
                int slopeIndex = slopeList.indexOf(cell);
                if (slopeIndex <= (int) Math.floor((slopeList.size() - 1) * 0.25)) {
                    withinRange.add(cell);
                }

            }
        }

        return withinRange;
    }

    public ArrayList<SlopeDataCell> getHighs(ArrayList<ArrayList<SlopeDataCell>> theHood) {
        ArrayList<SlopeDataCell> dataList = new ArrayList<SlopeDataCell>();
        for (ArrayList<SlopeDataCell> row : theHood) {
            dataList.addAll(row);
        }

        Collections.sort(dataList, new DataComparator());
        ArrayList<SlopeDataCell> withinRange = new ArrayList<SlopeDataCell>();

        // get cells within range
        for (ArrayList<SlopeDataCell> row : theHood) {
            for (SlopeDataCell cell : row) {
                int slopeIndex = dataList.indexOf(cell);
                if (slopeIndex >= (int) Math.floor((dataList.size() - 1) * 0.75)) {
                    withinRange.add(cell);
                }
            }
        }

        return withinRange;
    }

    public ArrayList<SlopeDataCell> getLows(ArrayList<ArrayList<SlopeDataCell>> theHood) {
        ArrayList<SlopeDataCell> dataList = new ArrayList<SlopeDataCell>();
        for (ArrayList<SlopeDataCell> row : theHood) {
            dataList.addAll(row);
        }

        Collections.sort(dataList, new DataComparator());
        ArrayList<SlopeDataCell> withinRange = new ArrayList<SlopeDataCell>();

        // get cells within range
        for (ArrayList<SlopeDataCell> row : theHood) {
            for (SlopeDataCell cell : row) {
                int slopeIndex = dataList.indexOf(cell);
                if (slopeIndex <= (int) Math.floor((dataList.size() - 1) * 0.25)) {
                    withinRange.add(cell);
                }
            }
        }

        return withinRange;
    }

    public ArrayList<SlopeDataCell> getHighestFlattest(ArrayList<ArrayList<SlopeDataCell>> theHood) {
        ArrayList<SlopeDataCell> slopeList = new ArrayList<SlopeDataCell>();
        ArrayList<SlopeDataCell> dataList = new ArrayList<SlopeDataCell>();

        for (ArrayList<SlopeDataCell> row : theHood) {
            slopeList.addAll(row);
            dataList.addAll(row);

        }
        Collections.sort(dataList, new DataComparator());
        Collections.sort(slopeList, new SlopeComparator());


        ArrayList<SlopeDataCell> highFlats = new ArrayList<SlopeDataCell>();

        // get items in bigger end of data list and smaller end of slope list
        for (ArrayList<SlopeDataCell> row : theHood) {
            int columnIndex = 0;
            for (SlopeDataCell cell : row) {

                int slopeIndex = slopeList.indexOf(cell);
                int dataIndex = dataList.indexOf(cell);
                if (slopeIndex <= (int) Math.floor((slopeList.size() - 1) * 0.25) && (dataIndex >= (int) Math.floor((dataList.size() - 1) * 0.75))) {
                    highFlats.add(cell);
                }
                columnIndex++;
            }
        }


        return highFlats;
    }
    
     public ArrayList<SlopeDataCell> getLowFlats(ArrayList<ArrayList<SlopeDataCell>> theHood) {
        ArrayList<SlopeDataCell> slopeList = new ArrayList<SlopeDataCell>();
        ArrayList<SlopeDataCell> dataList = new ArrayList<SlopeDataCell>();

        for (ArrayList<SlopeDataCell> row : theHood) {
            slopeList.addAll(row);
            dataList.addAll(row);

        }
        Collections.sort(dataList, new DataComparator());
        Collections.sort(slopeList, new SlopeComparator());


        ArrayList<SlopeDataCell> highFlats = new ArrayList<SlopeDataCell>();

        // get items in bigger end of data list and smaller end of slope list
        for (ArrayList<SlopeDataCell> row : theHood) {
            int columnIndex = 0;
            for (SlopeDataCell cell : row) {

                int slopeIndex = slopeList.indexOf(cell);
                int dataIndex = dataList.indexOf(cell);
                if (slopeIndex <= (int) Math.floor((slopeList.size() - 1) * 0.60) && (dataIndex <= (int) Math.floor((dataList.size() - 1) * 0.60))) {
                    highFlats.add(cell);
                }
                columnIndex++;
            }
        }


        return highFlats;
    }

    public ArrayList<ArrayList<SlopeDataCell>> getSlopeDataNeighborhood(int column, int row) {
        long t0 = new Date().getTime();
        ArrayList<ArrayList<SlopeDataCell>> slopeDataNeighborhood = new ArrayList<ArrayList<SlopeDataCell>>();



        // 1
        slopeDataNeighborhood.add(new ArrayList<SlopeDataCell>());
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column - 1, row - 1));
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column, row - 1));
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column + 1, row - 1));

        // 2
        slopeDataNeighborhood.add(new ArrayList<SlopeDataCell>());
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column - 1, row));
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column, row));
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column + 1, row));

        // 3
        slopeDataNeighborhood.add(new ArrayList<SlopeDataCell>());
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column + 1, row + 1));
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column, row + 1));
        slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(column + 1, row + 1));

        //========================


        return slopeDataNeighborhood;
    }

    public ArrayList<ArrayList<SlopeDataCell>> getEasternNeighborhoodPortion(ArrayList<ArrayList<SlopeDataCell>> hood, float percent) {
        ArrayList<ArrayList<SlopeDataCell>> easternMotors = new ArrayList<ArrayList<SlopeDataCell>>();
        int width = hood.get(0).size();
        int startIndex = (int) Math.floor((width - 1) * percent);
        for (int i = 0; i < hood.size(); i++) {
            easternMotors.add(new ArrayList<SlopeDataCell>());
            for (int j = startIndex; j < width; j++) {
                easternMotors.get(easternMotors.size() - 1).add(hood.get(i).get(j));
            }
        }
        return easternMotors;
    }

    /**
     * 0 is north, 1 is east, 2 is south, 3 is west
     * @param portionToBlockOut
     * @return modified neighborhood
     */
    public ArrayList<SlopeDataCell> mask(ArrayList<ArrayList<SlopeDataCell>> hood, ArrayList<SlopeDataCell> pointsOfInterest, int portionToBlockOut) {
        ArrayList<SlopeDataCell> thoseWhoMadeTheCut = new ArrayList<SlopeDataCell>();
        if (portionToBlockOut == 0) {
            int middleRow = hood.get((hood.size() - 1) / 2).get(0).getRow();
            for (SlopeDataCell cell : pointsOfInterest) {
                if (cell.getRow() <= middleRow) {
                    thoseWhoMadeTheCut.add(cell);
                }
            }
        } else if (portionToBlockOut == 2) {
            int middleRow = hood.get((hood.size() - 1) / 2).get(0).getRow();
            for (SlopeDataCell cell : pointsOfInterest) {
                if (cell.getRow() >= middleRow) {
                    thoseWhoMadeTheCut.add(cell);
                }
            }
        } else if (portionToBlockOut == 1) {
            ArrayList<SlopeDataCell> firstRow = hood.get(0);
            int middleColumn = firstRow.get((firstRow.size() - 1) / 2).getColumn();
            for (SlopeDataCell cell : pointsOfInterest) {
                if (cell.getRow() >= middleColumn) {
                    thoseWhoMadeTheCut.add(cell);
                }
            }
        } else if (portionToBlockOut == 1) {
            ArrayList<SlopeDataCell> firstRow = hood.get(3);
            int middleColumn = firstRow.get((firstRow.size() - 1) / 2).getColumn();
            for (SlopeDataCell cell : pointsOfInterest) {
                if (cell.getRow() <= middleColumn) {
                    thoseWhoMadeTheCut.add(cell);
                }
            }
        }

        return thoseWhoMadeTheCut;
    }

    public ArrayList<ArrayList<SlopeDataCell>> getSlopeDataNeighborhood(int column, int row, int range) {
        ArrayList<ArrayList<SlopeDataCell>> slopeDataNeighborhood = new ArrayList<ArrayList<SlopeDataCell>>();
        log.log(Level.INFO, "center cell is {0}, {1}", new Object[]{column, row});

        for (int i = row - range ; i < row + range +1; i++) {
            slopeDataNeighborhood.add(new ArrayList<SlopeDataCell>());
            for (int j = column - range ; j < column + range + 1; j++) {
                slopeDataNeighborhood.get(slopeDataNeighborhood.size() - 1).add(getSlopeDataCell(j, i));

            }
        }




        return slopeDataNeighborhood;
    }

    public double[] getLonLat(int column, int row) {

        double lon = getNwX() + column * getCellSize();
        double lat = getNwY() - row * getCellSize();

        double[] coord = new double[2];
        coord[0] = lon;
        coord[1] = lat;

        return coord;
    }

    public double[] getLonLat(float column, float row) {

        double lon = getNwX() + column * getCellSize();
        double lat = getNwY() - row * getCellSize();

        double[] coord = new double[2];
        coord[0] = lon;
        coord[1] = lat;

        return coord;
    }

    public int[] getPosition(double lon, double lat) {

        double columnDifference = lon - getNwX();
        double rowDifference = getNwY() - lat;

        int column = (int) Math.floor(columnDifference / getCellSize());
        int row = (int) Math.floor(rowDifference / getCellSize());
        int[] position = new int[2];

        position[0] = column;
        position[1] = row;

        return position;
    }

    public float[] getFloatPosition(double lon, double lat) {

        double columnDifference = lon - getNwX();
        double rowDifference = getNwY() - lat;

        float column = (float) (columnDifference / getCellSize());
        float row = (float) (rowDifference / getCellSize());
        float[] position = new float[2];

        position[0] = column;
        position[1] = row;

        return position;
    }

    public double calculateSlope(int column, int row) {
        double slopeX = (getCell(column + 1, row) - getCell(column - 1, row)) / (2 * 8);

        double slopeY = (getCell(column, row - 1) - getCell(column, row + 1)) / (2 * 8);


        return Math.sqrt(Math.pow(slopeX, 2) + Math.pow(slopeY, 2));
    }

    private float calculateExtremes() {
        float max = -1000;
        float min = 1000000;

        int maximumX = 0, maximumY = 0;
        int minimumX = 0, minimumY = 0;

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Float> row = data.get(i);
            for (int j = 0; j < row.size(); j++) {

                if (row.get(j).floatValue() > max) {
                    max = row.get(j);

                    maximumY = i;
                    maximumX = j;
//                    System.out.println( nwX + maximumX*this.cellSize );
//                    System.out.println( nwY - maximumY*this.cellSize );
                }

                if (row.get(j).floatValue() < min) {
                    min = row.get(j);

                    minimumY = i;
                    minimumX = j;

                }
            }
        }

        setMaxX(nwX + maximumX * this.cellSize);
        setMaxY(nwY - maximumY * this.cellSize);

        setMinX(nwX + minimumX * this.cellSize);
        setMinY(nwY - minimumY * this.cellSize);

        return max;
    }
    
      //======
    // north
    // ======
    public int getNorthVisibleCount(float[] loc, int visibilityRadius, float minSlope) {
        int northCount = getNorthVisibleCells( loc, visibilityRadius, minSlope).size();
        log.log(Level.INFO, "north count is {0}", new Object[]{northCount});

        return northCount;
    }

    public ArrayList<SlopeDataCell> getNorthVisibleCells(float[] loc, int visibilityRadius, float minSlope) {
        return getSlopeLessThan1D(getNorthernCells(getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius), (int) loc[0], (int) loc[1]), minSlope);
    }

    //=====
    // south
    //======
    public int getSouthVisibleCount( float[] loc, int visibilityRadius, float minSlope) {
        int southCount = getSouthVisibleCells( loc, visibilityRadius, minSlope).size();
        log.log(Level.INFO, "south count is {0} ", new Object[]{southCount});
        return southCount;
    }

    public ArrayList<SlopeDataCell> getSouthVisibleCells(float[] loc, int visibilityRadius, float minSlope) {
        return getSlopeLessThan1D(getSouthernCells(getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius), (int) loc[0], (int) loc[1]), minSlope);
    }

    // =======
    // west
    // =======
    public int getWestVisibleCount( float[] loc, int visibilityRadius, float minSlope) {
        int westCount = getWestVisibleCells(loc, visibilityRadius, minSlope).size();
        log.log(Level.INFO, "east count is {0} ", new Object[]{westCount});
        return westCount;
    }

    public ArrayList<SlopeDataCell> getWestVisibleCells(float[] loc, int visibilityRadius, float minSlope) {
        return getSlopeLessThan1D(getWesternCells(getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius), (int) loc[0], (int) loc[1]), minSlope);
    }

    //=======
    // east
    //======
    public int getEastVisibleCount( float[] loc, int visibilityRadius, float minSlope) {
        int eastCount = getEastVisibleCells(loc, visibilityRadius, minSlope).size();
        log.log(Level.INFO, "east count is {0} ", new Object[]{eastCount});
        return eastCount;
    }

    public ArrayList<SlopeDataCell> getEastVisibleCells(float[] loc, int visibilityRadius, float minSlope) {
        return getSlopeLessThan1D(getEasternCells(getVisibleCells((int) loc[0], (int) loc[1], visibilityRadius), (int) loc[0], (int) loc[1]), minSlope);
    }
}
