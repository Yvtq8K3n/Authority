package pt.ipleiria.authority;

import pt.ipleiria.authority.controller.ConnectionsController;
import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Connection;
import pt.ipleiria.authority.model.Contact;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

import static pt.ipleiria.authority.Sender.getOutboundAddress;

public class ChannelServer extends Thread {
    protected static final int PORT = 443;//UDP PORT

    @Override
    public void run() {
        Sender.logger.info("Channel Server Running");
        try (ServerSocket TCPChannelSocket = new ServerSocket(PORT+1)) {
            while (true){
                Socket connectionSocket = TCPChannelSocket.accept();
                DataInputStream is = new DataInputStream(connectionSocket.getInputStream());
                ObjectInputStream ois = new ObjectInputStream(is);

                //Retrieve srcAddress
                //String srcAddress = getOutboundAddress(connectionSocket.getRemoteSocketAddress()).getHostAddress();
                String srcAddress = connectionSocket.getInetAddress().getHostAddress();

                Contact contact = ContactController.getContact(srcAddress);
                Connection connection = ConnectionsController.getConnection(contact);

                Sender.logger.info("contact:" + contact);
                Sender.logger.info("connection:" + connection);

                if(connection == null){
                    connection = ConnectionsController.addConnection(contact);


                    /*
                    byte[] encryptSrcSecret = ConnectionsController.encrypt(conn.getSecretKey(), ContactController.getMyContact().getPrivateKeyClass());
                    byte[] encryptDestPub = ConnectionsController.encrypt(encryptSrcSecret, destination.getPublicKeyClass());
                    byte[] encodedKey = Base64.getEncoder().encode(Base64.getEncoder().encode(encryptDestPub));

                     */
                    //Receive key
                    byte[] decodedkey = Base64.getDecoder().decode((byte[]) ois.readObject());
                    //byte[] decryptDestPriv = ConnectionsController.decrypt(decodedkey, ContactController.getMyContact().getPrivateKeyClass());
                    byte[] decryptSrcPub = ConnectionsController.decrypt(decodedkey, contact.getPublicKeyClass());

                    Sender.logger.info("Key: " + new String(decryptSrcPub));
                    //connection.setSecretKey(decryptSrcPub);

                    //Sender.logger.info("Key: " + new String(decryptSrcPub));
                    boolean a = connection.getSecretKey() == null;
                    Sender.logger.info(new String(connection.getSecretKey()));
                    Sender.logger.info("ola");
                }

                //Uses key to decrypt message ....
                byte[] byteMessage = Base64.getDecoder().decode((byte[]) ois.readObject());

                System.out.println(connection.getSecretKeyClass()!=null);

                String message = ConnectionsController.decrypt(byteMessage, connection.getSecretKeyClass());
                System.out.println("message send:"+message);

                ConnectionsController.sendMessage(message);

                try {
                    ConnectionsController.getChatPanel(connection).addMessage(ContactController.getMyContact_pbk().getName(), message);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                //Show/Process new contact
                Sender.logger.info("Message Received");

                ois.close();
                is.close();
                connectionSocket.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(Contact destination, String message) throws IOException {
        //destination.getIpAddress()
        Socket TCPClient = new Socket(destination.getIpAddress(), Sender.PORT+1);

        DataOutputStream os = new DataOutputStream(TCPClient.getOutputStream());
        ObjectOutputStream oos = new ObjectOutputStream(os);

        Connection conn = ConnectionsController.getConnection(destination);

        if (!conn.hasSecretKey()){
            //generate key
            conn.generateKey();

            System.out.println("message send:"+conn.getSecretKey());

            byte[] encryptSrcSecret = ConnectionsController.encrypt(conn.getSecretKey(), ContactController.getMyContact().getPrivateKeyClass());
            //byte[] encryptDestPub = ConnectionsController.encrypt(encryptSrcSecret, destination.getPublicKeyClass());
            byte[] encodedKey = Base64.getEncoder().withoutPadding().encode(encryptSrcSecret);

            oos.writeObject(encodedKey);
        }

        //Sends message
        byte[] cypheredMessage = ConnectionsController.encrypt(message, conn.getSecretKeyClass());

        oos.writeObject(Base64.getEncoder().withoutPadding().encode(cypheredMessage));
        Sender.logger.info("Message Sent: "+message);
    }
}

