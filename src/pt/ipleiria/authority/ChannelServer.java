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
                String srcAddress = getOutboundAddress(connectionSocket.getRemoteSocketAddress()).getHostAddress();
                Contact contact = ContactController.getContact(srcAddress);
                Connection connection = ConnectionsController.getConnection(contact);

                Sender.logger.info("contact:" + contact);
                Sender.logger.info("connection:" + connection);

                if(connection == null){
                    connection = ConnectionsController.addConnection(contact);

                    //Receive key
                    byte[] key = Base64.getDecoder().decode((byte[]) ois.readObject());

                    connection.setSecretKey(ConnectionsController.decrypt(ConnectionsController.decrypt(
                            key, contact.getPublicKeyClass()),ContactController.getMyContact().getPrivateKeyClass()));
                }

                //Uses key to decrypt message ....
                byte[] byteMessage = Base64.getDecoder().decode((byte[]) ois.readObject());

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
            byte[] cypheredKey = ConnectionsController.encrypt(
                    ConnectionsController.encrypt(conn.getSecretKey(), ContactController.getMyContact().getPrivateKeyClass()), destination.getPublicKeyClass());

            oos.writeObject(Base64.getEncoder().encode(cypheredKey));
        }

        //Sends message
        byte[] cypheredMessage = ConnectionsController.encrypt(message, conn.getSecretKeyClass());

        oos.writeObject(Base64.getEncoder().encode(cypheredMessage));
        Sender.logger.info("Message Sent: "+message);
    }
}

