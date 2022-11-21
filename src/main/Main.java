package main;

import cliente.Cliente;
import servidor.Servidor;

import javax.swing.*;
import java.awt.*;

public class Main {
    public final static String ipLocal = "192.168.100.11";
    public final static String ipServidor = "192.168.100.11";
    public final static int puerto = 1100;
    public final static String rutaResultado = "resultado/";

    public final static Color botonColor = new Color(43, 43, 43);
    public final static Color fondo = new Color(60, 63, 65);
    public final static Color texto = new Color(232, 232, 232);
    public final static Font fuenteTexto = new Font("Arial", Font.PLAIN, 40);

    public static void main(String[] args) {
        System.setProperty("java.rmi.server.hostname",ipLocal);
        System.setProperty("awt.useSystemAAFontSettings", "on");
        Object[] options = {"Cliente",
                "Servidor"};
        int n = JOptionPane.showOptionDialog(null,
                "¿Cual programa quieres abrir?",
                "Inicio",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,     //do not use a custom Icon
                options,  //the titles of buttons
                options[0]); //default button title

        switch (n) {
            case 0:
                new Servidor();
                new Cliente();
                new Cliente();
                break;
            case 1:
                new Servidor();
                break;
        }
    }
}
