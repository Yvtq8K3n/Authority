package view;

import javax.swing.*;
import java.awt.*;

public class MainView extends JFrame {


    private JButton button;
    private ConnectionsPanel connectionsPanel;
    private ChatPanel chatPanel;
    private JSplitPane splitPane;


    public MainView(){
        initComponents();
        setTitle("Authority");
        setSize(600,200);
        setVisible(true);
        pack();
    }

    private void initComponents() {
        connectionsPanel = new ConnectionsPanel();
        chatPanel = new ChatPanel();

        //Adds panels into SplitPane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, connectionsPanel, chatPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);

        //Adds splitPane
        add(splitPane);
    }

    public static void main(String[] args) {
        new MainView();
    }
}
