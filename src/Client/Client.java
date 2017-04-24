package Client;

import Utils.Message;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;


/**
 * Created by franck on 4/3/17.
 */
public class Client {

    //Attributes
    private SSLSocket clientSocket;
    private PrintWriter output;
    private BufferedReader input;
    private String state;
    private String hostName;
    private int port;
    private BufferedReader inFromUser;

    //Constructors
    public Client(String hostName, int port) {
        this.hostName = hostName;
        this.port = port;
        this.state = "stopped";
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
    }

    //Methods

    public void run() {
        System.out.println("SMTP Client V0.01");
        //this.initialize();
        //this.connect();

        while(!this.state.equals("waitingForQuit")){
            Message message = this.typeMessage();

        }
    }

    private void initialize() {
        try {
            //Socket
            SSLSocketFactory fact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            this.clientSocket = (SSLSocket) fact.createSocket(String.valueOf(this.hostName), this.port);

            //Setting ciphers
            String[] supportedCiphers = clientSocket.getSupportedCipherSuites();
            ArrayList<String> enabledCiphers = new ArrayList<>();

            //Checking ciphers
            for (String s : supportedCiphers) {
                if (s.contains("anon")) {
                    enabledCiphers.add(s);
                }
            }

            String[] ciphersToSet = new String[enabledCiphers.size()];
            ciphersToSet = enabledCiphers.toArray(ciphersToSet);

            this.clientSocket.setEnabledCipherSuites(ciphersToSet);
            this.clientSocket.setNeedClientAuth(true);

            //Streams
            this.output = new PrintWriter(this.clientSocket.getOutputStream());
            this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            //State
            this.state = "initialisation";

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Message typeMessage() {

        String sender = "";
        ArrayList<String> recipients = new ArrayList<>();
        String mailText = "";

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
                mailText += s + "\r\n";
                s = this.inFromUser.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Message(sender, recipients, mailText);

        /*System.out.println("Sender : " + sender);
        System.out.println("Recipients : " + recipients);
        System.out.print("Message text : " + mailText);*/
    }

    private void connect() {
        System.out.println("Waiting for server ...");
        String received = this.read();
        if(received.startsWith("220")){
            System.out.println("Server joined.");
            this.send("EHLO");
            this.state = "ready";
        }
        else{
            System.out.println("Unable to contact server.");
            this.state = "stopped";
        }
    }

    //Stop
    public void stop() {
        System.out.println("Closing...");
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Closed");
    }

    // display the input or update the state
    private String read() {
        String line = "";
        try {
            line = this.input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    private void send(String sentence) {
        this.output.println(sentence);
        this.output.flush();
    }

    public void type(){
        try {
            String s = this.inFromUser.readLine();
            if(s.contains("quit")){
                this.state = "waitingForQuit";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}