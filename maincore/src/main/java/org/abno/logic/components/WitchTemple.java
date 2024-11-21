package org.abno.logic.components;


import java.util.ArrayList;

public class WitchTemple extends Component{
    int time;

    WitchTemple (){
        super();
        this.setPrice(2500);
        this.time = 300; //300 s, no configurable
    }

    //random si shield o kraken

    public void shield(ArrayList<Pair<Integer, Integer>> positions, Player player, int shots) { //falta lo del random pero como lo incorporo si debe recibir la lista de puntos a atacar

        for (Pair<Integer, Integer> p : positions) {
            int x = p.first;
            int y = p.second;

            if (isValidCell(x, y, player)) {
                Shield shield = new Shield(x, y, shots);
                ArrayList<Pair<Integer, Integer>> location = new ArrayList<>();
                location.add(new Pair<>(x, y));

                player.placeComponent(shield, location);

            }
        }
    }


    private boolean isValidCell(int x, int y, Player player) {
        return x >= 0 && x < 20 && y >= 0 && y < 20 && player.getSeaGrid()[x][y] == null;
    }

    public void kraken(Player enemy) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Item item = enemy.getSeaGrid()[i][j];
                if (item instanceof Component) {
                    Component target = (Component) item;

                    enemy.getGraph().removeNode(target);

                    for (Pair<Integer, Integer> coord : target.getLocation()) {
                        int x = coord.first;
                        int y = coord.second;
                        enemy.getSeaGrid()[x][y] = null;
                    }

                    return;
                }
            }
        }
    }



}
