package pt.ipleiria.authority.view;

import pt.ipleiria.authority.controller.ConnectionsController;
import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Connection;
import pt.ipleiria.authority.model.Contact;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
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
            addMouseListener(mouseInputAdapter);
            addMouseMotionListener(mouseInputAdapter);


            setSelectionModel(sModel);
        }

        public void setSelectedByValue(Object object){
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) model.getRoot();
            Enumeration e = parent.depthFirstEnumeration();
            while(e.hasMoreElements()){
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                if (object.equals(node.getUserObject())) {
                  trConnections.setSelectionPath(new TreePath(node.getPath()));
                }
            }
        }

        protected class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
            private JLabel label;

            CustomTreeCellRenderer() {
                label = new JLabel();
                label.setOpaque(true);
                label.setPreferredSize(new Dimension(150, 32));
            }

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                    boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                Object o =  node.getUserObject();

                //Sets default properties
                label.setBackground(null);
                label.setIcon(null);
                label.setText("" + value);

                //Analyse node and apply changes accordingly
                if(o != null ){
                    if (o instanceof String){
                        if (o.equals("Channels")) label.setIcon(new ImageIcon("images/channels32x32.png"));
                        else if (o.equals("Contacts")) label.setIcon(new ImageIcon("images/contacts32x32.png"));
                    }else {
                        if (selected) label.setBackground(UIManager.getColor("Button.shadow"));
                        if (o instanceof Connection) {
                            Contact contact = ((Connection) o).getContact();
                            label.setIcon(new ImageIcon("images/contacts/face_" + (contact.getId() % 30 + 1) + ".png"));
                            label.setText(contact.getName());
                        } else if (o instanceof Contact) {
                            Contact contact = (Contact) o;
                            label.setText(contact.getName() +"#"+contact.getId());
                            label.setIcon(new ImageIcon("images/contacts/face_" + (contact.getId() % 30 + 1) + ".png"));
                        }
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

        public void updateJTreeElement(DefaultMutableTreeNode parentNode, Object oldObj, Object newObj) {
            for (int i = 0; i < parentNode.getChildCount(); i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getChild(parentNode, i);
                if (oldObj != null && oldObj.getClass() == node.getUserObject().getClass()){
                    Object nodeObject = node.getUserObject();
                    if (oldObj.equals(nodeObject)) node.setUserObject(newObj);
                }

            }

            ((DefaultTreeModel)trConnections.getModel()).reload(parentNode);
        }

        final MouseInputAdapter mouseInputAdapter = new MouseInputAdapter() {
            int lastSelected=-1;

            @Override
            public void mousePressed(MouseEvent e) {
                JTree tree=(JTree) e.getSource();
                TreePath treePath = tree.getSelectionPath();
                if (treePath !=null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();

                    if (selectedNode!=null && selectedNode.isLeaf()) {
                        Object o = selectedNode.getUserObject();
                        if (o instanceof Connection) {
                            ConnectionsController.updateChannelChatView((Connection) o);
                        } else if (o instanceof Contact) {
                            ConnectionsController.addConnection((Contact) o);
                        }
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e){
                //Clear selection
                JTree tree=(JTree) e.getSource();
                lastSelected=-1;
                tree.clearSelection();
            }

            @Override
            public void mouseMoved(MouseEvent e){
                JTree tree=(JTree) e.getSource();
                int selRow=tree.getRowForLocation(e.getX(), e.getY());

                //Selects new node
                if(selRow!=lastSelected){
                    tree.setSelectionRow(selRow);
                    lastSelected=selRow;
                }

            }
        };

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
