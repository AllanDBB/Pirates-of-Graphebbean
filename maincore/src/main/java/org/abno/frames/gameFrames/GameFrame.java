package org.abno.frames.gameFrames;

import org.abno.logic.components.Player;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private static ChatPanel chat;
    private static PlayerInfoPanel playerInfo;
    private static GameBoardPanel gameBoardPanel;
    private static Player player;

    public GameFrame() {
        setTitle("Pirates Of Graphebbean - Game");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        // Crear el GameBoardPanel
        gameBoardPanel = new GameBoardPanel(player);

        // Ajustar el tamaño preferido del GameBoardPanel
        gameBoardPanel.setPreferredSize(new Dimension(600, 600)); // Tamaño más pequeño

        // Agregar el GameBoardPanel al marco
        add(gameBoardPanel, BorderLayout.CENTER);

        // Panel de información del jugador
        playerInfo = new PlayerInfoPanel();
        add(playerInfo, BorderLayout.WEST);

        // Panel de chat
        ChatPanel chatPanel = new ChatPanel();
        chat = chatPanel;
        add(chatPanel, BorderLayout.SOUTH);


    }

    public static void init() {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }

    public static ChatPanel getChat() { return chat; }

    public static void setPlayerInfo(String username, int iron, int money, Player playerLoad){
        playerInfo.setUsername(username);
        playerInfo.setIron(String.valueOf(iron));
        playerInfo.setMoney(String.valueOf(money));
        player = playerLoad;

        gameBoardPanel.setPlayer(player);
        gameBoardPanel.updateSeaGrid();
        gameBoardPanel.repaint();
        playerInfo.repaint();
    }
}