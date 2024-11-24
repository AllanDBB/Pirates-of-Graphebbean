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

    private static SmallBoardPanel gmp2;
    private static SmallBoardPanel gmp3;
    private static SmallBoardPanel gmp4;

    public GameFrame() {
        setTitle("Pirates Of Graphebbean - Game");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        // Panel principal del jugador
        gameBoardPanel = new GameBoardPanel(player);
        gameBoardPanel.setPreferredSize(new Dimension(600, 600));
        add(gameBoardPanel, BorderLayout.CENTER);

        // Panel para los tableros de otros jugadores
        JPanel otherPlayersPanel = new JPanel();
        otherPlayersPanel.setLayout(new GridLayout(3, 1, 10, 10)); // 3 filas, 1 columna
        otherPlayersPanel.setBackground(new Color(30, 30, 30));

        // Crear tableros para otros jugadores
        gmp2 = new SmallBoardPanel(new Player()); // Crear un nuevo jugador temporal
        gmp3 = new SmallBoardPanel(new Player()); // Crear un nuevo jugador temporal
        gmp4 = new SmallBoardPanel(new Player()); // Crear un nuevo jugador temporal

        // Ajustar el tamaño de los tableros de otros jugadores
        gmp2.setPreferredSize(new Dimension(300, 200)); // Tamaño pequeño
        gmp3.setPreferredSize(new Dimension(300, 200)); // Tamaño pequeño
        gmp4.setPreferredSize(new Dimension(300, 200)); // Tamaño pequeño

        // Agregar los tableros al panel de otros jugadores
        otherPlayersPanel.add(gmp2);
        otherPlayersPanel.add(gmp3);
        otherPlayersPanel.add(gmp4);

        // Agregar el panel de otros jugadores a la derecha del panel principal
        add(otherPlayersPanel, BorderLayout.EAST);

        // Información del jugador
        playerInfo = new PlayerInfoPanel();
        add(playerInfo, BorderLayout.WEST);

        // Chat
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

        // Actualizar los tableros de otros jugadores
        if (otherPlayers.size() > 0) {
            gmp2.setPlayer(otherPlayers.get(0));
            gmp2.updateSeaGrid();
        }
        if (otherPlayers.size() > 1) {
            gmp3.setPlayer(otherPlayers.get(1));
            gmp3.updateSeaGrid();
        }
        if (otherPlayers.size() > 2) {
            gmp4.setPlayer(otherPlayers.get(2));
            gmp4.updateSeaGrid();
        }

        gmp2.repaint();
        gmp3.repaint();
        gmp4.repaint();

        gameBoardPanel.repaint();
        playerInfo.repaint();
    }
}