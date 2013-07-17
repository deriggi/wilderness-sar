/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import raster.domain.Raster2D;
import raster.domain.agent.AgentName;
import raster.domain.agent.FSMFactory;
import raster.domain.agent.SkelatalAgent;

/**
 *
 * @author Johnny
 */
public class SimBuilder {

    private static final Logger log = Logger.getLogger(SimBuilder.class.getName());
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
//        new SimBuilder().runny();
        // sim id
        // for each line
        //type, behave
        // run until found simid

        new SimBuilder().processDirectory(new File("C:\\agentin"), 10);
    }

    private void processDirectory(File directory, int runsPerFile) {
        if (directory.isFile()) {
            log.log(Level.SEVERE, "expected a directory {0} ", directory.getAbsolutePath());
            return;
        }


        File[] files = directory.listFiles();
        for (File f : files) {


            AgentService service = AgentService.get();

            int i = 0;
            while (i++ < runsPerFile) {
                String simId = SimId.getNewSimId();
                readFile(f, simId);
                AgentService.get().runUntilFound(simId, 100, f.getName().substring(0,f.getName().length() - 4));
                service.clearAgents(simId);
            }

        }

        // for each file in directory
        //get simid
        //readfile()
        //run sim

    }

    private void readFile(File inputFile, String simId) {
        log.log(Level.INFO, "processing file {0} ", inputFile.getAbsolutePath());

        try {
            FileInputStream fis = new FileInputStream(inputFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = null;
            while ((line = br.readLine()) != null) {


                createAgentsFromLine(line.toUpperCase(), simId);

            }


        } catch (FileNotFoundException ex) {
            Logger.getLogger(SimBuilder.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createAgentsFromLine(String line, String simId) {
        String[] parts = line.split("\\s*,\\s*");
        log.log(Level.INFO, "processing line {0} ", line);

        if (parts.length != 2) {
            log.log(Level.SEVERE, "line is no good '{'0'}' {0}", line);
        }

        AgentName name = AgentName.valueOf(parts[0].trim());
        FSMFactory.MachineName machine = FSMFactory.MachineName.valueOf(parts[1].trim());

        int delay = 0;
        if (name.equals(AgentName.UAV)) {
            delay = getRandomFrom(500, 1500);
        }

        double[] randy = getRandomPoint(north, south, east, west);
        createAgent(randy[0], randy[1], name, machine, simId, delay);
        log.log(Level.INFO, "created {0} {1} ", new String[]{name.toString(), machine.toString(), Integer.toString(delay)});

    }

    private void runny() {

        // get sim Id
        String simId = SimId.getNewSimId();
        double[] randy = getRandomPoint(north, south, east, west);

        createAgent(randy[0], randy[1], AgentName.LOST, FSMFactory.MachineName.ADAPTIVE_EAST_WEST, simId);

        randy = getRandomPoint(north, south, east, west);
        createAgent(randy[0], randy[1], AgentName.UAV, FSMFactory.MachineName.EAST_WEST_LAWN_MOWER, simId);

        randy = getRandomPoint(north, south, east, west);
        createAgent(randy[0], randy[1], AgentName.UAV, FSMFactory.MachineName.EAST_WEST_LAWN_MOWER, simId);



        AgentService.get().runUntilFound(simId, 100, "testoutfolder");

    }

    public double[] getRandomPoint(double[] north, double[] south, double[] east, double[] west) {


        double eastWestSpread = east[0] - west[0];
        double northSouthSpread = north[1] - south[1];

        double randyLon = getRandomFrom(west[0], eastWestSpread);
        double randyLat = getRandomFrom(south[1], northSouthSpread);

        double[] randyCoords = new double[]{randyLon, randyLat};

        return randyCoords;

    }

    private void createAgent(double lon, double lat, AgentName nameTag, FSMFactory.MachineName behave, String simId) {
        createAgent(lon, lat, nameTag, behave, simId, 0);
    }

    private void createAgent(double lon, double lat, AgentName nameTag, FSMFactory.MachineName behave, String simId, int delay) {

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
        a.setDelay(delay);
        a.setNameTag(nameTag);
    }

    private double getRandomFrom(double start, double spread) {
        Random r = new Random();

        return (start + (r.nextDouble() * spread));
    }

    private int getRandomFrom(int start, int spread) {
        Random r = new Random();

        return (int) (start + (r.nextFloat() * spread));
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
