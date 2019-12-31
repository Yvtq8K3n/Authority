package pt.ipleiria.authority;

import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Contact;
import pt.ipleiria.authority.view.MainView;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class Sender implements Runnable{

    static Logger logger;
    protected static final int PORT = 443;//UDP PORT
    protected static final String BROADCAST_ADDRESS =  "192.168.1.255";
    protected static final String LOCALHOST = "127.0.0.1";
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

                //Retrieve srcAddress
                String srcAddress = getOutboundAddress(connectionSocket.getRemoteSocketAddress()).getHostAddress();

                //Retrieves data
                Contact contact = (Contact) ois.readObject();
                contact.updateId();
                contact.setIpAddress(srcAddress);
                ContactController.addContact(contact);

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

    /** Analisys the UDP package in order to identify the src
     * @param remoteAddress
     * @return
     * @throws SocketException
     */
    protected static InetAddress getOutboundAddress(SocketAddress remoteAddress) throws SocketException {
        DatagramSocket sock = new DatagramSocket();
        // connect is needed to bind the socket and retrieve the local address
        // later (it would return 0.0.0.0 otherwise)
        sock.connect(remoteAddress);

        final InetAddress localAddress = sock.getLocalAddress();

        sock.disconnect();
        sock.close();
        sock = null;

        return localAddress;
    }

}
