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
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
            activeConnection = connection;
        }
        return connection;
    }

    public static byte[] encrypt(byte[] data, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e){
            e.getStackTrace();
            System.out.println("ERRO: " + e.getMessage());
        }
        return null;
    }

    public static byte[] encrypt(byte[] data, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e){
            e.getStackTrace();
        }
            return null;
        }

    public static byte[] decrypt(byte[] data, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e){
            e.getStackTrace();
            }
        return null;
    }

    public static byte[] decrypt(byte[] data, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e){
            Sender.logger.info(e.getMessage());
        }
        return null;
    }

    public static byte[] encrypt(String data, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(byte[] data, SecretKey secretKey) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
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
        } catch (IOException | CloneNotSupportedException e) {
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

    public static ChannelChatView getChatPanel(Connection connection) {
        return chatPanels.get(connection);
    }
}
