package pt.ipleiria.authority;

import pt.ipleiria.authority.controller.ConnectionsController;
import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Connection;
import pt.ipleiria.authority.model.Contact;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
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

                    //Receive key
                    byte[] keyParteA = Base64.getDecoder().decode((byte[]) ois.readObject());
                    byte[] keyParteB = Base64.getDecoder().decode((byte[]) ois.readObject());

                    byte[] keyDecryptedA = ConnectionsController.decrypt(keyParteA,ContactController.getMyContact().getPrivateKeyClass());
                    byte[] keyDecryptedB = ConnectionsController.decrypt(keyParteB,ContactController.getMyContact().getPrivateKeyClass());

                    byte[] key = new byte[keyDecryptedA.length+keyDecryptedB.length];

                    System.arraycopy(keyDecryptedA,0, key, 0, keyDecryptedA.length);
                    System.arraycopy(keyDecryptedB, 0, key, keyDecryptedA.length, key.length);

                    System.out.println("Key: " + new String(key));

                    connection.setSecretKey(ConnectionsController.decrypt(ConnectionsController.decrypt(key, contact.getPublicKeyClass()),ContactController.getMyContact().getPrivateKeyClass()));

                    //connection.setSecretKey(ConnectionsController.decrypt(key, ContactController.getMyContact().getPrivateKeyClass()));

                    System.out.println(new String(connection.getSecretKey()));
                    System.out.println("ola");
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

            //byte[] cypheredKey = ConnectionsController.encrypt(ConnectionsController.encrypt(conn.getSecretKey(), ContactController.getMyContact().getPrivateKeyClass()), destination.getPublicKeyClass());

            //System.out.println(new String(cypheredKey));

            byte[] stCifra = ConnectionsController.encrypt(conn.getSecretKey(), ContactController.getMyContact().getPrivateKeyClass());

            byte[] parteA = new byte[stCifra.length/2];
            byte[] parteB = new byte[stCifra.length-parteA.length];

            System.arraycopy(stCifra,0,parteA,0,parteA.length);
            System.arraycopy(stCifra,parteA.length,parteB,0,parteB.length);


            byte[] encryptedA = ConnectionsController.encrypt(parteA, destination.getPublicKeyClass());
            byte[] encryptedB = ConnectionsController.encrypt(parteB, destination.getPublicKeyClass());

            oos.writeObject(Base64.getEncoder().encode(encryptedA));
            oos.writeObject(Base64.getEncoder().encode(encryptedB));

           //System.out.println("1st: " + new String(stCifra));
            //System.out.println(("2nd: " + new String(ndCifra)));


            //oos.writeObject(Base64.getEncoder().encode(ndCifra));
        }

        //Sends message
        byte[] cypheredMessage = ConnectionsController.encrypt(message, conn.getSecretKeyClass());

        oos.writeObject(Base64.getEncoder().encode(cypheredMessage));
        Sender.logger.info("Message Sent: "+message);
    }
}

