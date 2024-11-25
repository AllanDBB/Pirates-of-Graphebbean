package org.abno.logic.components;

import org.abno.logic.weapons.Bomb;
import org.abno.logic.weapons.Canon;
import org.abno.logic.weapons.SuperCanon;
import org.abno.logic.weapons.UltraCanon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Player implements Serializable {
    private int money;
    private int iron;
    private ArrayList<Weapon> weapons;
    private ArrayList<Component> components;
    private Item[][] seaGrid;
    private Graph graph;
    private int shield;
    private Item [][] visibleGrid = new Item[20][20];

    private static final long serialVersionUID = 1L;

    public Player() {
        this.money = 40000;
        this.components = new ArrayList<>();
        this.iron = 40000;
        this.seaGrid = new Item[20][20];
        this.weapons = new ArrayList<>();
        this.graph = new Graph();
        this.shield = 0;

        initializeComponents();
    }

    public void setSeaGrid(Item[][] temp){
        seaGrid = temp;
    }

    private void initializeComponents() {
        Market market = new Market();
        EnergySource energySource = new EnergySource();
        Connector connector = new Connector();

        addComponentToGraph(market, List.of(new Pair<>(0, 1), new Pair<>(0, 2)));
        addComponentToGraph(energySource, List.of(new Pair<>(5, 6), new Pair<>(5, 7), new Pair<>(4, 6), new Pair<>(4, 7)));
        addComponentToGraph(connector, List.of(new Pair<>(4, 4)));

        this.graph.addEdge(energySource, connector);
        this.graph.addEdge(connector, market);

        this.getSeaGrid()[17][1] = new MaelStorm();
        this.getSeaGrid()[0][19] = new MaelStorm();
    }

    private void addComponentToGraph(Component component, List<Pair<Integer, Integer>> positions) {
        placeComponent(component, positions);
        component.setLocation(positions);
        components.add(component);
        this.graph.addNode(component);
    }

    public Item[][] getVisibleGrid() {
        return visibleGrid;
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


    public String useCanon(Player enemy, int x, int y, Canon canon) {
        String s = "";
        if (!isValidCell(x, y) || !weapons.contains(canon)) return s;
        if (processShield(enemy, x, y)) return s;

        String ans = destroyTarget(enemy, x, y);
        s = s.concat(ans);

        this.weapons.remove(canon);

        return s;
    }

    public String useSuperCanon(Player enemy, int x, int y, SuperCanon superCanon) {
        String s = "";
        if (!weapons.contains(superCanon)) return s;
        if (processShield(enemy, x, y)) return s;

        s = s.concat(destroyTarget(enemy, x, y));
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            int randX = random.nextInt(20);
            int randY = random.nextInt(20);
            s = s.concat(destroyTarget(enemy, randX, randY));
        }
        this.weapons.remove(superCanon);
        return s;
    }

    public String useBomb(Player enemy, int x1, int y1, int x2, int y2, int x3, int y3, Bomb bomb) {
        String s = "";

        Random random = new Random();
        s = s.concat(applyBombEffect(enemy, x1, y1, random));
        s = s.concat(applyBombEffect(enemy, x2, y2, random));
        s = s.concat(applyBombEffect(enemy, x3, y3, random));
        this.weapons.remove(bomb);
        return s;
    }

    public String useUltraCanon(Player enemy, List<Pair<Integer, Integer>> targets, UltraCanon ultraCanon) {
        String s = "";
        if (targets.size() > 10 || !weapons.contains(ultraCanon)) return s;
        for (Pair<Integer, Integer> target : targets) {
            int x = target.first;
            int y = target.second;
            if (processShield(enemy, x, y)) continue;
            s = s.concat(destroyTarget(enemy, x, y));
        }
        this.weapons.remove(ultraCanon);
        return s;
    }

    private String destroyTarget(Player enemy, int x, int y) {
        String s = "";

        if (!isValidCell(x, y)) return s;

        Item target = enemy.getSeaGrid()[x][y];
        if (target instanceof MaelStorm) {
            s = s.concat("REMOLINO ENCONTRADO EN "+x +", " +y);
            s = s.concat(maelstormAttack());
        }


        if (target instanceof Component) {
            Component component = (Component) target;
            s = s.concat("ATAQUE EXITOSO EN  "+ x +", "+ y+ "  de "+component.getClass().getSimpleName());
            enemy.getSeaGrid()[x][y] = null;
            enemy.getVisibleGrid()[x][y] = new Fire();


            if (isComponentCompletelyRemoved(component, enemy)) {
                s = s.concat("    BLANCO ELIMINADO TOTALMENTE");
                enemy.getGraph().removeNode(component);
                enemy.getComponents().remove(component);

                if (component instanceof Connector || component instanceof EnergySource){
                    for (Component c: enemy.getComponents()){
                        System.out.println(c.getId());
                        if (enemy.getGraph().isDisconnectedSubgraph(c.getId())){
                            for (Pair<Integer, Integer> coord : c.getLocation()) {
                                System.out.println(coord.first);
                                System.out.println(coord.second);
                                enemy.getVisibleGrid()[coord.first][coord.second] = component;
                                System.out.println(enemy.getVisibleGrid()[coord.first][coord.second]);
                            }
                        }
                    }
                }
            }
        }
        return s;
    }

    private String applyBombEffect(Player enemy, int x, int y, Random random) {
        String s = "";
        if (!isValidCell(x, y)) return s;
        int direction = random.nextInt(2);
        if (direction == 0) {
            s = s.concat(destroyTarget(enemy, x, y));
            s= s.concat(destroyTarget(enemy, x, y + 1));
        } else {
            s = s.concat(destroyTarget(enemy, x, y));
            s = s.concat(destroyTarget(enemy, x + 1, y));
        }
        return s;
    }

    private boolean processShield(Player enemy, int x, int y) {
        if (enemy.getShield() > 0){
            enemy.setShield(enemy.getShield()-1);
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

    private String maelstormAttack() {
        String ret = " Remolino contraataca en: ";
        Random r = new Random();
        for (int i = 0; i < 3; i++) {

            int x = r.nextInt(20);
            int y = r.nextInt(20);
            ret = ret.concat("   "+x+", "+y+"   ");
            destroyTarget(this, x, y);
        }
        return ret;
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
