package pt.ipleiria.authority;

import pt.ipleiria.authority.controller.ContactController;
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

                //Retrieve srcAddress/destAddress
                String srcAddress = datagramPacket.getAddress().getHostAddress();
                String destAddress = Sender.getOutboundAddress(datagramPacket.getSocketAddress()).getHostAddress();

                if (!srcAddress.equals(destAddress)){
                    Contact contact = (Contact) oos.readObject();
                    contact.setIpAddress(srcAddress);
                    ContactController.addContact(contact);

                    //Show/Process new contact
                    Sender.logger.info("Interpretation - Success");




                    /*Sender.logger.info("name:"+contact.getName());
                    Sender.logger.info("IP:"+contact.getIpAddress());
                    Sender.logger.info("Mac:"+contact.getMAC());
                    Sender.logger.info("Public:"+contact.getPublicKey());
                    Sender.logger.info("----------------------------------------------------\n");*/

                    //Retrieves
                    receiverCallback(contact);
                }
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
    private void receiverCallback(Contact senderContact){

        Sender.logger.info("ip: "+senderContact.getIpAddress());
        Sender.logger.info("port: "+Sender.PORT);
        try(Socket TCPClient = new Socket(senderContact.getIpAddress(), Sender.PORT)) {
            DataOutputStream os = new DataOutputStream(TCPClient.getOutputStream());
            ObjectOutputStream oos = new ObjectOutputStream(os);

            Contact myContact = ContactController.getMyContact_pbk();

            //Wries data
            oos.writeObject(myContact);

            //Close connection
            oos.close();
            os.close();
        } catch (IOException | CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method receives a message in protocol TCP thought the network
     */
    public static void recieveMessage() {
        /*if (!txtEntryField.getText().isEmpty()) {
            txtChatArea.append("\n Bob: " + txtEntryField.getText());
            if (txtEntryField.getText().toLowerCase().contains("i'm bob"))
                txtChatArea.append("\n That's what she said");
            txtEntryField.setText("");
        }*/
    }
}
