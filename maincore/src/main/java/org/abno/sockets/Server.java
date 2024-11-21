package org.abno.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import org.abno.logic.components.Player;

public class Server {


    private static final int PORT = 8060;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

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

    // Game constants
    private List<Player> players;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
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

        if (message.startsWith("@")){
            if (message.equalsIgnoreCase("@Ready")) {
                setReady();
                out.println("You are ready!");
            }
        } else {
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
}

