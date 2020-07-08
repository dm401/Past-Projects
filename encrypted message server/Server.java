import java.io.*;
import java.net.*;
import java.security.*;
import java.util.ArrayList;


public class Server {
    public static void main(String [] args) throws Exception {

        if (args.length != 1) {
            System.out.println("Usage: java Server port");
        }

        ArrayList<Message> messages = new ArrayList<Message>();
        int port = Integer.parseInt(args[0]);
        ServerSocket ss = new ServerSocket(port);

        while(true) {
            System.out.println("Waiting incoming connection...");

            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            Socket s = ss.accept();
            in = new ObjectInputStream(s.getInputStream());
            out = new ObjectOutputStream(s.getOutputStream());

            String userIDHash = null;
            try {
                userIDHash = (String) in.readObject();
            } catch (IOException e) {
                System.out.println("Connection closed");
            }


            int numberOfMessages = 0;

            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).recipientHash.equals(userIDHash)) {
                    numberOfMessages++;
                }
            }

            out.writeObject(numberOfMessages);

            for(int i = 0; i < messages.size(); i++){
                if(messages.get(i).recipientHash.equals(userIDHash)) {
                    out.writeObject(messages.get(i));
                    messages.remove(i);
                    i--;
                }
            }

            int numberOfMessagesIncoming = (int) in.readObject();
            for(int i = 0; i < numberOfMessagesIncoming; i++){
                messages.add((Message) in.readObject());
            }



            System.out.println("Connection closed");
        }
    }
}
