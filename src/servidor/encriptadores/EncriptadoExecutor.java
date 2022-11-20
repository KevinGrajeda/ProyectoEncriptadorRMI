package servidor.encriptadores;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EncriptadoExecutor {
    byte data[];
    int partes;
    Set<Callable<Void>> callables;
    ExecutorService service;

    public EncriptadoExecutor(byte data[],int llave) {
        this.data = data;
        partes=Runtime.getRuntime().availableProcessors();
        //partes=4;
        service=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        int tamPartes=data.length/partes;

        callables = new HashSet<Callable<Void>>();  
        
        for(int i=0;i<partes;i++){
            int inicio=i*tamPartes;
            int fin=i+1!=partes?i*tamPartes+tamPartes-1:data.length;
            //System.out.println(inicio+","+fin);
            callables.add(new Callable<Void>() {  
                public Void call() throws Exception {  
                    for (int i=inicio;i<=fin;i++) {
                        data[i] = (byte) (data[i] ^ (int)(Math.pow(llave, i)));
                    }
                    return null;
                }  
            }); 
        }
    }

    public void encriptar() throws InterruptedException {
        
        service.invokeAll(callables);
        service.shutdown();
        
    }
}
