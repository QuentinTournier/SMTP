package Serveur;


import Utils.IOUtils;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by p1303674 on 03/04/2017.
 */
public class ThreadServer implements Runnable{

    private Socket connexion;
    private IOUtils ioSocket;
    private String domain;

    public ThreadServer(Socket connexion, String domain) throws IOException {
        this.connexion = connexion;
        this.ioSocket = new IOUtils(connexion);
        this.domain = domain;
    }

    @Override
    public void run(){
        String message = "220" + "Simple Main Transfer Service Ready";
        try {
            ioSocket.send("220" + "Simple Main Transfer Service Ready");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
