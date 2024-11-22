package org.abno.logic.components;

import java.util.ArrayList;

public abstract class Component extends Item {


    Component() {

    }


    private boolean isValidLocation(Pair<Integer, Integer> coord) {
        int x = coord.first;
        int y = coord.second;
        return x >= 0 && x < 20 && y >= 0 && y < 20;
    }

}
