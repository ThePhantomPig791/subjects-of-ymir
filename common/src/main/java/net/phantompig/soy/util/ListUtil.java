package net.phantompig.soy.util;

import net.minecraft.util.Tuple;

import java.util.Arrays;
import java.util.List;

public class ListUtil {
    public static <T> T getRandom(List<T> list) {
        return list.get((int) (list.size() * Math.random()));
    }

    public static <T> T getRandomWeighted(List<Tuple<T, Double>> weightedList) {
        double totalWeight = 0;
        for (Tuple<T, Double> tFloatTuple : weightedList) {
            totalWeight += tFloatTuple.getB();
        }
        double[] weights = new double[weightedList.size()];
        for (int i = 0; i < weights.length; i++) {
            for (int j = i; j < weights.length; j++) {
                weights[j] += weightedList.get(i).getB() / totalWeight;
            }
        }
        System.out.println(Arrays.toString(weights));
        for (int i = 0; i < weights.length; i++) {
            if (Math.random() < weights[i]) return weightedList.get(i).getA();
        }
        return weightedList.get(weightedList.size() - 1).getA();
    }
}
