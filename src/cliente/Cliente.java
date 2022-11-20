package cliente;

import main.InterfaceServidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static main.Main.ipServidor;
import static main.Main.puerto;

public class Cliente {
    public Cliente() {
        try {
            Registry rmi= LocateRegistry.getRegistry(ipServidor,puerto);

            InterfaceServidor servidor=(InterfaceServidor) rmi.lookup("Encriptador");
            new ImplementacionCliente(servidor);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }
}
