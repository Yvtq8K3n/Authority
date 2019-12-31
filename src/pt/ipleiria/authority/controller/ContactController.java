package pt.ipleiria.authority.controller;

import pt.ipleiria.authority.model.Contact;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public enum ContactController {
    CONTACT_CONTROLLER;

    private static final String PATH = "C:\\ola";//"/Users/joaoz/Downloads/";

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

            Contact contact = new Contact(id, ipAddress, name, MAC, publicKey);

            if (!contact.isMyContact()){
                contacts.add(contact);
            }else {
                byte[] privateKey = readContactPrivate();
                contact.setPrivateKey(privateKey);
                myContact = contact;
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

            writeContactsToFile(f, contact);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeContactsToFile(File file, Contact contact) throws IOException {
        File f = new File(file,"RSAPublic.txt");

        if(!f.exists()){
            FileOutputStream fos = new FileOutputStream(f, true);
            fos.write(contact.toString().getBytes());

            fos.flush();
            fos.close();
        } else {
            while(true) {
                boolean fileIsUnLocked = f.renameTo(f);
                if (fileIsUnLocked) {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    fos.write(contact.toString().getBytes());

                    fos.flush();
                    fos.close();
                    break;
                }
            }
        }
    }
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

    public static void updateContactsFile(String path, Contact contact) throws IOException {
        try {
            // input the (modified) file content to the StringBuffer "input"
            BufferedReader file = new BufferedReader(new FileReader(path+"/RSAPublic.txt"));
            StringBuffer inputBuffer = new StringBuffer();
            String line;

            while ((line = file.readLine()) != null) {
                String[] array = line.split("\t");
                int id = Integer.parseInt(array[0]);
                if(id == contact.getId()){
                    line +=  contact.toString();
                    //Needs to finish
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream("notes.txt");
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    public static void addContact(Contact c) throws IOException {
        boolean contains = false;
        for (Contact cs : contacts){
            if (c.compareTo(cs)){ //MAC PUBLIC
                cs.setName(c.getName());
                cs.setIpAddress(c.getIpAddress());
                updateContactsFile(PATH, cs);
                contains = true;
            }
        }

        if (!contains) {
            c.updateId();
            contacts.add(c);
            ContactController.writeToFileContact(c);
        }

    }

    public static Contact getMyContact() {
        return myContact;
    }

    public static Contact getMyContact_pbk() throws CloneNotSupportedException {
        Contact c = (Contact) myContact.clone();
        c.setPrivateKey(null);
        return c;
    }

    public static Iterator<Contact> getContacts() {
        return contacts.iterator();
    }
}
