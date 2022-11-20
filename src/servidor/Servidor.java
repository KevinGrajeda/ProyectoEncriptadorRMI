package servidor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static main.Main.puerto;

public class Servidor {
    public Servidor() {
        try {
            Registry rmi= LocateRegistry.createRegistry(puerto);
            rmi.rebind("Encriptador",(Remote) new ImplementacionServidor());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
