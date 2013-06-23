/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package resources;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import middletier.RasterConfig;
import middletier.RasterLoader;
import raster.domain.AgentService;
import raster.domain.Raster2D;
import raster.domain.agent.IdLoc;
import raster.domain.agent.VectorAgent;

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
    public String createAgent(@PathParam("lon") double lon, @PathParam("lat") double lat) {

        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        float[] position = raster.getFloatPosition(lon, lat);
        VectorAgent a = AgentService.get().createLostPersonAgent(position[0], position[1]);
        VectorAgent b = AgentService.get().createUAVAgent(position[0]+50, position[1]-60);

        double[] aLonLat = raster.getLonLat(a.getLocation()[0], a.getLocation()[1]);
        IdLoc aIdLoc = a.toIdLoc();
        aIdLoc.setLocation(aLonLat);
        
        double[] bLonLat = raster.getLonLat(b.getLocation()[0], b.getLocation()[1]);
        IdLoc bIdLoc = b.toIdLoc();
        bIdLoc.setLocation(bLonLat);
        
        ArrayList<IdLoc> idLocs = new ArrayList<IdLoc>();
        idLocs.add(bIdLoc);
        idLocs.add(aIdLoc);
        
        return new Gson().toJson(idLocs);


    }

    @POST
    @Path("/wander/")
    @Produces(MediaType.APPLICATION_JSON)
    public String wanderAllAgents() {
        Raster2D raster = RasterLoader.get(RasterConfig.BIG).getData();
        Collection<VectorAgent> agents = AgentService.get().getAllAgents();
        ArrayList<IdLoc> locs = new ArrayList<IdLoc>();
        
        for (VectorAgent a : agents) {
            a.wander();
            double[] lonLat = raster.getLonLat(a.getLocation()[0], a.getLocation()[1]);
            IdLoc idLoc = a.toIdLoc();
            idLoc.setLocation(lonLat);
            locs.add(idLoc);
        }
        
        Gson gson = new Gson();
        String json = gson.toJson(locs);
        gson = null;
        return json;

        
        /**int[] position = raster.getPosition(lon, lat);
        Agent a = AgentService.get().createAgent(position[0], position[1]);
        a.setLocation(position);

        double[] lonLat = raster.getLonLat(a.getLocation()[0], a.getLocation()[1]);
        IdLoc idLoc = a.toIdLoc();
        idLoc.setLocation(lonLat);

        return new Gson().toJson(idLoc);
         **/

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