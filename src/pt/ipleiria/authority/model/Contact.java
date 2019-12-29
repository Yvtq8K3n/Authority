package pt.ipleiria.authority.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;

public class Contact implements Serializable, Cloneable{

    public int id;
    public String name;
    public String ipAddress;
    public String MAC;
    public byte[] publicKey;
    public byte[] privateKey;

    public Contact(){
        try {
            KeyPairGen keys = new KeyPairGen("RSA", 1024);
            InetAddress ip = InetAddress.getLocalHost();

            this.id = 1;

            this.privateKey = keys.getPrivateKeyB64();
            this.publicKey = keys.getPublicKeyB64();

            this.ipAddress = ip.getHostAddress();
            this.name = ip.getCanonicalHostName();
            this.MAC = " ";

            NetworkInterface ni =  NetworkInterface.getByInetAddress(ip);
            if (ni != null) {
                StringBuilder macAddress = new StringBuilder();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {

                    /*
                     * Extract each array of mac address and convert it
                     * to hexadecimal with the following format
                     * 08-00-27-DC-4A-9E.
                     */
                    for (int i = 0; i < mac.length; i++) {
                        macAddress.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    this.MAC = macAddress.toString();
                } else {
                    System.out.println("Address doesn't exist or is not " +
                            "accessible.");
                }
            } else {
                System.out.println("Network Interface for the specified " +
                        "address is not found.");
            }

        } catch (NoSuchAlgorithmException | UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
    }

    public Contact(int id, String ipAddress, String name, String MAC, byte[] publicKey) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.MAC = MAC;
        this.publicKey = publicKey;
    }

    public Contact(int id, String ipAddress, String name, String MAC, byte[] publicKey, byte[] privateKey) {
        this(id, ipAddress, name, MAC, publicKey);
        this.privateKey = privateKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }



    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
