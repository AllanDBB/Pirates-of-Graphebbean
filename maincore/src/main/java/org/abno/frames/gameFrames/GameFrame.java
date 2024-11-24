package org.abno.frames.gameFrames;

import org.abno.logic.components.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GameFrame extends JFrame {

    private static ChatPanel chat;
    private static PlayerInfoPanel playerInfo;
    private static GameBoardPanel gameBoardPanel;
    private static Player player;
    private static List<Player> otherPlayers;

    public GameFrame() {
        setTitle("Pirates Of Graphebbean - Game");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        gameBoardPanel = new GameBoardPanel(player);
        gameBoardPanel.setPreferredSize(new Dimension(600, 600));
        add(gameBoardPanel, BorderLayout.CENTER);

        playerInfo = new PlayerInfoPanel();
        add(playerInfo, BorderLayout.WEST);

        chat = new ChatPanel();
        add(chat, BorderLayout.SOUTH);
    }

    public static void init() {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }

    public static ChatPanel getChat() {
        return chat;
    }

    public static void setPlayerInfo(String username, int iron, int money, Player playerLoad, List<Player> otherPlayersLoad) {
        playerInfo.setUsername(username);
        playerInfo.setIron(String.valueOf(iron));
        playerInfo.setMoney(String.valueOf(money));
        player = playerLoad;
        otherPlayers = otherPlayersLoad;

        gameBoardPanel.setPlayer(player);
        gameBoardPanel.updateSeaGrid();
        gameBoardPanel.repaint();
        playerInfo.repaint();
    }

}