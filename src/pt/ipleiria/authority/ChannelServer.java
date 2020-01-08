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

                if(connection == null || !connection.hasSecretKey()){
                    if (connection == null) {
                        connection = ConnectionsController.addConnection(contact);
                    }

                    //Receive key
                    byte[] key = Base64.getDecoder().decode((byte[]) ois.readObject());

                    //System.out.println("Key: " + Base64.getEncoder().encodeToString(key));

                    //byte[] keyParteA = Base64.getDecoder().decode((byte[]) ois.readObject());//128 ola121212 ->   5464216545231o5465454156d4asl54645a
                   // byte[] keyParteB = Base64.getDecoder().decode((byte[]) ois.readObject());//128 ola121212 ->   5464216545231o546545415612ghfgd645a

                    //byte[] keyDecryptedA = ConnectionsController.decrypt(keyParteA,ContactController.getMyContact().getPrivateKeyClass());//128 ola152143546456465465465
                    //byte[] keyDecryptedB = ConnectionsController.decrypt(keyParteB,ContactController.getMyContact().getPrivateKeyClass());//128

                    //byte[] key = new byte[128];

                    //System.arraycopy(keyDecryptedA,0, key, 0, 64);
                   // System.arraycopy(keyDecryptedB, 0, key, 64, 64);

                   // byte[] fim = ConnectionsController.decrypt(key, contact.getPublicKeyClass());

                    //connection.setSecretKey(fim);

                    //System.out.println("Key: " + new String(key));

                    //connection.setSecretKey(ConnectionsController.decrypt(ConnectionsController.decrypt(key, contact.getPublicKeyClass()),ContactController.getMyContact().getPrivateKeyClass()));

                    byte[] uncypheredKey = ConnectionsController.decrypt(key, ContactController.getMyContact().getPrivateKeyClass());
                    System.out.println(Base64.getEncoder().encodeToString(uncypheredKey));

                    connection.setSecretKey(uncypheredKey);

                    System.out.println(Base64.getEncoder().encodeToString(connection.getSecretKey()));
                    System.out.println("ola");
                }

                //Uses key to decrypt message ....
                byte[] byteMessage = Base64.getDecoder().decode((byte[]) ois.readObject());

                System.out.println(connection.getSecretKeyClass()!=null);

                String message = ConnectionsController.decrypt(byteMessage, connection.getSecretKeyClass());
                System.out.println("message send:"+message);

                ConnectionsController.updateChannelChatbox(contact,message);

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
        System.out.println("hello");
        try(Socket TCPClient = new Socket(destination.getIpAddress(), Sender.PORT+1)) {

            DataOutputStream os = new DataOutputStream(TCPClient.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(os);

            Connection conn = ConnectionsController.getConnection(destination);

            if (!conn.hasSecretKey()){
                //generate key
                conn.generateKey();

                //byte[] cypheredKey = ConnectionsController.encrypt(ConnectionsController.encrypt(conn.getSecretKey(), ContactController.getMyContact().getPrivateKeyClass()), destination.getPublicKeyClass());

                byte[] cypheredKey = ConnectionsController.encrypt(conn.getSecretKey(), destination.getPublicKeyClass());
                //System.out.println(new String(cypheredKey));

               // byte[] stCifra = ConnectionsController.encrypt(conn.getSecretKey(), ContactController.getMyContact().getPrivateKeyClass());

                //byte[] parteA = new byte[stCifra.length/2];//32
                //byte[] parteB = new byte[stCifra.length-parteA.length];//32

                //System.arraycopy(stCifra,0,parteA,0,parteA.length);
                //System.arraycopy(stCifra,parteA.length,parteB,0,parteB.length);


                //byte[] encryptedA = ConnectionsController.encrypt(parteA, destination.getPublicKeyClass());//128 ola121212 ->   5464216545231o5465454156d4asl54645a
                //byte[] encryptedB = ConnectionsController.encrypt(parteB, destination.getPublicKeyClass());//128 ola121212 ->   5464216545231o546545415612ghfgd645a

                //oos.writeObject(Base64.getEncoder().encode(encryptedA));
                //oos.writeObject(Base64.getEncoder().encode(encryptedB));

               //System.out.println("1st: " + new String(stCifra));
                //System.out.println(("2nd: " + new String(ndCifra)));


                oos.writeObject(Base64.getEncoder().encode(cypheredKey));


            }

            //Sends message
            byte[] cypheredMessage = ConnectionsController.encrypt(message, conn.getSecretKeyClass());

            oos.writeObject(Base64.getEncoder().encode(cypheredMessage));
            Sender.logger.info("Message Sent: "+message);

            oos.close();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

