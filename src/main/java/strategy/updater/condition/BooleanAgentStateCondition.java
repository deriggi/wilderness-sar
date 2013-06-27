/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import java.util.logging.Logger;
import raster.domain.agent.AgentPropertyManager;
import raster.domain.agent.VectorAgent;

/**
 *
 * @author Johnny
 */
public class BooleanAgentStateCondition extends SkelatalStateCondition {

    private AgentPropertyManager.AgentProperty agentProp;
//    private T compareToDouble = null;
    private static final Logger log = Logger.getLogger(BooleanAgentStateCondition.class.getName());

    public BooleanAgentStateCondition(AgentPropertyManager.AgentProperty p) {
        this.agentProp = p;
    }

    @Override
    public boolean checkState(VectorAgent va) {
        boolean checkedCondition = false;
        checkedCondition = AgentPropertyManager.getBooleanAgentProperty(agentProp, va);

        return checkedCondition;

    }
}
