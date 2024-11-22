package org.abno.logic.components;

import java.util.ArrayList;

public abstract class Component extends Item {
    private ArrayList<Pair<Integer, Integer>> location;

    Component() {
        location = new ArrayList<>();
    }

    public void placeComponent(ArrayList<Pair<Integer, Integer>> location) {
        for (Pair<Integer, Integer> coord : location) {
            if (!isValidLocation(coord)) {
                throw new IllegalArgumentException("Ubicación inválida: " + coord);
            }
        }
        this.location = location;
    }

    private boolean isValidLocation(Pair<Integer, Integer> coord) {
        int x = coord.first;
        int y = coord.second;
        return x >= 0 && x < 20 && y >= 0 && y < 20;
    }

    public ArrayList<Pair<Integer, Integer>> getLocation() {
        return location;
    }
}
