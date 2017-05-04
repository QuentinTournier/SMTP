package Client;

/**
 * Created by Quentin on 03/04/2017.
 */
public class ClientMain {

    public static void main (String[] args){
        Client cli = new Client("127.0.0.1", 110);
        cli.run();
        cli.stop();
    }
}

