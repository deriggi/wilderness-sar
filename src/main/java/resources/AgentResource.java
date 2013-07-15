/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import middletier.RasterConfig;
import middletier.RasterLoader;
import middletier.AgentService;
import middletier.SimId;
import raster.domain.Raster2D;
import raster.domain.agent.AgentName;
import raster.domain.agent.IdLoc;
import raster.domain.agent.FSMFactory;
import raster.domain.agent.SkelatalAgent;
import util.GsonGetter;

/**
 *
 * @author Johnny
 */
@Path("/agent")
public class AgentResource {
//    @Context ServletContext context;

    private static final Logger log = Logger.getLogger(AgentResource.class.getCanonicalName());

    @GET
    @Path("/nextstep/{lon: \\-?[0-9]{1,3}\\.[0-9]+}/{lat: \\-?[0-9]{1,3}\\.[0-9]+}/{dir}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getNextAgentPosition(@PathParam("lon") double lon, @PathParam("lat") double lat, @PathParam("dir") String direction) {

//        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
//        int[] position = raster.getPosition(lon, lat);
//        Agent agent = AgentService.get().getAgent();
//        agent.setLocation(position);

//        return new Gson().toJson(raster.getNextLatLon(agent.getLocation(), agent.getVelocityVector()));
        return null;

    }

    @POST
    @Path("/createagent/{lon: \\-?[0-9]{1,3}\\.[0-9]+}/{lat: \\-?[0-9]{1,3}\\.[0-9]+}")
    @Produces(MediaType.APPLICATION_JSON)
    public String createAgent(@PathParam("lon") double lon, @PathParam("lat") double lat, @FormParam("agenttype") String agentType, @FormParam("behaviour") String behaviour, @FormParam("simid") String simId) {

        log.log(Level.INFO, "agent type: {0}  behaviour: {1}", new Object[]{agentType, behaviour});

        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] position = raster.getFloatPosition(lon, lat);
        FSMFactory.MachineName behave = FSMFactory.getMachineName(behaviour);

        float speed = 0;
        AgentName nameTag;
        if ((agentType != null) && agentType.toLowerCase().startsWith("uav")) {

            speed = 4;
            nameTag = AgentName.UAV;
        } else {
            speed = 0.25f;
            nameTag = AgentName.LOST;
        }

        AgentService service = AgentService.get();

        SkelatalAgent a = service.createAgent(position[0], position[1], speed, behave, simId);
        a.setNameTag(nameTag);

        double[] aLonLat = raster.getLonLat(a.getLocation()[0], a.getLocation()[1]);
        IdLoc aIdLoc = a.toIdLoc();
        aIdLoc.setLocation(aLonLat);


        ArrayList<IdLoc> idLocs = new ArrayList<IdLoc>();

        idLocs.add(aIdLoc);

        return GsonGetter.get().toJson(idLocs);
    }

    @POST
    @Path("/runsim/{ simid: [a-zA-Z0-9]{6} }")
    @Produces(MediaType.APPLICATION_JSON)
    public String runSim(@PathParam("simid") String simId) {

        AgentService service = AgentService.get();
        ArrayList<IdLoc> locs = service.runUntilFound(simId);
        String json = GsonGetter.get().toJson(locs);

        return json;
    }

    @POST
    @Path("/clearagents/{ simid: [a-zA-Z0-9]{6} }")
    @Produces(MediaType.APPLICATION_JSON)
    public String clearAgents(@PathParam("simid") String simId) {

        AgentService service = AgentService.get();
        service.clearAgents(simId);

        return GsonGetter.get().toJson("agents cleared");
    }

    @POST
    @Path("/wander/{ simid: [a-zA-Z0-9]{6} }/")
    @Produces(MediaType.APPLICATION_JSON)
    public String wanderAllAgents(@PathParam("simid") String simId) {
        log.info("calling wander ");
        AgentService service = AgentService.get();
        ArrayList<IdLoc> locs = service.runAgents(simId);
        String json = GsonGetter.get().toJson(locs);

        return json;


    }

    @POST
    @Path("/wander/{ simid: [a-zA-Z0-9]{6} }/{steps}")
    @Produces(MediaType.APPLICATION_JSON)
    public String wanderNSteps(@PathParam("simid") String simId, @PathParam("steps") int steps) {
        ArrayList<IdLoc> locs = new ArrayList<IdLoc>();
        AgentService service = AgentService.get();
        log.log(Level.INFO, "moving agents in {0}", simId);
        
        int i = 0;
        while (i++ < steps) {
            locs.addAll(service.runAgents(simId));
        }
        
        
        String json = GsonGetter.get().toJson(locs);
        
        return json;
    }
    
    @POST
    @Path("/simid/")
    @Produces(MediaType.APPLICATION_JSON)
    public String getSimId() {

        String[] simId = new String[1];
        simId[0]= SimId.getNewSimId();
        String json = GsonGetter.get().toJson(simId);
        
        return json;


    }

    // 
    // ( -116.87791831904 , 40.31604488258014 )
    // 
    public static void main(String[] args) {
//         Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
//         Math.random() - Math.random();
//        Agent a = new Agent();
//        int i = 0;
//        while (i++ < 10) {
//            a.steer(Math.PI / 2.0f);
//            double[] vv = a.getVelocityVector();
//            System.out.println(vv[0] + "  " + vv[1]);
//        }

        int i = 0;
        while (i++ < 10) {
            double something = Math.random() - Math.random();
            System.out.println(something);
        }

    }
}
