package ryan.transformers;

import prins.simulator.Config;
import prins.simulator.model.Location;
import ryan.transformers.model.BlockArea;

import java.util.Random;

public class TransformerConfig {

    private static final int RANDOM_SEED = 28;

    public static final int MAX_TRANSFORMERS = 200;
    public static final int MAX_PATH = 30;
    public static final int MAX_SIZE = 5;
    public static final double MUTATION_PROBABILITY = 0.005;
    public static Random RANDOM = new Random(RANDOM_SEED);

    public static final Location AUTOBOT_START_LOCATION = new Location(2, 16);
    public static final Location[] ALL_SPARK_LOCATIONS = {
            new Location(27, 3),
            new Location(27, 27),
    };

    public static final BlockArea[] BLOCK_AREAS = {
            //new BlockArea(0, 5, 10, 1),
            new BlockArea(15, 8, 1, 15),
           // new BlockArea(20, 24, 10, 1)*/
           /*new BlockArea(0, 0, 30, 1),
            new BlockArea(0, 10, 25, 1),
            new BlockArea(5, 0, 2, 5),
            new BlockArea(10, 5, 2, 5)*/
    };

    public static void resetRandom() {
        RANDOM = new Random(RANDOM_SEED);
    }

    static {
        Config.max_simulation_speed = 2;
    }
}
