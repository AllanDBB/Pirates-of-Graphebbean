package org.abno.sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;


import org.abno.logic.components.*;
import org.abno.logic.enums.TypesOfItems;
import org.abno.logic.enums.TypesOfWeapons;
import org.abno.logic.weapons.Bomb;
import org.abno.logic.weapons.Canon;
import org.abno.logic.weapons.SuperCanon;
import org.abno.logic.weapons.UltraCanon;

public class Server {


    private static final int PORT = 8060;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    static int currentTurnIndex = 0;


    public static void main(String[] args) {
        System.out.println("Server starting...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error with server socket: " + e.getMessage());
        }


    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private List<ClientHandler> clients;
    private boolean isReady;
    private Player player;


    // Game constants
    private List<Player> players = new ArrayList<>();

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
        this.player = new Player();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Please, introduce your username: ");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous";
            }

            out.println(username + " has joined to the game.");

            String message;
            while ((message = in.readLine()) != null) {
                if ("exit".equalsIgnoreCase(message.trim())) {
                    out.println("You have left the game.");
                    break;
                }

                processMessage(username, message);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }


    private void processMessage(String username, String message){
        player.printSeaGrid();

        if (message.startsWith("@")){
            if (message.equalsIgnoreCase("@Ready")) {
                setReady();
                out.println("You are ready!");
            } else if (message.equalsIgnoreCase("@BuyConnector")) {
                try {
                    buyConnector(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@BuyEnergySource")) {
                try {
                    buyEnergySource(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@BuyMarket")) {
                try {
                    buyMarket(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@BuyMine")) {
                try {
                    buyTwoByOneComponent(player, "Mine");
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@BuyWitchTemple")) {
                try {
                    buyTwoByOneComponent(player, "WitchTemple");
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@BuyArmory")) {
                try {
                    buyArmory(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@ArmoryGenerate")) {
                try {
                    generateWeaponFromSelectedArmory(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@MineProduce")) {
                produceFromFirstMine(player);
            } else if (message.equalsIgnoreCase("@WitchTempleRandom")) {
                try {
                    activateWitchTemple(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@SellComponentToMarket")) {
                try {
                    sellComponentToMarket(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@SellIronToMarket")) {
                try {
                    sellIronToMarket(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@SellWeaponToMarket")) {
                try {
                    sellWeaponToMarket(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@BuyIronFromPlayer")) {
                try {
                    initiateIronTrade(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@SellWeaponToPlayer")) {
                try {
                    sellWeaponToPlayer(player);
                } catch (IOException e) {
                    out.println("An error occurred while processing your request. Please try again.");
                }
            } else if (message.equalsIgnoreCase("@MyMoney")) {
                out.println("Saldo actual: " + String.valueOf(player.getMoney()));
            } else if (message.equalsIgnoreCase("@MyIron")) {
                out.println("Acero actual: " + String.valueOf(player.getIron()));
            } else if (message.equalsIgnoreCase("@MyWeapons")) {
                for (Weapon w: player.getWeapons()){out.println(w.getClass());}
            } else if (message.equalsIgnoreCase("@UseCannon")){
                try {
                    String s = useCanonAction(player);
                    broadcastToAll(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (message.equalsIgnoreCase("@UseSuperCannon")){
                try {
                    String s = useSuperCanonAction(player);
                    broadcastToAll(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (message.equalsIgnoreCase("@UseBomb")){
                try {
                    String s = useBombAction(player);
                    broadcastToAll(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (message.equalsIgnoreCase("@UseUltraCannon")){
                try {
                    String s = useUltraCanonAction(player);
                    broadcastToAll(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (message.equalsIgnoreCase("@BuyShip")){
                try {
                    buyShip(player);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (message.equalsIgnoreCase("@GetPlayerList")){
                System.out.println("Requesting players list...");
                sendPlayersList();
            }
        }
        else {
            broadcast(username + ": " + message);
        }
    }



    private void setReady(){
        isReady = true;
        broadcast(username + (isReady ? " is ready!" : " is not ready."));
        checkReadyPlayers();
    }

    private void checkReadyPlayers() {
        long readyCount = clients.stream().filter(client -> client.isReady).count();
        if (readyCount >= 2) {
            startGame();

            enableChatForPlayers();

        }
    }

    private void enableChatForPlayers(){
        for (ClientHandler client : clients){
            client.out.println("@SetChat");
        }
    }

    private void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
        clients.remove(this);
        broadcast(username + " has disconnected.");
    }

    private void broadcast(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.out.println(message);
                }
            }
        }
    }

    private void broadcastToAll(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.out.println(message);
            }
        }
    }

    // here starts the game logic:

    private void sendPlayersList() {
        try {
            List<Player> playerList = clients.stream().map(client -> client.player).collect(Collectors.toList());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(playerList);
            oos.flush();
        } catch (IOException e) {
            System.err.println("Error sending players list: " + e.getMessage());
        }
    }
    private void startGame(){
        synchronized (clients){
            for (ClientHandler client : clients){
                client.out.println("@StartGame");
            }
        }
    }





    //adicionales para la lógica


    private void buyConnector(Player currentPlayer) throws IOException {
        boolean connectorBought = false;

        for (Component c : currentPlayer.getComponents()) {
            if (c instanceof Market) {
                Connector connector = (Connector) ((Market) c).marketSells(currentPlayer, TypesOfItems.CONNECTOR);
                if (connector != null) {

                    out.println("Enter the coordinates x, y to place the connector (1x1): 0-19");
                    String input = in.readLine();
                    try {
                        String[] parts = input.split(",");
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());

                        if (isValidPosition(currentPlayer, x, y)) {

                            List<Pair<Integer, Integer>> location = List.of(new Pair<>(x, y));
                            currentPlayer.placeComponent(connector, location);



                            currentPlayer.getGraph().addNode(connector);


                            Component energySource = findEnergySource(currentPlayer);
                            if (energySource != null) {
                                currentPlayer.getGraph().addEdge(connector, energySource);
                                out.println("Connector connected to the Energy Source.");
                            } else {
                                out.println("No Energy Source found. Connector not connected.");
                            }

                            List<Component> otherComponents = getOtherComponents(currentPlayer, energySource);
                            if (!otherComponents.isEmpty()) {
                                out.println("Available components to connect:");
                                for (int i = 0; i < otherComponents.size(); i++) {
                                    out.println((i + 1) + ". " + otherComponents.get(i).getClass().getSimpleName());
                                }

                                out.println("Enter the number of the component to connect to:");
                                input = in.readLine();
                                int choice = Integer.parseInt(input) - 1;
                                if (choice >= 0 && choice < otherComponents.size()) {
                                    Component target = otherComponents.get(choice);
                                    currentPlayer.getGraph().addEdge(connector, target);
                                    out.println("Connector connected to " + target.getClass().getSimpleName() + ".");
                                } else {
                                    out.println("Invalid choice. Connector not connected to any additional component.");
                                }
                            }
                            connectorBought = true;
                            break;
                        } else {
                            out.println("Invalid or occupied position. Connector not placed.");
                        }
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        out.println("Invalid input format. Please use 'x,y' format.");
                    }
                }
            }
        }

        if (!connectorBought) {
            out.println("Failed to buy a connector. Ensure you have enough resources or a market.");
        }
    }

    private void buyEnergySource(Player currentPlayer) throws IOException {
        boolean energySourceBought = false;

        Market m = null;
        boolean hasMarket = false;
        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Market) {
                m = (Market) component;
                hasMarket = true;
                break;
            }
        }


        if (!hasMarket) {
            out.println("You must have a Market.");
            return;
        }

        out.println("Enter the coordinates x, y to place the Energy Source (2x2): 0-18");
        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (isValidPositionForEnergySource(currentPlayer, x, y)) {

                EnergySource energySource = (EnergySource) m.marketSells(player, TypesOfItems.ENERGYSOURCE);

                if (energySource == null){return;}

                List<Pair<Integer, Integer>> location = List.of(
                        new Pair<>(x, y),
                        new Pair<>(x + 1, y),
                        new Pair<>(x, y + 1),
                        new Pair<>(x + 1, y + 1)
                );
                currentPlayer.placeComponent(energySource, location);



                currentPlayer.getGraph().addNode(energySource);
                out.println("Energy Source placed at (" + x + ", " + y + ").");


                List<Component> availableConnectors = getConnectors(currentPlayer);
                if (!availableConnectors.isEmpty()) {
                    out.println("Available connectors to connect:");
                    for (int i = 0; i < availableConnectors.size(); i++) {
                        out.println((i + 1) + ". " + availableConnectors.get(i).getClass().getSimpleName());
                    }

                    out.println("Enter the number of the connector to connect to:");
                    input = in.readLine();
                    int choice = Integer.parseInt(input) - 1;
                    if (choice >= 0 && choice < availableConnectors.size()) {
                        Component targetConnector = availableConnectors.get(choice);
                        currentPlayer.getGraph().addEdge(energySource, targetConnector);
                        out.println("Energy Source connected to " + targetConnector.getClass().getSimpleName() + ".");
                    } else {
                        out.println("Invalid choice. Energy Source not connected to any connector.");
                    }
                } else {
                    out.println("No connectors available to connect the Energy Source.");
                }

                energySourceBought = true;
            } else {
                out.println("Invalid or occupied position. Energy Source not placed.");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input format. Please use 'x,y' format.");
        }

        if (!energySourceBought) {
            out.println("Failed to buy an Energy Source. Ensure you have enough resources.");
        }
    }

    private void buyMarket(Player currentPlayer) throws IOException {
        boolean marketBought = false;

        Market m = null;
        boolean hasMarket = false;
        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Market) {
                m = (Market) component;
                hasMarket = true;
                break;
            }
        }


        if (!hasMarket) {
            out.println("You must have a Market.");
            return;
        }


        out.println("Enter the coordinates x, y to place the Market (1x2): 0-20 y 0-19");
        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (isValidPositionForMarket(currentPlayer, x, y)) {
                Market market = (Market) m.marketSells(player, TypesOfItems.MARKET);

                if (market == null){return;}

                List<Pair<Integer, Integer>> location = List.of(
                        new Pair<>(x, y),
                        new Pair<>(x, y + 1)
                );
                currentPlayer.placeComponent(market, location);


                currentPlayer.getComponents().add(market);
                currentPlayer.getGraph().addNode(market);
                out.println("Market placed at (" + x + ", " + y + ").");


                List<Component> availableConnectors = getConnectors(currentPlayer);
                if (!availableConnectors.isEmpty()) {
                    out.println("Available connectors to connect:");
                    for (int i = 0; i < availableConnectors.size(); i++) {
                        out.println((i + 1) + ". " + availableConnectors.get(i).getClass().getSimpleName());
                    }

                    out.println("Enter the number of the connector to connect to:");
                    input = in.readLine();
                    int choice = Integer.parseInt(input) - 1;
                    if (choice >= 0 && choice < availableConnectors.size()) {
                        Component targetConnector = availableConnectors.get(choice);
                        currentPlayer.getGraph().addEdge(market, targetConnector);
                        out.println("Market connected to " + targetConnector.getClass().getSimpleName() + ".");
                    } else {
                        out.println("Invalid choice. Market not connected to any connector.");
                    }
                } else {
                    out.println("No connectors available to connect the Market.");
                }

                marketBought = true;
            } else {
                out.println("Invalid or occupied position. Market not placed.");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input format. Please use 'x,y' format.");
        }

        if (!marketBought) {
            out.println("Failed to buy a Market. Ensure you have enough resources.");
        }
    }

    private void buyTwoByOneComponent(Player currentPlayer, String componentType) throws IOException {
        boolean componentBought = false;

        Market m = null;
        boolean hasMarket = false;
        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Market) {
                m = (Market) component;
                hasMarket = true;
                break;
            }
        }


        if (!hasMarket) {
            out.println("You must have a Market.");
            return;
        }

        Component component;
        switch (componentType.toLowerCase()) {
            case "mine":
                component = (Component) m.marketSells(player, TypesOfItems.MINE);
                break;
            case "witchtemple":
                component = (Component) m.marketSells(player, TypesOfItems.WITCHTEMPLE);
                break;
            case "armory":
                component = (Component) m.marketSells(player, TypesOfItems.ARMORY);
                break;
            default:
                out.println("Invalid component type.");
                return;
        }

        if (component == null){return;}

        out.println("Enter the coordinates x, y to place the " + componentType + " (2x1): 0-19 y 0-20");
        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());


            if (isValidPositionForTwoByOne(currentPlayer, x, y)) {

                List<Pair<Integer, Integer>> location = List.of(
                        new Pair<>(x, y),
                        new Pair<>(x + 1, y)
                );
                currentPlayer.placeComponent(component, location);


                currentPlayer.getGraph().addNode(component);
                out.println(componentType + " placed at (" + x + ", " + y + ").");


                List<Component> availableConnectors = getConnectors(currentPlayer);
                if (!availableConnectors.isEmpty()) {
                    out.println("Available connectors to connect:");
                    for (int i = 0; i < availableConnectors.size(); i++) {
                        out.println((i + 1) + ". " + availableConnectors.get(i).getClass().getSimpleName());
                    }

                    out.println("Enter the number of the connector to connect to:");
                    input = in.readLine();
                    int choice = Integer.parseInt(input) - 1;
                    if (choice >= 0 && choice < availableConnectors.size()) {
                        Component targetConnector = availableConnectors.get(choice);
                        currentPlayer.getGraph().addEdge(component, targetConnector);
                        out.println(componentType + " connected to " + targetConnector.getClass().getSimpleName() + ".");
                    } else {
                        out.println("Invalid choice. " + componentType + " not connected to any connector.");
                    }
                } else {
                    out.println("No connectors available to connect the " + componentType + ".");
                }

                componentBought = true;
            } else {
                out.println("Invalid or occupied position. " + componentType + " not placed.");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input format. Please use 'x,y' format.");
        }

        if (!componentBought) {
            out.println("Failed to buy a " + componentType + ". Ensure you have enough resources.");
        }
    }

    private void buyArmory(Player currentPlayer) throws IOException {
        boolean armoryBought = false;

        Market m = null;
        boolean hasMarket = false;
        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Market) {
                m = (Market) component;
                hasMarket = true;
                break;
            }
        }


        if (!hasMarket) {
            out.println("You must have a Market.");
            return;
        }


        out.println("Enter the coordinates x, y to place the Armory (2x1): 0-19 y 0-20");
        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (isValidPositionForTwoByOne(currentPlayer, x, y)) {
                Armory armory = (Armory) m.marketSells(player, TypesOfItems.ARMORY);

                if (armory == null){return;}

                List<Pair<Integer, Integer>> location = List.of(
                        new Pair<>(x, y),
                        new Pair<>(x + 1, y)
                );
                currentPlayer.placeComponent(armory, location);


                TypesOfWeapons chosenWeapon = chooseWeaponForArmory();
                if (chosenWeapon == null) {
                    out.println("Invalid choice. Armory not placed.");
                    return;
                }
                armory.setWeapon(chosenWeapon);


                currentPlayer.getGraph().addNode(armory);
                out.println("Armory placed at (" + x + ", " + y + "). It will produce " + chosenWeapon + ".");


                List<Component> availableConnectors = getConnectors(currentPlayer);
                if (!availableConnectors.isEmpty()) {
                    out.println("Available connectors to connect:");
                    for (int i = 0; i < availableConnectors.size(); i++) {
                        out.println((i + 1) + ". " + availableConnectors.get(i).getClass().getSimpleName());
                    }

                    out.println("Enter the number of the connector to connect to:");
                    input = in.readLine();
                    int choice = Integer.parseInt(input) - 1;
                    if (choice >= 0 && choice < availableConnectors.size()) {
                        Component targetConnector = availableConnectors.get(choice);
                        currentPlayer.getGraph().addEdge(armory, targetConnector);
                        out.println("Armory connected to " + targetConnector.getClass().getSimpleName() + ".");
                    } else {
                        out.println("Invalid choice. Armory not connected to any connector.");
                    }
                } else {
                    out.println("No connectors available to connect the Armory.");
                }

                armoryBought = true;
            } else {
                out.println("Invalid or occupied position. Armory not placed.");
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input format. Please use 'x,y' format.");
        }

        if (!armoryBought) {
            out.println("Failed to buy an Armory. Ensure you have enough resources.");
        }
    }



    private void generateWeaponFromSelectedArmory(Player currentPlayer) throws IOException {
        List<Armory> armories = new ArrayList<>();


        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Armory) {
                armories.add((Armory) component);
            }
        }

        if (armories.isEmpty()) {
            out.println("No Armory found.");
            return;
        }


        out.println("Available Armories:");
        for (int i = 0; i < armories.size(); i++) {
            Armory armory = armories.get(i);
            String weaponType = armory.getWeapon() != null ? armory.getWeapon().toString() : "No weapon set";
            out.println((i + 1) + ". Armory configured to generate: " + weaponType);
        }

        out.println("Enter the number of the Armory to activate:");

        try {
            String input = in.readLine();
            int choice = Integer.parseInt(input) - 1;

            if (choice >= 0 && choice < armories.size()) {
                Armory selectedArmory = armories.get(choice);
                Weapon weapon = selectedArmory.generateWeapon(currentPlayer);

                if (weapon != null) {
                    currentPlayer.getWeapons().add(weapon);
                    out.println("Weapon generated: " + weapon.getClass().getSimpleName() + ". Remaining Iron: " + currentPlayer.getIron());
                } else {
                    out.println("Not enough Iron to generate: " + selectedArmory.getWeapon());
                }
            } else {
                out.println("Invalid selection. No weapon generated.");
            }
        } catch (NumberFormatException e) {
            out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void produceFromFirstMine(Player currentPlayer) {
        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Mine) {
                Mine mine = (Mine) component;
                mine.mine(currentPlayer);
                out.println("Mine activated. Produced " + mine.getQuantity() + " iron. Current Iron: " + currentPlayer.getIron());
                return;
            }
        }

        out.println("No Mine found.");
    }



    private Component findEnergySource(Player player) {
        for (Component component : player.getComponents()) {
            if (component instanceof EnergySource) {
                return component;
            }
        }
        return null;
    }

    private List<Component> getOtherComponents(Player player, Component exclude) {
        List<Component> otherComponents = new ArrayList<>();
        for (Component component : player.getComponents()) {
            if (component != exclude && !(component instanceof Connector)) {
                otherComponents.add(component);
            }
        }
        return otherComponents;
    }

    private boolean isValidPosition(Player player, int x, int y) {
        if (x < 0 || x >= 20 || y < 0 || y >= 20) {
            return false;  // Fuera de los límites de la matriz
        }
        return player.getSeaGrid()[x][y] == null;  // Verificar si la posición está libre
    }

    private List<Component> getConnectors(Player player) {
        List<Component> connectors = new ArrayList<>();
        for (Component component : player.getComponents()) {
            if (component instanceof Connector && !connectors.contains(component)) {
                connectors.add(component);
            }
        }
        return connectors;
    }

    private boolean isValidPositionForEnergySource(Player player, int x, int y) {
        if (x < 0 || x >= 19 || y < 0 || y >= 19) {
            return false;
        }
        return player.getSeaGrid()[x][y] == null &&
                player.getSeaGrid()[x + 1][y] == null &&
                player.getSeaGrid()[x][y + 1] == null &&
                player.getSeaGrid()[x + 1][y + 1] == null;
    }

    private boolean isValidPositionForMarket(Player player, int x, int y) {
        if (x < 0 || x >= 20 || y < 0 || y >= 19) {
            return false;
        }
        return player.getSeaGrid()[x][y] == null &&
                player.getSeaGrid()[x][y + 1] == null;
    }

    private boolean isValidPositionForTwoByOne(Player player, int x, int y) {

        if (x < 0 || x >= 19 || y < 0 || y >= 20) {
            return false;
        }


        return player.getSeaGrid()[x][y] == null &&
                player.getSeaGrid()[x + 1][y] == null;
    }

    private TypesOfWeapons chooseWeaponForArmory() throws IOException {
        out.println("Choose the type of weapon the Armory will produce:");
        out.println("1. Canon (Cost: 500 Iron)");
        out.println("2. Super Canon (Cost: 1000 Iron)");
        out.println("3. Ultra Canon (Cost: 5000 Iron)");
        out.println("4. Bomb (Cost: 2000 Iron)");
        out.println("Enter the number of your choice:");

        String input = in.readLine();
        try {
            int choice = Integer.parseInt(input);
            switch (choice) {
                case 1:
                    return TypesOfWeapons.CANON;
                case 2:
                    return TypesOfWeapons.SUPERCANON;
                case 3:
                    return TypesOfWeapons.ULTRACANON;
                case 4:
                    return TypesOfWeapons.BOMB;
                default:
                    out.println("Invalid choice.");
                    return null;
            }
        } catch (NumberFormatException e) {
            out.println("Invalid input format.");
            return null;
        }
    }

    private Player getPlayerByUsername(String username) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.username.equalsIgnoreCase(username)) {
                    return client.player;
                }
            }
        }
        return null;
    }

    private void activateWitchTemple(Player currentPlayer) throws IOException {
        Random random = new Random();
        int action = random.nextInt(2); // 0 = Shield, 1 = Kraken

        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof WitchTemple) {
                WitchTemple witchTemple = (WitchTemple) component;

                if (action == 0) {

                    int shots = random.nextInt(4) + 2;
                    String s = witchTemple.shield(currentPlayer, shots);
                    out.println("Shield activated with " + shots + " shots.");
                    broadcastToAll(s);
                } else if (action == 1) {

                    out.println("Enter the target player's username for Kraken attack:");
                    String targetUsername = in.readLine();
                    Player targetPlayer = getPlayerByUsername(targetUsername);

                    if (targetPlayer != null) {
                        witchTemple.kraken(targetPlayer);
                        out.println("Kraken unleashed on " + targetUsername + "'s components!");
                        broadcastToAll("KRAKEN A "+ targetUsername);
                    } else {
                        out.println("Invalid target. Kraken attack cancelled.");
                    }
                }
                return;
            }
        }

        out.println("No WitchTemple found.");
    }

    private void sellComponentToMarket(Player currentPlayer) throws IOException {
        boolean hasMarket = false;
        Market market = null;
        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Market) {
                hasMarket = true;
                market = (Market) component;
                break;
            }
        }

        if (!hasMarket) {
            out.println("You must have a Market to sell components.");
            return;
        }

        List<Component> componentsToSell = new ArrayList<>();
        componentsToSell.addAll(currentPlayer.getComponents());

        if (componentsToSell.isEmpty()) {
            out.println("You don't have any components to sell.");
            return;
        }


        out.println("Available components to sell:");
        for (int i = 0; i < componentsToSell.size(); i++) {
            Component component = componentsToSell.get(i);
            out.println((i + 1) + ". " + component.getClass().getSimpleName() + " - Price: " + component.getPrice()/2);
        }

        out.println("Enter the number of the component you want to sell:");

        try {
            String input = in.readLine();
            int choice = Integer.parseInt(input) - 1;

            if (choice >= 0 && choice < componentsToSell.size()) {
                Component selectedComponent = componentsToSell.get(choice);
                market.marketBuysComponent(currentPlayer, selectedComponent);
                out.println("You have successfully sold the " + selectedComponent.getClass().getSimpleName() + "!");
            } else {
                out.println("Invalid choice. No component was sold.");
            }
        } catch (NumberFormatException e) {
            out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void sellIronToMarket(Player currentPlayer) throws IOException {
        boolean hasMarket = false;
        Market m = null;

        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Market) {
                hasMarket = true;
                m = (Market) component;
                break;
            }
        }


        if (!hasMarket) {
            out.println("You must have a Market to sell iron.");
            return;
        }


        out.println("Enter the quantity of iron you want to sell:");

        String input = in.readLine();
        try {
            int quantity = Integer.parseInt(input);

            if (quantity <= 0) {
                out.println("Quantity must be greater than zero.");
                return;
            }


            if (currentPlayer.getIron() >= quantity) {
                m.marketBuysIron(currentPlayer, quantity);
                out.println("You have successfully sold " + quantity + " iron. Now you have " + player.getIron());
            } else {
                out.println("You do not have enough iron to sell that quantity.");
            }

        } catch (NumberFormatException e) {
            out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void sellWeaponToMarket(Player currentPlayer) throws IOException {

        Market m = null;
        boolean hasMarket = false;
        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Market) {
                m = (Market) component;
                hasMarket = true;
                break;
            }
        }


        if (!hasMarket) {
            out.println("You must have a Market to sell weapons.");
            return;
        }


        List<Weapon> weaponsToSell = currentPlayer.getWeapons();
        if (weaponsToSell.isEmpty()) {
            out.println("You don't have any weapons to sell.");
            return;
        }

        out.println("Available weapons to sell:");
        for (int i = 0; i < weaponsToSell.size(); i++) {
            Weapon weapon = weaponsToSell.get(i);
            out.println((i + 1) + ". " + weapon.getClass().getSimpleName());
        }

        out.println("Enter the number of the weapon you want to sell:");

        try {
            String input = in.readLine();
            int choice = Integer.parseInt(input) - 1;

            if (choice >= 0 && choice < weaponsToSell.size()) {
                Weapon selectedWeapon = weaponsToSell.get(choice);
                m.marketBuysWeapons(currentPlayer, selectedWeapon);
                out.println("You have successfully sold the " + selectedWeapon.getClass().getSimpleName() + "!");
            } else {
                out.println("Invalid choice. No weapon was sold.");
            }
        } catch (NumberFormatException e) {
            out.println("Invalid input. Please enter a valid number.");
        }
    }

    private void initiateIronTrade(Player buyer) throws IOException {
        Market m = null;
        boolean hasMarket = false;
        for (Component component : buyer.getComponents()) {
            if (component instanceof Market) {
                m = (Market) component;
                hasMarket = true;
                break;
            }
        }

        if (!hasMarket) {
            out.println("You must have a Market to trade iron.");
            return;
        }


        out.println("Enter the username of the player you want to trade with:");

        String targetUsername = in.readLine();
        Player seller = getPlayerByUsername(targetUsername);

        if (seller == null) {
            out.println("Player not found.");
            return;
        }


        out.println("Enter the quantity of iron you want to buy:");

        String quantityInput = in.readLine();
        int quantity = Integer.parseInt(quantityInput);


        out.println("Enter the price you agree to pay for " + quantity + " iron:");

        String priceInput = in.readLine();
        int price = Integer.parseInt(priceInput);


        m.playerTransactionIron(buyer, seller, quantity, price);
    }

    private void sellWeaponToPlayer(Player seller) throws IOException {
        Market m = null;
        boolean hasMarket = false;
        for (Component component : seller.getComponents()) {
            if (component instanceof Market) {
                m = (Market) component;
                hasMarket = true;
                break;
            }
        }

        if (!hasMarket) {
            out.println("You must have a Market to sell weapons.");
            return;
        }

        List<Weapon> weaponsToSell = seller.getWeapons();
        if (weaponsToSell.isEmpty()) {
            out.println("You don't have any weapons to sell.");
            return;
        }

        out.println("Available weapons to sell:");
        for (int i = 0; i < weaponsToSell.size(); i++) {
            Weapon weapon = weaponsToSell.get(i);
            out.println((i + 1) + ". " + weapon.getClass().getSimpleName());
        }

        out.println("Enter the number of the weapon you want to sell:");


        String input = in.readLine();
        int weaponIndex = Integer.parseInt(input) - 1;

        if (weaponIndex < 0 || weaponIndex >= weaponsToSell.size()) {
            out.println("Invalid weapon selection.");
            return;
        }

        Weapon selectedWeapon = weaponsToSell.get(weaponIndex);

        out.println("Enter the username of the buyer:");

        String targetUsername = in.readLine();
        Player buyer = getPlayerByUsername(targetUsername);

        if (buyer == null) {
            out.println("Buyer not found.");
            return;
        }


        out.println("Enter the price for the weapon (" + selectedWeapon.getClass().getSimpleName() + "):");
        String priceInput = in.readLine();
        int price = Integer.parseInt(priceInput);

        m.playerTransactionWeapons(seller, buyer, selectedWeapon, price);

        out.println("You have successfully sold the " + selectedWeapon.getClass().getSimpleName() + " for " + price + " money.");
    }

    private String useCanonAction(Player currentPlayer) throws IOException {
        String s = "";
        Canon canonToUse = null;
        for (Weapon weapon : currentPlayer.getWeapons()) {
            if (weapon instanceof Canon) {
                canonToUse = (Canon) weapon;
                break;
            }
        }

        if (canonToUse == null) {
            out.println("You don't have any cannons to use.");
            return s;
        }

        out.println("Enter the username of the player you want to attack:");

        String targetUsername = in.readLine();
        Player enemy = getPlayerByUsername(targetUsername);

        if (enemy == null) {
            out.println("Player not found.");
            return s;
        }


        out.println("Enter the coordinates to attack (x, y):");

        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());


            if (!isValidCell(x, y)) {
                out.println("Invalid coordinates. Attack canceled.");
                return s;
            }

            s = s.concat(currentPlayer.useCanon(enemy, x, y, canonToUse));
            out.println("You fired a cannon at (" + x + ", " + y + ") on " + targetUsername + "'s board!");

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input format. Please use 'x, y' format.");
        }
        return s;
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < 20 && y >= 0 && y < 20;
    }

    private String useSuperCanonAction(Player currentPlayer) throws IOException {
        String s = "";
        SuperCanon superCanonToUse = null;
        for (Weapon weapon : currentPlayer.getWeapons()) {
            if (weapon instanceof SuperCanon) {
                superCanonToUse = (SuperCanon) weapon;
                break;
            }
        }


        if (superCanonToUse == null) {
            out.println("You don't have any SuperCannons to use.");
            return s;
        }


        out.println("Enter the username of the player you want to attack:");

        String targetUsername = in.readLine();
        Player enemy = getPlayerByUsername(targetUsername);

        if (enemy == null) {
            out.println("Player not found.");
            return s;
        }


        out.println("Enter the coordinates to attack (x, y):");

        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());


            if (!isValidCell(x, y)) {
                out.println("Invalid coordinates. Attack canceled.");
                return s;
            }

            s = s.concat(currentPlayer.useSuperCanon(enemy, x, y, superCanonToUse));
            out.println("You fired a SuperCanon at (" + x + ", " + y + ") and four random locations on " + targetUsername + "'s board!");

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input format. Please use 'x, y' format.");
        }
        return s;
    }

    private String useBombAction(Player currentPlayer) throws IOException {
        String s = "";
        Bomb bombToUse = null;
        for (Weapon weapon : currentPlayer.getWeapons()) {
            if (weapon instanceof Bomb) {
                bombToUse = (Bomb) weapon;
                break;
            }
        }


        if (bombToUse == null) {
            out.println("You don't have any bombs to use.");
            return s;
        }


        out.println("Enter the username of the player you want to attack:");

        String targetUsername = in.readLine();
        Player enemy = getPlayerByUsername(targetUsername);

        if (enemy == null) {
            out.println("Player not found.");
            return s;
        }

        out.println("Enter three coordinates to attack (format: x1,y1;x2,y2;x3,y3):");

        String input = in.readLine();
        try {
            String[] coordinateSets = input.split(";");
            if (coordinateSets.length != 3) {
                out.println("You must provide exactly three coordinate sets. Attack canceled.");
                return s;
            }


            String[] coord1 = coordinateSets[0].split(",");
            String[] coord2 = coordinateSets[1].split(",");
            String[] coord3 = coordinateSets[2].split(",");

            int x1 = Integer.parseInt(coord1[0].trim());
            int y1 = Integer.parseInt(coord1[1].trim());
            int x2 = Integer.parseInt(coord2[0].trim());
            int y2 = Integer.parseInt(coord2[1].trim());
            int x3 = Integer.parseInt(coord3[0].trim());
            int y3 = Integer.parseInt(coord3[1].trim());


            if (!isValidCell(x1, y1) || !isValidCell(x2, y2) || !isValidCell(x3, y3)) {
                out.println("One or more coordinates are invalid. Attack canceled.");
                return s;
            }


            s= s.concat(currentPlayer.useBomb(enemy, x1, y1, x2, y2, x3, y3, bombToUse));
            out.println("You used a bomb at (" + x1 + "," + y1 + "), (" + x2 + "," + y2 + "), and (" + x3 + "," + y3 + ") on " + targetUsername + "'s board!");

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input format. Please use 'x1,y1;x2,y2;x3,y3' format.");
        }
        return s;
    }

    private String useUltraCanonAction(Player currentPlayer) throws IOException {
        String s = "";
        UltraCanon ultraCanonToUse = null;
        for (Weapon weapon : currentPlayer.getWeapons()) {
            if (weapon instanceof UltraCanon) {
                ultraCanonToUse = (UltraCanon) weapon;
                break;
            }
        }


        if (ultraCanonToUse == null) {
            out.println("You don't have any UltraCannons to use.");
            return s;
        }


        out.println("Enter the username of the player you want to attack:");

        String targetUsername = in.readLine();
        Player enemy = getPlayerByUsername(targetUsername);

        if (enemy == null) {
            out.println("Player not found.");
            return s;
        }


        out.println("Enter up to 10 coordinates to attack (format: x1,y1;x2,y2;...):");

        String input = in.readLine();
        try {
            String[] coordinateSets = input.split(";");
            if (coordinateSets.length > 10) {
                out.println("You can specify a maximum of 10 coordinates. Attack canceled.");
                return s;
            }

            List<Pair<Integer, Integer>> targets = new ArrayList<>();
            for (String coordSet : coordinateSets) {
                String[] parts = coordSet.split(",");
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());

                if (!isValidCell(x, y)) {
                    out.println("Invalid coordinate: (" + x + "," + y + "). Attack canceled.");
                    return s;
                }

                targets.add(new Pair<>(x, y));
            }


            s = s.concat(currentPlayer.useUltraCanon(enemy, targets, ultraCanonToUse));
            out.println("You used an UltraCanon on " + targets.size() + " targets on " + targetUsername + "'s board!");

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input format. Please use 'x1,y1;x2,y2;...' format.");
        }
        return s;
    }

    private void buyShip(Player currentPlayer) throws IOException {
        Market m = new Market();
        boolean hasMarket = false;
        for (Component component : currentPlayer.getComponents()) {
            if (component instanceof Market) {
                m = (Market) component;
                hasMarket = true;
                break;
            }
        }

        if (!hasMarket) {
            out.println("You must have a Market to buy a ship.");
            return;
        }


        Ship ship = (Ship)m.marketSells(currentPlayer, TypesOfItems.SHIP);
        if (ship == null) {
            out.println("You do not have enough money to buy a ship.");
            return;
        }

        out.println("You have successfully bought a ship.");


        out.println("Enter the username of the player to send the ship to:");
        String targetUsername = in.readLine();
        Player enemy = getPlayerByUsername(targetUsername);

        if (enemy == null) {
            out.println("Player not found. Ship purchase canceled.");
            return;
        }


        out.println("Enter the coordinates (x, y) to inspect with the ship:");
        String input = in.readLine();

        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (x < 0 || x >= 20 || y < 0 || y >= 20) {
                out.println("Invalid coordinates. Ship purchase canceled.");
                return;
            }

            String info = ship.getInfo(enemy, x, y);
            out.println(info);

        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            out.println("Invalid input. Ship purchase canceled.");
        }
    }

}

