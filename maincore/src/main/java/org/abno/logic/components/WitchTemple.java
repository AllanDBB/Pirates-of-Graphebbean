package org.abno.logic.components;


import java.util.ArrayList;

public class WitchTemple extends Component{
    int time;

    public WitchTemple(){
        super();
        this.setPrice(2500);
        this.time = 300; //300 s, no configurable
    }

    //random si shield o kraken

    public void shield(Player player, int shots) {
        player.setShield(shots);
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
