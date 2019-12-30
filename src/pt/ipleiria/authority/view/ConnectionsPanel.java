package pt.ipleiria.authority.view;

import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Contact;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Iterator;

public class ConnectionsPanel extends JPanel{

    private JLabel lblTitle;
    private JTree trConnections;

    public ConnectionsPanel(){
        initComponents();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(125, 50));
    }

    private void initComponents() {
        //Adds Tittle
        lblTitle = new JLabel("Connections:");
        lblTitle.setPreferredSize(new Dimension(75, 25));
        add(lblTitle);

        //Adds jTree
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        DefaultTreeModel model = new DefaultTreeModel(root);

        //Adds ConnectionsTab
        DefaultMutableTreeNode connectionsNode = new DefaultMutableTreeNode("Connections");
        root.add(connectionsNode);

        //Adds ContactsTab
        DefaultMutableTreeNode contactsNode = new DefaultMutableTreeNode("Contacts");
        root.add(contactsNode);
        Iterator<Contact> contacts = ContactController.getContacts();
        while(contacts.hasNext()){
            contactsNode.add(new DefaultMutableTreeNode(contacts.next()));
        }

        trConnections = new JTree(root);
        trConnections.setModel(model);
        DefaultTreeSelectionModel sModel = new DefaultTreeSelectionModel();
        sModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        trConnections.setSelectionModel(sModel);
        trConnections.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent selection) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getPath().getLastPathComponent();
                if(selectedNode.isLeaf()) {
                    selectedNode.getUserObject();
                }
            }
        });
        trConnections.setRootVisible(false);
        trConnections.setBorder(BorderFactory.createEtchedBorder(Color.BLACK,Color.BLACK));
        JScrollPane scroll = new JScrollPane(trConnections);
        add(scroll);
    }
}
