/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Ventanas;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author WINDOWS
 */
public class jfmPanelInicio extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(jfmPanelInicio.class.getName());

    /**
     * Creates new form jfmPanelInicio
     */
    public jfmPanelInicio() {
        initComponents();
        
        //maximo caracteres publicacion
        ((javax.swing.text.AbstractDocument) txtPublicar.getDocument()) .setDocumentFilter(new javax.swing.text.DocumentFilter() {
        
        public void insertString(FilterBypass fb, int off, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if ((fb.getDocument().getLength() + str.length()) <= 350)
                super.insertString(fb, off, str, a);
        }
        
        public void replace(FilterBypass fb, int off, int len, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if ((fb.getDocument().getLength() - len + str.length()) <= 350)
                super.replace(fb, off, len, str, a);
        }
    });
        
        setLocationRelativeTo(null);
        
        PLFeed.setLayout(new BoxLayout(PLFeed, BoxLayout.Y_AXIS)); //Dimensiones de Feed
        PLPublicar.setMaximumSize(new Dimension(625, 136));
        PLPublicar.setAlignmentX(Component.LEFT_ALIGNMENT);
                
        File foto = new File("src\\main\\resources\\Imgs_PFP\\" + Sesion.fotodeperfil); //fdp en el inicio
        lbNomUsuario.setText(Sesion.nombreCompleto); //Usuario en inicio
        lbTipo.setText(Sesion.tipoUsuario); //FDP
        if (foto.exists()) {
            ImageIcon icon = new ImageIcon(foto.getAbsolutePath());
            Image imgEscalada = icon.getImage().getScaledInstance(
            lbFotodePerfil.getWidth(), lbFotodePerfil.getHeight(), Image.SCALE_SMOOTH);
            lbFotodePerfil.setIcon(new ImageIcon(imgEscalada));
        }
        
        cargarFeed();
        SwingUtilities.invokeLater(() -> {
        jScrollPane1.getVerticalScrollBar().setValue(0);
        });
        
        
    }

    
    private void cargarFeed() {
    PLFeed.removeAll(); // limpia antes de recargar
    PLFeed.add(PLPublicar);
    
    Component[] componentes = PLFeed.getComponents();
    for (Component c : componentes) {
        if (c != PLPublicar) {
            PLFeed.remove(c);
        }
    }
    
    File carpeta = new File("src\\main\\Publicaciones");
    if (!carpeta.exists()) carpeta.mkdirs();
    
    File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".txt"));

    if (archivos != null) {
        // ordenar del más reciente al más viejo
        Arrays.sort(archivos, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        for (File f : archivos) {
            JPanel tarjeta = crearTarjetaPost(f);
            PLFeed.add(tarjeta);
            PLFeed.add(Box.createVerticalStrut(10)); // espacio entre tarjetas
        }
    }

    PLFeed.revalidate();
    PLFeed.repaint();
    }
    
    private void guardarPublicacion() {
    String contenido = txtPublicar.getText().trim();

    if (contenido.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Escribe algo antes de publicar.");
        return;
    }

    File carpeta = new File("src\\main\\Publicaciones");
    if (!carpeta.exists()) carpeta.mkdirs();

    String nombreArchivo = Sesion.usuarioActual + "_" + System.currentTimeMillis() + ".txt";
    File archivo = new File(carpeta, nombreArchivo);

    try {
        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        String fecha = ahora.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        FileWriter writer = new FileWriter(archivo);
        writer.write(Sesion.nombreCompleto + "\n"); // línea 1: nombre
        writer.write(fecha + "\n");                 // linea 2: fecha
        writer.write(contenido + "\n");             // línea 3: contenido
        writer.write(Sesion.tipoUsuario + "\n");    // linea 4: Tipo de user
        writer.close();

        txtPublicar.setText(""); // limpia el campo
        cargarFeed();            // recarga el feed para mostrar la nueva publicación

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al publicar.");
    }
}

    private JPanel crearTarjetaPost(File archivo) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(26, 34, 53));
    panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(56, 189, 248, 40)),BorderFactory.createEmptyBorder(10, 12, 10, 12)));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    try {
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String usuario = reader.readLine();
        String fecha     = reader.readLine();
        String contenido = reader.readLine();
        String tipo      = reader.readLine();
        reader.close();

        //Panel de publicacion
        JPanel panelNombre = new JPanel();
        panelNombre.setLayout(new BoxLayout(panelNombre, BoxLayout.X_AXIS));
        panelNombre.setOpaque(false);
        panelNombre.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel LBUsuario = new JLabel(usuario);
        LBUsuario.setForeground(new Color(232, 244, 253));
        LBUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel LBFecha = new JLabel(fecha != null ? fecha : "");
        LBFecha.setForeground(new Color(56, 189, 248));
        LBFecha.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        LBFecha.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel LBTipo = new JLabel(" " + (tipo != null ? tipo : "") + " ");
        LBTipo.setForeground(new Color(56, 189, 248));
        LBTipo.setBackground(new Color(56, 189, 248, 30));
        LBTipo.setFont(new Font("Segoe UI", Font.BOLD, 9));
        LBTipo.setOpaque(true);
        
        JTextArea txtContenido = new JTextArea(contenido);
        txtContenido.setEditable(false);
        txtContenido.setLineWrap(true);
        txtContenido.setWrapStyleWord(true);
        txtContenido.setBackground(new Color(0, 0, 0, 0));
        txtContenido.setOpaque(false); // fondo transparente, hereda el del panel
        txtContenido.setForeground(new Color(107, 138, 170));
        txtContenido.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtContenido.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtContenido.setMaximumSize(new Dimension(580, Integer.MAX_VALUE));
        txtContenido.setBorder(null);
        txtContenido.setFocusable(false);// ← evita que se vea "seleccionable"
        txtContenido.setHighlighter(null);
        txtContenido.setSize(580, Short.MAX_VALUE);
        
        int altoNecesario = txtContenido.getPreferredSize().height;
        txtContenido.setMaximumSize(new Dimension(580, altoNecesario));
        txtContenido.setPreferredSize(new Dimension(580, altoNecesario));
        
        panelNombre.add(LBUsuario);
        panelNombre.add(Box.createHorizontalStrut(6));
        panelNombre.add(LBTipo);

        panel.add(panelNombre);
        panel.add(LBFecha); 
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtContenido);

    } catch (IOException e) {
        e.printStackTrace();
    }
    
    panel.setMaximumSize(new Dimension(625, panel.getPreferredSize().height));

    return panel;
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
        JPBarraVertical = new javax.swing.JPanel();
        lbNomUsuario = new javax.swing.JLabel();
        lbFotodePerfil = new javax.swing.JLabel();
        lbTipo = new javax.swing.JLabel();
        lbLogo = new javax.swing.JLabel();
        JPBarraHorizontal = new javax.swing.JPanel();
        btnIrPerfil = new javax.swing.JButton();
        btnCierreSesion = new javax.swing.JButton();
        btnInicio = new javax.swing.JButton();
        btnAmigos = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        PLFeed = new javax.swing.JPanel();
        PLPublicar = new javax.swing.JPanel();
        btnPublicar = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtPublicar = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(10, 15, 30));

        JPBarraVertical.setBackground(new java.awt.Color(0, 6, 23));

        lbNomUsuario.setBackground(new java.awt.Color(232, 244, 253));
        lbNomUsuario.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lbNomUsuario.setForeground(new java.awt.Color(232, 244, 253));
        lbNomUsuario.setText("Nombre Usuario");
        lbNomUsuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbNomUsuarioMouseClicked(evt);
            }
        });

        lbFotodePerfil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/iconUser48.png"))); // NOI18N
        lbFotodePerfil.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbFotodePerfilMousePressed(evt);
            }
        });

        lbTipo.setBackground(new java.awt.Color(232, 244, 253));
        lbTipo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lbTipo.setForeground(new java.awt.Color(21, 137, 242));
        lbTipo.setText("Tipo de Usuario");
        lbTipo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbTipoMouseClicked(evt);
            }
        });

        lbLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/ChatGPT Image 28 jun 2026, 06_51_33 p.m. (3).png"))); // NOI18N

        javax.swing.GroupLayout JPBarraVerticalLayout = new javax.swing.GroupLayout(JPBarraVertical);
        JPBarraVertical.setLayout(JPBarraVerticalLayout);
        JPBarraVerticalLayout.setHorizontalGroup(
            JPBarraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPBarraVerticalLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lbFotodePerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(JPBarraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbNomUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 285, Short.MAX_VALUE)
                .addComponent(lbLogo)
                .addGap(19, 19, 19))
        );
        JPBarraVerticalLayout.setVerticalGroup(
            JPBarraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JPBarraVerticalLayout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(JPBarraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbFotodePerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(JPBarraVerticalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lbLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(JPBarraVerticalLayout.createSequentialGroup()
                            .addComponent(lbNomUsuario)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lbTipo))))
                .addContainerGap())
        );

        JPBarraHorizontal.setBackground(new java.awt.Color(21, 34, 68));

        btnIrPerfil.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnIrPerfil.setText("Perfil");
        btnIrPerfil.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnIrPerfil.addActionListener(this::btnIrPerfilActionPerformed);

        btnCierreSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/Salir.png"))); // NOI18N
        btnCierreSesion.setBorderPainted(false);
        btnCierreSesion.setContentAreaFilled(false);
        btnCierreSesion.addActionListener(this::btnCierreSesionActionPerformed);

        btnInicio.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnInicio.setText("Inicio");
        btnInicio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInicio.addActionListener(this::btnInicioActionPerformed);

        btnAmigos.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAmigos.setText("Amigos");
        btnAmigos.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAmigos.addActionListener(this::btnAmigosActionPerformed);

        javax.swing.GroupLayout JPBarraHorizontalLayout = new javax.swing.GroupLayout(JPBarraHorizontal);
        JPBarraHorizontal.setLayout(JPBarraHorizontalLayout);
        JPBarraHorizontalLayout.setHorizontalGroup(
            JPBarraHorizontalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPBarraHorizontalLayout.createSequentialGroup()
                .addGroup(JPBarraHorizontalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JPBarraHorizontalLayout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(btnCierreSesion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(JPBarraHorizontalLayout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(JPBarraHorizontalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAmigos)
                            .addGroup(JPBarraHorizontalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnIrPerfil)
                                .addComponent(btnInicio)))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        JPBarraHorizontalLayout.setVerticalGroup(
            JPBarraHorizontalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPBarraHorizontalLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(btnInicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnIrPerfil)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAmigos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 201, Short.MAX_VALUE)
                .addComponent(btnCierreSesion)
                .addGap(18, 18, 18))
        );

        jScrollPane1.setBackground(new java.awt.Color(10, 15, 30));
        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(650, 400));

        PLFeed.setBackground(new java.awt.Color(10, 15, 30));
        PLFeed.setLayout(new javax.swing.BoxLayout(PLFeed, javax.swing.BoxLayout.LINE_AXIS));

        PLPublicar.setBackground(new java.awt.Color(26, 34, 56));

        btnPublicar.setText("Publicar");
        btnPublicar.addActionListener(this::btnPublicarActionPerformed);

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        txtPublicar.setBackground(new java.awt.Color(31, 48, 78));
        txtPublicar.setColumns(20);
        txtPublicar.setForeground(new java.awt.Color(255, 255, 255));
        txtPublicar.setLineWrap(true);
        txtPublicar.setRows(5);
        txtPublicar.setTabSize(5);
        jScrollPane2.setViewportView(txtPublicar);

        javax.swing.GroupLayout PLPublicarLayout = new javax.swing.GroupLayout(PLPublicar);
        PLPublicar.setLayout(PLPublicarLayout);
        PLPublicarLayout.setHorizontalGroup(
            PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PLPublicarLayout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addGroup(PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 577, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPublicar))
                .addGap(23, 23, 23))
        );
        PLPublicarLayout.setVerticalGroup(
            PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PLPublicarLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnPublicar)
                .addGap(15, 15, 15))
        );

        PLFeed.add(PLPublicar);

        jScrollPane1.setViewportView(PLFeed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(JPBarraHorizontal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(JPBarraVertical, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(JPBarraVertical, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(JPBarraHorizontal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCierreSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCierreSesionActionPerformed
       Sesion.usuarioActual = "";
       Sesion.nombreCompleto = "";
       Sesion.tipoUsuario = "";
        
       FrmLogin I = new FrmLogin();
       I.setVisible(true);
       dispose(); 
    }//GEN-LAST:event_btnCierreSesionActionPerformed

    private void lbNomUsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbNomUsuarioMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lbNomUsuarioMouseClicked

    private void lbFotodePerfilMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbFotodePerfilMousePressed
    }//GEN-LAST:event_lbFotodePerfilMousePressed

    private void btnIrPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIrPerfilActionPerformed
    FrmPerfilDeUsuario Perfil = new FrmPerfilDeUsuario(Sesion.usuarioActual);
    Perfil.setVisible(true);
    dispose();        
       
    }//GEN-LAST:event_btnIrPerfilActionPerformed

    private void lbTipoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbTipoMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lbTipoMouseClicked

    private void btnInicioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInicioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnInicioActionPerformed

    private void btnPublicarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPublicarActionPerformed
        // Publica y carga feed
        guardarPublicacion();
        SwingUtilities.invokeLater(() -> {
        jScrollPane1.getVerticalScrollBar().setValue(0);
        });
    }//GEN-LAST:event_btnPublicarActionPerformed

    private void btnAmigosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmigosActionPerformed
        new FrmBuscarAmigos().setVisible(true);
        dispose();
    }//GEN-LAST:event_btnAmigosActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new jfmPanelInicio().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPBarraHorizontal;
    private javax.swing.JPanel JPBarraVertical;
    private javax.swing.JPanel PLFeed;
    private javax.swing.JPanel PLPublicar;
    private javax.swing.JButton btnAmigos;
    private javax.swing.JButton btnCierreSesion;
    private javax.swing.JButton btnInicio;
    private javax.swing.JButton btnIrPerfil;
    private javax.swing.JButton btnPublicar;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbFotodePerfil;
    private javax.swing.JLabel lbLogo;
    private javax.swing.JLabel lbNomUsuario;
    private javax.swing.JLabel lbTipo;
    private javax.swing.JTextArea txtPublicar;
    // End of variables declaration//GEN-END:variables
}
