package servidor;

import main.InterfaceCliente;
import main.InterfaceServidor;
import servidor.encriptadores.EncriptadoSecuencial;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ImplementacionServidor extends UnicastRemoteObject implements InterfaceServidor {
    byte[][] archivos;
    String[] nombresArchivos;
    ArrayList<InterfaceCliente> clientes;

    public ImplementacionServidor() throws RemoteException {
        clientes = new ArrayList<>();
        archivos = new byte[2][];
        nombresArchivos = new String[2];
        System.out.println("esperando clientes");
    }

    @Override
    public int registro(InterfaceCliente cliente) throws RemoteException {
        if (clientes.size() < 2) {
            clientes.add(cliente);
            System.out.println("cliente " + (clientes.size() - 1) + " conectado");
            return (clientes.size() - 1);
        }
        System.err.println("ya hay 2 clientes, no se aceptan mas");
        return 0;
    }

    @Override
    public void setArchivo(byte[] archivo, String nombreArchivo, int codCliente) throws RemoteException {
        archivos[codCliente] = archivo;
        nombresArchivos[codCliente] = nombreArchivo;
        if (archivo == null)
            System.out.println("cliente " + codCliente + " borró su archivo");
        else {
            System.out.println("cliente " + codCliente + " envio archivo: " + nombreArchivo);
        }
    }

    @Override
    public void encriptar(int llave, int tipoEncriptacion, int codCliente) throws RemoteException {
        System.out.println("cliente:" + codCliente + " envia llave:" + llave + "y tipo:" + tipoEncriptacion);

        if (archivos[0] == null || archivos[1] == null) {
            enviarMensaje(codCliente, "El otro cliente no ha enviado su imagen");
            return;
        }

        if (tipoEncriptacion == SECUENCIAL || tipoEncriptacion == TODOS) {
            EncriptadoSecuencial encriptadoSecuencial = new EncriptadoSecuencial(archivos[0]);
            EncriptadoSecuencial encriptadoSecuencial2 = new EncriptadoSecuencial(archivos[1]);
            long startTime = System.nanoTime();
            encriptadoSecuencial.encriptar(llave);
            encriptadoSecuencial2.encriptar(llave);
            long stopTime = System.nanoTime();
            clientes.get(codCliente).setTiempos(stopTime - startTime, SECUENCIAL);
        }
        //TODO otros tipos de encriptacion

        String[] nombresArchivosNuevos=new String[2];
        for (int i=0;i<nombresArchivos.length;i++) {
            String nombreArchivo=nombresArchivos[i];
            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
            if (extension.equals(".ceticrypt")) {
                nombresArchivosNuevos[i] = nombreArchivo.substring(0, nombreArchivo.length() - 10);
            }else{
                nombresArchivosNuevos[i] = nombreArchivo+".ceticrypt";
            }
        }

        clientes.get(codCliente).setResultado(archivos,nombresArchivosNuevos);
    }

    public void enviarMensaje(int codCliente, String mensaje) throws RemoteException {
        clientes.get(codCliente).setAdvertencia(mensaje);
    }
}
