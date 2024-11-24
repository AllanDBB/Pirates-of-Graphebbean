package org.abno.sockets;

import org.abno.frames.gameFrames.ChatPanel;
import org.abno.frames.gameFrames.GameFrame;
import org.abno.frames.initFrame.InitFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.abno.logic.components.*;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8060;
    private static PrintWriter out;

    private static ChatPanel chat;
    private static InitFrame frame;
    private static Player player;
    private static String username;
    private static boolean ASF;
    private static GraphFrame graphFrame;
    private static int activePlayers;

    private static List<Player> otherPlayers = new ArrayList<>();


    public static void send(String message){
        if (out != null){
            out.println(message);

            if (username == null){
                username = message;
            }


            System.out.println("Sent value: " + message);
        } else {
            System.out.println("Vea mi loco aquí no tiene que llegar, algo pasó");
        }
    }

    public static void sendToClient(String message){
        chat.sendMessage(message);
    }

    public static void initPlayer(){
        player = new Player();
        chat = frame.getChat();
        System.out.println(chat);
        ASF = true;

    }

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Conectado al servidor. Ingresa comandos:");

            frame = new InitFrame();
            frame.Init();


            Thread updateUserT = new Thread(()->{

                while(ASF){
                    try {
                        updateUser();
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
            });


            // Hilo para escuchar respuestas del servidor
            Thread listener = new Thread(() -> {
                try {

                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);

                        if (chat != null && !response.startsWith("@")){
                            sendToClient(response);
                        }

                        if (response.equals("@SetChat")){
                            Thread.sleep(1000);
                            initPlayer();
                            updateUserT.start();
                            activePlayers = Integer.parseInt(in.readLine());

                            for (int i = 0; i < activePlayers-1; i++){
                                Player temp = new Player();
                                otherPlayers.add(temp);
                            }
                        }

                        if (response.equals("@StartGame")){
                            frame.getLobbyFrame().startGame();
                        }

                        if (response.startsWith("@Matrix")){
                            parseSeaGrids(response.substring(8).trim());
                            updateUser();
                        }

                        if (response.startsWith("@UserInfo")){
                            parseUserInfo(response.substring(10).trim());
                            updateUser();
                        }

                    }
                } catch (IOException e) {
                    System.err.println("Conexión con el servidor cerrada.");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            listener.start();

            String input;
            while ((input = console.readLine()) != null) {
                if (username == null){
                    username = input;
                }

                /*
                if (input == "@ShowGraph"){
                    System.out.println("aca estoy");
                    graphFrame = new GraphFrame(player.getGraph());
                    graphFrame.init(player);
                }*/


                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Desconectando del servidor...");
                    break;
                }
                out.println(input);
            }

            socket.close();
        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    private static void updateUser(){
        //send("@GetUserInfo");
        //send("@GetSeaGrids");
        frame.getLobbyFrame().getGameFrame().setPlayerInfo(username, player.getIron(), player.getMoney(), player, otherPlayers);
    }

    public static void parseSeaGrids(String serializedGrids) {

        // Limpia la entrada, eliminando espacios y corchetes innecesarios
        serializedGrids = serializedGrids.trim();
        String[] gridData = serializedGrids.split("\\], \\["); // Divide por matrices separadas

        System.out.println(serializedGrids);
        // Elimina los corchetes iniciales y finales de la lista general
        gridData[0] = gridData[0].substring(1); // Elimina el "[" inicial de la primera matriz
        gridData[gridData.length - 1] = gridData[gridData.length - 1].replace("]", ""); // Elimina "]" final

        System.out.println(serializedGrids);
        // Asegúrate de que el número de matrices coincida con el número de jugadores
        if (gridData.length != otherPlayers.size()) {
            throw new IllegalArgumentException("Número de matrices no coincide con el número de jugadores");
        }

        // Itera sobre cada jugador y su correspondiente matriz
        for (int playerIndex = 0; playerIndex < otherPlayers.size(); playerIndex++) {
            String[] cellData = gridData[playerIndex].split(", "); // Divide las celdas por cada matriz
            int gridSize = (int) Math.sqrt(cellData.length); // Asume que la matriz es cuadrada

            if (gridSize * gridSize != cellData.length) {
                throw new IllegalArgumentException("Matriz no es cuadrada para jugador " + playerIndex);
            }

            // Reconstruye la matriz `Item[][]`
            Item[][] seaGrid = new Item[gridSize][gridSize];
            int cellIndex = 0;

            for (int i = 0; i < gridSize; i++) {
                for (int j = 0; j < gridSize; j++) {
                    String cell = cellData[cellIndex++].trim();
                    switch (cell) {
                        case "E":
                            seaGrid[i][j] = new EnergySource();
                            break;
                        case "C":
                            seaGrid[i][j] = new Connector();
                            break;
                        case "M":
                            seaGrid[i][j] = new Market();
                            break;
                        case "m":
                            seaGrid[i][j] = new Mine();
                            break;
                        case "T":
                            seaGrid[i][j] = new WitchTemple();
                            break;
                        case "A":
                            seaGrid[i][j] = new Armory();
                            break;
                        case "O":
                        default:
                            seaGrid[i][j] = null; // O representa vacío
                            break;
                    }
                }
            }

            // Asigna la matriz al jugador correspondiente
            otherPlayers.get(playerIndex).setSeaGrid(seaGrid);
        }

    }

    private static void parseUserInfo(String message) {
        String data = message;
        System.out.print(data);
        String[] parts = data.split("], ");

        if (parts.length != 3) {
            throw new IllegalArgumentException("El mensaje no contiene las tres partes requeridas: [money], [iron], [seaGrid]");
        }

        // Procesa el dinero
        String moneyPart = parts[0].substring(1); // Elimina el corchete de apertura '['
        int money = Integer.parseInt(moneyPart.trim());
        player.setMoney(money);

        // Procesa el hierro
        String ironPart = parts[1].substring(1); // Elimina el corchete de apertura '['
        int iron = Integer.parseInt(ironPart.trim());
        player.setIron(iron);

        // Procesa la matriz de seaGrid
        String seaGridPart = parts[2].substring(1, parts[2].length() - 1); // Elimina '[' y ']'
        String[] gridElements = seaGridPart.split(", ");

        // Convierte el array plano en la matriz
        Item[][] seaGrid = parseSeaGridP(gridElements);

        // Asigna la matriz al jugador
        player.setSeaGrid(seaGrid);
    }

    private static Item[][] parseSeaGridP(String[] gridElements) {
        int gridSize = (int) Math.sqrt(gridElements.length); // Asume que la matriz es cuadrada
        if (gridSize * gridSize != gridElements.length) {
            throw new IllegalArgumentException("La matriz no es cuadrada");
        }

        Item[][] seaGrid = new Item[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                String element = gridElements[i * gridSize + j];
                seaGrid[i][j] = createItemFromSymbol(element.trim());
            }
        }
        return seaGrid;
    }

    private static Item createItemFromSymbol(String symbol) {
        switch (symbol) {
            case "E":
                return new EnergySource();
            case "C":
                return new Connector();
            case "M":
                return new Market();
            case "m":
                return new Mine();
            case "T":
                return new WitchTemple();
            case "A":
                return new Armory();
            case "O":
            default:
                return null;
        }
    }

}
