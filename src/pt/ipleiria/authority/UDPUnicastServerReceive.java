package pt.ipleiria.authority;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class UDPUnicastServerReceive implements Runnable{
    private final int UPD_MAX_PAYLOAD = 65507;

    @Override
    public void run() {
        byte[] buffer = new byte[UPD_MAX_PAYLOAD];
        UDPBroadcast.logger.info("Server Running!");

        try(DatagramSocket serverSocket = new DatagramSocket(UDPBroadcast.PORT)) {

            while (true){
                // now we know the length of the payload
                DatagramPacket datagramPacket  = new DatagramPacket(buffer, 0, buffer.length);
                serverSocket.receive(datagramPacket);

                UDPBroadcast.logger.info("Message Received!");

                ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
                ObjectInputStream oos = new ObjectInputStream(baos);
                Contact contact = (Contact) oos.readObject();

                //Show success
                UDPBroadcast.logger.info("name:"+contact.getName());
                UDPBroadcast.logger.info("IP:"+contact.getIpAddress());
                UDPBroadcast.logger.info("Mac:"+contact.getMAC());
                UDPBroadcast.logger.info("Public:"+contact.getPublicKey());

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            UDPBroadcast.logger.info(e.getMessage());
        }
    }
}
