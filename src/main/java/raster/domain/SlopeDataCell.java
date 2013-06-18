/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

/**
 * A simple class to hold both the data value and the slope of a cell
 * @author Johnny
 */
public class SlopeDataCell  {
    private double slope;
    private double data;
    private int column;
    private int row;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SlopeDataCell other = (SlopeDataCell) obj;
        if (this.column != other.column) {
            return false;
        }
        if (this.row != other.row) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Float.floatToIntBits(this.column);
        hash = 71 * hash + Float.floatToIntBits(this.row);
        return hash;
    }

   

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }
    
    public double getData() {
        return data;
    }

    public void setData(double data) {
        this.data = data;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }
    
    public SlopeDataCell(double slope, double data, int column, int row){
        this.slope = slope;
        this.data = data;
        this.column = column;
        this.row = row;
    }

    
    
   
}
