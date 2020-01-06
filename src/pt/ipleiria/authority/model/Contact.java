package pt.ipleiria.authority.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.*;
import java.nio.channels.SocketChannel;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Contact implements Serializable, Cloneable {
    private static final int MY_CONTACT_ID = 1;
    private static final AtomicInteger count = new AtomicInteger(1);

    private int id;
    private String name;
    private String ipAddress;
    private String MAC;
    private byte[] publicKey;
    private byte[] privateKey;

    public Contact() {
        try {
            this.id = MY_CONTACT_ID;

            InetAddress ip = getActiveInetAddress();
            this.name = ip.getLocalHost().getCanonicalHostName();
            this.ipAddress = ip.getHostAddress();
            this.MAC = getMac(ip);

            //Generate Asymmetric Keys
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair pair = keyGen.generateKeyPair();

            this.privateKey = Base64.getEncoder().encode(pair.getPrivate().getEncoded());
            this.publicKey = Base64.getEncoder().encode(pair.getPublic().getEncoded());

        } catch (NoSuchAlgorithmException | UnknownHostException | SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Contact(int id, String ipAddress, String name, String MAC, byte[] publicKey) {
        if (id>count.get()) count.set(id);
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.MAC = MAC;
        this.publicKey = publicKey;
    }

    public boolean isMyContact(){
        return Contact.MY_CONTACT_ID == id;
    }

    public void updateId(){
        this.id = count.incrementAndGet();
    }

    public int getId() {
        return id;
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

    public PrivateKey getPrivateKeyClass() {
        try{
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    public PublicKey getPublicKeyClass() {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(publicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    private InetAddress getActiveInetAddress() throws IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface networkInterface : Collections.list(interfaces)) {
            if (networkInterface.isLoopback())
                continue;
            if (!networkInterface.isUp())
                continue;
            if (networkInterface.getDisplayName().contains("Virtual"))
                continue;

            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            for (InetAddress address : Collections.list(addresses)) {
                // look only for ipv4 addresses
                if (address instanceof Inet6Address) continue;

                // use a timeout big enough for your needs
                if (!address.isReachable(3000)) continue;


                return address;
            }
        }
        return null;
    }

    private String getMac(InetAddress address) throws SocketException {
        NetworkInterface ni = NetworkInterface.getByInetAddress(address);
        if(ni !=null) {
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
                return macAddress.toString();
            } else {
                System.out.println("Address doesn't exist or is not " +
                        "accessible.");
            }
        } else {
            System.out.println("Network Interface for the specified " +
                    "address is not found.");
        }
        return null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s", id, ipAddress, name, MAC, new String(publicKey));
    }


    /** Compares if
     * @param c Contact matches MAC and publicKey
     * @return true
     */
    public boolean compareTo(Contact c) {
        return MAC == c.getMAC() && publicKey == c.getPublicKey();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Contact contact = (Contact) object;
        return id == contact.id;
    }
}

