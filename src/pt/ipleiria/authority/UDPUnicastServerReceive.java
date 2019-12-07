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

        try(DatagramSocket serverSocket = new DatagramSocket(UDPBroadcast.PORT)) {

            while (true){
                byte[] data = new byte[4];
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                serverSocket.receive(datagramPacket);

                // now we know the length of the payload
                datagramPacket = new DatagramPacket(buffer, 0, buffer.length);
                serverSocket.receive(datagramPacket);

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
            UDPBroadcast.logger.info("A");
        } catch (IOException e) {
            e.printStackTrace();
            UDPBroadcast.logger.info("B");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void run() {
        try(DatagramSocket serverSocket = new DatagramSocket(UDPBroadcast.PORT)) {

            while (true){
                byte[] data = new byte[4];
                DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
                serverSocket.receive(datagramPacket);

                int len = 0;
                // byte[] -> int
                for (int i = 0; i < 4; ++i) {
                    len |= (data[3-i] & 0xff) << (i << 3);
                }

                // now we know the length of the payload
                byte[] buffer = new byte[len];
                datagramPacket = new DatagramPacket(buffer, buffer.length );
                serverSocket.receive(datagramPacket);

                ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
                ObjectInputStream oos = new ObjectInputStream(baos);
                Contact contact = (Contact) oos.readObject();

                //Show success
                UDPBroadcast.logger.info("name:"+contact.getName());
            }

        } catch (SocketException e) {
            e.printStackTrace();
            UDPBroadcast.logger.info("A");
        } catch (IOException e) {
            e.printStackTrace();
            UDPBroadcast.logger.info("B");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }*/
}
