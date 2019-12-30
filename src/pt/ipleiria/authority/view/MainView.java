package pt.ipleiria.authority.view;


import pt.ipleiria.authority.Sender;
import pt.ipleiria.authority.Receiver;
import pt.ipleiria.authority.controller.ContactController;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.awt.*;
import java.net.UnknownHostException;
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
        setSize(600,200);
        setVisible(true);
        pack();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        toolBar = new ToolBar();
        connectionsPanel = new ConnectionsPanel();
        chatPanel = new ChatPanel();

        //Adds panels into SplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, connectionsPanel, chatPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(150);

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

        ContactController.getContactController();

        System.out.println(ContactController.getMyContact().getPrivateKeyClass().toString());

        Sender.broadcast();
    }


}
