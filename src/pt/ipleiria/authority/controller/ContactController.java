package pt.ipleiria.authority.controller;

import pt.ipleiria.authority.model.Contact;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;

public enum ContactController {
    CONTACT_CONTROLLER;

    private static final String PATH = "D:\\ola";//"/Users/joaoz/Downloads/";
    private static final int MY_CONTACT_ID = 1;

    private static Contact myContact;
    private static ArrayList<Contact> contacts;

    static {
        contacts = new ArrayList<>();

        try {
            readContacts();
            //TODO: ler hash file
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(myContact == null){
            myContact = new Contact();

            writeToFileContact(myContact);

            //TODO: escrever para hash file
        }
    }

    private static void writeToFileContact(Contact contact) {
        File f = new File(PATH);

        //Create directory when does not exists
        if (!f.exists()) {
            f.getParentFile().mkdirs();
        }

        try {
            if (contact.getPrivateKey() != null) {
                writePrivateKeyFile(f, contact);
            }

            writePublicKeyToFile(f, contact);
            writeHashFile(f, contact);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writePublicKeyToFile(File file, Contact contact) throws IOException {
        File f = new File(file,"RSAPublic.txt");

        if(!f.exists()){
            FileOutputStream fos = new FileOutputStream(f, true);
            fos.write(String.valueOf(contact.getId()).getBytes());
            fos.write("\t".getBytes());

            fos.write(contact.getIpAddress().getBytes());
            fos.write("\t".getBytes());

            fos.write(contact.getName().getBytes());
            fos.write("\t".getBytes());

            fos.write(contact.getMAC().getBytes());
            fos.write("\t".getBytes());

            fos.write(contact.getPublicKey());
            fos.write("\n".getBytes());

            fos.flush();
            fos.close();
        } else {
            while(true) {
                boolean fileIsUnLocked = f.renameTo(f);
                if (fileIsUnLocked) {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    fos.write(String.valueOf(contact.getId()).getBytes());
                    fos.write("\t".getBytes());

                    fos.write(contact.getIpAddress().getBytes());
                    fos.write("\t".getBytes());

                    fos.write(contact.getName().getBytes());
                    fos.write("\t".getBytes());

                    fos.write(contact.getMAC().getBytes());
                    fos.write("\t".getBytes());

                    fos.write(contact.getPublicKey());
                    fos.write("\n".getBytes());

                    fos.flush();
                    fos.close();
                    break;
                }
            }
        }
    }

    //TODO: eventually[optional] revoke key
    //write hash map file
    public static void writeHashFile(File directory, Contact contact) throws IOException{
        File f = new File(directory,"Hash.txt");

        if(!f.exists()){
            FileOutputStream fos = new FileOutputStream(f, true);
            fos.write(contact.getIpAddress().getBytes());
            fos.write("\t".getBytes());

            fos.write(String.valueOf(contact.getId()).getBytes());
            fos.write("\n".getBytes());

            fos.flush();
            fos.close();
        } else{
            while(true) {
                boolean fileIsUnLocked = f.renameTo(f);
                if (fileIsUnLocked) {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    fos.write(contact.getIpAddress().getBytes());
                    fos.write("\t".getBytes());

                    fos.write(String.valueOf(contact.getId()).getBytes());
                    fos.write("\n".getBytes());

                    fos.flush();
                    fos.close();
                    break;
                }
            }
        }
    }

    //write private key
    public static void writePrivateKeyFile(File directory, Contact contact) throws IOException{
        File f = new File(directory,"RSAPrivate.txt");

        if(!f.exists()){
            FileOutputStream fos = new FileOutputStream(f, true);
            fos.write(contact.getPrivateKey());
            fos.flush();
            fos.close();
        } else {
            while(true) {
                boolean fileIsUnLocked = f.renameTo(f);
                if (fileIsUnLocked) {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    fos.write(contact.getPrivateKey());
                    fos.flush();
                    fos.close();
                    break;
                }
            }
        }
    }

    private static void readContacts() throws IOException {
        File f = new File(PATH,"RSAPublic.txt");

        if (!f.exists()){
            return;
        }

        FileInputStream fis = new FileInputStream(f);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

        String line = reader.readLine();
        while(line != null){

            //System.out.println("ESTOU A LER O ARRAY DE CONTACTOS");

            String[] array = line.split("\t");

            int id = Integer.parseInt(array[0]);
            String ipAddress = array[1];
            String name = array[2];
            String MAC = array[3];
            byte[] publicKey = array[4].getBytes();

            if(id == MY_CONTACT_ID){
                byte[] privateKey = readContactPrivate();
                myContact = new Contact(id, ipAddress, name, MAC, publicKey, privateKey);
            } else {
                contacts.add(new Contact(id, ipAddress, name, MAC, publicKey));
            }

            line = reader.readLine();
        }

        reader.close();
        fis.close();
    }

    public static byte[] readContactPrivate() throws IOException {
        File f = new File(PATH, "RSAPrivate.txt");

        if (!f.exists()){
            return null;
        }

        FileInputStream fis = new FileInputStream(f);
        byte[] key = fis.readAllBytes();
        fis.close();

        return key;
    }

    public static Contact getMyContact() {
        return myContact;
    }

    public static Contact getMyContact_pbk() throws CloneNotSupportedException {
        Contact c = (Contact) myContact.clone();
        c.setPrivateKey(null);
        return c;
    }

    public static void addContact(Contact c){
        contacts.add(c);
        ContactController.writeToFileContact(c);
    }

    public static ContactController getContactController() {
        return CONTACT_CONTROLLER;
    }

    public static Iterator<Contact> getContacts() {
        return contacts.iterator();
    }
}
