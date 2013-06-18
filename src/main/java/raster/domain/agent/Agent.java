/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

/**
 *
 * @author Johnny
 */
public class Agent {
    
    private Integer id = null;

    public Integer getId() {
        return id;
    }
    
    public IdLoc toIdLoc(){
        IdLoc idLoc = new IdLoc();
        idLoc.setId(getId());
        return idLoc;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    

    private double getALittle(){
        return 0.0;
//        return Math.random() - Math.random();
    }
    public void wander(){
        double amountToSteer = (Math.PI/4) * getALittle();
        steer(amountToSteer);
        steerEast();
        int[] currentLoc = getLocation();
        double[] v  =  getVelocityVector();
        
        int newX = (int)Math.floor(currentLoc[0] + v[0]);
        int newY = (int)Math.floor(currentLoc[1] + v[1]);
        
        setLocation(new int[]{newX,newY} );
        
    }
    
    // radians
    private double bearing = Math.PI/2; 
    
    // cells per whatever
    private float speed = 4;

    public int[] getLocation() {
        return location;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }
    
    // column, row
    private int[] location = new int[2];

//    public 
    public double getBearing() {
        return bearing;
    }
    
    public void steer(double radians){
        this.bearing += radians;
    }
    
    public double dotProduct(double[][] vectorOne, double[][] vectorTwo) {
        double xa1 = vectorOne[0][0];
        double xa2 = vectorOne[1][0];

        double ya1 = vectorOne[0][1];
        double ya2 = vectorOne[1][1];

        double xb1 = vectorTwo[0][0];
        double xb2 = vectorTwo[1][0];

        double yb1 = vectorTwo[0][1];
        double yb2 = vectorTwo[1][1];

        double v1[] = {xa2 - xa1, ya2 - ya1};
        double v2[] = {xb2 - xb1, yb2 - yb1};

        double v1magnitude = Math.sqrt(Math.pow(v1[0], 2) + Math.pow(v1[1], 2));
        double v2magnitude = Math.sqrt(Math.pow(v2[0], 2) + Math.pow(v2[1], 2));

        double[] v1Unit = {v1[0] / v1magnitude, v1[1] / v1magnitude};
        double[] v2Unit = {v2[0] / v2magnitude, v2[1] / v2magnitude};

        double dotProduct = 0;
        dotProduct += v1Unit[0] * v2Unit[0];
        dotProduct += v1Unit[1] * v2Unit[1];

        return dotProduct;

    }
    
    public void steerEast(){
        
        System.out.println("");
        System.out.println(" == steer east == ");
        
        double currentDy = speed * Math.sin(bearing);
        double currentDx = speed * Math.cos(bearing);
//        if(currentDx <= -4){
//            steerSouth();
//            return;
//        }
        
        System.out.println("currentDy is\t" + currentDy);
        System.out.println("currentDx is\t" + currentDx);
        System.out.println("current bearing is\t" + bearing);
        System.out.println("arcos of\t" + (currentDx-0.5)/speed + " is: ");
        steer((0 - bearing )* 0.25);
        System.out.println("bearing west:  " + bearing);
        
    }
    public void steerSouth(){
        System.out.println("");
        System.out.println(" == steer south == ");
        
        double currentDy = speed * Math.sin(bearing);
        double currentDx = speed * Math.cos(bearing);
        
       
        
        System.out.println("currentDy is\t" + currentDy);
        System.out.println("currentDx is\t" + currentDx);
        System.out.println("current bearing is\t" + bearing);
        System.out.println("arcsin of\t" + (currentDy+0.5)/speed + " is: ");
        steer(-Math.asin(((4 - currentDy))/speed));
        
        System.out.println("bearing south:  " + bearing);
        
    }
    
    public void steerSouthWithVectors(){
        double[] vel = getVelocityVector();
        vel[1] += 0.5;
        
    }

    public double[] getVelocityVector() {
        double[] velocity = new double[2];
        double dy = speed * Math.sin(bearing);
//        double dx = Math.sqrt(Math.pow(speed,2) - Math.pow(dy,2));
        double dx = Math.cos(bearing) * speed;
        
        velocity[0] = dx;
        velocity[1] = dy;
        
        return velocity;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
}
