package org.abno.logic.components;

import org.abno.logic.components.Component;
import org.abno.logic.components.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphPanel extends JPanel {
    private Graph graph;

    public GraphPanel(Graph graph) {
        this.graph = graph;
        setPreferredSize(new Dimension(800, 600));  // Tamaño del panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Establecer el color de los nodos
        g2d.setColor(Color.BLUE);

        // Dibujar los nodos
        List<Component> nodes = graph.getNodes();
        int angle = 360 / nodes.size();  // Distribuir los nodos en un círculo
        int radius = 200;  // Radio del círculo donde se dibujarán los nodos
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int i = 0;

        for (Component node : nodes) {
            // Calcular la posición de cada nodo
            int x = (int) (centerX + radius * Math.cos(Math.toRadians(i * angle)));
            int y = (int) (centerY + radius * Math.sin(Math.toRadians(i * angle)));

            // Dibujar el nodo como un círculo
            g2d.fillOval(x - 15, y - 15, 30, 30);

            // Dibujar el id del nodo dentro del círculo
            g2d.setColor(Color.WHITE);
            g2d.drawString(String.valueOf(node.getId()), x - 5, y + 5);

            // Volver a poner el color para los arcos
            g2d.setColor(Color.BLUE);
            i++;
        }

        // Dibujar las conexiones entre nodos
        for (Component from : nodes) {
            int fromIndex = nodes.indexOf(from);
            int fromX = (int) (centerX + radius * Math.cos(Math.toRadians(fromIndex * angle)));
            int fromY = (int) (centerY + radius * Math.sin(Math.toRadians(fromIndex * angle)));

            List<Component> connections = graph.getConnections(from);
            for (Component to : connections) {
                int toIndex = nodes.indexOf(to);
                int toX = (int) (centerX + radius * Math.cos(Math.toRadians(toIndex * angle)));
                int toY = (int) (centerY + radius * Math.sin(Math.toRadians(toIndex * angle)));

                // Dibujar la línea entre los nodos
                g2d.drawLine(fromX, fromY, toX, toY);
            }
        }
    }
}
