package org.abno.frames.gameFrames;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class PlayerInfoPanel extends JPanel {

    private String username;
    private String iron;
    private String money;
    private String status;

    private JLabel playerNameLabel;
    private JLabel playerIronLabel;
    private JLabel playerMoneyLabel;

    public PlayerInfoPanel() {
        setLayout(new GridLayout(4, 1));
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Player Info", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), Color.WHITE));
        setBackground(new Color(30, 30, 30));

        playerNameLabel = new JLabel("username: " + username, SwingConstants.CENTER);
        playerNameLabel.setFont(new Font("Arial ", Font.PLAIN, 18));
        playerNameLabel.setForeground(Color.WHITE);

        playerIronLabel = new JLabel("iron: " + iron, SwingConstants.CENTER);
        playerIronLabel.setFont(new Font("Arial ", Font.PLAIN, 18));
        playerIronLabel.setForeground(Color.WHITE);

        playerMoneyLabel = new JLabel("money: " + money, SwingConstants.CENTER);
        playerMoneyLabel.setFont(new Font("Arial ", Font.PLAIN, 18));
        playerMoneyLabel.setForeground(Color.WHITE);

        add(playerNameLabel);
        add(playerIronLabel);
        add(playerMoneyLabel);
    }

    public void setUsername(String username) {
        this.username = username;
        playerNameLabel.setText("username: " + username);
        repaint(); // Actualiza la interfaz
    }

    public void setIron(String iron) {
        this.iron = iron;
        playerIronLabel.setText("iron: " + iron);
        repaint(); // Actualiza la interfaz
    }

    public void setMoney(String money) {
        this.money = money;
        playerMoneyLabel.setText("money: " + money);
        repaint();
    }
}