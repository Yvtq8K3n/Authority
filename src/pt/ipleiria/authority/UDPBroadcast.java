package pt.ipleiria.authority;

import pt.ipleiria.authority.view.MainView;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public enum UDPBroadcast {
    UDPBroadcast;

    static Logger logger;
    protected static final int PORT = 443;//UDP PORT
    private static final String BROADCAST_ADDRESS =  "192.168.1.255";
    private static final String LOCALHOST = "127.0.0.1";


    private static final Contact contact;

    static {
        logger = Logger.getLogger(MainView.class.getName());
        contact = new Contact(
            "PC-Marquez",
            "192.168.1.1",
            "jsahdkjashdkaskdj",
                "zxczxczxczxcc"
        );
    }

    public static void broadcast(){
        try(DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setBroadcast(true);

            // Serialize Class to a byte array
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            ObjectOutput oo = new ObjectOutputStream(bStream);
            oo.writeObject(contact);
            oo.close();
            byte[] Buf= bStream.toByteArray();

            //Creating udp header
            DatagramPacket packet = new DatagramPacket(Buf,
                    Buf.length,
                    InetAddress.getByName(LOCALHOST),
                    PORT
            );
            clientSocket.send(packet);

            System.out.println("data sent!");
        } catch (SocketException e) {
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
