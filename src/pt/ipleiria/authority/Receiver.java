package pt.ipleiria.authority;

import pt.ipleiria.authority.model.Contact;

import java.io.*;
import java.net.*;

public class Receiver implements Runnable{
    private final int UPD_MAX_PAYLOAD = 65507;


    /**
     * and then dispatches to receiverCallBack
     */
    @Override
    public void run() {
        byte[] buffer = new byte[UPD_MAX_PAYLOAD];

        try(DatagramSocket serverSocket = new DatagramSocket(Sender.PORT)) {

            while (true){
                // now we know the length of the payload
                DatagramPacket datagramPacket  = new DatagramPacket(buffer, 0, buffer.length);
                serverSocket.receive(datagramPacket);

                ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
                ObjectInputStream oos = new ObjectInputStream(baos);
                Contact contact = (Contact) oos.readObject();

                //Show/Process new contact
                Sender.logger.info("Interpretation - Success");
                /*Sender.logger.info("name:"+contact.getName());
                Sender.logger.info("IP:"+contact.getIpAddress());
                Sender.logger.info("Mac:"+contact.getMAC());
                Sender.logger.info("Public:"+contact.getPublicKey());
                Sender.logger.info("----------------------------------------------------\n");*/

                //Retrieves
                receiverCallback();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Sender.logger.info(e.getMessage());
        }
    }

    /**
     * Return pc information
     * via TCP
     */
    private void receiverCallback(){
        try(Socket TCPClient = new Socket(Sender.contact.getIpAddress(), Sender.PORT)) {
            DataOutputStream os = new DataOutputStream(TCPClient.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(os);

            //Wries data
            oos.writeObject(Sender.contact);

            //Close connection
            oos.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
