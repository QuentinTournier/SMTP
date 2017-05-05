package Utils;

import java.util.ArrayList;

/**
 * Created by franck on 4/24/17.
 */
public class Message {

    private String sender;
    private ArrayList <String> recipients;
    private String subject;
    private String text;
    private String data;

    public Message(String sender, ArrayList<String> recipients, String subject, String text) {
        this.sender = sender;
        this.recipients = recipients;
        this.subject = subject;
        this.text = text;
        compile();
    }

    private void compile(){
        StringBuilder builder = new StringBuilder();
        builder.append("FROM: ").append(sender).append("\r\n");
        builder.append("TO: ");
        for (String s : this.recipients) {
            builder.append(s).append(" ");
        }
        builder.append("\r\n");
        builder.append("SUBJECT: ").append(subject).append("\r\n");
        builder.append("\r\n");
        builder.append(this.text);
        this.data = builder.toString();
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
