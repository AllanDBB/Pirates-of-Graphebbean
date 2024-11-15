/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.pirates.of.graphebbean;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



/**
 *
 * @author Usuario 2024
 */
public class PlayerScreen extends javax.swing.JFrame {
    public int [][] myMatrix;
    public int [][] matrix2;
    public int [][] matrix3;
    public int [][] matrix4;
    public boolean myTurn=false;
    private int selectedRow = -1;
    private int selectedColumn = -1;
    
    public int currentSelectedVictim; //esto aquí con player, depende de la lógica 
    private final ArrayList<Point> selectedCells = new ArrayList<>();
    

    /**
     * Creates new form PlayerScreen
     */
    public PlayerScreen() {
        initComponents();
        jPanel2.setLayout(new GridLayout(20, 20)); // Configuración de cuadrícula 20x20
        jPanel3.setLayout(new GridLayout(20, 20)); // Configuración de cuadrícula 20x20

        // Inicializar la matriz y configurar el panel
        myMatrix = generateExampleMatrix();
        inicialSea();
        matrix2=generateExampleMatrix();
        matrix3=generateExampleMatrix();
        matrix4=generateExampleMatrix();
        configureMySea(myMatrix);
        
        
    }
    public void write(String Text){
        jTextAreaChat.append(Text+"\n");
    }
    
    public void writeAttack(String Text){
        jTextAreaAttacksOccurred.append(Text+"\n");
    }
    
    private int[][] generateExampleMatrix() {
        // Crear una matriz de ejemplo de 20x20 con valores aleatorios entre 0 y 2
        int[][] matrix = new int[20][20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                matrix[i][j] = (int) (Math.random() * 3); // Valores aleatorios: 0, 1, o 2
            }
        }
        return matrix;
    }
    
    public void configureMySea(int[][] matrix) { // Que reciba una matriz
    jPanel2.removeAll();

    // Asegurar que la matriz tiene 20x20 elementos
    if (matrix.length != 20 || matrix[0].length != 20) {
        throw new IllegalArgumentException("La matriz debe ser de 20x20.");
    }

    // Recorrer la matriz y configurar el color de cada celda
    for (int i = 0; i < 20; i++) {
        for (int j = 0; j < 20; j++) {
            JPanel cell = new JPanel();
            int value = matrix[i][j];

            // Crear un JLabel para mostrar la imagen si es necesario
           

            // Asignar color o imagen basado en el valor de la celda
            switch (value) {
                case 1:
                    
                    cell.setBackground(Color.GREEN);// Agregar el JLabel con la imagen al panel
                    break;
                case 2:
                    cell.setBackground(Color.RED);
                    break;
                case 0:
                    cell.setBackground(Color.BLUE);
                    break;
                // Puedes agregar más casos según los valores de la matriz
                default:
                    cell.setBackground(Color.GRAY); // Color predeterminado
                    break;
            }

            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Borde opcional
            jPanel2.add(cell);
        }
    }

    jPanel2.revalidate(); // Actualizar el panel después de modificarlo
    jPanel2.repaint();
}

    
    public void inicialSea() {
    // Elimina todos los componentes actuales de los paneles
    jPanel2.removeAll();
    jPanel3.removeAll();

    // Configura un panel de 20x20 con celdas de color azul
    for (int i = 0; i < 20; i++) {
        for (int j = 0; j < 20; j++) {
            JPanel cell = new JPanel();

            // Pinta cada celda de azul
            cell.setBackground(Color.BLUE);

            // Añade un borde opcional
            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            // Añade la celda a ambos paneles
            jPanel2.add(cell);
            jPanel3.add(cell);
        }
    }

    // Actualiza los paneles después de modificar su contenido
    jPanel2.revalidate();
    jPanel2.repaint();
    jPanel3.revalidate();
    jPanel3.repaint();
}

    
    public void configureEnemySea(int[][] matrix) {
        jPanel3.removeAll();

        if (matrix.length != 20 || matrix[0].length != 20) {
            throw new IllegalArgumentException("La matriz debe ser de 20x20.");
        }

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                JPanel cell = new JPanel();
                int value = matrix[i][j];

                switch (value) {
                    case 1 -> cell.setBackground(Color.GREEN);
                    case 2 -> cell.setBackground(Color.RED);
                    case 0 -> cell.setBackground(Color.BLUE);
                    default -> cell.setBackground(Color.GRAY);
                }

                int row = i;
                int col = j;

                cell.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedRow = row;
                        selectedColumn = col;
                        System.out.println("Celda seleccionada en color: " + cell.getBackground());
                        // Selección para cuatro celdas
                        Point cellPosition = new Point(row, col);
                        if (selectedCells.contains(cellPosition)) {
                            selectedCells.remove(cellPosition); // Si ya está seleccionada, la deselecciona
                        } else if (selectedCells.size() < 4) {
                            selectedCells.add(cellPosition); // Añadir si hay espacio
                        } else {
                            JOptionPane.showMessageDialog(null, "Ya tienes cuatro celdas seleccionadas.");
                        }
                    }
                });

                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                jPanel3.add(cell);
            }
        }

        jPanel3.revalidate();
        jPanel3.repaint();
    }

    public void basicAttack() {
        if (selectedRow == -1 || selectedColumn == -1) {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna celda.");
        } else {
            String message= "Atacando la celda en fila " + selectedRow + " y columna " + selectedColumn;
            JOptionPane.showMessageDialog(this, message);
            writeAttack(message.toString());
        }
        this.selectedRow=-1; 
        this.selectedColumn=-1;
        
    }
    
    public void multiAttack() {
        if (selectedCells.size() == 4) {
            StringBuilder message = new StringBuilder("Atacando las celdas seleccionadas:\n");
            for (Point cell : selectedCells) {
                message.append("Fila ").append((int) cell.getX()).append(", Columna ").append((int) cell.getY()).append("\n");
            
                
            }
            JOptionPane.showMessageDialog(this, message.toString());
            writeAttack(message.toString());
        } else {
            JOptionPane.showMessageDialog(this, "Necesitas seleccionar cuatro celdas para un ataque múltiple.");
        }
        
        selectedCells.clear();
        this.selectedRow=-1; 
        this.selectedColumn=-1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaAttacksOccurred = new javax.swing.JTextArea();
        btnBasicAttack = new javax.swing.JButton();
        btnBarbaRojaCannon = new javax.swing.JButton();
        btnMultiAttack = new javax.swing.JButton();
        btnBomb = new javax.swing.JButton();
        btnShowPlayer2 = new javax.swing.JButton();
        btnShowPlayer3 = new javax.swing.JButton();
        btnShowPlayer4 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaChat = new javax.swing.JTextArea();
        jTextFieldChat = new javax.swing.JTextField();
        jButtonSendMessage = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(179, 166, 107));
        jPanel1.setBorder(new javax.swing.border.MatteBorder(null));

        jPanel4.setBackground(new java.awt.Color(211, 141, 71));
        jPanel4.setForeground(new java.awt.Color(178, 153, 62));

        jPanel3.setBackground(new java.awt.Color(153, 153, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 0, 204), 2));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 303, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 217, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI Historic", 3, 18)); // NOI18N
        jLabel2.setText("Enemy Sea");

        jTextAreaAttacksOccurred.setBackground(new java.awt.Color(255, 168, 137));
        jTextAreaAttacksOccurred.setColumns(20);
        jTextAreaAttacksOccurred.setRows(5);
        jScrollPane2.setViewportView(jTextAreaAttacksOccurred);

        btnBasicAttack.setBackground(new java.awt.Color(255, 51, 51));
        btnBasicAttack.setFont(new java.awt.Font("Segoe UI Black", 3, 14)); // NOI18N
        btnBasicAttack.setText("Basic Attack");
        btnBasicAttack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBasicAttackMouseClicked(evt);
            }
        });

        btnBarbaRojaCannon.setBackground(new java.awt.Color(255, 51, 51));
        btnBarbaRojaCannon.setFont(new java.awt.Font("Segoe UI Black", 3, 14)); // NOI18N
        btnBarbaRojaCannon.setText("Barba Roja Cannon");
        btnBarbaRojaCannon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBarbaRojaCannonMouseClicked(evt);
            }
        });

        btnMultiAttack.setBackground(new java.awt.Color(255, 0, 0));
        btnMultiAttack.setFont(new java.awt.Font("Segoe UI Black", 3, 14)); // NOI18N
        btnMultiAttack.setText("Multi-Attack");
        btnMultiAttack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnMultiAttackMouseClicked(evt);
            }
        });

        btnBomb.setBackground(new java.awt.Color(255, 0, 0));
        btnBomb.setFont(new java.awt.Font("Segoe UI Black", 3, 14)); // NOI18N
        btnBomb.setText("Bomb");
        btnBomb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBombMouseClicked(evt);
            }
        });

        btnShowPlayer2.setFont(new java.awt.Font("Segoe Script", 3, 14)); // NOI18N
        btnShowPlayer2.setForeground(new java.awt.Color(255, 0, 0));
        btnShowPlayer2.setText("Player2");
        btnShowPlayer2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnShowPlayer2MouseClicked(evt);
            }
        });

        btnShowPlayer3.setFont(new java.awt.Font("Segoe Script", 3, 14)); // NOI18N
        btnShowPlayer3.setForeground(new java.awt.Color(255, 0, 51));
        btnShowPlayer3.setText("Player3");
        btnShowPlayer3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnShowPlayer3MouseClicked(evt);
            }
        });

        btnShowPlayer4.setFont(new java.awt.Font("Segoe Script", 3, 14)); // NOI18N
        btnShowPlayer4.setForeground(new java.awt.Color(255, 0, 51));
        btnShowPlayer4.setText("Player4");
        btnShowPlayer4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnShowPlayer4MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBasicAttack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBomb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnBarbaRojaCannon, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnMultiAttack, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnShowPlayer2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnShowPlayer4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnShowPlayer3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(141, 141, 141)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(btnShowPlayer2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnShowPlayer3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnShowPlayer4))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel2)))
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 96, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnMultiAttack)
                    .addComponent(btnBasicAttack))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBarbaRojaCannon)
                    .addComponent(btnBomb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 153), 2));
        jPanel2.setForeground(new java.awt.Color(51, 0, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 285, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 278, Short.MAX_VALUE)
        );

        jLabel1.setFont(new java.awt.Font("Segoe UI Historic", 3, 18)); // NOI18N
        jLabel1.setText("My Sea");

        jTextAreaChat.setBackground(new java.awt.Color(228, 222, 202));
        jTextAreaChat.setColumns(20);
        jTextAreaChat.setRows(5);
        jScrollPane1.setViewportView(jTextAreaChat);

        jTextFieldChat.setText("Escribe:");

        jButtonSendMessage.setFont(new java.awt.Font("Segoe UI Emoji", 3, 14)); // NOI18N
        jButtonSendMessage.setText("Enviar");
        jButtonSendMessage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonSendMessageMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addGap(21, 21, 21)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addGap(127, 127, 127)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTextFieldChat, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonSendMessage)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonSendMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldChat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 146, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonSendMessageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSendMessageMouseClicked
        // TODO add your handling code here:
        String text= jTextFieldChat.getText();
        
        //enviar mensaje
        
    }//GEN-LAST:event_jButtonSendMessageMouseClicked

    private void btnBasicAttackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBasicAttackMouseClicked
        // TODO add your handling code here:
        if(myTurn){
            basicAttack();
        }else{
            JOptionPane.showMessageDialog(this, "No es mi turno");
        }
    }//GEN-LAST:event_btnBasicAttackMouseClicked

    private void btnMultiAttackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMultiAttackMouseClicked
        // TODO add your handling code here:
        if(myTurn){
            multiAttack();
        }else{
            JOptionPane.showMessageDialog(this, "No es mi turno");
        }
    }//GEN-LAST:event_btnMultiAttackMouseClicked

    private void btnBarbaRojaCannonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBarbaRojaCannonMouseClicked
        // TODO add your handling code here:
        if(myTurn){
            //no hecho aún
        }else{
            JOptionPane.showMessageDialog(this, "No es mi turno");
        }
    }//GEN-LAST:event_btnBarbaRojaCannonMouseClicked

    private void btnBombMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBombMouseClicked
        // TODO add your handling code here:
        if(myTurn){
            //no hecho aún
        }else{
            JOptionPane.showMessageDialog(this, "No es mi turno");
        }
    }//GEN-LAST:event_btnBombMouseClicked

    private void btnShowPlayer2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnShowPlayer2MouseClicked
        // TODO add your handling code here:
        configureEnemySea(matrix2);
    }//GEN-LAST:event_btnShowPlayer2MouseClicked

    private void btnShowPlayer3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnShowPlayer3MouseClicked
        // TODO add your handling code here:
        configureEnemySea(matrix3);
    }//GEN-LAST:event_btnShowPlayer3MouseClicked

    private void btnShowPlayer4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnShowPlayer4MouseClicked
        // TODO add your handling code here:
        configureEnemySea(matrix4);
    }//GEN-LAST:event_btnShowPlayer4MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PlayerScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlayerScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlayerScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlayerScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        int[][] matrix = null;
        
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PlayerScreen().setVisible(true);
                
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBarbaRojaCannon;
    private javax.swing.JButton btnBasicAttack;
    private javax.swing.JButton btnBomb;
    private javax.swing.JButton btnMultiAttack;
    private javax.swing.JButton btnShowPlayer2;
    private javax.swing.JButton btnShowPlayer3;
    private javax.swing.JButton btnShowPlayer4;
    private javax.swing.JButton jButtonSendMessage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaAttacksOccurred;
    private javax.swing.JTextArea jTextAreaChat;
    private javax.swing.JTextField jTextFieldChat;
    // End of variables declaration//GEN-END:variables
}
