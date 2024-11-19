package org.adbbnod;

import jdk.internal.net.http.common.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    private int money;
    private int iron;
    private ArrayList<Weapon> weapons;
    private ArrayList<Component> components;
    private Component[][] seaGrid;
    private Graph graph;


    Player (){
        this.money = 4000;
        this.components = new ArrayList<>();
        this.iron = 0;
        this.weapons = new ArrayList<>();
        seaGrid = new Component[20][20];
        weapons = new ArrayList<>();
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArrayList<Component> getComponents() {
        return components;
    }

    public int getIron() {
        return iron;
    }

    public void setIron(int iron) {
        this.iron = iron;
    }

    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }

    public void placeComponent(Component component, List<Pair<Integer, Integer>> location) {
        for (Pair<Integer, Integer> coord : location) {
            int x = coord.first;
            int y = coord.second;
            if (seaGrid[x][y] != null) {
                throw new IllegalArgumentException("ocupada");
            }
        }

        for (Pair<Integer, Integer> coord : location) {
            seaGrid[coord.first][coord.second] = component;
        }
        graph.addNode(component);
    }

    public void useCanon(Player enemy, int x, int y, Canon canon) {
        Component target = enemy.seaGrid[x][y];
        if (isValidCell(x,y) && target != null && weapons.contains(canon)){
            enemy.graph.removeNode(target);
            }
        this.weapons.remove(canon);
    }

    public void useSuperCanon(Player enemy, int x, int y, SuperCanon superCanon){
        Component target = enemy.seaGrid[x][y];

        if (target != null && weapons.contains(superCanon)){
            enemy.graph.removeNode(target);

            Random r = new Random();

            enemy.graph.removeNode(seaGrid[r.nextInt(20)][r.nextInt(20)]);
            enemy.graph.removeNode(seaGrid[r.nextInt(20)][r.nextInt(20)]);
            enemy.graph.removeNode(seaGrid[r.nextInt(20)][r.nextInt(20)]);
            enemy.graph.removeNode(seaGrid[r.nextInt(20)][r.nextInt(20)]);
        }
        this.weapons.remove(superCanon);
    }

    public void useBomb(Player enemy, int x1, int y1, int x2, int y2, int x3, int y3, Bomb bomb) {
        Random random = new Random();

        if (isValidCell(x1, y1)) {
            applyBombEffect(enemy, x1, y1, random);
        }

        if (isValidCell(x2, y2)) {
            applyBombEffect(enemy, x2, y2, random);
        }

        if (isValidCell(x3, y3)) {
            applyBombEffect(enemy, x3, y3, random);
        }

        this.weapons.remove(bomb);
    }


    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < 20 && y >= 0 && y < 20;
    }


    private void applyBombEffect(Player enemy, int x, int y, Random random) {
        Component target = enemy.seaGrid[x][y];

        if (target != null) {

            int direction = random.nextInt(2);

            if (direction == 0) {
                destroyCell(enemy, x, y);
                destroyCell(enemy, x, y + 1);
            } else {
                destroyCell(enemy, x, y);
                destroyCell(enemy, x + 1, y);
            }
        }
    }


    private void destroyCell(Player enemy, int x, int y) {
        if (isValidCell(x, y) && enemy.seaGrid[x][y] != null) {
            Component target = enemy.seaGrid[x][y];

            enemy.graph.removeNode(target);
        }
    }

    private void useUltraCanon(Player enemy, List<Pair<Integer, Integer>> targets, Weapon ultraCanon){ //maximo 10
        if (targets.size() > 10){
            return;
        }

        for (Pair<Integer, Integer> target : targets){
            int x = target.first;
            int y = target.second;

            if (isValidCell(x, y)) {
                Component component =  enemy.seaGrid[x][y];

                if (component != null){
                    enemy.graph.removeNode(component);
                }
            }

        }

        this.weapons.remove(ultraCanon);

    }







}
