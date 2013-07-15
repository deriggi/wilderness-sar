/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import java.util.Random;
import raster.domain.Raster2D;
import raster.domain.agent.AgentName;
import raster.domain.agent.FSMFactory;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class SimBuilder {

    // north
    // http://localhost:8080/wisar/q/agent/createagent/-116.82260513305663/40.40186269942073
    double[] north = new double[]{-116.82260513305663, 40.40186269942073};
    //south
    // http://localhost:8080/wisar/q/agent/createagent/-116.81419372558595/40.37205123555801
    double[] south = new double[]{-116.81419372558595, 40.37205123555801};
    // east
//        http://localhost:8080/wisar/q/agent/createagent/-116.81213378906249/40.39075031913329
    double[] east = new double[]{-116.81213378906249, 40.39075031913329};
    // west
    double[] west = new double[]{-116.83084487915039, 40.38891986882411};

    
    public static void main(String[] args) {
        new SimBuilder().runny();
    }

    private void runny() {

        // get sim Id
        String simId = SimId.getNewSimId();
        
//        createAgent(randy[0], randy[1], AgentName.LOST, FSMFactory.MachineName.DO_NOTHING, simId);
        
        double[] randy = getRandomPoint(north, south, east, west);
        createAgent(randy[0], randy[1], AgentName.UAV, FSMFactory.MachineName.EAST_WEST_LAWN_MOWER, simId);  
        
        AgentService.get().runFor(simId, 1000);

    }

    public double[] getRandomPoint(double[] north, double[] south, double[] east, double[] west) {


        double eastWestSpread = east[1] - west[1];
        double northSouthSpread = north[0] - south[0];

        double randyLon = getRandomFrom(west[0], eastWestSpread);
        double randyLat = getRandomFrom(south[1], northSouthSpread);

        double[] randyCoords = new double[]{randyLon, randyLat};

        return randyCoords;

    }

    private void createAgent(double lon, double lat, AgentName nameTag, FSMFactory.MachineName behave, String simId) {

        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] position = raster.getFloatPosition(lon, lat);

        float speed = 0;

        if (nameTag.equals(AgentName.UAV)) {
            speed = 4;
        } else {
            speed = 0.25f;
        }

        AgentService service = AgentService.get();
        SkelatalAgent a = service.createAgent(position[0], position[1], speed, behave, simId);
        a.setNameTag(nameTag);
    }

    private double getRandomFrom(double start, double spread) {
        Random r = new Random();

        return (start + (r.nextDouble() * spread));
    }

    private void testyRandy() {
        System.out.println(new SimBuilder().getRandomFrom(23.5, 10.0));

        SimBuilder sim = new SimBuilder();

        int i = 0;
        while (i++ < 20) {
            double[] randy = sim.getRandomPoint(sim.north, sim.south, sim.east, sim.west);
            System.out.println(randy[0] + " " + randy[1]);
        }


    }
}
