package org.abno.logic.components;

import org.abno.logic.weapons.Bomb;
import org.abno.logic.weapons.Canon;
import org.abno.logic.weapons.SuperCanon;
import org.abno.logic.weapons.UltraCanon;

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
    private int shield;

    public Player() {
        this.money = 40000000;
        this.components = new ArrayList<>();
        this.iron = 0;
        this.seaGrid = new Component[20][20];
        this.weapons = new ArrayList<>();
        this.graph = new Graph();
        this.shield = 0;

        initializeComponents();
    }

    private void initializeComponents() {
        Market market = new Market();
        EnergySource energySource = new EnergySource();
        Connector connector = new Connector();

        addComponentToGraph(market, List.of(new Pair<>(0, 1), new Pair<>(0, 2)));
        addComponentToGraph(energySource, List.of(new Pair<>(5, 6), new Pair<>(5, 7), new Pair<>(4, 6), new Pair<>(4, 7)));
        addComponentToGraph(connector, List.of(new Pair<>(4, 4)));

        graph.addEdge(energySource, connector);
        graph.addEdge(connector, market);
    }

    private void addComponentToGraph(Component component, List<Pair<Integer, Integer>> positions) {
        placeComponent(component, positions);
        component.setLocation(positions);
        components.add(component);
        graph.addNode(component);
    }

    public int getShield() {
        return shield;
    }

    public void setShield(int shield) {
        this.shield = shield;
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

    public void placeComponent(Item component, List<Pair<Integer, Integer>> location) {
        for (Pair<Integer, Integer> coord : location) {
            int x = coord.first;
            int y = coord.second;
            if (seaGrid[x][y] != null) {
                throw new IllegalArgumentException("Celda ocupada en (" + x + ", " + y + ")");
            }
        }
        for (Pair<Integer, Integer> coord : location) {
            seaGrid[coord.first][coord.second] = component;
        }

        component.setLocation(location);
    }


    public void useCanon(Player enemy, int x, int y, Canon canon) {
        if (!isValidCell(x, y) || !weapons.contains(canon)) return;
        if (processShield(enemy, x, y)) return;

        destroyTarget(enemy, x, y);
        this.weapons.remove(canon);
    }

    public void useSuperCanon(Player enemy, int x, int y, SuperCanon superCanon) {
        if (!weapons.contains(superCanon)) return;
        if (processShield(enemy, x, y)) return;

        destroyTarget(enemy, x, y);
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int randX = random.nextInt(20);
            int randY = random.nextInt(20);
            destroyTarget(enemy, randX, randY);
        }
        this.weapons.remove(superCanon);
    }

    public void useBomb(Player enemy, int x1, int y1, int x2, int y2, int x3, int y3, Bomb bomb) {
        Random random = new Random();
        applyBombEffect(enemy, x1, y1, random);
        applyBombEffect(enemy, x2, y2, random);
        applyBombEffect(enemy, x3, y3, random);
        this.weapons.remove(bomb);
    }

    public void useUltraCanon(Player enemy, List<Pair<Integer, Integer>> targets, UltraCanon ultraCanon) {
        if (targets.size() > 10 || !weapons.contains(ultraCanon)) return;
        for (Pair<Integer, Integer> target : targets) {
            int x = target.first;
            int y = target.second;
            if (processShield(enemy, x, y)) continue;
            destroyTarget(enemy, x, y);
        }
        this.weapons.remove(ultraCanon);
    }

    private void destroyTarget(Player enemy, int x, int y) {
        if (!isValidCell(x, y)) return;
        Item target = enemy.seaGrid[x][y];
        if (target instanceof MaelStorm) {
            maelstormAttack();
        } else if (target instanceof Component) {
            Component component = (Component) target;
            enemy.seaGrid[x][y] = null;
            if (isComponentCompletelyRemoved(component, enemy)) {
                enemy.getGraph().removeNode(component);
                enemy.getComponents().remove(component);
            }
        }
    }

    private void applyBombEffect(Player enemy, int x, int y, Random random) {
        if (!isValidCell(x, y)) return;
        int direction = random.nextInt(2);
        if (direction == 0) {
            destroyTarget(enemy, x, y);
            destroyTarget(enemy, x, y + 1);
        } else {
            destroyTarget(enemy, x, y);
            destroyTarget(enemy, x + 1, y);
        }
    }

    private boolean processShield(Player enemy, int x, int y) {
        if (enemy.shield > 0){
            enemy.shield--;
            return true;
        }
        return false;
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < 20 && y >= 0 && y < 20;
    }

    private boolean isComponentCompletelyRemoved(Component component, Player enemy) {
        for (Pair<Integer, Integer> coord : component.getLocation()) {
            int x = coord.first;
            int y = coord.second;
            if (enemy.seaGrid[x][y] != null) return false;
        }
        return true;
    }

    private void maelstormAttack() {
        Random r = new Random();
        for (int i = 0; i < 3; i++) {
            int x = r.nextInt(20);
            int y = r.nextInt(20);
            destroyTarget(this, x, y);
        }
    }

    public void printSeaGrid() {
        System.out.println("---------------------------------------");
        for (int i = 0; i < seaGrid.length; i++) {
            for (int j = 0; j < seaGrid[i].length; j++) {
                Item item = seaGrid[i][j];
                if (item instanceof EnergySource) {
                    System.out.print("e ");
                } else if (item instanceof Connector) {
                    System.out.print("c ");
                } else if (item instanceof Market) {
                    System.out.print("m ");
                } else if (item instanceof Mine) {
                    System.out.print("f ");
                } else if (item instanceof WitchTemple) {
                    System.out.print("t ");
                } else if (item instanceof Armory) {
                    System.out.print("a ");
                } else {
                    System.out.print(". "); // Celda vacía
                }
            }
            System.out.println(); // Nueva línea para cada fila
        }
    }


}
