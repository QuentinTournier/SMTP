package Client;

import Utils.IOUtils;
import Utils.Message;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


/**
 * Created by franck on 4/3/17.
 * This is the client code
 */
public class Client {

    //Attributes
    private Socket clientSocket;
    private String state;
    private String hostName;
    private int port;
    private BufferedReader inFromUser;
    private IOUtils ioSocket;

    //Constructors
    public Client(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        this.state = "stopped";
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));

    }

    //Methods

    public void run() {
        String sentence;
        System.out.println("SMTP Client V0.01");
        this.initialize();
        this.connect();

        while(!this.state.equals("waitingForQuit")){

            // Write your message
            Message message = this.typeMessage();

            // Send the sender to the server
            if(!this.state.equals("waitingForQuit")) {
                sentence = "MAIL FROM " + message.getSender();
                ioSocket.send(sentence);
                sentence = ioSocket.read();
                if (sentence.startsWith("250")) {
                    this.state = "sendRecipients";
                } else {
                    System.out.println("Error, shutting down...");
                    this.state = "waitingForQuit";
                }
            }

            // Send all the recipients to the server
            for (String s : message.getRecipients()) {
                if (!this.state.equals("waitingForQuit")) {
                    sentence = "RCPT " + s;
                    ioSocket.send(sentence);
                    sentence = ioSocket.read();
                }
            }
            if(!this.state.equals("waitingForQuit"))
                this.state = "sendData";

            // Send the datas to the server
            if(!this.state.equals("waitingForQuit")) {
                sentence = "DATA";
                ioSocket.send(sentence);
                sentence = ioSocket.read();
                if (sentence.startsWith("354")) {
                    ioSocket.send(message.getText());
                    this.state = "ready";
                }
                else {
                    System.out.println("Error, shutting down...");
                    this.state = "waitingForQuit";
                }
            }

            // Ask if the user wants to write an other message
            System.out.println("Write an other message ? [Y/n]");
            type();
        }
    }

    // initialize the connection to the server
    private void initialize() {
        try {
            //Socket
            this.clientSocket = new Socket(this.hostName, this.port);

            //Streams
            this.ioSocket = new IOUtils(this.clientSocket);

            //State
            this.state = "initialisation";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Asks to the user to write his message
    private Message typeMessage() {

        String sender = "";
        ArrayList<String> recipients = new ArrayList<>();
        StringBuilder mailText = new StringBuilder();

        //Sender address
        System.out.println("Please enter your mail address.");
        try {
            String s = this.inFromUser.readLine();
            while(!s.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")){
                System.out.println("Please enter a valid mail address.");
                s = this.inFromUser.readLine();
            }
            sender = s.toLowerCase();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Recipients addresses
        System.out.println("Please enter mail recipients. Enter blank line to finish.");
        try {
            String s = this.inFromUser.readLine();
            while(!s.equals("")){
                if(s.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")){
                    recipients.add(s.toLowerCase());
                }
                else{
                    System.out.println("Please enter a valid mail address.");
                }
                s = this.inFromUser.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Mail Text
        System.out.println("Mail text. Enter . to finish.");
        try {
            String s = this.inFromUser.readLine();
            while(!s.equals(".")){
                mailText.append(s).append("\r\n");
                s = this.inFromUser.readLine();
            }
            mailText.append(s);


        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Message(sender, recipients, mailText.toString());
    }

    // connect to the server
    private void connect() {
        System.out.println("Waiting for server ...");
        String received = ioSocket.read();
        if(received.startsWith("220")){
            System.out.println("Server joined.");
            ioSocket.send("EHLO client");
            received = ioSocket.read();
            if(received.startsWith("250"))
                this.state = "ready";
            else {
                System.out.println("Error on EHLO sequence.");
                this.state = "waitingForQuit";
            }
        }
        else{
            System.out.println("Unable to contact server.");
            this.state = "waitingForQuit";
        }
    }

    //Stop the client
    public void stop() {
        System.out.println("Closing...");
        try {
            ioSocket.send("QUIT");
            String s = ioSocket.read();
            if(s.startsWith("221")){
                System.out.println("Closed");
            }
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* // receive and return the server's message
    private String read() {
        String line = "";
        try {
            line = this.input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    // send sentence to the server
    private void send(String sentence) {
        this.output.println(sentence);
        this.output.flush();
    }*/

    // Check if the user wants to quit the application
    private void type(){
        try {
            String s = this.inFromUser.readLine();
            if(s.equalsIgnoreCase("n") || s.equalsIgnoreCase("no") || s.equalsIgnoreCase("quit")){
                this.state = "waitingForQuit";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}