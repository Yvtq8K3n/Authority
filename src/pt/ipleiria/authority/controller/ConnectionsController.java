package pt.ipleiria.authority.controller;

import pt.ipleiria.authority.ChannelServer;
import pt.ipleiria.authority.Sender;
import pt.ipleiria.authority.model.Connection;
import pt.ipleiria.authority.model.Contact;
import pt.ipleiria.authority.view.ChannelChatView;
import pt.ipleiria.authority.view.ConnectionsPanel;
import pt.ipleiria.authority.view.MainView;

import javax.crypto.*;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;
import java.util.List;


public enum ConnectionsController {
    CONNECTIONS_CONTROLLER;

    private static MainView view;

    private static Connection activeConnection;
    private static List<Connection> connections;
    private static HashMap<Connection, ChannelChatView> chatPanels;

    private static SecretKey secretKey;
    private static byte[] key;

    static {
        connections = new ArrayList<>();
        chatPanels = new HashMap<>();

        for (Connection c: connections) {
            byte[] cypheredSecretKey = sendKey(c);

            //receiver needs to receiveKey() and store it somewhere
            byte[] uncypheredSecretKey = receiveKey(cypheredSecretKey);
        }

    }

    public static Connection addConnection(Contact c){
        boolean contains = false;
        Connection connection = null;

        for (Connection con : connections){
            if (c.equals(con.getContact())){
                contains = true;
                connection = con;
                break;
            }
        }

        ConnectionsPanel.CustomJTree trConnections = view.connectionsPanel.getTrConnections();
        if (!contains) {
            connection = new Connection(c);
            connections.add(connection);

            //Adds to JTree new Connection
            trConnections.addJTreeElement(trConnections.getConnectionsNode(), connection, true);

            //Create respective view
            ChannelChatView chat = new ChannelChatView();
            chatPanels.put(connection, chat);
        }
        return connection;
    }

    public static byte[] encrypt(byte[] data, PublicKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, PrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(String data, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(data));
    }

    public static void sendMessage(String message, Connection connection){
        try {
            //encrypt with secret key
            encrypt(message, connection.getSecretKeyClass());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public static String receiveMessage(byte[] response, Connection connection){
        try {
            //decrypt with secret
            return decrypt(response, connection.getSecretKeyClass());

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    //send key method
    public static byte[] sendKey(Connection connection){
        try {
            Contact myContact = ContactController.getMyContact_pbk();

            //need to check connection
            if (!connection.hasSecretKey()){
                //generate key
                connection.generateKey();
            }

            //change secret key
            return encrypt(connection.getSecretKey(), myContact.getPublicKeyClass());

        } catch (CloneNotSupportedException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    //receive key method
    public static byte[] receiveKey(byte [] key){
        try {
            Contact myContact = ContactController.getMyContact();

            //change secret key
            return decrypt(key, myContact.getPrivateKeyClass());

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void sendMessage(String message){
        try {
            if(activeConnection!=null){
                Contact dest = activeConnection.getContact();
                ChannelServer.sendMessage(dest, message);

                //Updates
                ChannelChatView chatPanel= chatPanels.get(activeConnection);
                chatPanel.addMessage(ContactController.getMyContact_pbk().getName(), message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }


    public static void updateChannelChatView(Connection connection){
        activeConnection = connection;

        //Retrieves view and displays it
        ChannelChatView chat = chatPanels.get(connection);
        chat.getLblTitle().setText(connection.getContact().getName());
        view.splitPane.setRightComponent(chat);
    }

    public static void setView(MainView view) {
        ConnectionsController.view = view;
    }

    public static Iterator<Connection> getConnections() {
        return connections.iterator();
    }

    public static Connection getConnection(Contact contact){
        Connection connection = null;
        for (Iterator<Connection> it = getConnections(); it.hasNext(); ) {
            Connection c = it.next();
            if (c.getContact().equals(contact)){
                connection = c;
                break;
            }

        }
        return connection;
    }

    public static void notifyNameChange(String previousName, String newName) {
        for (Connection con:connections) {
            ChannelChatView channel = chatPanels.get(con);
            channel.addMessage("Formerly known has \""+previousName+"\" changed its name to", "\""+newName+"\"");
        }
    }
}
