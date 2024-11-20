package org.abno.logic.components;

import jdk.internal.net.http.common.Pair;

import java.util.ArrayList;

public abstract class Item {
    private int price;
    private ArrayList<Pair<Integer, Integer>> location; //puede tener varios porque algunas son 2x2, etc

    Item(){
        location = new ArrayList<>();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ArrayList<Pair<Integer, Integer>> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<Pair<Integer, Integer>> location) {
        this.location = location;
    }
}
