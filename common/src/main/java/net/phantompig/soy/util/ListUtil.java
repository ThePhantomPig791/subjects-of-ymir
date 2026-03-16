package net.phantompig.soy.util;

import net.minecraft.util.Tuple;

import java.util.List;

public class ListUtil {
    public static <T> T getRandom(List<T> list) {
        return list.get((int) (list.size() * Math.random()));
    }

    // untested sorry
    public static <T> T getRandomWeighted(List<Tuple<T, Double>> weightedList) {
        double totalWeight = 0;
        for (Tuple<T, Double> tFloatTuple : weightedList) {
            totalWeight += tFloatTuple.getB();
        }
        double[] weights = new double[weightedList.size()];
        double r = Math.random() * totalWeight;
        for (int i = 0; i < weights.length; i++) {
            for (int j = i; j < weights.length; j++) {
                weights[j] += weightedList.get(i).getB() / totalWeight;
            }
        }
        for (int i = 0; i < weights.length; i++) {
            if (r < weights[i]) return weightedList.get(i).getA();
        }
        return weightedList.get(weightedList.size() - 1).getA();

    }
}
