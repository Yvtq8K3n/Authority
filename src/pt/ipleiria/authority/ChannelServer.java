package pt.ipleiria.authority;

import pt.ipleiria.authority.controller.ConnectionsController;
import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Connection;
import pt.ipleiria.authority.model.Contact;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static pt.ipleiria.authority.Sender.LOCALHOST;
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

                    //Generate key and send key
                    String key = (String) ois.readObject();
                }

                //Uses key to decrypt message ....
                String message = (String) ois.readObject();
                System.out.println("message send:"+message);

                //Show/Process new contact
                Sender.logger.info("Message Received");

                ois.close();
                is.close();
                connectionSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(Contact destination, String message) throws IOException {
        //destination.getIpAddress()
        Socket TCPClient = new Socket(LOCALHOST, Sender.PORT+1);

        DataOutputStream os = new DataOutputStream(TCPClient.getOutputStream());
        ObjectOutputStream oos = new ObjectOutputStream(os);

        Connection conn = ConnectionsController.getConnection(destination);

        if (!conn.hasSecretKey()){
            //Generate key and send key
            String key = "MY_ULTRA_SECRET";
            oos.writeObject(key);
        }

        //Sends message
        oos.writeObject(message);
        Sender.logger.info("Message Sent:"+message);
    }
}

