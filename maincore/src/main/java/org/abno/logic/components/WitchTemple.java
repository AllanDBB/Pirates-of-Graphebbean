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

    public String shield(Player player, int shots) {
        player.setShield(shots);
        return "ESCUDO ACTIVADO!";
    }


    private boolean isValidCell(int x, int y, Player player) {
        return x >= 0 && x < 20 && y >= 0 && y < 20 && player.getSeaGrid()[x][y] == null;
    }

    public void kraken(Player enemy) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Item item = enemy.getSeaGrid()[i][j];
                System.out.println(i);
                System.out.println(j);
                if (item instanceof Component) {
                    Component target = (Component) item;

                    if (target instanceof Connector || target instanceof EnergySource){
                        for (Component c: enemy.getComponents()){
                            System.out.println(c.getId());
                            if (enemy.getGraph().isDisconnectedSubgraph(c.getId())){
                                for (Pair<Integer, Integer> coord : c.getLocation()) {
                                    System.out.println(coord.first);
                                    System.out.println(coord.second);
                                    if (!(enemy.getVisibleGrid()[coord.first][coord.second] instanceof Fire)){
                                        enemy.getVisibleGrid()[coord.first][coord.second] = c;}
                                    System.out.println(enemy.getVisibleGrid()[coord.first][coord.second]);
                                }
                            }
                        }
                    }


                    enemy.getGraph().removeNode(target);
                    enemy.getComponents().remove(target);

                    for (Pair<Integer, Integer> coord : target.getLocation()) {
                        int x = coord.first;
                        int y = coord.second;
                        enemy.getSeaGrid()[coord.first][coord.second] = null;
                    }

                    return;
                }
            }
        }
    }



}
