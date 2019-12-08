package pt.ipleiria.authority.model;

import java.io.Serializable;

public class Contact implements Serializable{
    public String name;
    public String ipAddress;
    public String MAC;
    public String publicKey;

    public Contact(String name, String ipAddress, String MAC, String publicKey) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.MAC = MAC;
        this.publicKey = publicKey;
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

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
