package ryan.transformers;

import static ryan.transformers.TransformerConfig.RANDOM;

import prins.simulator.Simulator;
import prins.simulator.model.Location;
import prins.simulator.view.Gui;
import ryan.transformers.model.AllSpark;
import ryan.transformers.model.AutoBot;
import ryan.transformers.model.Planet;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TransformerSim extends Simulator {

    private Planet planet;
    private Gui gui;
    private List<AutoBot> bots;
    private AllSpark allSpark;


    public TransformerSim() {
        this.planet = new Planet();
        this.gui = new Gui(planet);
        this.bots = new ArrayList<>();

        gui.registerAgentColors(AutoBot.class, Color.GREEN);
        gui.registerAgentColors(AllSpark.class, Color.RED);

        populate();
    }

    @Override
    protected void prepare() {
        gui.addPropertyChangeListener(this);
    }

    @Override
    protected void render() {
        gui.setStep(step);
        gui.render();
    }

    @Override
    protected void reset() {
        planet.clear();
        bots.clear();
        TransformerConfig.resetRandom();
        populate();
    }

    @Override
    protected void update() {
        if (step % TransformerConfig.MAX_PATH  == 0) {
            bots.forEach(bot -> {
                int xDiff = bot.getLocation().getX() - allSpark.getLocation().getX();
                int yDiff = bot.getLocation().getY() - allSpark.getLocation().getY();
                double hypotenuse = Math.hypot(xDiff, yDiff);
                double fitness = 1 / hypotenuse;
                bot.setFitness(fitness);
            });
            // selection - select from gene pool
            List<AutoBot> rocketSelection = selection();

            // clear down the environment
            clear();

            // add the new rockets selection
            addAll(rocketSelection);
        }

        for(AutoBot bot : bots) {
            bot.act(planet);
        }
    }

    /**
     * @return the selection of AutoBots from the gene pool
     */
    private List<AutoBot> selection() {
        Random random = new Random();
        List<AutoBot> genePool = generateGenePool();
        System.out.println("genepool=" + genePool.size());
        List<AutoBot> selection = new ArrayList<>();

        if(!genePool.isEmpty()) {
            for(int i = 0; i < TransformerConfig.MAX_TRANSFORMERS; i++) {
                AutoBot randomAutoBot = genePool.get(random.nextInt(genePool.size()));
                selection.add(reproduce(randomAutoBot));
            }
        }


        return selection;
    }

    /**
     * Generates a gene pool based on the bots fitness
     * @return a gene pool of AutoBots proportioned by their respective fitness
     */
    private List<AutoBot> generateGenePool() {
        List<AutoBot> genePool = new ArrayList<>();
        System.out.println("AutoBots size=" + bots.size());
        bots.forEach(rocket -> {
            // calculate the number of rockets to add to the gene pool
            // the greater the fitness, the more rockets added to the gene pool
            int rocketSize = (int) (rocket.getFitness() * 100 * TransformerConfig.MAX_TRANSFORMERS);
            // add the number of calculated rockets to the gene pool
            //System.out.println("fitness=" + rocket.getFitness() + ":" + rocketSize + " number of rockets");
            for(int i = 0; i < rocketSize; i++) {
                genePool.add(rocket);
            }
        });
        return genePool;
    }

    /**
     * Reproduce a baby AutoBot based on the genes of the parents (paths)
     * @param parent AutoBot
     * @return a baby AutoBot with a path generated based on the parents genes
     */
    private AutoBot reproduce(AutoBot parent) {
        AutoBot baby = new AutoBot(TransformerConfig.AUTOBOT_START_LOCATION);

        List<Location> mergedPath = mergePath(baby.getLocation(), parent);
        baby.setPath(mergedPath);
        return baby;
    }

    /**
     * Merge the paths of the mum and dad to produce a path based on the parents genes
     * @param start location
     * @param parent AutoBo
     * @return a path made up of both parents paths as this is makes up their genes
     */
    private List<Location> mergePath(Location start, AutoBot parent) {
        List<Location> path = new ArrayList<>();
        path.add(start);

        for(int i = 1; i < TransformerConfig.MAX_PATH; i++) {
            List<Location> parentPath = parent.getPath();
            Location nextParentLocation = parentPath.get(i);
            Location currentParentLocation = parentPath.get(i-1);
            int xDiff = nextParentLocation.getX() - currentParentLocation.getX();
            int yDiff = nextParentLocation.getY() - currentParentLocation.getY();

            Location currentLocation = path.get(path.size()-1);
            int nextX = currentLocation.getX() + xDiff;
            int nextY = currentLocation.getY() + yDiff;

            nextX = mutate(nextX);
            nextY = mutate(nextY);

            Location nextLocation = null;

            if(planet.withinBounds(nextX, nextY)) {
                nextLocation = new Location(nextX, nextY);
            } else {
                nextLocation = currentLocation;
            }

            path.add(nextLocation);

        }
        return path;
    }


    /**
     * Populate the planet with AutoBots and single AllSpark
     */
    private void populate() {
        this.allSpark = new AllSpark();
        planet.setAgent(allSpark, allSpark.getLocation());

        for(int i = 0; i < TransformerConfig.MAX_TRANSFORMERS; i++) {
            AutoBot bot = new AutoBot(TransformerConfig.AUTOBOT_START_LOCATION);
            bot.generateRandomPath(planet);
            bots.add(bot);
        }
    }

    /**
     * Clears down the bots list and planet so the simulation can repeat
     */
    private void clear() {
        bots.forEach(bot -> planet.setAgent(null, bot.getLocation()));
        bots.clear();
        planet.clearWorld();

    }

    /**
     * Adds all bots passed into the environment
     * @param bots all bots to be added to the planet
     */
    private void addAll(List<AutoBot> bots) {
        this.bots.addAll(bots);
        this.bots.forEach(bot -> planet.setAgent(bot, bot.getLocation()));
    }

    /**
     * Return a mutated coordinate based on a low probability
     * @param coordinate to be mutated
     * @return a coordinate that may have been mutated (altered)
     */
    private int mutate(int coordinate) {
        //invert the coordinate for mutation
        return RANDOM.nextDouble() >= 0.02 ? coordinate : coordinate + RANDOM.nextInt(3) - 1;
    }


}
