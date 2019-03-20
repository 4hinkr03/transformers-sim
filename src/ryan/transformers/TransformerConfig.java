package ryan.transformers;

import prins.simulator.model.Location;

import java.util.Random;

public class TransformerConfig {

    private static final int RANDOM_SEED = 28;

    public static final int MAX_TRANSFORMERS = 10;
    public static final int MAX_PATH = 30;
    public static  final int MAX_SIZE = 5;
    public static Random RANDOM = new Random(RANDOM_SEED);

    public static final Location AUTOBOT_START_LOCATION = new Location(1, 15);
    public static final Location ENERGY_SOURCE_LOCATION = new Location(29, 15);

    public static void resetRandom() {
        RANDOM = new Random(RANDOM_SEED);
    }


}
