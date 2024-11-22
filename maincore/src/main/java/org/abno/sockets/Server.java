package org.abno.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


import org.abno.logic.components.*;
import org.abno.logic.enums.TypesOfItems;
import org.abno.logic.enums.TypesOfWeapons;

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


                            currentPlayer.getComponents().add(connector);
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

        out.println("Enter the coordinates x, y to place the Energy Source (2x2): 0-18");
        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (isValidPositionForEnergySource(currentPlayer, x, y)) {

                EnergySource energySource = new EnergySource();
                List<Pair<Integer, Integer>> location = List.of(
                        new Pair<>(x, y),
                        new Pair<>(x + 1, y),
                        new Pair<>(x, y + 1),
                        new Pair<>(x + 1, y + 1)
                );
                currentPlayer.placeComponent(energySource, location);


                currentPlayer.getComponents().add(energySource);
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


        out.println("Enter the coordinates x, y to place the Market (1x2): 0-20 y 0-19");
        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (isValidPositionForMarket(currentPlayer, x, y)) {
                Market market = new Market();
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

        Component component;
        switch (componentType.toLowerCase()) {
            case "mine":
                component = new Mine();
                break;
            case "witchtemple":
                component = new WitchTemple();
                break;
            case "armory":
                component = new Armory();
                break;
            default:
                out.println("Invalid component type.");
                return;
        }

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


                currentPlayer.getComponents().add(component);
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

        out.println("Enter the coordinates x, y to place the Armory (2x1): 0-19 y 0-20");
        String input = in.readLine();
        try {
            String[] parts = input.split(",");
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (isValidPositionForTwoByOne(currentPlayer, x, y)) {
                Armory armory = new Armory();
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


                currentPlayer.getComponents().add(armory);
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
            if (component instanceof Connector) {
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
                    witchTemple.shield(currentPlayer, shots);
                    out.println("Shield activated with " + shots + " shots.");
                } else if (action == 1) {

                    out.println("Enter the target player's username for Kraken attack:");
                    String targetUsername = in.readLine();
                    Player targetPlayer = getPlayerByUsername(targetUsername);

                    if (targetPlayer != null) {
                        witchTemple.kraken(targetPlayer);
                        out.println("Kraken unleashed on " + targetUsername + "'s components!");
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

}

