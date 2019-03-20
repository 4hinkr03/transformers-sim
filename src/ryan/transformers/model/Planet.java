package ryan.transformers.model;

import prins.simulator.Config;
import prins.simulator.model.Agent;
import prins.simulator.model.Environment;
import prins.simulator.model.Location;

public class Planet extends Environment {

    @Override
    public void clear() {

    }

    @Override
    public Agent getAgent(Location location) {
        return null;
    }

    @Override
    public int getHeight() {
        return Config.world_height;
    }

    @Override
    public int getWidth() {
        return Config.world_width;
    }

    @Override
    public void setAgent(Agent agent, Location location) {

    }
}
