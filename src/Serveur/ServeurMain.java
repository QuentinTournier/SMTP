package Serveur;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

/**
 * Created by Quentin on 03/04/2017.
 */
public class ServeurMain{

    public static void main(String[] args) {
        try {
            SSLServerSocket server = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(110);
            server.setEnabledCipherSuites(server.getSupportedCipherSuites());
            while(true){
                SSLSocket connexion = (SSLSocket) server.accept();
                ThreadServer ts = new ThreadServer(connexion);
                new Thread(ts).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
