package pt.ipleiria.authority.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
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
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair pair = keyGen.generateKeyPair();
            InetAddress ip = InetAddress.getLocalHost();

            this.id = 1;
            this.privateKey = Base64.getEncoder().encode(pair.getPrivate().getEncoded());
            this.publicKey = Base64.getEncoder().encode(pair.getPublic().getEncoded());
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

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKeyClass() throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public PublicKey getPublicKeyClass() throws InvalidKeySpecException, NoSuchAlgorithmException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(publicKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
