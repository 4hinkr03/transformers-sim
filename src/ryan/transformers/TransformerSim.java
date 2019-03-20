package ryan.transformers;

import prins.simulator.Simulator;
import prins.simulator.view.Gui;
import ryan.transformers.model.Planet;

public class TransformerSim extends Simulator {

    private Planet planet;
    private Gui gui;

    public TransformerSim() {
        this.planet = new Planet();
        this.gui = new Gui(planet);

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

    }

    @Override
    protected void update() {

    }
}
