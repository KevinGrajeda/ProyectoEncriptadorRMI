package main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceServidor extends Remote {
    int SECUENCIAL=0;
    int EXECUTOR=1;
    int FORKJOIN=2;
    int TODOS=3;

    int registro(InterfaceCliente cliente)throws RemoteException;
    void setArchivo(byte[] archivo, String nombreArchivo, int codCliente)throws RemoteException;
    void encriptar(int llave,int tipoEncriptacion,int codCliente)throws RemoteException;
}
