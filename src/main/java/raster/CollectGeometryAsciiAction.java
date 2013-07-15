/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster;

/**
 *
 * @author Johnny
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.logging.Level;
import raster.domain.Raster2D;
import raster.domain.GridCell;
import com.vividsolutions.jts.geom.Polygon;
import geomutils.GeomBuilder;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import middletier.BinaryFileConfig;
import statsutils.BasicAverager;

/**
 *
 * @author wb385924
 */
public class CollectGeometryAsciiAction implements AsciiAction {

    private final static Logger log = Logger.getLogger(CollectGeometryAsciiAction.class.getName());
    private BasicAverager ba = new BasicAverager();

    public BasicAverager getBa() {
        return ba;
    }
    private double cellSize = 0;
    private int count = 0;
    private Float lastY = null;
    public int calculatedRowCount = 0;
    public Polygon nw = null;
    public Polygon se = null;
    private float upperXBound = 0, upperYBound = 0, lowerXBound = 0;

    public float getLowerXBound() {
        return lowerXBound;
    }
    
    public void setLowerXBound(float lowerX) {
        this.lowerXBound = lowerX;
    }
    
    private RandomAccessFile raf = null;

    public float getUpperXBound() {
        return upperXBound;
    }

    public void setUpperXBound(float upperXBound) {
        this.upperXBound = upperXBound;
    }

    public float getUpperYBound() {
        return upperYBound;
    }

    public void setUpperYBound(float upperYBound) {
        this.upperYBound = upperYBound;
    }

    public double getCellSize() {
        return cellSize;
    }

    
    public void setCellSize(float cellSize) {
        this.cellSize = cellSize;
    }
    private Set<GridCell> gridCells = new HashSet<GridCell>();

    public Set<GridCell> getGridCells() {
        return gridCells;
    }
    private ArrayList<ArrayList<Float>> themCells = new ArrayList<ArrayList<Float>>();
    
    public ArrayList<ArrayList<Float>> getRaster(){
        return themCells;
    }
    private double nwX = 0, nwY = 0;
    
    @Override
    public void setup(){
        openBinaryFile();
    }
    
    private RandomAccessFile openBinaryFile(){
        
        try{
            raf = new RandomAccessFile(BinaryFileConfig.OUT_PATH.getPath() , "rw");
            
        } catch (FileNotFoundException fnfe){
            fnfe.printStackTrace();
        }
        return raf;
        
    }
    
    private void writeFloat(float someFloat){
        try {
            this.raf.writeFloat(someFloat);
        } catch (IOException ex) {
            Logger.getLogger(CollectGeometryAsciiAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void closeBinaryFile(RandomAccessFile raf){
        if(raf == null){
            return;
        }
        try {
            raf.close();
        } catch (IOException ex) {
            Logger.getLogger(CollectGeometryAsciiAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
            
    @Override
    public void handleNonNullData(double y, double x, double data) {
        if (x < getUpperXBound() && y < getUpperYBound() && y > getLowerXBound()) {
           
            
//            ba.update(data);
            if (count == 0) {
                nw = GeomBuilder.createGridCellFromLowerLeftPoint(x, y, getCellSize());
                themCells.add(new ArrayList<Float>());
                this.nwX = x;
                this.nwY = y;
                lastY = new Float(y);
            }

//            se = GeomBuilder.createGridCellFromLowerLeftPoint(x, y, getCellSize());

            if (lastY != null && !new Float(y).equals(lastY)) {
//                log.log(Level.INFO, "row y values {0} : {1}   ", new Object[]{lastY, new Float(y)});
                themCells.add(new ArrayList<Float>());
                calculatedRowCount++;
            }

            themCells.get(themCells.size() - 1).add(new Float(data));

            lastY = new Float(y);
            count++;


//        Polygon gridCell = GeomBuilder.createGridCellFromLowerLeftPoint(x, y, 0.5);
//        gridCells.add(new GridCell(gridCell, data));
        }
    }
    
    @Override
    public void cleanUp(){
        this.closeBinaryFile(null);
    }

    public int getSize() {
        return gridCells.size();
    }

    @Override
    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }
    
    public Raster2D getRaster2D(){
        return new Raster2D(themCells, this.cellSize, nwY, nwX);
    }
}
