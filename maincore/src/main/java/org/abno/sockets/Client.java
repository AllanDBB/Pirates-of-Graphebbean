package org.abno.sockets;

import org.abno.frames.gameFrames.ChatPanel;
import org.abno.frames.initFrame.InitFrame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.abno.logic.components.Connector;
import org.abno.logic.components.EnergySource;
import org.abno.logic.components.Market;
import org.abno.logic.components.Player;

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8060;
    private static PrintWriter out;

    private static ChatPanel chat;
    private static InitFrame frame;
    private static Player player;

    public static void send(String message){
        if (out != null){
            out.println(message);
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
    }

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Conectado al servidor. Ingresa comandos:");

            frame = new InitFrame();
            frame.Init();


            // Hilo para escuchar respuestas del servidor
            Thread listener = new Thread(() -> {
                try {
                    String response;
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);

                        if (chat != null){
                            sendToClient(response);
                        }

                        if (response.equals("@SetChat")){
                            Thread.sleep(1);
                            initPlayer();
                        }

                        if (response.equals("@StartGame")){
                            frame.getLobbyFrame().startGame();
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
                if ("exit".equalsIgnoreCase(input)) {
                    System.out.println("Desconectando del servidor...");
                    break;
                }
                out.println(input);
            }

            // Cerrar el socket después de salir
            socket.close();
        } catch (IOException e) {
            System.err.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}
