package pt.ipleiria.authority.controller;

import pt.ipleiria.authority.model.Contact;

import java.io.*;
import java.util.ArrayList;

public enum ContactController {
    CONTACT_CONTROLLER;

    private static final String PATH = "/Users/joaoz/Downloads/";
    private static final int MY_CONTACT_ID = 0;

    private static Contact myContact;
    private static ArrayList<Contact> contacts;

    /*my contact details*/

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
        if(!f.exists()){
            f.getParentFile().mkdirs();
        }

        try {
            if(contact.getPrivateKey() != null){
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

        FileOutputStream fos = new FileOutputStream(f, true);
        fos.write(contact.id);
        fos.write("\t".getBytes());

        fos.write(contact.ipAddress.getBytes());
        fos.write("\t".getBytes());

        fos.write(contact.name.getBytes());
        fos.write("\t".getBytes());

        fos.write(contact.MAC.getBytes());
        fos.write("\t".getBytes());

        fos.write(contact.publicKey);
        fos.write("\n".getBytes());

        fos.flush();
        fos.close();
    }

    //TODO: eventually[optional] revoke key
    //write hash map file
    public static void writeHashFile(File directory, Contact contact) throws IOException{
        File f = new File(directory,"Hash.txt");

        FileOutputStream fos = new FileOutputStream(f, true);
        fos.write(contact.ipAddress.getBytes());
        fos.write("\t".getBytes());

        fos.write(contact.id);
        fos.write("\n".getBytes());

        fos.flush();
        fos.close();
    }

    //write private key
    public static void writePrivateKeyFile(File directory, Contact contact) throws IOException{
        File f = new File(directory,"RSAPrivate.txt");

        FileOutputStream fos = new FileOutputStream(f, true);
        fos.write(contact.privateKey);
        fos.write("\n".getBytes());

        fos.flush();
        fos.close();
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
            System.out.println(line);
            line = reader.readLine();

            System.out.println("ESTOU A LER O ARRAY DE CONTACTOS");

            String[] array = line.split("\t");

            int id = Integer.parseInt(array[0]);
            String name = array[1];
            String ipAddress = array[2];
            String MAC = array[3];
            byte[] publicKey = array[4].getBytes();

            if(id == MY_CONTACT_ID){
                byte[] privateKey = readContactPrivate();
                myContact = new Contact(id, ipAddress, name, MAC, privateKey);
            } else {
                contacts.add(new Contact(id, name, ipAddress, MAC, publicKey));
            }
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

    public static ArrayList<Contact> getContacts() {
        return contacts;
    }

    public static Contact getMyContact() {
        return myContact;
    }

    public static Contact getMyContact_pbk() throws CloneNotSupportedException {
        Contact c = (Contact) myContact.clone();
        c.setPrivateKey(null);
        return c;
    }

    public static void setMyContact(Contact myContact) {
        ContactController.myContact = myContact;
    }

    public static void setMyContact() {
        myContact = new Contact();
    }

    public static void setContacts(ArrayList<Contact> contacts) {
        ContactController.contacts = contacts;
    }

    public static void addContact(Contact c){
        contacts.add(c);
        ContactController.writeToFileContact(c);
    }

    public static int getContactController() {
        return MY_CONTACT_ID;
    }
}
