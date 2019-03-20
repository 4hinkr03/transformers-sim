package ryan.transformers.model;

import prins.simulator.model.Agent;
import prins.simulator.model.Location;
import ryan.transformers.TransformerConfig;

public class EnergySource extends Agent {

    public EnergySource() {
        super(TransformerConfig.ENERGY_SOURCE_LOCATION);
    }
}
