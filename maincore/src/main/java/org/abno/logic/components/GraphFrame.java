package org.abno.logic.components;

import org.abno.logic.components.Graph;
import org.abno.logic.components.Component;

import javax.swing.*;

public class GraphFrame extends JFrame {

    public GraphFrame(Graph graph) {
        setTitle("Graph Visualization");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Crear y agregar el panel de grafo
        GraphPanel graphPanel = new GraphPanel(graph);
        add(graphPanel);
    }

    public void init(Player player){
        SwingUtilities.invokeLater(() -> {
            GraphFrame frame = new GraphFrame(player.getGraph());
            frame.setVisible(true);
        });
    }

}


