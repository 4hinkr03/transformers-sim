package ryan.transformers;

import prins.simulator.Config;
import prins.simulator.model.Location;

import java.util.Random;

public class TransformerConfig {

    private static final int RANDOM_SEED = 28;

    public static final int MAX_TRANSFORMERS = 30;
    public static final int MAX_PATH = 30;
    public static  final int MAX_SIZE = 5;
    public static final double MUTATION_PROBABILITY = 0.01;
    public static Random RANDOM = new Random(RANDOM_SEED);

    public static final Location AUTOBOT_START_LOCATION = new Location(3, 15);
    public static final Location ENERGY_SOURCE_LOCATION = new Location(27, 15);

    public static void resetRandom() {
        RANDOM = new Random(RANDOM_SEED);
    }

    static {
        Config.max_simulation_speed = 2;
    }

}
