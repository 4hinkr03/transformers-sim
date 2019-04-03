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
    private double fitness;
    private int size;
    private int collisions;
    private boolean reachedEnergySource;

    public AutoBot(Location location) {
        this(location, RANDOM.nextInt(4) + 1);
    }

    public AutoBot(Location location, int size) {
        super(location);
        this.path = new ArrayList<>();
        //size between 1 and 5
        this.size = size;
        this.reachedEnergySource = false;
        collisions = 0;

    }

    /**
     * Generate a random for AutoBot within the planet
     * @param planet - AutoBot environment
     */
    public void generateRandomPath(Planet planet) {
        // clear and add the location of the bot to start off the path
        path.clear();
        path.add(location);
        for (int i = 1; i < getMaxPathSize(); i++) {
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
                planet.setAgent(null, location);
                Location nextLocation = path.get(nextLocationIndex);
                if(!planet.isBlock(nextLocation)) {
                    location = nextLocation;
                    planet.setAgent(this, location);
                } else {
                    incrementCollisions();
                }

            }
        }

    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public int getSize() {
        return size;
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

    public int getMaxPathSize() {
        return TransformerConfig.MAX_PATH / getSize();
    }

    public void resetCollisions() {
        this.collisions = 0;
    }

    public void incrementCollisions() {
        this.collisions++;
    }

    public int getCollisions() {
        return collisions;
    }
}
