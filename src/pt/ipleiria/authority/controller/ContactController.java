package pt.ipleiria.authority.controller;

import pt.ipleiria.authority.model.Contact;
import pt.ipleiria.authority.view.ChannelChatView;
import pt.ipleiria.authority.view.ConnectionsPanel;
import pt.ipleiria.authority.view.MainView;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.io.*;
import java.util.*;
import java.util.List;

public enum ContactController {
    CONTACT_CONTROLLER;

    private static final String PATH = "C:\\ola";//"/Users/joaoz/Downloads/";

    private static MainView view;

    private static Contact myContact;
    private static ArrayList<Contact> contacts;

    //Helps identify the actives contacts
    private static List<String> activeIpAddress;
    private static Map<String, Contact> hashmap;

    //View
    private static ConnectionsPanel connectionsPanel;

    static {
        contacts = new ArrayList<>();
        hashmap = new HashMap<>();
        activeIpAddress = new ArrayList<>();

        try {
            //Reads all known contacts into memory
            readContacts();
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
            fos.write("\n".getBytes());

            System.out.println("hey");

            fos.flush();
            fos.close();
        } else {
            while(true) {
                boolean fileIsUnLocked = f.renameTo(f);
                if (fileIsUnLocked) {
                    FileOutputStream fos = new FileOutputStream(f, true);
                    fos.write(contact.toString().getBytes());
                    fos.write("\n".getBytes());

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
                    line = contact.toString();
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            file.close();

            // write the new string with the replaced line OVER the same file
            FileOutputStream fileOut = new FileOutputStream(path+"/RSAPublic.txt");
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();

        } catch (Exception e) {
            System.out.println("Problem reading file.");
        }
    }

    public static void addContact(Contact newContact) throws IOException {
        boolean contains = false;
        for (Contact c : contacts){
            if (newContact.compareTo(c)){ //MAC PUBLIC
                c.setName(newContact.getName());
                c.setIpAddress(newContact.getIpAddress());
                updateContactsFile(PATH, c);
                contains = true;
            }
        }

        if (!contains) {
            newContact.updateId();
            contacts.add(newContact);
            ContactController.writeToFileContact(newContact);
        }

        //Adds newContact to hash map
        ConnectionsPanel.CustomJTree trConnections = view.connectionsPanel.getTrConnections();

        String ipAddress = newContact.getIpAddress();
        if(!activeIpAddress.contains(ipAddress)){
            activeIpAddress.add(ipAddress);
            //Adds new Member

            trConnections.addJTreeElement(trConnections.getContactsNode(), newContact, false);
            hashmap.put(ipAddress, newContact);
        }else{
            //Retrieve newContact on hash map
            Contact oldContact = hashmap.get(ipAddress);

            //Updates JTreeElement
            trConnections.updateJTreeElement(trConnections.getContactsNode(), oldContact, newContact);

            //Rewrite newContact to hash map
            hashmap.put(ipAddress, newContact);
        }
    }

    public static Iterator<Contact> getActiveContacts() {
        return new Iterator<>() {
            private Iterator<String> a = activeIpAddress.iterator();

            public boolean hasNext() {
                return a.hasNext();
            }
            public Contact next() {
                if (!hasNext()) throw new NoSuchElementException();

                return hashmap.get(a.next());
            }
        };
    }

    public static Contact getContact(String ipAddress){
        Contact contact = null;
        for (Iterator<Contact> it = getActiveContacts(); it.hasNext(); ) {
            Contact c = it.next();
            if (c.getIpAddress().equals(ipAddress)){
                contact = c;
                break;
            }

        }
        return contact;
    }

    public static void setConnectionsPanel(ConnectionsPanel panel){
        connectionsPanel = panel;
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

    public static void updateMyContactName(String name) throws IOException {
        String previous = myContact.getName();
        myContact.setName(name);
        updateContactsFile(PATH, myContact);
        ConnectionsController.notifyNameChange(previous, name);
    }

    public static void setView(MainView view) {
        ContactController.view = view;
    }

}
