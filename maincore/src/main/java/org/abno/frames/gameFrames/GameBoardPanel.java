package org.abno.frames.gameFrames;

import org.abno.logic.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GameBoardPanel extends JPanel {
    private static final int TILE_SIZE = 24; // Tamaño de cada celda
    private static final int BOARD_SIZE = 20; // Tamaño del tablero (20x20)
    private Item[][] seaGrid; // Matriz que representa el estado del tablero
    private static Player player;

    public static void setPlayer(Player player) {
        GameBoardPanel.player = player;
    }

    public void updateSeaGrid(){
        seaGrid = player.getSeaGrid();
    }

    public GameBoardPanel(Player player) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Game Board", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), Color.WHITE));
        setBackground(new Color(30, 30, 30));

        setPreferredSize(new Dimension(BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE));
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar el tablero
        for (int i = 0; i < BOARD_SIZE; i++) { // Dibujar desde la fila 0 hasta la fila 19
            for (int j = 0; j < BOARD_SIZE; j++) {
                drawTile(g, seaGrid[i][j], j, i); // Usa i y j directamente
            }
        }
    }

    private void drawTile(Graphics g, Item item, int x, int y) {
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
            color = Color.BLUE; // Azul océano
        }

        // Dibujar el rectángulo que representa la celda
        g.setColor(color);
        g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g.setColor(Color.BLACK); // Color del borde
        g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE); // Dibuja el borde
    }
}