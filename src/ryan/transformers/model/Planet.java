package ryan.transformers.model;

import prins.simulator.Config;
import prins.simulator.model.Agent;
import prins.simulator.model.Environment;
import prins.simulator.model.Location;
import ryan.transformers.TransformerConfig;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Planet extends Environment {

    private Agent[][] planet;

    public Planet() {
        clear();
    }

    @Override
    public void clear() {
        planet = new Agent[getHeight()][getWidth()];
    }

    @Override
    public Agent getAgent(Location location) {
        return planet[location.getY()][location.getX()];
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
        planet[location.getY()][location.getX()] = agent;
    }

    public static int randomInt(int min, int max) {
        Random random = TransformerConfig.RANDOM;
        return random.nextInt((max - min) + 1) + min;
    }

    public Location getAdjacentLocation(Location location) {
        List<Location> locations = getAdjacentLocations(location);
        if(!locations.isEmpty()) {
            return locations.get(randomInt(0, locations.size()-1));
        }
        return null;
    }

    public void clearWorld() {
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                Location location = new Location(x, y);
                if(getAgent(location) instanceof AutoBot) {
                    setAgent(null, new Location(x, y));
                }
            }
        }
    }

    public boolean withinBounds(int x, int y) {
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    public double calculateFitness(Location allSpark, Location location) {
        int xDiff = location.getX() - allSpark.getX();
        int yDiff = location.getY() - allSpark.getY();
        double hypotenuse = Math.hypot(xDiff, yDiff);
        return 1 / hypotenuse;
    }

    private List<Location> getAdjacentLocations(Location location) {
        List<Location> adjacentLocations = new ArrayList<>();
        int currentX = location.getX();
        int currentY = location.getY();

        for(int xOffset = -1; xOffset <= 1; xOffset++) {
            for(int yOffset = -1; yOffset <= 1; yOffset++) {
                int x = currentX + xOffset;
                int y = currentY + yOffset;
                Location adjacentLocation = new Location(x, y);
                if(withinBounds(x, y)) {
                    if(!location.matches(adjacentLocation)) {
                        adjacentLocations.add(adjacentLocation);
                    }
                }
            }
        }
        return adjacentLocations;
    }
}
