/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdomain;

/**
 *
 * @author Johnny
 */
public class Neighborhood {

    private double[][] bbox = null;
    private double[][] ridgePoints = null;
    private double[][] forceVector = null;
    private double[] nextMove = null;

    public void setBbox(double[][] bbox) {
        this.bbox = bbox;
    }

    public void setRidgePoints(double[][] ridgePoints) {
        this.ridgePoints = ridgePoints;
    }

    public double[][] getForceVector() {
        return forceVector;
    }

    public void setForceVector(double[][] forceVector) {
        this.forceVector = forceVector;
    }

    public double[] getNextMove() {
        return nextMove;
    }

    public void setNextMove(double[] nextMove) {
        this.nextMove = nextMove;
        flip(this.nextMove);
    }
    

    public double[][] getRidgePoints() {
        return ridgePoints;
    }

    private void flipAll(double[][] allCoords) {
        if (allCoords != null) {
            for (double[] coords : allCoords) {
                flip(coords);
            }
        }
    }

    private void flip(double coords[]) {
        double temp;
        temp = coords[0];
        coords[0] = coords[1];
        coords[1] = temp;
    }

    public Neighborhood(double[][] bbox, double[][] ridgePoints, double[][] forceVector) {
        this.bbox = bbox;
        flipAll(this.bbox);

        this.ridgePoints = ridgePoints;
        flipAll(this.ridgePoints);

        this.forceVector = forceVector;
        flipAll(this.forceVector);
    }

    public double[][] getBbox() {
        return bbox;
    }
}
