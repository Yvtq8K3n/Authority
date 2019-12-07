package pt.ipleiria.authority.view;


import pt.ipleiria.authority.UDPBroadcast;
import pt.ipleiria.authority.UDPUnicastServerReceive;

import javax.swing.*;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainView extends JFrame {

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

    public static void main(String[] args) throws UnknownHostException {
        new MainView();
        UDPUnicastServerReceive server=  new UDPUnicastServerReceive();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(server);

        UDPBroadcast.broadcast();
    }
}
