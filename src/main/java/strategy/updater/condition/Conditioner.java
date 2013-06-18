/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater.condition;

import java.util.EnumMap;

/**
 *
 * @author Johnny
 */
public class Conditioner {

    private static interface ConditionerRunner {

        public boolean runIt(Float a, Float b);

        public boolean runIt(Integer a, Integer b);

        public boolean runIt(Double a, Double b);

    }
    
    private static class GreaterThanRunner<T extends Number> implements ConditionerRunner {
        
        @Override
        public boolean runIt(Float a, Float b) {
            return Conditioner.greaterThan(a, b);
        }

        @Override
        public boolean runIt(Integer a, Integer b) {
            return Conditioner.greaterThan(a, b);
        }

        @Override
        public boolean runIt(Double a, Double b) {
            return Conditioner.greaterThan(a, b);
        }
    }
    

    private static class GreaterThanEqualRunner implements ConditionerRunner {

        @Override
        public boolean runIt(Float a, Float b) {
            return Conditioner.greaterThanOrEqual(a, b);
        }

        @Override
        public boolean runIt(Integer a, Integer b) {
            return Conditioner.greaterThanOrEqual(a, b);
        }

        @Override
        public boolean runIt(Double a, Double b) {
            return Conditioner.greaterThanOrEqual(a, b);
        }
    }

    private static class LessThanRunner implements ConditionerRunner {

        @Override
        public boolean runIt(Float a, Float b) {
            return Conditioner.lessThan(a, b);
        }

        @Override
        public boolean runIt(Integer a, Integer b) {
            return Conditioner.lessThan(a, b);
        }

        @Override
        public boolean runIt(Double a, Double b) {
            return Conditioner.lessThan(a, b);
        }
    }

    private static class LessThanEqualRunner implements ConditionerRunner {

        @Override
        public boolean runIt(Float a, Float b) {
            return Conditioner.lessThanOrEqual(a, b);
        }

        @Override
        public boolean runIt(Integer a, Integer b) {
            return Conditioner.lessThanOrEqual(a, b);
        }

        @Override
        public boolean runIt(Double a, Double b) {
            return Conditioner.lessThanOrEqual(a, b);
        }
    }

    private static class EqualToRunner implements ConditionerRunner {

        @Override
        public boolean runIt(Float a, Float b) {
            return Conditioner.equalTo(a, b);
        }

        @Override
        public boolean runIt(Integer a, Integer b) {
            return Conditioner.equalTo(a, b);
        }

        @Override
        public boolean runIt(Double a, Double b) {
            return Conditioner.equalTo(a, b);
        }
    }

    public static enum Condish {

        GT, LT, GTE, LTE, EQ;
    }
    private static final EnumMap<Condish, ConditionerRunner> map;

    
    static {
        map = new EnumMap(Condish.class);
        map.put(Condish.GT, new GreaterThanRunner());
        map.put(Condish.LT, new LessThanRunner());
        map.put(Condish.GTE, new GreaterThanEqualRunner());
        map.put(Condish.LTE, new LessThanEqualRunner());
        map.put(Condish.EQ, new EqualToRunner());
    }

    
    public static boolean doComparison(Condish c, Float a, Double b) {
        return map.get(c).runIt((double) a, b);
    }
    
    public static boolean doComparison(Condish c, Integer a, Integer b) {
        return map.get(c).runIt( a, b);
    }
    
    

    public static boolean doComparison(Condish c, Double a, Float b) {
        return map.get(c).runIt(a, (double) b);
    }
    
    public static boolean doComparison(Condish c, Double a, Integer b) {
        return map.get(c).runIt(a, (double) b);
    }

    public static boolean doComparison(Condish c, Float a, Float b) {
        return map.get(c).runIt(a, b);
    }

    public static boolean doComparison(Condish c, Double a, Double b) {
        return map.get(c).runIt(a, b);
    }

    private static boolean greaterThan(Float a, Float b) {
        return a > b;
    }
    
    private static boolean greaterThan(Integer a, Integer b) {
        return a > b;
    }

    private static boolean greaterThan(Double a, Double b) {
        return a > b;
    }

    private static boolean lessThan(Float a, Float b) {
        return a < b;
    }
    
    private static boolean lessThan(Integer a, Integer b) {
        return a < b;
    }

    private static boolean lessThan(Double a, Double b) {
        return a < b;
    }

    private static boolean greaterThanOrEqual(Float a, Float b) {
        return a >= b;
    }
    
    private static boolean greaterThanOrEqual(Integer a, Integer b) {
        return a >= b;
    }
    

    private static boolean greaterThanOrEqual(Double a, Double b) {
        return a >= b;
    }

    private static boolean lessThanOrEqual(Double a, Double b) {
        return a <= b;
    }
    
    private static boolean lessThanOrEqual(Integer a, Integer b) {
        return a <= b;
    }

    private static boolean equalTo(Double a, Double b) {
        return a == b;
    }

    private static boolean equalTo(Float a, Float b) {
        return a == b;
    }

    private static boolean equalTo(Integer a, Integer b) {
        return a == b;
    }

    private static boolean lessThanOrEqual(Float a, Float b) {
        return a <= b;
    }
}
