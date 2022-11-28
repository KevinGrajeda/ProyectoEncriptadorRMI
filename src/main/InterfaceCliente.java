package main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceCliente extends Remote {
    int SECUENCIAL=0;
    int EXECUTOR=1;
    int FORKJOIN=2;
    int TODOS=3;
    void setResultado(byte[] archivoEncriptado, String nombreArchivo) throws RemoteException;
    void setImagenes(byte[] archivoEncriptado, int tamanioPrimer) throws RemoteException;

    void setTiempos(long tiempo, int tipo) throws RemoteException;

    void setAdvertencia(String mensaje) throws RemoteException;

    void setModo(int modo) throws RemoteException;

}
