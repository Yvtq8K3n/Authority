package pt.ipleiria.authority.view;


import pt.ipleiria.authority.Sender;
import pt.ipleiria.authority.Receiver;
import pt.ipleiria.authority.controller.ConnectionsController;
import pt.ipleiria.authority.controller.ContactController;

import javax.swing.*;
import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainView extends JFrame {

    private ToolBar toolBar;
    private ConnectionsPanel connectionsPanel;
    private ChatPanel chatPanel;

    public MainView(){
        initComponents();
        setTitle("Authority");
        setSize(720,225);
        setVisible(true);
        pack();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        toolBar = new ToolBar();

        connectionsPanel = new ConnectionsPanel();
        ContactController.setConnectionsPanel(connectionsPanel);
        ConnectionsController.setConnectionsPanel(connectionsPanel);

        chatPanel = new ChatPanel();

        //Adds panels into SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, connectionsPanel, chatPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(toolBar, BorderLayout.PAGE_START);
        panel.add(splitPane, BorderLayout.CENTER);

        add(panel);
    }

    public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
        new MainView();

        Sender sender = new Sender();
        Receiver receiver = new Receiver();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.submit(sender);
        executorService.submit(receiver);

        Sender.broadcast();
    }


}
