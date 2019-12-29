package pt.ipleiria.authority;

import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Contact;
import pt.ipleiria.authority.model.KeyPairGen;
import pt.ipleiria.authority.view.MainView;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class Sender implements Runnable{

    static Logger logger;
    protected static final int PORT = 443;//UDP PORT
    protected static final String BROADCAST_ADDRESS =  "192.168.1.255";
    protected static final String LOCALHOST = "127.0.0.1";

    //private static final String PATH = "/Users/joaoz/Downloads/"; //MARQUEZ
    private static final String PATH = "/Users/joaoz/Downloads/"; //JONNY

    protected static Contact contact;

    public Sender() {
        logger = Logger.getLogger(MainView.class.getName());


        //RSA - DES - TRIPLE DES

        try {
            contact = ContactController.getMyContact_pbk();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method thought the protocol UDP, broadcasts the network
     * in order to introduce itself into the network.
     */
    public static void broadcast(){
        try(DatagramSocket UDPClientSocket = new DatagramSocket()) {
            UDPClientSocket.setBroadcast(true);

            // Serialize Class to a byte array
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(contact);
            oo.close();
            byte[] Buf = bStream.toByteArray();

            //Creating udp header
            DatagramPacket packet = new DatagramPacket(Buf,
                    Buf.length,
                    InetAddress.getByName(BROADCAST_ADDRESS),
                    PORT
            );
            UDPClientSocket.send(packet);


            Sender.logger.info("Introduction - Success");

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (ServerSocket TCPServerSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket connectionSocket = TCPServerSocket.accept();
                DataInputStream is = new DataInputStream(connectionSocket.getInputStream());
                ObjectInputStream ois = new ObjectInputStream(is);

                //Retrieves data
                Contact contact1 = (Contact) ois.readObject();
                ContactController.addContact(contact1);

                //Show/Process new contact
                Sender.logger.info("Integration - Success");
                /*Sender.logger.info("name:"+contact1.getName());
                Sender.logger.info("IP:"+contact1.getIpAddress());
                Sender.logger.info("Mac:"+contact1.getMAC());
                Sender.logger.info("Public:"+contact1.getPublicKey());
                Sender.logger.info("----------------------------------------------------\n");*/

                ois.close();
                is.close();
                connectionSocket.close();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /*

String hostname = "Unknown";

try
{
    InetAddress addr;
    addr = InetAddress.getLocalHost();
    hostname = addr.getHostName();
}
catch (UnknownHostException ex)
{
    System.out.println("Hostname can not be resolved");
}

     */
}
