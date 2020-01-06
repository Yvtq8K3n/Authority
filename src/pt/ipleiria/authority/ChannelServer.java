package pt.ipleiria.authority;

import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Contact;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static pt.ipleiria.authority.Sender.LOCALHOST;

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

                //Retrieves data
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

    public static void sendMessage(Contact senderContact, String message) throws IOException {
        //senderContact.getIpAddress()
        Socket TCPClient = new Socket(LOCALHOST, Sender.PORT+1);

        DataOutputStream os = new DataOutputStream(TCPClient.getOutputStream());
        ObjectOutputStream oos = new ObjectOutputStream(os);

        //Write message
        oos.writeObject(message);
        Sender.logger.info("Message Sent:"+message);
    }
}

