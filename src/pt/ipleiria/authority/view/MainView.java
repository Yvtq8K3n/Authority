package pt.ipleiria.authority.view;


import pt.ipleiria.authority.UDPBroadcast;
import pt.ipleiria.authority.UDPUnicastServerReceive;

import javax.swing.*;
import java.awt.*;
import java.net.UnknownHostException;
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

    public static void main(String[] args) throws UnknownHostException {
        new MainView();
        UDPUnicastServerReceive server=  new UDPUnicastServerReceive();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(server);

        UDPBroadcast.broadcast();
    }
}
