package org.abno.logic.components;

public class Connector extends Component {

    public Connector() {
        super();
        this.setPrice(100);
    }

    public boolean isConnectedToEnergySource(Graph graph) {
        for (Component connected : graph.getConnections(this)) {
            if (connected instanceof EnergySource) {
                return true;
            }
        }
        return false;
    }
}
