import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Client {
    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            System.out.println("Usage: java Server host port userID");
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String userID = args[2];

        String hashedUserID = generateHash(userID);

        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            Socket s = new Socket(host, port);
            out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(hashedUserID);
            in = new ObjectInputStream(s.getInputStream());

        } catch (IOException e) {
            System.err.println("Cannot connect to server.");
        }

        int numberOfMessages = -1;
        try {
            numberOfMessages = (int) in.readObject();
        } catch (IOException e) {
            System.out.println("Connection closed");
        }

        System.out.println("you have " + numberOfMessages + " new messages:");

        for (int i = 0; i < numberOfMessages; i++) {
            Message message = (Message) in.readObject();
            printMessage(message, userID);
        }

        boolean sendMessage = true;
        ArrayList<Message> messagesToSend = new ArrayList<Message>();
        while(sendMessage == true){
            System.out.println("\nWould you like to send a message? ");
            Scanner s = new Scanner(System.in);
            if (s.nextLine().toLowerCase().equals("n")){
                sendMessage = false;
            }
            else{
                System.out.println("\nWho to? ");
                String recipientPlaintext = s.nextLine();
                String recipientHash = generateHash(recipientPlaintext);
                System.out.println("\nType your message: ");
                String plaintextMessageToSend = s.nextLine();
                messagesToSend.add(createMessage(recipientHash, plaintextMessageToSend, userID, recipientPlaintext));
            }
        }
        try {
            out.writeObject(messagesToSend.size());
            for (int i = 0; i < messagesToSend.size(); i++) {
                out.writeObject(messagesToSend.get(i));
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static String generateHash(String userID) {
        try {
            //generate hash
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(userID.getBytes());

            //convert to a string of hexadecimal and return
            StringBuffer output = new StringBuffer();
            for (int i = 0; i < hashedBytes.length; i++) {
                String hex = Integer.toHexString(0xff & hashedBytes[i]);
                if (hex.length() == 1) {
                    output.append('0');
                }
                output.append(hex);
            }
            return output.toString();


        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            return "error";
        }
    }

    //TODO decrypt message, verify sig and print
    public static void printMessage(Message message, String userID) {

        try{
            ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(userID + ".prv"));
            PrivateKey userPrivateKey = (PrivateKey) keyIn.readObject();

            Cipher c = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            c.init(Cipher.DECRYPT_MODE, userPrivateKey);
            byte[] decryptedAESBytes = c.doFinal(message.key);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKey sk = new SecretKeySpec(decryptedAESBytes, "AES");
            cipher.init(Cipher.DECRYPT_MODE, sk, new IvParameterSpec(message.iv));
            byte[] decryptedMessageBytes = cipher.doFinal(message.encryptedMsg);

            //below is format:    MESSAGE     \n     PLAINTEXT USERID OF SENDER
            String decryptedMessageString = new String(decryptedMessageBytes, "UTF8");

            String[] stringSplitter = decryptedMessageString.split("\\r?\\n");
            String plaintext = stringSplitter[0];
            String senderID = stringSplitter[1];

            //verify signature against senderID
            ObjectInputStream senderPubKeyIn = new ObjectInputStream(new FileInputStream(senderID + ".pub"));
            PublicKey senderPubKey = (PublicKey)senderPubKeyIn.readObject();

            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(senderPubKey);
            if (!(sig.verify(message.signature))){
                System.out.println("Signature not valid");
            }else{
                System.out.println("\n" + senderID + "'s message:");
                System.out.println(plaintext);
                System.out.println(message.timestamp);
            }


        }catch(Exception e){System.out.println(e);}
    }

    public static Message createMessage(String recipientHash, String plaintext, String senderID, String recipientPlaintext){
        Message message = new Message();
        message.recipientHash = recipientHash;

        Date date = new Date();
        message.timestamp = date;

        //generate AES Key
        SecretKey unencryptedAESKey = null;
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            unencryptedAESKey = kg.generateKey();
        }catch(NoSuchAlgorithmException e){System.out.println(e);}

        //generate 16BYTE IV
        SecureRandom sr = new SecureRandom();
        byte[] iv = new byte[16];
        sr.nextBytes(iv);
        message.iv = iv;

        //encrypt message
        plaintext = plaintext + "\n" + senderID;
        byte[] plaintextBytes = plaintext.getBytes();
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, unencryptedAESKey, new IvParameterSpec(iv));
            message.encryptedMsg = c.doFinal(plaintextBytes);
        }catch(Exception e){System.out.println(e);}

        //encrypt AES key with recipient public key
        try{
            ObjectInputStream keyIn = new ObjectInputStream(new FileInputStream(recipientPlaintext + ".pub"));
            PublicKey recipientPublicKey = (PublicKey) keyIn.readObject();


            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, recipientPublicKey);
            message.key = cipher.doFinal(unencryptedAESKey.getEncoded());
        }catch(Exception e){System.out.println(e);}

        //create signiture
        try {
            ObjectInputStream senderPrvKeyIn = new ObjectInputStream(new FileInputStream(senderID + ".prv"));
            PrivateKey senderPrvKey = (PrivateKey) senderPrvKeyIn.readObject();
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initSign(senderPrvKey);
            message.signature = sig.sign();
        }catch(Exception e){System.out.println(e);}

        return message;
    }
}