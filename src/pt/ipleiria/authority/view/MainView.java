package pt.ipleiria.authority.view;


import pt.ipleiria.authority.Sender;
import pt.ipleiria.authority.Receiver;
import pt.ipleiria.authority.controller.ConnectionsController;
import pt.ipleiria.authority.controller.ContactController;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainView extends JFrame {

    private ToolBar toolBar;
    public ConnectionsPanel connectionsPanel;
    public JSplitPane splitPane;
    public JPanel chatPanel;

    public MainView(){
        initComponents();
        ContactController.setView(this);
        ConnectionsController.setView(this);
        setTitle("Authority");
        setSize(720,225);
        setVisible(true);
        pack();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        toolBar = new ToolBar();

        connectionsPanel = new ConnectionsPanel();

        //Temporary ChannelChatView
        chatPanel = new JPanel();
        chatPanel.setPreferredSize(new Dimension(500, 400));

        //Adds panels into SplitPane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, connectionsPanel, chatPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(toolBar, BorderLayout.PAGE_START);
        panel.add(splitPane, BorderLayout.CENTER);

        add(panel);
    }


    public JPanel getChatPanel() {
        return chatPanel;
    }

    public void setChatPanel(JPanel chatPanel) {
        this.chatPanel = chatPanel;
    }


    public static void main(String[] args) {
        new MainView();

        Sender sender = new Sender();
        Receiver receiver = new Receiver();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.submit(sender);
        executorService.submit(receiver);

        Sender.broadcast();
    }


}
