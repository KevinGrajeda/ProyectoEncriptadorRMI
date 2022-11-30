package servidor.encriptadores;

import java.util.concurrent.RecursiveAction;

import static main.Main.SALT;

public class EncriptadoForkJoin extends RecursiveAction {
    
    private byte data[];
    private int inicio;
    private int fin;
    private int llave;

    public EncriptadoForkJoin(byte data[], int inicio, int fin, int llave) {
        this.data = data;
        this.inicio = inicio;
        this.fin = fin;
        this.llave=llave;
    }

    @Override
    protected void compute() {
        if (fin - inicio < 500) {
            encriptar(data, inicio, fin);
        } else {
            int mitad=(inicio+fin)/2;
            RecursiveAction arrIzq=new EncriptadoForkJoin(data,inicio,mitad,llave);
            RecursiveAction arrDer=new EncriptadoForkJoin(data,mitad+1,fin,llave);
            invokeAll(arrIzq,arrDer);
        }
    }

    private void encriptar(byte[] data, int inicio, int fin) {
        for (int i=inicio;i<=fin;i++) {
            data[i] = (byte) (data[i] ^ (int)(Math.pow(llave,SALT)));
        }
    }


}
