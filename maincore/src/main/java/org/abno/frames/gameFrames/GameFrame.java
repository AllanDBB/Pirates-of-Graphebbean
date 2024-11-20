package org.abno.frames.gameFrames;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {

    private static ChatPanel chat;

    public GameFrame() {
        setTitle("Pirates Of Graphebbean - Game");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        // Panel del tablero de juego
        GameBoardPanel gameBoardPanel = new GameBoardPanel();
        add(gameBoardPanel, BorderLayout.CENTER);

        // Panel de información del jugador
        PlayerInfoPanel playerInfoPanel = new PlayerInfoPanel();
        add(playerInfoPanel, BorderLayout.WEST);

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
}