package org.abno.frames.gameFrames;

import org.abno.logic.components.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
    // Ruta base donde se encuentran las imágenes
    final String BASE_PATH = "C:\\Users\\Usuario 2024\\Documents\\Github\\Pirates-of-Graphebbean\\maincore\\assets\\";
    //C:\\Users\\Usuario 2024\\Documents\   cambiar esto por tu ruta
    // Imagen y color por defecto
    String imagePath = null;
    Color color = Color.BLUE; // Azul océano por defecto

    // Determina la imagen y el color según el tipo de `Item`
    if (item instanceof EnergySource) {
        imagePath = BASE_PATH + "fuentePoder.png";
        color = Color.PINK; // Respaldo: rosado
    } else if (item instanceof Connector) {
        imagePath = BASE_PATH + "conector.png";
        color = Color.YELLOW; // Respaldo: amarillo
    } else if (item instanceof Market) {
        imagePath = BASE_PATH + "mercadoDePiratas.png";
        color = Color.ORANGE; // Respaldo: naranja
    } else if (item instanceof Mine) {
        imagePath = BASE_PATH + "mina.png";
        color = Color.CYAN; // Respaldo: celeste
    } else if (item instanceof WitchTemple) {
        imagePath = BASE_PATH + "temploBruja.png";
        color = Color.GREEN; // Respaldo: verde
    } else if (item instanceof Armory) {
        imagePath = BASE_PATH + "armeria.png";
        color = Color.LIGHT_GRAY; // Respaldo: gris claro
    }

    // Intenta dibujar la imagen si existe una ruta definida
    if (imagePath != null) {
        File imgFile = new File(imagePath);  // Verificar si el archivo existe
        if (imgFile.exists()) {
            try {
                // Cargar la imagen desde el archivo usando ImageIO
                Image img = ImageIO.read(imgFile);

                // Redimensionar la imagen al tamaño de la celda
                img = img.getScaledInstance(TILE_SIZE, TILE_SIZE, Image.SCALE_SMOOTH);

                // Dibujar la imagen redimensionada en la celda
                g.drawImage(img, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
            } catch (IOException e) {
                // Capturar errores de lectura de imagen
                System.err.println("Error al cargar la imagen: " + imagePath);
                e.printStackTrace();
                g.setColor(color);
                g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        } else {
            // Si el archivo no existe, usa el color de respaldo
            System.err.println("Archivo no encontrado: " + imagePath);
            g.setColor(color);
            g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    } else {
        // Si no hay imagen definida, dibuja solo el color
        g.setColor(color);
        g.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    // Dibujar el borde de la celda
    g.setColor(Color.BLACK);
    g.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
}


}