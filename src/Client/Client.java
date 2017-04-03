package Client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;


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
        this.state = "";
        this.inFromUser = new BufferedReader(new InputStreamReader(System.in));
    }

    //Methods

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        System.out.println("SMTP Client V0.01");
        //this.initialize();
        this.typeMessage();
    }

    private void typeMessage() {
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
        System.out.println("Sender : " + sender);
        System.out.println("Recipients : " + recipients);
        System.out.print("Message text : " + mailText);
    }

    private void write(String sentence) {
        this.output.println(sentence);
        this.output.flush();
    }

    private void engage() {

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

    //update the status
    private Boolean updateStatus(String line) {
        return null;
    }

    // display the input or update the state
    private void read() {
        String line = "";
        Boolean update = false;
        try {
            line = this.input.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (line.contains("") || line.contains("")) {
            update = updateStatus(line);
        }
        if (!update) System.out.println(line);
    }
}