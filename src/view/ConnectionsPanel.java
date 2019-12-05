package view;

import javax.swing.*;
import java.awt.*;

public class ConnectionsPanel extends JPanel{

    private JLabel lblTitle;
    private JTree trConnections;

    public ConnectionsPanel(){
        initComponents();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(125, 50));
    }

    private void initComponents() {
        lblTitle = new JLabel("Connections:");
        lblTitle.setPreferredSize(new Dimension(75, 25));
        add(lblTitle);

        //Adds connections Tree
        trConnections = new JTree();
        trConnections.setBorder(BorderFactory.createEtchedBorder(Color.BLACK,Color.BLACK));
        JScrollPane scroll = new JScrollPane(trConnections);
        add(scroll);
    }
}
