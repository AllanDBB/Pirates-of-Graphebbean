package org.abno.frames.initFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.abno.frames.gameFrames.ChatPanel;
import org.abno.frames.gameFrames.GameFrame;
import org.abno.frames.lobbyFrame.LobbyFrame;

public class InitFrame extends JFrame {

    private static LobbyFrame lobbyFrame;
    private Image shipImage;

    public InitFrame() {
        setTitle("Pirates Of Graphebbean");
        setSize(1366, 768);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            shipImage = ImageIO.read(new File("C:\\Users\\adbyb\\OneDrive\\Documentos\\GitHub\\Pirates-of-Graphebbean\\maincore\\assets\\initShip.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 50, 100), 0, getHeight(), new Color(0, 150, 200));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                if (shipImage != null) {
                    int newWidth = shipImage.getWidth(null) / 2;
                    int newHeight = shipImage.getHeight(null) / 2;
                    Image resizedShipImage = shipImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    int x = (getWidth() - newWidth) / 2;
                    int y = getHeight() - newHeight - 150; // Mover más hacia abajo

                    // Establecer opacidad
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)); // 30% de opacidad
                    g2d.drawImage(resizedShipImage, x, y, null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // Restablecer opacidad
                }
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Pirates Of Graphebbean", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 64));
        label.setForeground(Color.WHITE);

        JButton button = new JButton("Start");
        button.setFont(new Font("Arial", Font.BOLD, 36));
        button.setBackground(new Color(0, 150, 136));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 60));
        button.setOpaque(true);
        button.setContentAreaFilled(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 180, 160));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 150, 136));
            }
        });

        // Agregar ActionListener al botón para abrir el LobbyFrame
        button.addActionListener(e -> {
            lobbyFrame = new LobbyFrame();
            lobbyFrame.init();
            dispose();
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(label, gbc);

        gbc.gridy = 1;
        mainPanel.add(button, gbc);

        add(mainPanel);

    }

    public static ChatPanel getChat(){
        return lobbyFrame.getChat();
    }

    public static void Init() {
        SwingUtilities.invokeLater(() -> {
            InitFrame frame = new InitFrame();
            frame.setVisible(true);
        });
    }
}