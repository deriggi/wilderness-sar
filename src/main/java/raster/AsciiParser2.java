/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster;

import raster.domain.Raster2D;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wb385924
 */
public class AsciiParser2 {

    private static final Logger log = Logger.getLogger(AsciiParser2.class.getName());
    private final String NCOLS = "ncols";
    private final String NROWS = "nrows";
    private final String XLLCORNER = "xllcorner";
    private final String YLLCORNER = "yllcorner";
    private final String CELLSIZE = "cellsize";
    private final String NODATA = "NODATA_value";
    private final int STANDARD_HEADER_SIZE = 6;
    private int numCols;
    private int numRows;
    private double xllcorner;
    private double yllcorner;
    private double cellSize;
    private int nullValue;
    private AsciiAction action = null;
    final String INF = "Inf";
//    private File f = null;
//    public AsciiDataLoader(File f) {
//        // set date, numCols, etc...
//        this.f = f;
//    }

    public AsciiParser2(AsciiAction action) {
        this.action = action;
    }

    public void parseAsciiFile(InputStream is) {

        parseHeader(is);

    }

    private String getHeaderType(String line) {
        log.log(Level.FINE, "about to get header from {0}", line);
        if (line.indexOf(NCOLS) != -1) {
            return NCOLS;
        }

        if (line.indexOf(NROWS) != -1) {
            return NROWS;
        }

        if (line.indexOf(XLLCORNER) != -1) {
            return XLLCORNER;
        }

        if (line.indexOf(YLLCORNER) != -1) {
            return YLLCORNER;
        }

        if (line.indexOf(NODATA) != -1) {
            return NODATA;
        }

        if (line.indexOf(CELLSIZE) != -1) {
            return CELLSIZE;
        }

        return null;

    }

    private Double getHeaderValue(String line) {
        if (line == null) {
            return null;
        }
        log.log(Level.FINE, "about to get value from {0}", line);
        String[] parts = line.split("\\s");
        for (String p : parts) {
//            System.out.println(partCounter++ +  " " + p);
        }
        log.log(Level.FINE, "parts length is {0}", parts.length);
//        if(parts.length !=2){
//            return null;
//        }

//        try {
//            DecimalFormat format = new DecimalFormat("#.##");
//            return format.parse(parts[parts.length - 1]).doubleValue();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return myParseDouble(parts[parts.length - 1]);
    }

    private Double myParseDouble(String number) {
        if (number == null) {
            return null;
        }
        
        String delim = null;
        
        // identify delimiter
        if (number.indexOf("e") != -1) {
            delim = "e";
        }

        else if (number.indexOf("E") != -1) {
            delim = "E";
        }
        else{
            // no delim found
            
            return Double.parseDouble(number);
        }
        
        String[] parts = number.split(delim);
        Double result = null;
        
        if (parts.length > 1) {
            // get first part
            double firstPart = Double.parseDouble(parts[0]);

            // get second part
            int secondPart = Integer.parseInt(parts[1]);

            
            

            // divide first by second
            result = firstPart * Math.pow(10.0, new Double(secondPart));
            
            
        }
        
        return result;


    }

    public static void main(String[] args) {
        try {
            CollectGeometryAsciiAction ca = new CollectGeometryAsciiAction();
            
            AsciiParser2 ap = new AsciiParser2(ca);
//            FileInputStream fis = new FileInputStream("C:\\cru\\cru_ts_3_10_01.1901.2009.raster_ascii.pre\\pre\\cru_ts_3_10_01.1901.2009.pre_1901_1.asc");
            FileInputStream fis = new FileInputStream("C:\\gis data\\terrain\\grdn41w117_13.txt");
            ap.parseAsciiFile(fis);
            
            System.out.println(" size of this file is " +  ca.getBa().getCount() + " " + ca.getBa().getAvg());
            System.out.println("nw " + ca.nw.toText());
//            System.out.println("se " + ca.se.toText());
            System.out.println("calculatedRowCount " + ca.calculatedRowCount);
            
            Raster2D raster = ca.getRaster2D();
            float calcedMax = raster.getMaxData();
            System.out.println("i understand the cellsize to be " + raster.getCellSize());
            
            System.out.println("max " + raster.getMaxX() + " " + raster.getMaxY());
            System.out.println("min " + raster.getMinX() + " " + raster.getMinY());
            System.out.println("se " + raster.getSeX() + " " + raster.getSeY());
            System.out.println("nw " + raster.getNwX() + " " + raster.getNwY());
//            raster.printNeighbourhood(21, 1120);
            
            
//            BasicAverager rowCounter = new BasicAverager();
//            int n = 0;
//            while ( n < raster.size() ){
//                rowCounter.update(raster.get(n).size());
//                System.out.println("row size " + raster.get(n).size());
//                n++;
//            }
//            System.out.println("average column size is : " + rowCounter.getAvg());
            // @TODO get the max x and y of the data and then derive location
            
           
            
//            System.out.println("maxY and maxX " + maxY + " , " + maxX);
            
            fis.close();
        
        } catch (IOException ex) {
            Logger.getLogger(AsciiParser2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initParams(HashMap<String, Object> header) {
        this.nullValue = ((Double) header.get(NODATA)).intValue();
        this.numCols = ((Double) header.get(NCOLS)).intValue();
        this.numRows = ((Double) header.get(NROWS)).intValue();
        this.xllcorner = ((Double) header.get(XLLCORNER));
        this.yllcorner = ((Double) header.get(YLLCORNER));
        this.cellSize = ((Double) header.get(CELLSIZE));
    }

    private void validateParams() {
        if (nullValue == 0) {
            log.warning("null value not defined");
        }

        if (numCols == 0) {
            log.warning("num cols not defined");
        }

        if (numRows == 0) {
            log.warning("num rows not defined");
        }

        if (xllcorner == 0) {
            log.fine("xllcorner not defined");
        }

        if (yllcorner == 0) {
            log.warning("yllcorner not defined");
        }

        if (cellSize == 0) {
            log.warning("cellsize not defined");
        }
    }

    //iterate through top six lines
    private void parseHeader(InputStream f) {
        InputStreamReader isr = null;
        BufferedReader br = null;
        HashMap<String, Object> header = new HashMap<String, Object>();


        try {
            isr = new InputStreamReader(f);
            br = new BufferedReader(isr);
            String line = null;
            int lineCounter = 0;
            while ((line = br.readLine()) != null && lineCounter < STANDARD_HEADER_SIZE) {
                log.log(Level.INFO, line);
                String type = getHeaderType(line);
                Double d = getHeaderValue(line);
                log.log(Level.INFO, "type and value {0} {1}", new Object[]{type, d});

                if (type != null && d != null) {
                    header.put(type, d);
                } else {
                    log.log(Level.WARNING, "error processing header line: ", line);
                }
                if (header.size() == STANDARD_HEADER_SIZE) {
                    initParams(header);
                    validateParams();
                }

                lineCounter++;
            }

            if (header.size() != STANDARD_HEADER_SIZE) {
                log.log(Level.WARNING, "header object is now size {0}", header.size());
            }

            parseData(br);

        } catch (IOException ex) {
            Logger.getLogger(AsciiParser2.class.getName()).log(Level.SEVERE, null, ex);
            try {
                br.close();
            } catch (IOException ex1) {
                Logger.getLogger(AsciiParser2.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }
    }

    private void parseData(BufferedReader bufferedReader) {
        this.action.setCellSize(cellSize);
        
        try {
            int columnsRead = 0;
            int rowsRead = 0;

            String line;
            double xOffset = 0;
            double yOffset = 0;
            double x = 0;
            double y = 0;
            
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\s+");
               
                if (parts.length != this.numCols){
                    System.out.println("length is " + parts.length + " cols is: " + this.numCols);
                }
                for (String s : parts) {

                    xOffset = (columnsRead) * cellSize;
                    x = xllcorner + xOffset;

                    yOffset = (numRows - rowsRead) * cellSize;
                    y = yllcorner + yOffset;

                    // if s != inf and not the null data
                    if (s.length() > 0 && !s.equals(INF) ) {
                        try{
                            double val = Double.parseDouble(s);
//                            if(val != nullValue){
                                this.action.handleNonNullData(y, x, val);
//                            }
                        } catch (NumberFormatException nfe){
                            System.out.println("trying to parse double : " + s);
                            nfe.printStackTrace();
                        }
                    }
                    
                    columnsRead++;
//                    if (columnsRead == numCols) {
//                        rowsRead++;
//                        columnsRead = 0;
//                    }

                }
                rowsRead++;
                columnsRead = 0;
                
            }
            
            log.log(Level.INFO,"number of rows read is {0} num should have is {1}", new Object[] {rowsRead, numRows});
            log.log(Level.INFO,"last columns read was {0} ", new Object[] {columnsRead});

        } catch (IOException ioe) {
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException ex) {
                Logger.getLogger(AsciiParser2.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

//    private double dostuff(String data){
//        final String INF = "Inf";
//        if (data.equalsIgnoreCase(INF)){
//            return new Double(nullValue);
//        }
//        try {
//            Double.parseDouble(INF);
//        } catch (NumberFormatException nfe){
//            nfe.printStackTrace();
//        }
//        
//    }
//    
    private boolean isData(String data) {

        if (data == null || data.trim().length() == 0) {
            return false;
        }
        return true;


    }

//    private int getOrAddPoint(double longitude, double latitude) {
//        int id = GeoDao.getGeometryBoundsId(getCon(), "POINT(" + longitude + " " + latitude + ")", "cru_pr_point", "geom");
//        if (id == -1) {
//            id = GeoDao.storeGeometry(getCon(), "cru_pr_point", "geom", "POINT(" + longitude + " " + latitude + ")");
//        }
//        return id;
//
//    }
    @Deprecated
    private void handleLine(String line, double xmin, double ymin, int lineNumber) {
        String[] lineParts = line.split("\\s+");
        if (lineParts[0].trim().length() > 0) {
            log.log(Level.FINE, "{0} first:{1} last: {2}", new Object[]{lineParts.length, lineParts[0], lineParts[lineParts.length - 1]});
        }

        int colNumber = 0;
        log.log(Level.FINE, "col width: {0}", lineParts.length);
        final String INF = "Inf";
        for (String s : lineParts) {

            // make column count and line count global
            // increment line number only after column count reached

            if (s.trim().length() > 0) {
                double rowOffset = (numRows - (lineNumber - STANDARD_HEADER_SIZE)) * cellSize;
                double y = ymin + rowOffset - cellSize;

                double colOffset = (colNumber) * cellSize;
                double x = xmin + colOffset;

                if (!s.equalsIgnoreCase(INF)) {
                    try {
                        double val = Double.parseDouble(s);
                        if (val != nullValue) {
//                            this.action.handleNonNullData(y, x, val);
                            log.log(Level.FINE, "row: {0} col: {1}  x: {2}  y:{3}", new Object[]{lineNumber - STANDARD_HEADER_SIZE, colNumber, x, y});
                        }
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        System.out.println(line);


                    }
                }
                colNumber++;
            }

        }

    }
}
