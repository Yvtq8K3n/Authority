package pt.ipleiria.authority.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.Base64;

public class Contact implements Serializable{
    public int id;
    public String name;
    public String ipAddress;
    public String MAC;
    public byte[] publicKey;

    public Contact(String name, String ipAddress, String MAC) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.MAC = MAC;
    }

    public Contact(String name, String ipAddress, String MAC, byte[] publicKey) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.MAC = MAC;
        this.publicKey = publicKey;
    }

    public void writePubKeyToFile(String path, byte[] key, String name, String ipAddress, String MAC) throws IOException {
        File f = new File(path);

        if(!f.exists()) {
            System.out.println("!EXISTS");
            f.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(f, true);
        fos.write("1".getBytes());
        fos.write("\t".getBytes());

        fos.write(ipAddress.getBytes());
        fos.write("\t".getBytes());

        fos.write(name.getBytes());
        fos.write("\t".getBytes());

        fos.write(MAC.getBytes());
        fos.write("\t".getBytes());

        fos.write(Base64.getEncoder().encode(key));
        fos.write("\n".getBytes());

        fos.flush();
        fos.close();
    }

    //TODO: Criar ficheiro tipo hash  ---   criar o ficheiro tipo hosts (informação do contacto)  --eventually[optional] revoke key

    public void writeHashFile(String path, Contact contact) throws IOException{
        File f = new File(path);
        if(!f.exists()) {
            System.out.println("!EXISTS");
            f.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(f, true);
        fos.write(contact.id);
        fos.write("\t".getBytes());

        fos.write(contact.ipAddress.getBytes());
        fos.write("\t".getBytes());

        fos.flush();
        fos.close();
    }

    //TODO: Add write private key

    /*public void writePrivateKeyFile(String path, Contact contact) throws IOException{
        File f = new File(path);
        if(!f.exists()) {
            System.out.println("!EXISTS");
            f.getParentFile().mkdirs();
        }

        FileOutputStream fos = new FileOutputStream(f, true);
        fos.write(contact.privateKey);
        fos.write("\t".getBytes());

        fos.flush();
        fos.close();
    }*/

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
}
