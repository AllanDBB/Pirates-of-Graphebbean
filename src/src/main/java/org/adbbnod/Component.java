package org.adbbnod;

import jdk.internal.net.http.common.Pair;

import java.util.ArrayList;

public abstract class Component extends Item{
    //cada componente, ya sean fuentes de energía, conectores, mercado, fábricas, armas , remolinos, barcos
    //tienen un precio, una ubicación (los cuadros que ocupa en la matriz del jugador), sus conexiones


    private ArrayList<Component> connections; // No puede conectarse un componente a otro directamente, siempre debe haber un conector de por medio.


    Component(){
        connections = new ArrayList<>();

    }


    public void placeComponent(ArrayList<Pair<Integer, Integer>> location) {
        for (Pair<Integer, Integer> coord : location) {
            if (!isValidLocation(coord)) {
                throw new IllegalArgumentException("Ubicación inválida: " + coord);
            }
        }
        this.setLocation(location);
    }

    private boolean isValidLocation(Pair<Integer, Integer> coord) {
        int x = coord.first;
        int y = coord.second;
        return x >= 0 && x < 20 && y >= 0 && y < 20; // Supone una matriz de 20x20.
    }


    public ArrayList<Component> getConnections() {
        return connections;
    }

    public void addConnection(Component connector) {
        if (connector instanceof Connector) {
            connections.add(connector);
        }
    }

}
