package org.abno.frames.gameFrames;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static org.abno.sockets.Client.send;

public class ChatPanel extends JPanel {

    private JTextArea chatArea;
    private JTextField chatInputField;
    private JButton sendButton;

    public ChatPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1), "Chat", TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 16), Color.WHITE));
        setBackground(new Color(30, 30, 30));

        // Área de chat
        chatArea = new JTextArea(10, 20);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBackground(new Color(30, 30, 30));
        chatArea.setForeground(Color.WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Panel de scroll para el área de chat
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        add(chatScrollPane, BorderLayout.CENTER);

        // Crear un panel para el campo de entrada y el botón
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        // Campo de entrada para el chat
        chatInputField = new JTextField();
        chatInputField.setFont(new Font("Arial", Font.PLAIN, 14));
        chatInputField.setBackground(new Color(50, 50, 50));
        chatInputField.setForeground(Color.WHITE);
        chatInputField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        chatInputField.setPreferredSize(new Dimension(200, 30));
        inputPanel.add(chatInputField, BorderLayout.CENTER);

        // Botón para enviar
        sendButton = new JButton("Enviar");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(70, 70, 70));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage(null);
            }
        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Escuchar la tecla Enter para enviar mensajes
        chatInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage(null);
                }
            }
        });

    }

    public void sendMessage(String message) {

        if (message == null){
            message = chatInputField.getText().trim();
            if (!message.isEmpty()) {
                chatArea.append("Tú: " + message + "\n");
                chatInputField.setText("");

                send(message);

            }
        } else {
            chatArea.append(message + "\n");
            chatInputField.setText("");
        }
    }
}