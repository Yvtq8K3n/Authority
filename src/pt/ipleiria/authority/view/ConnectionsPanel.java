package pt.ipleiria.authority.view;

import javafx.scene.control.SelectionModel;
import pt.ipleiria.authority.controller.ConnectionsController;
import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Connection;
import pt.ipleiria.authority.model.Contact;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.util.Iterator;

public class ConnectionsPanel extends JPanel{

    private JLabel lblTitle;
    public CustomJTree trConnections;

    public ConnectionsPanel(){
        initComponents();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void initComponents() {
        //Adds Tittle
        lblTitle = new JLabel("Active:");
        lblTitle.setPreferredSize(new Dimension(75, 25));
        add(lblTitle);

        //Adds jTree inside ScrollPane
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        trConnections = new CustomJTree(root);
        JScrollPane scroll = new JScrollPane(trConnections);
        add(scroll);
    }

    public class CustomJTree extends JTree{
        private DefaultTreeModel model;

        private DefaultMutableTreeNode connectionsNode;
        private DefaultMutableTreeNode contactsNode;


        public CustomJTree(DefaultMutableTreeNode root) {
            super(root);
            initComponents(root);
            populateJTree();
            addEvents();

            //Set Properties
            setRootVisible(false);
            setBorder(BorderFactory.createEtchedBorder(Color.BLACK,Color.BLACK));

            CustomTreeCellRenderer cellRenderer = new CustomTreeCellRenderer();
            setCellRenderer(cellRenderer);
        }

        private void initComponents(DefaultMutableTreeNode root){
            //Adds Model
            model = new DefaultTreeModel(root);
            setModel(model);

            //Adds ConnectionsTab
            connectionsNode = new DefaultMutableTreeNode("Channels", true);
            root.add(connectionsNode);

            //Adds ContactsTab
            contactsNode = new DefaultMutableTreeNode("Contacts");
            root.add(contactsNode);
        }

        private void populateJTree(){
            Iterator<Connection> connections = ConnectionsController.getConnections();
            while(connections.hasNext()){
                connectionsNode.add(new DefaultMutableTreeNode(connections.next()));
            }

            Iterator<Contact> contacts = ContactController.getActiveContacts();
            while(contacts.hasNext()){
                contactsNode.add(new DefaultMutableTreeNode(contacts.next()));
            }
        }

        private void addEvents() {
            //Adds Select Model
            DefaultTreeSelectionModel sModel = new DefaultTreeSelectionModel();
            sModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            addTreeSelectionListener(new TreeSelectionListener() {
                @Override
                public void valueChanged(TreeSelectionEvent selection) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)selection.getPath().getLastPathComponent();

                    if(selectedNode.isLeaf()) {
                        Object o = selectedNode.getUserObject();
                        if (o instanceof Connection){

                        }else if (o instanceof Contact){
                            ConnectionsController.addConnection((Contact) o);
                        }

                    }
                }
            });
            setSelectionModel(sModel);

        }

        protected class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
            private JLabel label;

            CustomTreeCellRenderer() {
                label = new JLabel();
            }

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {
                Object o =  ((DefaultMutableTreeNode) value).getUserObject();

                //Remove previous selection
                label.setBorder(null);

                if(o == null || o instanceof String){
                    label.setIcon(null);
                    label.setText("" + value);
                }else {
                    if (selected) label.setBorder(BorderFactory.createEtchedBorder(Color.BLACK,Color.BLACK));
                    if (o instanceof Connection) {
                        label.setIcon(new ImageIcon("images/shoutface32x32.png"));
                        label.setText("Connection");
                    } else if (o instanceof Contact) {
                        Contact contact = (Contact) o;
                        label.setIcon(new ImageIcon("images/contacts/face_" + (contact.getId() % 30 + 1) + ".png"));
                        label.setText(contact.getName());
                    }
                }
                return label;
            }
        }

        public void addJTreeElement(DefaultMutableTreeNode root, Object value, boolean select){
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(value);

            //Updates Connections
            model.insertNodeInto(newNode, root, root.getChildCount());

            //Expand root tree
            trConnections.expandPath(new TreePath(root.getPath()));

            //Select new node?
            if (select) trConnections.setSelectionPath(new TreePath(newNode.getPath()));
            //trConnections.updateUI();
        }

        public void updateJTreeElement(DefaultMutableTreeNode contactsNode, Object oldObj, Object newObj) {
            for (int i = 0; i < contactsNode.getChildCount(); i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getChild(contactsNode, i);
                if (oldObj != null && oldObj.getClass() == node.getUserObject().getClass()){
                    Object nodeObject = node.getUserObject();
                    if (oldObj.equals(nodeObject)) node.setUserObject(newObj);
                }

            }

            trConnections.updateUI();
        }

        public DefaultMutableTreeNode getConnectionsNode() {
            return connectionsNode;
        }

        public DefaultMutableTreeNode getContactsNode() {
            return contactsNode;
        }

    }

    public CustomJTree getTrConnections() {
        return trConnections;
    }


}
