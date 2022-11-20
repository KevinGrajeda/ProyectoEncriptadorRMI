package cliente;


import main.InterfaceCliente;
import main.InterfaceServidor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ImplementacionCliente extends UnicastRemoteObject implements InterfaceCliente {

    JButton botonEncriptar;
    JLabel labelImagen, lblSecuencial, lblExecutor, lblFork, lblInfo;
    JFrame frame;
    JTextField txtLlave;
    JComboBox tipoAlgoritmo;
    String rutaImagen;
    boolean esEncriptado;
    InterfaceServidor servidor;
    int codCliente;

    public ImplementacionCliente(InterfaceServidor servidor) throws RemoteException {
        this.servidor = servidor;
        codCliente = servidor.registro(this);

        frame = new JFrame();
        frame.setTitle("encriptador de imagenes");
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        labelImagen = new JLabel();

        JScrollPane scrollpane = new JScrollPane(labelImagen);
        scrollpane.setBounds(240, 20, 800, 600);
        frame.add(scrollpane);

        lblInfo = new JLabel("selecciona un archivo");
        lblInfo.setBounds(240, 620, 500, 30);
        frame.add(lblInfo);

        JButton botonArchivo = new JButton("seleccionar imagen");
        botonArchivo.setBounds(40, 20, 170, 30);
        botonArchivo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    escogerImagen();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        frame.add(botonArchivo);


        JLabel lblLlave = new JLabel("clave:");
        lblLlave.setBounds(40, 80, 170, 20);
        frame.add(lblLlave);
        txtLlave = new JTextField();
        txtLlave.setBounds(40, 100, 170, 30);
        frame.add(txtLlave);

        JLabel lblTipo = new JLabel("algoritmo:");
        lblTipo.setBounds(40, 160, 170, 20);
        frame.add(lblTipo);
        String[] tipos = {"Secuencial", "ExecutorService", "ForkJoin", "Todos"};
        tipoAlgoritmo = new JComboBox<String>(tipos);
        tipoAlgoritmo.setBounds(40, 180, 170, 30);
        frame.add(tipoAlgoritmo);


        JLabel lblTiempos = new JLabel("tiempos:");
        lblTiempos.setBounds(40, 250, 170, 20);
        frame.add(lblTiempos);
        lblSecuencial = new JLabel("Secuencial: 0", SwingConstants.RIGHT);
        lblSecuencial.setBounds(40, 270, 170, 30);
        frame.add(lblSecuencial);
        lblExecutor = new JLabel("Executor: 0", SwingConstants.RIGHT);
        lblExecutor.setBounds(40, 300, 170, 30);
        frame.add(lblExecutor);
        lblFork = new JLabel("ForkJoin: 0", SwingConstants.RIGHT);
        lblFork.setBounds(40, 330, 170, 30);
        frame.add(lblFork);

        botonEncriptar = new JButton("encriptar imagen");
        botonEncriptar.setEnabled(false);
        botonEncriptar.setBounds(40, 400, 170, 30);
        botonEncriptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int llave = Integer.parseInt(txtLlave.getText());
                    int index = tipoAlgoritmo.getSelectedIndex();
                    servidor.encriptar(llave, index, codCliente);
                } catch (NumberFormatException n) {
                    JOptionPane.showMessageDialog(null, "Escribe una llave valida", "Error", JOptionPane.ERROR_MESSAGE);
                    txtLlave.setText("");
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        frame.add(botonEncriptar);
        frame.setVisible(true);

    }

    private void escogerImagen() throws IOException {
        JFileChooser chooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imagen", "jpg", "jpeg", "png", "ceticrypt");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String nombre = chooser.getSelectedFile().getName();
            lblInfo.setText("archivo: " + nombre);
            String extension = nombre.substring(nombre.lastIndexOf("."), nombre.length());
            rutaImagen = chooser.getSelectedFile().getPath();

            if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg")
                    || extension.equals(".mkv")) {
                esEncriptado = false;
                leerArchivo(rutaImagen);
                //System.out.println(ruta);
                labelImagen.setIcon(new ImageIcon(rutaImagen));
                botonEncriptar.setText("encriptar imagen");
                botonEncriptar.setEnabled(true);
                //System.out.println("imagen");
            } else if (extension.equals(".ceticrypt")) {
                esEncriptado = true;
                leerArchivo(rutaImagen);
                labelImagen.setIcon(new ImageIcon());
                botonEncriptar.setText("desencriptar imagen");
                botonEncriptar.setEnabled(true);
                //System.out.println("encriptacion");
            } else {
                esEncriptado = false;
                //System.out.println("no");
                leerArchivo(rutaImagen);
                botonEncriptar.setText("encriptar archivo");
                botonEncriptar.setEnabled(true);
            }
        }
    }

    private void leerArchivo(String ruta) throws IOException {
        FileInputStream fis = null;
        fis = new FileInputStream(ruta);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        servidor.setArchivo(data, codCliente);

        fis.close();

    }


    @Override
    public void setResultado(byte[][] archivosEncriptados) throws RemoteException {

    }

    @Override
    public void setTiempos(long tiempo, int tipo) throws RemoteException {
        switch (tipo) {
            case SECUENCIAL -> lblSecuencial.setText("Secuencial: " + tiempo);
            case EXECUTOR -> lblExecutor.setText("Executor: " + tiempo);
            case FORKJOIN -> lblFork.setText("ForkJoin: " + tiempo);
        }
    }


    @Override
    public void setAdvertencia(String mensaje) {
        Runnable doAssist = new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null, mensaje, "mensaje", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        SwingUtilities.invokeLater(doAssist);
    }
}
