package org.abno.logic.components;

import org.abno.logic.weapons.Bomb;
import org.abno.logic.weapons.Canon;
import org.abno.logic.weapons.SuperCanon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    private int money;
    private int iron;
    private ArrayList<Weapon> weapons;
    private ArrayList<Component> components;
    private Item[][] seaGrid;
    private Graph graph;
    private Shield[][] shields;

    public Player() {
        this.money = 4000;
        this.components = new ArrayList<>();
        this.iron = 0;
        this.seaGrid = new Item[20][20]; // Asegúrate de que Item esté definido
        this.weapons = new ArrayList<>();
        this.graph = new Graph();
        this.shields = new Shield[20][20];

        Market market = new Market();
        EnergySource energySource = new EnergySource();
        Connector connector = new Connector();

        components.add(market);
        components.add(energySource);
        components.add(connector);

        market.addConnection(connector);
        energySource.addConnection(connector);

        connector.addConnection(market);
        connector.addConnection(energySource);

        ArrayList<Pair<Integer, Integer>> positionsEnergySource = new ArrayList<>();
        positionsEnergySource.add(new Pair<>(5, 6));
        positionsEnergySource.add(new Pair<>(5, 7));
        positionsEnergySource.add(new Pair<>(4, 6));
        positionsEnergySource.add(new Pair<>(4, 7));
        placeComponent(energySource, positionsEnergySource);
        energySource.setLocation(positionsEnergySource);

        ArrayList<Pair<Integer, Integer>> positionsMarket = new ArrayList<>();
        positionsMarket.add(new Pair<>(0, 1));
        positionsMarket.add(new Pair<>(0, 2));
        placeComponent(market, positionsMarket);
        market.setLocation(positionsMarket);

        ArrayList<Pair<Integer, Integer>> positionsConnector = new ArrayList<>();
        positionsConnector.add(new Pair<>(4, 4));
        placeComponent(connector, positionsConnector);
        connector.setLocation(positionsConnector);
    }

    public Graph getGraph() {
        return graph;
    }

    public Item[][] getSeaGrid() {
        return seaGrid;
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

    public void placeComponent(Item component, ArrayList<Pair<Integer, Integer>> location) {
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

        if (component instanceof Component){
            graph.addNode((Component) component);}
    }

    public void useCanon(Player enemy, int x, int y, Canon canon) {
        Item target = enemy.seaGrid[x][y];

        if (enemy.shields[x][y]!=null){
            if (enemy.shields[x][y].shotsLeft <= 0){
                enemy.shields[x][y] = null;
            }
            else{
                enemy.shields[x][y].shotsLeft-=1;
                return;
            }
        }

        if (target instanceof MaelStorm){
            maelstormAttack();
        }

        if (isValidCell(x,y) && target != null && weapons.contains(canon)){
            if (target instanceof Component){
                //enemy.graph.removeNode((Component) target);
                enemy.seaGrid[x][y] = null;
            }
            }

        this.weapons.remove(canon);
    }

    public void useSuperCanon(Player enemy, int x, int y, SuperCanon superCanon) {
        Random random = new Random();

        if (!weapons.contains(superCanon)) {
            return;
        }

        Item target = enemy.seaGrid[x][y];
        processTarget(target, enemy, x, y);

        for (int i = 0; i < 4; i++) {
            int randX = random.nextInt(20);
            int randY = random.nextInt(20);
            Item randomTarget = enemy.seaGrid[randX][randY];
            processTarget(randomTarget, enemy, randX, randY);
        }
        this.weapons.remove(superCanon);
    }


    private void processTarget(Item target, Player enemy, int x, int y) {

        if (enemy.shields[x][y]!=null){
            if (enemy.shields[x][y].shotsLeft <= 1){
                enemy.shields[x][y] = null;
            }
            else{
                enemy.shields[x][y].shotsLeft-=1;
                return;
            }
        }

        if (target instanceof MaelStorm) {
            maelstormAttack();
        }
        if (target instanceof Component) {
            //enemy.getGraph().removeNode((Component) target);
            enemy.seaGrid[x][y] = null;
        }
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

        if (enemy.seaGrid[x1][y1] instanceof MaelStorm){
            maelstormAttack();
        }

        if (enemy.seaGrid[x2][y2] instanceof MaelStorm){
            maelstormAttack();
        }

        if (enemy.seaGrid[x3][y3] instanceof MaelStorm){
            maelstormAttack();
        }

        this.weapons.remove(bomb);
    }


    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < 20 && y >= 0 && y < 20;
    }


    private void applyBombEffect(Player enemy, int x, int y, Random random) {
        Item target = enemy.seaGrid[x][y];

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
            Item target = enemy.seaGrid[x][y];

            if (enemy.shields[x][y]!=null){
                if (enemy.shields[x][y].shotsLeft <= 1){
                    enemy.shields[x][y] = null;
                }
                else{
                    enemy.shields[x][y].shotsLeft-=1;
                    return;
                }
            }

            if (target instanceof Component){
                enemy.seaGrid[x][y] = null;}
        }
    }

    public void useUltraCanon(Player enemy, List<Pair<Integer, Integer>> targets, Weapon ultraCanon){ //maximo 10
        if (targets.size() > 10){
            return;
        }

        for (Pair<Integer, Integer> target : targets){
            int x = target.first;
            int y = target.second;

            if (enemy.seaGrid[x][y] instanceof MaelStorm){
                maelstormAttack();
            }

            if (isValidCell(x, y)) {
                Item component =  enemy.seaGrid[x][y];

                if (enemy.shields[x][y]!=null){
                    if (enemy.shields[x][y].shotsLeft <= 1){
                        enemy.shields[x][y] = null;
                    }
                    else{
                        enemy.shields[x][y].shotsLeft-=1;
                        return;
                    }
                }

                if (component != null && component instanceof Component){
                    enemy.seaGrid[x][y] = null;
                }
            }

        }



        this.weapons.remove(ultraCanon);

    }

    private void maelstormAttack(){
        Random r = new Random();
        for (int i = 0; i<3; i++){
            int x = r.nextInt(20)+1;
            int y = r.nextInt(20)+1;

            if (this.getSeaGrid()[x][y] != null && this.getSeaGrid()[x][y] instanceof Component){
                this.getGraph().removeNode((Component) this.seaGrid[x][y]);
            }
        }

    }






}
