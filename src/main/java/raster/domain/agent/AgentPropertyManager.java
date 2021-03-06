/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package raster.domain.agent;

import java.util.EnumMap;
import middletier.RasterConfig;
import middletier.RasterLoader;

/**
 *
 * @author Johnny
 */
public  class AgentPropertyManager {

    public interface AgentPropertyGetter<T> {
        public T getAgentProperty(VectorAgent agent);
    };

//    private interface DoubleAgentPropertyGetter extends AgentPropertyGetter {
//
//        @Override
//        public Double getAgentProperty(VectorAgent agent);
//    }
//
//    private interface IntegerAgentPropertyGetter extends AgentPropertyGetter {
//
//        @Override
//        public Integer getAgentProperty(VectorAgent agent);
//    }

    private static class LongitudePropertyGetter implements AgentPropertyGetter<Double> {

        @Override
        public Double getAgentProperty(VectorAgent agent) {
            if (agent == null) {
                return null;
            }

            return RasterLoader.get(RasterConfig.BIG).getData().getLonLat(agent.getLocation()[0], agent.getLocation()[1])[0];
        }
    }
    
    private static class NamePropertyGetter implements AgentPropertyGetter<String> {

        @Override
        public String getAgentProperty(VectorAgent agent) {
            if (agent == null) {
                return null;
            }

            return "rick james";
        }
    }
    
    private static class NextStepWithinBoundsProeprtyGetter implements AgentPropertyGetter<Boolean> {

        @Override
        public Boolean getAgentProperty(VectorAgent agent) {
            if (agent == null){
                return false;
            }
            return agent.isNextStepOutOfBounds();
        }

        
    }
    
    

    private static class LatitudePropertyGetter implements AgentPropertyGetter<Double> {

        @Override
        public Double getAgentProperty(VectorAgent agent) {
            if (agent == null) {
                return null;
            }

            return RasterLoader.get(RasterConfig.BIG).getData().getLonLat(agent.getLocation()[0], agent.getLocation()[1])[1];
        }
    }
    
   

    private static class StepsTakenPropertyGetter implements AgentPropertyGetter {

        @Override
        public Integer getAgentProperty(VectorAgent agent) {
            return agent.getStepsTaken();
        }

    }

    public static enum AgentProperty {

        LATITUDE, LONGITUDE, COLUMN, ROW, STACK_SIZE, STEPS_TAKEN, NEXT_STEP_OUT_OF_BOUNDS;
    }

    private static final EnumMap<AgentProperty, AgentPropertyGetter<? extends Number>> numberMap;
    
    private static final EnumMap<AgentProperty, AgentPropertyGetter<Boolean>> booleanMap;

    static {
        numberMap = new EnumMap<AgentProperty,  AgentPropertyGetter<? extends Number>>(AgentProperty.class);
        numberMap.put(AgentProperty.LONGITUDE, new LongitudePropertyGetter());
        numberMap.put(AgentProperty.LATITUDE, new LatitudePropertyGetter());
        numberMap.put(AgentProperty.STEPS_TAKEN, new StepsTakenPropertyGetter());
        
        booleanMap = new EnumMap<AgentProperty,  AgentPropertyGetter<Boolean>>(AgentProperty.class);
        booleanMap.put(AgentProperty.NEXT_STEP_OUT_OF_BOUNDS, new NextStepWithinBoundsProeprtyGetter());
    }
    
    public static Integer getIntegerAgentProperty(AgentProperty prop, VectorAgent agent){
        return numberMap.get(prop).getAgentProperty(agent).intValue();
    }
    
    public static Double getDoubleAgentProperty(AgentProperty prop, VectorAgent agent){
        return numberMap.get(prop).getAgentProperty(agent).doubleValue();
    }
    
    public static boolean getBooleanAgentProperty(AgentProperty prop, VectorAgent agent){
        return booleanMap.get(prop).getAgentProperty(agent);
    }
}
