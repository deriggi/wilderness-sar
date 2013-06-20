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
import raster.domain.agent.FSMFactory;
import raster.domain.agent.FSMFactory.MachineName;
import webdomain.WebBehaviourFactory;
import webdomain.WebBehaviour;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import util.GsonGetter;

/**
 *
 * @author Johnny
 */
@Path("/behaviour")
public class BehaviourResource {


    private static final Logger log = Logger.getLogger(BehaviourResource.class.getCanonicalName());

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public String getBehaviours() {
        // Collection<FSMFactory.MachineName> fsms = FSMFactory.MachineName.values();
        List<WebBehaviour> fsmList = new ArrayList<WebBehaviour>();
        fsmList  =  WebBehaviourFactory.toList(Arrays.asList(FSMFactory.MachineName.values()));
        
        return GsonGetter.get().toJson(fsmList);
    }

}
