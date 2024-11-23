package org.abno.frames.gameFrames;

import org.abno.logic.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class GameBoardPanel extends JPanel {
    private static final int TILE_SIZE = 24; // Tamaño de cada celda
    private static final int BOARD_SIZE = 20; // Tamaño del tablero (20x20)
    private static final int PADDING = 15; // Espacio entre el borde y el tablero
    private Item[][] seaGrid; // Matriz que representa el estado del tablero
    private Player mainPlayer; // Jugador principal
    private List<Player> otherPlayers; // Lista de jugadores adicionales

    public GameBoardPanel(Player mainPlayer) {
        this.mainPlayer = mainPlayer;
        setLayout(null); // Sin diseño adicional
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
                "Game Board",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16),
                Color.WHITE));
        setBackground(new Color(30, 30, 30));

        // Ajustar el tamaño preferido del panel para incluir el padding
        int panelWidth = BOARD_SIZE * TILE_SIZE + 2 * PADDING;
        int panelHeight = BOARD_SIZE * TILE_SIZE + 2 * PADDING;
        setPreferredSize(new Dimension(panelWidth + 3 * (BOARD_SIZE * TILE_SIZE / 2), panelHeight));
    }

    public void setPlayer(Player mainPlayer) {
        this.mainPlayer = mainPlayer;
    }

    public void setOtherPlayers(List<Player> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }

    public void updateSeaGrids() {
        // Actualiza el estado del tablero para el jugador principal
        if (mainPlayer != null) {
            seaGrid = mainPlayer.getSeaGrid();
        }
        // Actualiza el estado del tablero para los otros jugadores
        if (otherPlayers != null) {
            for (Player player : otherPlayers) {
                if (player != null) {
                    if (player != mainPlayer) {
                        player.getSeaGrid();
                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar el tablero del jugador principal
        drawMainBoard(g, mainPlayer, 0, 0); // Tablero grande

        // Dibujar los tableros de los otros jugadores
        if (otherPlayers != null) {
            drawOtherPlayersBoards(g);
        }
    }

    private void drawMainBoard(Graphics g, Player player, int offsetX, int offsetY) {
        if (player != null) {
            Item[][] seaGrid = player.getSeaGrid();
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    drawTile(g, seaGrid[i][j], j, i, offsetX, offsetY); // Sin desplazamiento para el tablero principal
                }
            }
        }
    }

    private void drawOtherPlayersBoards(Graphics g) {
        for (int i = 0; i < otherPlayers.size(); i++) {
            Player player = otherPlayers.get(i);
            if (player != null) {
                int offsetX = BOARD_SIZE * TILE_SIZE + PADDING + i * (BOARD_SIZE * TILE_SIZE / 2);
                int offsetY = 0;
                Item[][] seaGrid = player.getSeaGrid();
                for (int j = 0; j < BOARD_SIZE; j++) {
                    for (int k = 0; k < BOARD_SIZE; k++) {
                        drawTile(g, seaGrid[j][k], k, j, offsetX, offsetY); // Desplazamiento para los tableros pequeños
                    }
                }
            }
        }
    }

    private void drawTile(Graphics g, Item item, int x, int y, int offsetX, int offsetY) {
        Color color;
        if (item instanceof EnergySource) {
            color = Color.PINK; // Rosado
        } else if (item instanceof Connector) {
            color = Color.YELLOW; // Amarillo
        } else if (item instanceof Market) {
            color = Color.ORANGE; // Naranja
        } else if (item instanceof Mine) {
            color = Color.CYAN; // Celeste
        } else if (item instanceof WitchTemple) {
            color = Color.GREEN; // Verde
        } else if (item instanceof Armory) {
            color = Color.LIGHT_GRAY; // Gris claro
        } else {
            color = Color .BLUE; // Azul océano
        }

        // Dibujar el rectángulo que representa la celda con el padding aplicado
        int xPos = PADDING + offsetX + x * TILE_SIZE;
        int yPos = PADDING + offsetY + y * TILE_SIZE;
        g.setColor(color);
        g.fillRect(xPos, yPos, TILE_SIZE, TILE_SIZE);
        g.setColor(Color.BLACK); // Color del borde
        g.drawRect(xPos, yPos, TILE_SIZE, TILE_SIZE); // Dibuja el borde
    }
}