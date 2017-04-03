package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by p1303674 on 03/04/2017.
 */
public class IOUtils{

    private InputStream is;
    private OutputStream os;

    public IOUtils(Socket connexion) throws IOException {
        this.is = connexion.getInputStream();
        this.os = connexion.getOutputStream();
    }

    public String read() throws IOException {
        boolean cr = false;
        boolean lf = false;
        String message = "";

        while(!cr || !lf){
            int data = is.read();
            if(data == -1){
                return "quitnonsafe";
            }
            char c = (char)data;
            message += c;

            if(cr && c == '\n')
                lf = true;
            else
                cr = false;
            if(c == '\r')
                cr = true;
        }
        if(message.equalsIgnoreCase("quitnonsafe")){
            message += ".";
        }
        message = message.replaceAll("[\r,\n]", "");
        return message;
    }

    public void send(String message) throws IOException {
        os.write(message.getBytes());
    }
}
