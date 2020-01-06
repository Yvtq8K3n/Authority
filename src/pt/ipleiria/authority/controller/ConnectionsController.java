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
            keyExchange(c);
        }

    }

    public static void addConnection(Contact c){
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
        }else {
            //Force selection, heavy operation
            view.connectionsPanel.getTrConnections().setSelectedByValue(connection);
        }

        updateChannelChatView(connection);
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
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, SecretKey secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data));
    }

    public static void sendMessage(String message, Connection connection){
        try {
            Contact c = ContactController.getMyContact();
            //encrypt with contact pub key, then secret key
            encrypt(encrypt(message, connection.getSecretKeyClass()), c.getPublicKeyClass());

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public static String receiveMessage(byte[] response, Connection connection){
        try {
            Contact c = ContactController.getMyContact();
            //decrypt with secret then private
            return decrypt(decrypt(response,c.getPrivateKeyClass()), connection.getSecretKeyClass());

        } catch (InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    //exchange key method
    public static void keyExchange(Connection connection){
        try {
            Contact myContact = ContactController.getMyContact_pbk();

            //need to check connection
            if (!connection.hasSecretKey()){
                //generate key
                connection.generateKey();
            }

            //change secret key
            encrypt(connection.getSecretKey(), myContact.getPublicKeyClass());

        } catch (CloneNotSupportedException | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
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

    public static void notifyNameChange(String previousName, String newName) {
        for (Connection con:connections) {
            ChannelChatView channel = chatPanels.get(con);
            channel.addMessage("Formerly known has \""+previousName+"\" changed its name to", "\""+newName+"\"");
        }
    }
}
