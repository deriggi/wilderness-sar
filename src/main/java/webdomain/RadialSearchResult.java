/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webdomain;

/**
 *
 * @author Johnny
 */
public class RadialSearchResult {

    private double[][] bbox = null;
    
    private double[][][] searchPaths = null;
    private double[] nextMove = null;
    private Integer winnerIndex = null;

    public Integer getLoserIndex() {
        return loserIndex;
    }

    public void setLoserIndex(Integer loserIndex) {
        this.loserIndex = loserIndex;
    }

    public double[][][] getSearchPaths() {
        return searchPaths;
    }

    public void setSearchPaths(double[][][] searchPaths) {
        this.searchPaths = searchPaths;
    }

    public Integer getWinnerIndex() {
        return winnerIndex;
    }

    public void setWinnerIndex(Integer winnerIndex) {
        this.winnerIndex = winnerIndex;
    }
    private Integer loserIndex = null;
    

    public double[] getNextMove() {
        return nextMove;
    }

    public void setNextMove(double[] nextMove) {
        this.nextMove = nextMove;
        flip(this.nextMove);
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

    public RadialSearchResult(){
        
    }
    
    public RadialSearchResult(double[][] bbox, double[][][] searchPaths) {
        this.bbox = bbox;
        flipAll(this.bbox);

        for(double[][] line : searchPaths){
            flipAll(line);
        }
    }
    

    public double[][] getBbox() {
        return bbox;
    }
}
