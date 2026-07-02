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
import javax.swing.JFileChooser;
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
    
    private String rutaArchivoAdjunto = "";
    
    public jfmPanelInicio() {
        initComponents();
        
        //maximo caracteres publicacion
        ((javax.swing.text.AbstractDocument) txtPublicar.getDocument()) .setDocumentFilter(new javax.swing.text.DocumentFilter() {
        
        @Override
        public void insertString(FilterBypass fb, int off, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if ((fb.getDocument().getLength() + str.length()) <= 350)
                super.insertString(fb, off, str, a);
        }
        
        @Override
        public void replace(FilterBypass fb, int off, int len, String str, javax.swing.text.AttributeSet a)
                throws javax.swing.text.BadLocationException {
            if ((fb.getDocument().getLength() - len + str.length()) <= 350)
                super.replace(fb, off, len, str, a);
        }
    });
        
        setLocationRelativeTo(null);
        
        PLFeed.setLayout(new BoxLayout(PLFeed, BoxLayout.Y_AXIS)); //Dimensiones de Feed
        PLPublicar.setMaximumSize(new Dimension(700, 136));
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
        
        int contadorPosts = 0;
        
        for (File f : archivos) {
            JPanel tarjeta = crearTarjetaPost(f);
            PLFeed.add(tarjeta);
            PLFeed.add(Box.createVerticalStrut(10)); // espacio entre tarjetas
        
            contadorPosts++;
            
            /* Dinamica de publicidad */
            if(!Sesion.rolSuscripción.equalsIgnoreCase("Premium") && contadorPosts == 1) {
                PLFeed.add(crearTarjetaDePublicidad());
                PLFeed.add(Box.createVerticalStrut(10));
            }
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
            
         String nombreAdjunto = "";
        if (!rutaArchivoAdjunto.isEmpty()) {
            File carpetaAdj = new File("src\\main\\Publicaciones\\adjuntos\\");
            carpetaAdj.mkdirs();
            File origen = new File(rutaArchivoAdjunto);
            String ext = rutaArchivoAdjunto.substring(rutaArchivoAdjunto.lastIndexOf("."));
            nombreAdjunto = Sesion.usuarioActual + "_" + System.currentTimeMillis() + ext;
            java.nio.file.Files.copy(origen.toPath(),
                new File(carpetaAdj, nombreAdjunto).toPath(),java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        
        
            FileWriter writer = new FileWriter(archivo);
            writer.write(Sesion.nombreCompleto + "\n"); // línea 1: nombre
            writer.write(fecha + "\n");                 // linea 2: fecha
            writer.write(contenido + "\n");             // línea 3: contenido
            writer.write(Sesion.tipoUsuario + "\n");    // linea 4: Tipo de user 
            writer.write(nombreAdjunto + "\n");         // linea 5: Archivo adjunto
            writer.close();

        txtPublicar.setText(""); // limpia el campo
        cargarFeed();            // recarga el feed para mostrar la nueva publicación
        lblArchivoAdjunto.setText(""); //limpia despues de tener archivo seleccionado
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al publicar.");
    }
}

    private JPanel crearTarjetaPost(File archivo) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBackground(new Color(30, 41, 70));
    panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(56, 189, 248, 40)),BorderFactory.createEmptyBorder(10, 12, 10, 12)));
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    try(BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        
        String usuario = reader.readLine();
        String fecha     = reader.readLine();
        String contenido = reader.readLine();
        String tipo      = reader.readLine();
        String adjunto   = reader.readLine(); 
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
        txtContenido.setMaximumSize(new Dimension(700, altoNecesario));
        txtContenido.setPreferredSize(new Dimension(700, altoNecesario));
        
        panelNombre.add(LBUsuario);
        panelNombre.add(Box.createHorizontalStrut(6));
        panelNombre.add(LBTipo);

        panel.add(panelNombre);
        panel.add(LBFecha); 
        panel.add(Box.createVerticalStrut(4));
        panel.add(txtContenido);
        
        if (adjunto != null && !adjunto.isEmpty()) {
            File archivoAdj = new File("src\\main\\Publicaciones\\adjuntos\\" + adjunto);

        if (adjunto.endsWith(".jpg") || adjunto.endsWith(".jpeg") || adjunto.endsWith(".png")) {
         // Es imagen — la muestra
          JLabel lblImg = new JLabel();
          ImageIcon icon = new ImageIcon(archivoAdj.getAbsolutePath());
          Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
          lblImg.setIcon(new ImageIcon(img));
          lblImg.setAlignmentX(Component.LEFT_ALIGNMENT);
          panel.add(Box.createVerticalStrut(6));
          panel.add(lblImg);
          } else {
         // Es documento — muestra un link/label
          JLabel lblAdj = new JLabel("📎 " + adjunto);
          lblAdj.setForeground(new Color(56, 189, 248));
          lblAdj.setFont(new Font("Segoe UI", Font.PLAIN, 11));
          lblAdj.setAlignmentX(Component.LEFT_ALIGNMENT);
          lblAdj.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
          lblAdj.addMouseListener(new java.awt.event.MouseAdapter() {
              public void mouseClicked(java.awt.event.MouseEvent e) {
                  try {
                       java.awt.Desktop.getDesktop().open(archivoAdj);
                   } catch (IOException ex) {
                      ex.printStackTrace();
                   }
               }
           });
          panel.add(Box.createVerticalStrut(6));
          panel.add(lblAdj);
         }
        }
        
    } catch (IOException e) {
        e.printStackTrace();
    }
    
    panel.setMaximumSize(new Dimension(700, panel.getPreferredSize().height));

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

        jFileChooser1 = new javax.swing.JFileChooser();
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
        btnAdjuntar = new javax.swing.JButton();
        lblArchivoAdjunto = new javax.swing.JLabel();
        btnBorrarArchivo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(15, 23, 42));

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

        JPBarraHorizontal.setBackground(new java.awt.Color(17, 30, 58));

        btnIrPerfil.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnIrPerfil.setForeground(new java.awt.Color(255, 255, 255));
        btnIrPerfil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/user.png"))); // NOI18N
        btnIrPerfil.setText("Perfil");
        btnIrPerfil.setContentAreaFilled(false);
        btnIrPerfil.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnIrPerfil.addActionListener(this::btnIrPerfilActionPerformed);

        btnCierreSesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/Salir.png"))); // NOI18N
        btnCierreSesion.setBorderPainted(false);
        btnCierreSesion.setContentAreaFilled(false);
        btnCierreSesion.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCierreSesion.addActionListener(this::btnCierreSesionActionPerformed);

        btnInicio.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnInicio.setForeground(new java.awt.Color(255, 255, 255));
        btnInicio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/house-chimney (1).png"))); // NOI18N
        btnInicio.setText("Inicio");
        btnInicio.setContentAreaFilled(false);
        btnInicio.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnInicio.addActionListener(this::btnInicioActionPerformed);

        btnAmigos.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnAmigos.setForeground(new java.awt.Color(255, 255, 255));
        btnAmigos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/users.png"))); // NOI18N
        btnAmigos.setText("Amigos");
        btnAmigos.setContentAreaFilled(false);
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
                        .addContainerGap()
                        .addComponent(btnAmigos))
                    .addGroup(JPBarraHorizontalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnIrPerfil))
                    .addGroup(JPBarraHorizontalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnInicio)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JPBarraHorizontalLayout.setVerticalGroup(
            JPBarraHorizontalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPBarraHorizontalLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(btnInicio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnIrPerfil)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAmigos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCierreSesion)
                .addGap(18, 18, 18))
        );

        jScrollPane1.setBackground(new java.awt.Color(10, 15, 30));
        jScrollPane1.setHorizontalScrollBar(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(650, 400));

        PLFeed.setBackground(new java.awt.Color(10, 15, 30));
        PLFeed.setLayout(new javax.swing.BoxLayout(PLFeed, javax.swing.BoxLayout.LINE_AXIS));

        PLPublicar.setBackground(new java.awt.Color(30, 41, 70));

        btnPublicar.setBackground(new java.awt.Color(79, 160, 241));
        btnPublicar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPublicar.setForeground(new java.awt.Color(255, 255, 255));
        btnPublicar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/pencil.png"))); // NOI18N
        btnPublicar.setText("Publicar");
        btnPublicar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPublicar.addActionListener(this::btnPublicarActionPerformed);

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        txtPublicar.setBackground(new java.awt.Color(38, 55, 90));
        txtPublicar.setColumns(20);
        txtPublicar.setForeground(new java.awt.Color(255, 255, 255));
        txtPublicar.setLineWrap(true);
        txtPublicar.setRows(5);
        txtPublicar.setTabSize(5);
        jScrollPane2.setViewportView(txtPublicar);

        btnAdjuntar.setBackground(new java.awt.Color(79, 160, 241));
        btnAdjuntar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnAdjuntar.setForeground(new java.awt.Color(255, 255, 255));
        btnAdjuntar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/edit.png"))); // NOI18N
        btnAdjuntar.setText("Adjuntar");
        btnAdjuntar.addActionListener(this::btnAdjuntarActionPerformed);

        lblArchivoAdjunto.setForeground(new java.awt.Color(255, 255, 255));

        btnBorrarArchivo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imgs_S/icons8-trash-24.png"))); // NOI18N
        btnBorrarArchivo.setContentAreaFilled(false);
        btnBorrarArchivo.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBorrarArchivo.addActionListener(this::btnBorrarArchivoActionPerformed);

        javax.swing.GroupLayout PLPublicarLayout = new javax.swing.GroupLayout(PLPublicar);
        PLPublicar.setLayout(PLPublicarLayout);
        PLPublicarLayout.setHorizontalGroup(
            PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PLPublicarLayout.createSequentialGroup()
                .addGroup(PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PLPublicarLayout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(btnBorrarArchivo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblArchivoAdjunto, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAdjuntar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnPublicar))
                    .addGroup(PLPublicarLayout.createSequentialGroup()
                        .addContainerGap(46, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        PLPublicarLayout.setVerticalGroup(
            PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PLPublicarLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
                .addGroup(PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PLPublicarLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnPublicar)
                            .addComponent(btnAdjuntar)))
                    .addGroup(PLPublicarLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(PLPublicarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnBorrarArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblArchivoAdjunto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(23, 23, 23))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 731, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
            .addComponent(JPBarraVertical, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(JPBarraVertical, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(JPBarraHorizontal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 18, Short.MAX_VALUE))))
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

    private void btnAdjuntarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdjuntarActionPerformed
        // TODO add your handling code here:
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes y documentos", "jpg", "jpeg", "png", "pdf", "docx", "txt"));
        chooser.setAcceptAllFileFilterUsed(false);

    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        File archivo = chooser.getSelectedFile();

        // Validar tamaño máximo 5MB
        if (archivo.length() > 5 * 1024 * 1024) {
            JOptionPane.showMessageDialog(null, "El archivo no puede superar 5 MB.");
            return;
        }

        rutaArchivoAdjunto = archivo.getAbsolutePath();
        lblArchivoAdjunto.setText("📎 " + archivo.getName());
    }
    
    }//GEN-LAST:event_btnAdjuntarActionPerformed

    private void btnBorrarArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBorrarArchivoActionPerformed
        // Limpiar Archivo
        rutaArchivoAdjunto = "";
        lblArchivoAdjunto.setText("");
    }//GEN-LAST:event_btnBorrarArchivoActionPerformed
    
    private JPanel crearTarjetaDePublicidad(){
        JPanel panelAd = new JPanel();
        panelAd.setLayout(new BoxLayout(panelAd, BoxLayout.Y_AXIS));
        panelAd.setBackground(new Color(31, 41, 55)); /* Un color un poco diferente para resaltar */
        panelAd.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(234, 179, 8, 80), 2), /* Borde dorado de "Anuncio" */
        BorderFactory.createEmptyBorder(12, 12, 12, 12)
    ));
        panelAd.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelAd.setMaximumSize(new Dimension(700, 90));

        JLabel lblTag = new JLabel("PATROCINADO");
        lblTag.setForeground(new Color(234, 179, 8));
        lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));

        JLabel lblContenido = new JLabel("¡Pásate a Premium hoy! Navega sin anuncios y obtén una medalla exclusiva por solo $2.99.");
        lblContenido.setForeground(new Color(209, 213, 219));
        lblContenido.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        
        panelAd.add(lblTag);
        panelAd.add(Box.createVerticalStrut(6));
        panelAd.add(lblContenido);
        
        return panelAd;
    }
    
    
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
    private javax.swing.JButton btnAdjuntar;
    private javax.swing.JButton btnAmigos;
    private javax.swing.JButton btnBorrarArchivo;
    private javax.swing.JButton btnCierreSesion;
    private javax.swing.JButton btnInicio;
    private javax.swing.JButton btnIrPerfil;
    private javax.swing.JButton btnPublicar;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbFotodePerfil;
    private javax.swing.JLabel lbLogo;
    private javax.swing.JLabel lbNomUsuario;
    private javax.swing.JLabel lbTipo;
    private javax.swing.JLabel lblArchivoAdjunto;
    private javax.swing.JTextArea txtPublicar;
    // End of variables declaration//GEN-END:variables
}
