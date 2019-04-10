package ryan.transformers;

import static ryan.transformers.TransformerConfig.BLOCK_AREAS;
import static ryan.transformers.TransformerConfig.RANDOM;

import prins.simulator.Simulator;
import prins.simulator.model.Agent;
import prins.simulator.model.Location;
import prins.simulator.view.Gui;
import ryan.transformers.model.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class TransformerSim extends Simulator {

    private Planet planet;
    private Gui gui;
    private List<AutoBot> bots;
    private List<Block> blocks;
    private List<AllSpark> allSparks;
    private List<Decepticon> decepticons;
    private int generation;

    public TransformerSim() {
        this.planet = new Planet();
        this.gui = new Gui(planet);
        this.bots = new ArrayList<>();
        this.blocks = new ArrayList<>();
        this.allSparks = new ArrayList<>();
        this.decepticons = new ArrayList<>();
        this.generation = 1;

        gui.registerAgentColors(AutoBot.class, Color.GREEN);
        gui.registerAgentColors(AllSpark.class, Color.RED);
        gui.registerAgentColors(Block.class, Color.BLACK);
        gui.registerAgentColors(Decepticon.class, Color.PINK);

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
        if (step % TransformerConfig.MAX_PATH == 0) {
            //System.out.println("generation = " + generation);
            bots.forEach(bot -> {
                bot.setFitness(planet.calculateFitness(allSparks, bot));
                bot.resetCollisions();
            });

            // selection - select from gene pool
            List<AutoBot> rocketSelection = selection();

            // clear down the environment
            clear();

            // add the new rockets selection
            addAll(rocketSelection);

            //reset step and increment generation
            generation++;
            step = 0;
        }

        //autobots act
        for (AutoBot bot : bots) {
            if (step % bot.getSize() == 0) {
                bot.act(planet);
            }
        }

        //decepticons act
        decepticons.forEach(decepticon -> decepticon.act(planet));

        //set allspark locations - ensure it remains on the sim
        allSparks.forEach(allSpark -> planet.setAgent(allSpark, allSpark.getLocation()));

        //set blocks locations - so they are displayed on the sim
        blocks.forEach(block -> planet.setAgent(block, block.getLocation()));
    }

    /**
     * @return the selection of AutoBots from the gene pool
     */
    private List<AutoBot> selection() {
        Random random = new Random();
        List<AutoBot> genePool = generateGenePool();
        List<AutoBot> selection = new ArrayList<>();
       // System.out.println("genepool=" + genePool.size());
        if (!genePool.isEmpty()) {
            for (int i = 0; i < TransformerConfig.MAX_TRANSFORMERS; i++) {
                AutoBot randomAutoBot = genePool.get(random.nextInt(genePool.size()));
                selection.add(reproduce(randomAutoBot));
            }
        }
        return selection;
    }

    /**
     * Generates a gene pool based on the bots fitness
     *
     * @return a gene pool of AutoBots proportioned by their respective fitness
     */
    private List<AutoBot> generateGenePool() {
        List<AutoBot> genePool = new ArrayList<>();
        int totalSize = 0;
        for (AutoBot bot : bots) {
            // calculate the number of rockets to add to the gene pool
            // the greater the fitness, the more rockets added to the gene pool
            int botSize = (int) (bot.getFitness() * 100 * TransformerConfig.MAX_TRANSFORMERS);
            // add the number of calculated rockets to the gene pool
            for (int i = 0; i < botSize; i++) {
                genePool.add(bot);
                totalSize += bot.getSize();
            }
        }
        double avg = ((double) totalSize) / genePool.size();
        //System.out.println("avg size=" + avg);
        return genePool;
    }

    /**
     * Reproduce a baby AutoBot based on the genes of the parents (paths)
     *
     * @param parent AutoBot
     * @return a baby AutoBot with a path generated based on the parents genes
     */
    private AutoBot reproduce(AutoBot parent) {
        AutoBot baby = new AutoBot(TransformerConfig.AUTOBOT_START_LOCATION, parent.getSize());

        List<Location> mergedPath = mergePath(baby.getLocation(), parent);
        baby.setPath(mergedPath);
        return baby;
    }

    /**
     * Merge the paths of the mum and dad to produce a path based on the parents genes
     *
     * @param start  location
     * @param parent AutoBo
     * @return a path made up of both parents paths as this is makes up their genes
     */
    private List<Location> mergePath(Location start, AutoBot parent) {
        List<Location> path = new ArrayList<>();
        path.add(start);

        for (int i = 1; i < parent.getMaxPathSize(); i++) {
            List<Location> parentPath = parent.getPath();
            Location nextParentLocation = parentPath.get(i);
            Location currentParentLocation = parentPath.get(i - 1);
            Location currentLocation = path.get(path.size() - 1);
            Location nextLocation;

            if (planet.getAdjacentLocationMatches(currentLocation, Block.class).isEmpty()) {
                int xDiff = nextParentLocation.getX() - currentParentLocation.getX();
                int yDiff = nextParentLocation.getY() - currentParentLocation.getY();

                int nextX = currentLocation.getX() + xDiff;
                int nextY = currentLocation.getY() + yDiff;

                if (mutate()) {
                    Location mutateLoc = planet.getAdjacentLocation(currentLocation);
                    if (mutateLoc != null) {
                        nextX = mutateLoc.getX();
                        nextY = mutateLoc.getY();
                    }
                }

                if (planet.withinBounds(nextX, nextY)) {
                    nextLocation = new Location(nextX, nextY);
                } else {
                    nextLocation = currentLocation;
                }
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
        //populate all spark
        for (Location location : TransformerConfig.ALL_SPARK_LOCATIONS) {
            AllSpark allSpark = new AllSpark(location);
            allSparks.add(allSpark);
            planet.setAgent(allSpark, allSpark.getLocation());
        }

        //populate autobots
        for (int i = 0; i < TransformerConfig.MAX_TRANSFORMERS; i++) {
            AutoBot bot = new AutoBot(TransformerConfig.AUTOBOT_START_LOCATION);
            bot.generateRandomPath(planet);
            bots.add(bot);
        }

        //populate decepticons
        decepticons.add(new Decepticon(new Location(17, 17)));

        //populate blocks
        populateObstacleCourse();
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
     *
     * @param bots all bots to be added to the planet
     */
    private void addAll(List<AutoBot> bots) {
        this.bots.addAll(bots);
        this.bots.forEach(bot -> planet.setAgent(bot, bot.getLocation()));
    }

    /**
     * @return whether the path should be mutate
     */
    private boolean mutate() {
        return RANDOM.nextDouble() <= TransformerConfig.MUTATION_PROBABILITY;
    }

    private void populateObstacleCourse() {
        for(BlockArea area : BLOCK_AREAS) {
            blocks.addAll(area.getArea());
        }
    }
}
