package org.abno.frames.gameFrames;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;


public class PlayerInfoPanel extends JPanel {

    public PlayerInfoPanel() {
        setLayout(new GridLayout(4, 1));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Player Info", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), Color.WHITE));
        setBackground(new Color(30, 30, 30));

        JLabel playerNameLabel = new JLabel("Player Name: Player1", SwingConstants.CENTER);
        playerNameLabel.setFont(new Font("Arial ", Font.PLAIN, 18));
        playerNameLabel.setForeground(Color.WHITE);

        JLabel playerScoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        playerScoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        playerScoreLabel.setForeground(Color.WHITE);

        JLabel playerStatusLabel = new JLabel("Status: Waiting", SwingConstants.CENTER);
        playerStatusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        playerStatusLabel.setForeground(Color.WHITE);

        add(playerNameLabel);
        add(playerScoreLabel);
        add(playerStatusLabel);
    }
}