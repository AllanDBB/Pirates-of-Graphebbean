package org.abno.logic.components;

import java.util.ArrayList;
import java.util.List;

public abstract class Item {
    private int price;
    private List<Pair<Integer, Integer>> location; //puede tener varios porque algunas son 2x2, etc

    Item(){
        location = new ArrayList<>();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Pair<Integer, Integer>> getLocation() {
        return location;
    }

    public void setLocation(List<Pair<Integer, Integer>> location) {
        this.location = location;
    }
}
