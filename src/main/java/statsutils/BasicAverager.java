/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package statsutils;

import java.util.Collection;
import java.util.Iterator;


public class BasicAverager {

    private double sum = 0;
    private int count = 0;
    private Double max = null;
    private Double min = null;
    private Double avg = null;

    public Double getAvg() {
        return avg;
    }

    public Double getMax() {
        return max;
    }

    public Double getMin() {
        return min;
    }

    public void update(double val) {
        setMax(val);
        setMin(val);
        count++;
        sum += val;
        avg = sum / count;
    }

    private void setMax(double val) {
        if (max == null) {
            max = val;
        } else if (max < val) {
            max = val;
        }
    }

    private void setMin(double val) {
        if (min == null) {
            min = val;
        } else if (min > val) {
            min = val;
        }
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public static double averageTheAveragers(Collection<BasicAverager> averagers) {
        Iterator<BasicAverager> baIterator = averagers.iterator();
        BasicAverager primaryAverager = new BasicAverager();
        while (baIterator.hasNext()) {
            BasicAverager ba = baIterator.next();
            primaryAverager.update(ba.getAvg());
        }
        return primaryAverager.getAvg();

    }

     public static double maxOfTheAveragers(Collection<BasicAverager> averagers) {
        Iterator<BasicAverager> baIterator = averagers.iterator();
        BasicAverager primaryAverager = new BasicAverager();
        while (baIterator.hasNext()) {
            BasicAverager ba = baIterator.next();
            primaryAverager.update(ba.getMax());
        }
        return primaryAverager.getMax();

    }

      public static double minOfTheAveragers(Collection<BasicAverager> averagers) {
        Iterator<BasicAverager> baIterator = averagers.iterator();
        BasicAverager primaryAverager = new BasicAverager();
        while (baIterator.hasNext()) {
            BasicAverager ba = baIterator.next();
            primaryAverager.update(ba.getMin());
        }
        return primaryAverager.getMin();

    }
}

