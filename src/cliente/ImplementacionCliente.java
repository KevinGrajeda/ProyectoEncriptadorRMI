package cliente;


import main.InterfaceCliente;
import main.InterfaceServidor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import static main.Main.rutaResultado;

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
        botonArchivo.addActionListener(e -> {
            try {
                escogerImagen();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
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
        tipoAlgoritmo = new JComboBox<>(tipos);
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
        botonEncriptar.addActionListener(e -> {
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
            String extension = nombre.substring(nombre.lastIndexOf("."));
            rutaImagen = chooser.getSelectedFile().getPath();

            leerArchivo(rutaImagen,nombre);
            if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg")  || extension.equals(".mkv")) {
                esEncriptado = false;
                labelImagen.setIcon(new ImageIcon(rutaImagen));
                botonEncriptar.setText("encriptar imagen");
                botonEncriptar.setEnabled(true);
            } else if (extension.equals(".ceticrypt")) {
                esEncriptado = true;
                labelImagen.setIcon(new ImageIcon());
                botonEncriptar.setText("desencriptar imagen");
                botonEncriptar.setEnabled(true);
            } else {
                esEncriptado = false;
                botonEncriptar.setText("encriptar archivo");
                botonEncriptar.setEnabled(true);
            }
        }
    }

    private void leerArchivo(String ruta, String nombreArchivo) throws IOException {
        FileInputStream fis = new FileInputStream(ruta);
        byte[] data = new byte[fis.available()];
        fis.read(data);
        servidor.setArchivo(data, nombreArchivo,codCliente);

        fis.close();

    }


    @Override
    public void setResultado(byte[][] archivosEncriptados,String[] nombresArchivos) throws RemoteException {
        System.out.println("servidor regreso"+ Arrays.toString(nombresArchivos));
        try {
            for(int i=0;i<archivosEncriptados.length;i++){
                FileOutputStream fos = new FileOutputStream(rutaResultado+nombresArchivos[i]);
                fos.write(archivosEncriptados[i]);
                fos.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //TODO mostrar las dos imagenes
        /*try {
            FileOutputStream fos = null;
            if (esEncriptado) {
                fos = new FileOutputStream(ruta.substring(0, ruta.length() - 10));
                fos.write(datosFinal);
                fos.close();
                //System.out.println(ruta.substring(0, ruta.length() - 10));
                label.setIcon(new ImageIcon(datosFinal));
                botonEncriptar.setEnabled(false);
                new File(ruta).delete();
            } else {
                fos = new FileOutputStream(ruta + ".ceticrypt");
                fos.write(datosFinal);
                fos.close();
                label.setIcon(new ImageIcon());
                botonEncriptar.setEnabled(false);
                new File(ruta).delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
        Runnable doAssist = () -> JOptionPane.showMessageDialog(null, mensaje, "mensaje", JOptionPane.INFORMATION_MESSAGE);
        SwingUtilities.invokeLater(doAssist);
    }
}
