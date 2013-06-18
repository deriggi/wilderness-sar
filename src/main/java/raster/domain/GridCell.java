/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain;

import com.vividsolutions.jts.geom.Polygon;

/**
 *
 * @author Johnny
 */
public class GridCell implements Comparable<GridCell>{

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GridCell other = (GridCell) obj;
        if (this.polygon != other.polygon && (this.polygon == null || !this.polygon.equals(other.polygon))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.polygon != null ? this.polygon.hashCode() : 0);
        return hash;
    }

   

   
    
    private Polygon polygon;
    private double value;

    public GridCell(Polygon polygon, double value) {
        this.polygon = polygon;
        this.value = value;
    }
    
    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(GridCell o) {
        return this.polygon.compareTo(o.getPolygon());
    }


}
