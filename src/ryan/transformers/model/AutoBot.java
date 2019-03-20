package ryan.transformers.model;

import static ryan.transformers.TransformerConfig.RANDOM;
import static ryan.transformers.TransformerConfig.MAX_SIZE;

import prins.simulator.model.Agent;
import prins.simulator.model.Location;
import ryan.transformers.TransformerConfig;

import java.util.ArrayList;
import java.util.List;

public class AutoBot extends Agent {

    private List<Location> path;
    private double fitness, size;
    private boolean reachedEnergySource;

    public AutoBot(Location location) {
        super(location);
        this.path = new ArrayList<>();
        this.size = RANDOM.nextDouble() * MAX_SIZE;
        this.reachedEnergySource = false;
    }

    /**
     * Generate a random for AutoBot within the planet
     * @param planet - AutoBot environment
     */
    public void generateRandomPath(Planet planet) {
        // clear and add the location of the bot to start off the path
        path.clear();
        path.add(location);
        for (int i = 1; i < TransformerConfig.MAX_PATH; i++) {
            // retrieve the last location within the path
            Location lastLocation = path.get(path.size()-1);
            // calculate the next location using the last known location from the path
            Location adjLocation = planet.getAdjacentLocation(lastLocation);
            // add the new adj location to the path
            path.add(adjLocation);
        }
    }

    /**
     * Procedure for the AutoBot to act within said environment
     * @param planet - AutoBot environment
     */
    public void act(Planet planet) {
        if(!hasReachedEnergySource()) {
            int nextLocationIndex = path.indexOf(location) + 1;
            if(nextLocationIndex < path.size()) {
                //space.setAgent(new RocketTrail(location), location);
                planet.setAgent(null, location);
                location = path.get(nextLocationIndex);
                planet.setAgent(this, location);
            }
        }

    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getSize() {
        return size;
    }

    public double getSpeed() {
        return 1 / size;
    }

    public List<Location> getPath() {
        return path;
    }

    public void setPath(List<Location> path) {
        this.path = path;
    }

    public boolean hasReachedEnergySource() {
        return reachedEnergySource;
    }

    public void setReachedEnergySource(boolean reachedEnergySource) {
        this.reachedEnergySource = reachedEnergySource;
    }
}
