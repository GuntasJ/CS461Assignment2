package genetics.math;

import java.util.*;

public class ProbabilityDistributor<T extends Probability> {

    private final List<Double> probabilities;
    private final List<T> items;
    private final static SplittableRandom random = new SplittableRandom();

    public ProbabilityDistributor(List<T> items) {
        this.items = items;
        probabilities = items.stream()
                .map(Probability::getProbability)
                .toList();
    }

    public T pick(int failSafe) {
        for (int i = 0; i < probabilities.size(); i++) {
            if (random.nextDouble(1) <= probabilities.get(i)) {
                return items.get(i);
            }
        }
        return items.get(failSafe);
    }


}
