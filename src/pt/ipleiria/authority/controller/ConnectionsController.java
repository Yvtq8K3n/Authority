package pt.ipleiria.authority.controller;

import pt.ipleiria.authority.model.Connection;
import pt.ipleiria.authority.model.Contact;
import pt.ipleiria.authority.view.ConnectionsPanel;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.tree.TreePath;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public enum ConnectionsController {
    CONNECTIONS_CONTROLLER;

    private static List<Connection> connections;
    private static ConnectionsPanel connectionsPanel;

    static {
        connections = new ArrayList<>();
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

        ConnectionsPanel.CustomJTree trConnections = connectionsPanel.getTrConnections();
        if (!contains) {
            connection = new Connection(c);
            connections.add(connection);

            //Adds to JTree new Connection
            trConnections.addJTreeElement(trConnections.getConnectionsNode(), connection, true);
        }else {
            //Force selection, heavy operation
            connectionsPanel.getTrConnections().setSelectedByValue(connection);
        }


    }

    public byte[] encrypt(byte[] data, PublicKey key) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] data, PrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static Iterator<Connection> getConnections() {
        return connections.iterator();
    }

    public static void setConnectionsPanel(ConnectionsPanel panel){
        connectionsPanel = panel;
    }
}
