package org.abno.frames.gameFrames;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;



public class GameBoardPanel extends JPanel {

    public GameBoardPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Game Board", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), Color.WHITE));
        setBackground(new Color(30, 30, 30));

        JLabel boardLabel = new JLabel("Game Board Area", SwingConstants.CENTER);
        boardLabel.setFont(new Font("Arial", Font.BOLD, 24));
        boardLabel.setForeground(Color.WHITE);
        add(boardLabel, BorderLayout.CENTER);
    }
}