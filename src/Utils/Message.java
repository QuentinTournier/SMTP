package Utils;

import java.util.ArrayList;

/**
 * Created by franck on 4/24/17.
 */
public class Message {

    private String sender;
    private ArrayList <String> recipients;
    private String text;

    public Message(String sender, ArrayList<String> recipients, String text) {
        this.sender = sender;
        this.recipients = recipients;
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ArrayList<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(ArrayList<String> recipients) {
        this.recipients = recipients;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
