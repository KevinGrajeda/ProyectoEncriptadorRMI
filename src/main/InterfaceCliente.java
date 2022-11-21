package main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceCliente extends Remote {
    int SECUENCIAL=0;
    int EXECUTOR=1;
    int FORKJOIN=2;
    int TODOS=3;
    void setResultado(byte[][] archivosEncriptados, String[] nombresArchivos) throws RemoteException;

    void setTiempos(long tiempo, int tipo) throws RemoteException;

    void setAdvertencia(String mensaje) throws RemoteException;
}
