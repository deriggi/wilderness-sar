/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package middletier;

import raster.domain.agent.SkelatalAgent;
import util.FileExportHelper;

/**
 *
 * @author Johnny
 */
public class AgentDataExtractor {
    private static final String COMMA = ",";
    
    public static String getLineFromAgent(SkelatalAgent agent){
        
        StringBuilder builder  = new StringBuilder();
        
        builder.append(agent.getLongitude());
        builder.append(COMMA);
        
        builder.append(agent.getLatitude());
        builder.append(COMMA);
        
        builder.append(agent.isStuck());
        builder.append(COMMA);
        
        builder.append(agent.getDirection());
        builder.append(COMMA);
        
        builder.append(agent.averageDistanceLastXPoints(50));
        builder.append(COMMA);
        
        builder.append(agent.getDotProductBufferAverage());
        builder.append(FileExportHelper.LINE_SEPARATOR);
        
        
        return builder.toString();
        
    }
    
}
