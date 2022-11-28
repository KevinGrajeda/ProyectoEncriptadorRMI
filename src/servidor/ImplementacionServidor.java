package servidor;

import main.InterfaceCliente;
import main.InterfaceServidor;
import servidor.encriptadores.EncriptadoSecuencial;

import java.nio.ByteBuffer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;

public class ImplementacionServidor extends UnicastRemoteObject implements InterfaceServidor {
    byte[][] archivos;
    String[] nombresArchivos;
    ArrayList<InterfaceCliente> clientes;
    boolean listo;
    boolean encriptado;

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

        String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
        if (extension.equals(".ceticrypt")) {
            listo = true;
            encriptado = true;
            archivos[0] = archivo;
            nombresArchivos[0] = nombreArchivo;
            enviarMensaje(codCliente == 1 ? 0 : 1, "El otro cliente ha enviado un archivo encriptado, puedes desencriptarlo presionando el boton");
            enviarModo(DESENCRIPTAR_DISPONIBLE);
            archivos[1]=null;
        } else {
            encriptado = false;
            archivos[codCliente] = archivo;
            nombresArchivos[codCliente] = nombreArchivo;
            if (archivos[0] != null && archivos[1] != null) {
                listo = true;
                enviarModo(ENCRIPTAR_DISPONIBLE);
            }
            System.out.println("cliente " + codCliente + " envio archivo: " + nombreArchivo);

        }

    }

    @Override
    public void encriptar(int llave, int tipoEncriptacion, int codCliente) throws RemoteException {
        System.out.println("cliente:" + codCliente + " envia llave:" + llave + "y tipo:" + tipoEncriptacion);

        byte[] archivoParaEncriptar=null;
        String nombreArchivoNuevo=null;
        if (!listo) {
            enviarMensaje(codCliente, "El otro cliente no ha enviado su imagen");
            return;
        }
        if(encriptado){
            archivoParaEncriptar=Arrays.copyOf(archivos[0],archivos[0].length);
            nombreArchivoNuevo=nombresArchivos[0];
            System.out.println("tamaño archivo="+archivoParaEncriptar.length);
        }else{
            archivoParaEncriptar = Arrays.copyOf(archivos[0], archivos[0].length + archivos[1].length + 4);
            System.arraycopy(archivos[1], 0, archivoParaEncriptar, archivos[0].length, archivos[1].length);

            nombreArchivoNuevo = nombresArchivos[0] + "-Y-" + nombresArchivos[1] + ".ceticrypt";
            byte[] bytesTamanio = ByteBuffer.allocate(4).putInt(archivos[0].length).array();
            for (int i = 0; i < 4.; i++) {
                archivoParaEncriptar[archivoParaEncriptar.length - 4 + i] = bytesTamanio[i];
            }
        }
        if (tipoEncriptacion == SECUENCIAL || tipoEncriptacion == TODOS) {
            EncriptadoSecuencial encriptadoSecuencial = new EncriptadoSecuencial(archivoParaEncriptar);
            long startTime = System.nanoTime();
            encriptadoSecuencial.encriptar(llave);
            long stopTime = System.nanoTime();
            clientes.get(codCliente).setTiempos(stopTime - startTime, SECUENCIAL);
        }
        //TODO otros tipos de encriptacion

        /*
        String[] nombresArchivosNuevos=new String[2];
        for (int i=0;i<nombresArchivos.length;i++) {
            String nombreArchivo=nombresArchivos[i];
            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
            if (extension.equals(".ceticrypt")) {
                nombresArchivosNuevos[i] = nombreArchivo.substring(0, nombreArchivo.length() - 10);
            }else{
                nombresArchivosNuevos[i] = nombreArchivo+".ceticrypt";
            }
        }*/
        if(encriptado){
            byte[] tamanioBytes=Arrays.copyOfRange(archivoParaEncriptar,archivoParaEncriptar.length-4,archivoParaEncriptar.length);
            int tamanio=java.nio.ByteBuffer.wrap(tamanioBytes).getInt();
            byte[] archivoSinBytesTamanio=Arrays.copyOfRange(archivoParaEncriptar,0,archivoParaEncriptar.length-4);
            clientes.get(codCliente).setImagenes(archivoSinBytesTamanio, tamanio);
        }else{
            clientes.get(codCliente).setResultado(archivoParaEncriptar, nombreArchivoNuevo);
        }


    }

    public void enviarMensaje(int codCliente, String mensaje) throws RemoteException {
        clientes.get(codCliente).setAdvertencia(mensaje);
    }
    public void enviarModo(int modo) throws RemoteException {
        for(InterfaceCliente cliente:clientes){
            cliente.setModo(modo);
        }
    }
}
