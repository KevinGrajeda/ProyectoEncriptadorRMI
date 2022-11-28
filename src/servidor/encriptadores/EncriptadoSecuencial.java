package servidor.encriptadores;

import java.util.Arrays;

import static main.Main.SALT;

public class EncriptadoSecuencial {
    byte data[];

    public EncriptadoSecuencial(byte data[]) {
        this.data= data;
    }

    public void encriptar(int llave) {
        int i = 0;
        for (byte b : data) {
            data[i] = (byte) (b ^ (int)(Math.pow(llave,SALT)));
            //data[i] = (byte) (b ^ llave);
            i++;
        }


    }

}
