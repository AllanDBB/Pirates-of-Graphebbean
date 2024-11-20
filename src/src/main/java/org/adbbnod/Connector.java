package org.adbbnod;

import jdk.internal.net.http.common.Pair;

import java.util.ArrayList;

public class Connector extends Component{

    Connector (){
        super();
        this.setPrice(100);
    }

    public void addConnection(Component component){
        if (!this.getConnections().contains(component)) {
            this.getConnections().add(component);
        }
    }

    public void removeConnection(Component component){
        this.getConnections().remove(component);
    }

    public boolean isConnectedToEnergySource() {
        for (Component c : this.getConnections()) {
            if (c instanceof EnergySource) {
                return true;
            }
        }
        return false;
    }
}
