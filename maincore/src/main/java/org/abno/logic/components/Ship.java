package org.abno.logic.components;

import java.util.Random;

public class Ship extends Item{

    Ship(){
        super();
        this.setPrice(2500);
    }

    public String getInfo(Player enemy, int x, int y) {
        Random random = new Random();
        int radius = random.nextInt(5) + 4;

        StringBuilder info = new StringBuilder();
        boolean foundSomething = false;

        for (int i = Math.max(0, x - radius); i <= Math.min(19, x + radius); i++) {
            for (int j = Math.max(0, y - radius); j <= Math.min(19, y + radius); j++) {
                Item item = enemy.getSeaGrid()[i][j];
                if (item instanceof Component) {
                    foundSomething = true;
                    Component component = (Component) item;
                    info.append(component.getClass().getSimpleName())
                            .append(" en ")
                            .append(i)
                            .append(", ")
                            .append(j)
                            .append("; ");
                }
            }
        }

        if (foundSomething) {
            return "Información obtenida: " + info.toString();
        } else {
            return "No hay componentes en el radio de " + radius + " casillas alrededor de (" + x + ", " + y + ").";
        }
    }

}
