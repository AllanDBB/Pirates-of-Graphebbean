package org.abno.frames.lobbyFrame;

import org.abno.frames.gameFrames.ChatPanel;
import org.abno.frames.gameFrames.GameFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.abno.sockets.Client.send;

public class LobbyFrame extends JFrame {

    private JTextField nameField; // Campo para ingresar el nombre
    private JButton readyButton; // Botón para marcar como listo
    private JCheckBox readyCheckBox; // Checkbox para indicar que está listo

    private static GameFrame gameFrame;

    public LobbyFrame() {
        setTitle("Lobby");
        setSize(800, 600); // Tamaño de la ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana

        // Crear un panel personalizado para el fondo
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Crear un fondo con gradiente
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 30, 30), 0, getHeight(), new Color(20, 20, 20));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        panel.setLayout(new GridBagLayout()); // Usar GridBagLayout para organizar los componentes
        add(panel); // Agregar el panel al JFrame

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Espaciado entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Llenar el espacio horizontalmente

        // Etiqueta para el campo de nombre
        JLabel nameLabel = new JLabel("Enter your name: ");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        gbc.gridwidth = 2; // Ocupa dos columnas
        panel.add(nameLabel, gbc);

        // Campo para ingresar el nombre
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        nameField.setBackground(new Color(60, 60, 60)); // Fondo del campo de texto
        nameField.setForeground(Color.WHITE); // Texto blanco
        nameField.setCaretColor(Color.WHITE); // Color del cursor
        nameField.setText("Enter your name: "); // Texto de marcador de posición
        nameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (nameField.getText().equals("Enter your name: ")) {
                    nameField.setText("");
                    nameField.setForeground(Color.WHITE);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (nameField.getText().isEmpty()) {
                    nameField.setForeground(Color.GRAY);
                    nameField.setText("Enter your name: ");
                }
            }
        });
        gbc.gridy = 1; // Fila 1
        panel.add(nameField, gbc);

        // Checkbox para indicar que está listo
        readyCheckBox = new JCheckBox("Ready");
        readyCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        readyCheckBox.setBackground(new Color(40, 40, 40)); // Fondo oscuro
        readyCheckBox.setForeground(Color.WHITE); // Texto blanco
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 2; // Fila 2
        gbc.gridwidth = 1; // Ocupa una columna
        panel.add(readyCheckBox, gbc);

        // Botón para marcar como listo
        readyButton = new JButton("Go to the game!");
        readyButton.setFont(new Font("Arial", Font.BOLD, 16));
        readyButton.setBackground(new Color(70, 130, 180)); // Fondo del botón
        readyButton.setForeground(Color.WHITE); // Texto blanco
        readyButton.setBorder(BorderFactory.createRaisedBevelBorder()); // Añadir borde elevado
        readyButton.setFocusPainted(false); // Quitar el borde de enfoque
        readyButton.setContentAreaFilled(false); // Hacer que el área del botón sea transparente
        readyButton.setOpaque(true); // Hacer que el botón sea opaco
        readyButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                readyButton.setBackground(new Color(100, 150, 200)); // Color al pasar el mouse
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                readyButton.setBackground(new Color(70, 130, 180)); // Color original
            }
        });
        readyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (readyCheckBox.isSelected()) {
                    JOptionPane.showMessageDialog(LobbyFrame.this, "You are ready!", "Status", JOptionPane.INFORMATION_MESSAGE);
                    send(nameField.getText());
                    send("@Ready");

                    // Cambiar el color del botón para indicar que el jugador está listo
                    readyButton.setBackground(new Color(0, 128, 0)); // Verde para indicar listo
                    readyButton.setText("Ready!"); // Cambiar el texto del botón

                } else {
                    JOptionPane.showMessageDialog(LobbyFrame.this, "You need to be marked as ready.", "Status", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        gbc.gridx = 1;
        panel.add(readyButton, gbc);
    }

    public void startGame() {
        setVisible(false); // Ocultar el LobbyFrame
        gameFrame = new GameFrame();
        gameFrame.init();
    }

    public static ChatPanel getChat() {
        return gameFrame.getChat();
    }

    public static void init() {
        SwingUtilities.invokeLater(() -> {
            LobbyFrame lobbyFrame = new LobbyFrame();
            lobbyFrame.setVisible(true);
        });
    }
}