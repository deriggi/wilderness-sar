/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strategy.updater;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Johnny
 */
public class MemoryData implements Comparable<MemoryData> {

    public MemoryData(String name, float data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MemoryData other = (MemoryData) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (Float.floatToIntBits(this.data) != Float.floatToIntBits(other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 97 * hash + Float.floatToIntBits(this.data);
        return hash;
    }
    private String name;
    private float data;

    public float getData() {
        return data;
    }

    public void setData(float data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(MemoryData o) {
        if (o == null) {
            return 1;
        }
        if (o.equals(this)) {
            return 0;
        }

        if (o.getData() > getData()) {
            return -1;
        }

        if (o.getData() < getData()) {
            return 1;
        }
        return 0;
    }

    public static List<MemoryData> toMemoryData(HashMap<String, Float> mem) {

        Set<String> keys = mem.keySet();
        List<MemoryData> memoryData = new ArrayList<MemoryData>();

        for (String s : keys) {
            memoryData.add(new MemoryData(s, mem.get(s)));
        }

        Collections.sort(memoryData);

        return memoryData;

    }
}
