package org.abno.logic.components;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Component extends Item implements Serializable {
    private int id;
    private static int idCounter = 0;

    Component() {
        this.id = idCounter++;
    }


    private boolean isValidLocation(Pair<Integer, Integer> coord) {
        int x = coord.first;
        int y = coord.second;
        return x >= 0 && x < 20 && y >= 0 && y < 20;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Component-" + id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Component component = (Component) obj;
        return id == component.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

}
