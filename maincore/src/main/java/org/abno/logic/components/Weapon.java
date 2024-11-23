package org.abno.logic.components;

import java.io.Serializable;

public abstract class Weapon implements Serializable {
    private int iron;

    public int getIron() {
        return iron;
    }

    public void setIron(int iron) {
        this.iron = iron;
    }

}
