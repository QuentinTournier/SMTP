package Serveur;


import Utils.IOUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by p1303674 on 03/04/2017.
 */
public class ThreadServer implements Runnable{

    private final int WAITINGMAIL = 0;
    private final int WAITINGRCPT = 1;
    private final int WAITINGDATA = 2;

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
        try {
            String message;
            String[] cmd;
            String remoteDomain;
            String sender;
            List recipients = new ArrayList<String>();
            int state;
            boolean ehlo = false;
            boolean quit = false;

            ioSocket.send("220 " + domain + " Simple Main Transfer Service Ready");
            while(!ehlo && !quit){
                message = ioSocket.read();
                if(message.startsWith("EHLO")){
                    cmd = message.split(" ");
                    if(cmd.length > 1) {
                        remoteDomain = cmd[1];
                        ehlo = true;
                        ioSocket.send("250 - Hello "+remoteDomain);
                    }
                    else
                        ioSocket.send("501 Who are you ?");
                }else if(message.startsWith("QUIT")){
                    quit = true;
                }else
                    ioSocket.send("503 - EHLO required.");
            }
            state = WAITINGMAIL;
            while(!quit){
                message = ioSocket.read();
                cmd = message.split(" ");

                switch(cmd[0].toUpperCase()){
                    case "MAIL" :
                        if(cmd.length >1 ){
                            sender = cmd[1];
                            recipients.clear();
                        }
                        else{
                            ioSocket.send("501 Who are you ?");
                        }
                        break;
                    case "RCPT" :
                        //Vérifier que le client est sur le serveur
                        if(state == WAITINGMAIL){
                            ioSocket.send("503 MAIL required");
                            continue;
                        }
                        if(cmd.length >1 ){
                            recipients.add(cmd[1]);
                        }
                        else{
                            ioSocket.send("501 Who are you ?");
                        }
                        break;
                    case "DATA" :
                        if(state == WAITINGRCPT){
                            ioSocket.send("503 RCPT required");
                            continue;
                        }
                        if(state == WAITINGMAIL){
                            ioSocket.send("503 MAIL required");
                            continue;
                        }

                        break;
                    case "RSET" :
                        break;
                    case "QUIT" :
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
