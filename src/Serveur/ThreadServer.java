package Serveur;


import Utils.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by p1303674 on 03/04/2017.
 */
public class ThreadServer implements Runnable{

    private final int LISTENINGDATA = 3;
    private final int WAITINGMAIL = 0;
    private final int WAITINGRCPT = 1;
    private final int WAITINGDATA = 2;
    private final String STOCKPATH = "./mails/";
    private final String USERFILE = "./users.txt";


    private Socket connexion;
    private IOUtils ioSocket;
    private String domain;
    private String mail;

    public ThreadServer(Socket connexion, String domain) throws IOException {
        this.connexion = connexion;
        this.ioSocket = new IOUtils(connexion);
        this.domain = domain;
        mail = "";
    }

    @Override
    public void run(){
        try {
            String message;
            String[] cmd;
            String remoteDomain;
            String sender = null;
            List<String> recipients = new ArrayList<String>();
            int state;
            boolean ehlo = false;
            boolean quit = false;
            System.out.println("Connected");

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
                if(state == LISTENINGDATA){
                    boolean endOfMessage = addLine(message);
                    if (endOfMessage){
                        state = WAITINGMAIL;
                        String messageToWrite = "FROM : "+ sender+ "\r\n";
                        messageToWrite += mail;
                        for (String recipient: recipients) {
                            String userName = recipient.split("@")[0];
                            Path file = (new File(STOCKPATH+userName+".txt")).toPath();
                            Files.write(file,messageToWrite.getBytes(), StandardOpenOption.APPEND);
                        }
                        mail = "";
                    }
                    else{
                        mail += message;
                    }
                    continue;
                }
                cmd = message.split(" ");

                switch(cmd[0].toUpperCase()){
                    case "MAIL" :
                        if(cmd.length >1 ){
                            sender = cmd[1];
                            recipients.clear();
                            ioSocket.send("250 OK");
                            state = WAITINGRCPT;
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
                            String mailUser = cmd[1];
                            String[] userNameAndDomain = mailUser.split("@");
                            if(checkValidUser(userNameAndDomain)){
                                recipients.add(cmd[1]);
                                state = WAITINGDATA;
                                ioSocket.send("250 OK");
                            }
                            else{
                                ioSocket.send("550 no such user");
                            }
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
                        ioSocket.send("354 Start mail input; end with <CRLF>.<CRLF>");
                        state = LISTENINGDATA;
                        break;
                    case "RSET" :
                            recipients.clear();
                            sender = "";
                            state = WAITINGMAIL;
                            ioSocket.send("250 OK");
                        break;
                    case "QUIT" :
                        recipients.clear();
                        sender = "";
                        quit = true;
                        break;
                    default:
                        ioSocket.send("UNKNOWN COMMAND");
                        break;
                }
            }
            //On a quitté
            ioSocket.send("221 service closing");
            connexion.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkValidUser(String[] userNameAndDomain) {
        if(!domain.equals(userNameAndDomain[1])){
            return false;
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(USERFILE));
            String currentLine;

            while((currentLine = br.readLine()) != null){
                if(currentLine.equals(userNameAndDomain[0])){
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean addLine(String message) {
        if(message.equals(".")){
            return true;
        }
        mail += message + "\r\n";
        return false;
    }
}
