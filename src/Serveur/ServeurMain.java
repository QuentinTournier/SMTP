package Serveur;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Quentin on 03/04/2017.
 */
public class ServeurMain{

    public static void main(String[] args) {
        try {
            //SSLServerSocket server = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(110);
            ServerSocket server = new ServerSocket(1100);
            //server.setEnabledCipherSuites(server.getSupportedCipherSuites());
            System.out.println("Server launched on port 1100.");
            while(true){
                //SSLSocket connexion = (SSLSocket) server.accept();
                Socket connexion = server.accept();
                ThreadServer ts = new ThreadServer(connexion, "serveur1.fr");
                new Thread(ts).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
