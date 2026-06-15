package pt.ipleiria.authority.view;

import pt.ipleiria.authority.Sender;
import pt.ipleiria.authority.controller.ConnectionsController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ChannelChatView extends JPanel {
    //ChatTextWidget
    private JLabel lblChannel;
    public JLabel lblTitle;
    private JTextArea txtChatArea;

    //ChatEntryWidget
    private JButton btnUpload;
    private JTextField txtEntryField;
    private JButton btnSend;

    public ChannelChatView(){
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        //Adds ChatArea
        lblChannel = new JLabel("Channel -> ");
        lblTitle = new JLabel("");

        //Chat labels
        JPanel pnlTitle = new JPanel();
        pnlTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.X_AXIS));
        pnlTitle.add(lblChannel);
        pnlTitle.add(lblTitle);

        //Chat body
        txtChatArea = new JTextArea();
        txtChatArea.setEditable(false);
        txtChatArea.setLineWrap(true);
        JScrollPane pnlScroll = new JScrollPane(txtChatArea);

        JPanel pnlPanel = new JPanel();
        pnlPanel.setLayout(new BoxLayout(pnlPanel, BoxLayout.Y_AXIS));
        pnlPanel.add(pnlTitle);
        pnlPanel.add(pnlScroll);

        add(pnlPanel, BorderLayout.CENTER);


        //Adds ChatEntry
        JPanel pnlTextEntry = createPnlTextEntry();
        btnSend = new JButton("➤");
        btnSend.setToolTipText("Send Message");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Notify Controller a message to a network
                ConnectionsController.sendMessage(txtEntryField.getText());
                txtEntryField.setText("");
            }
        });

        JPanel pnlChatEntry = new JPanel();
        pnlChatEntry.setLayout(new BoxLayout(pnlChatEntry, BoxLayout.X_AXIS));
        pnlChatEntry.add(pnlTextEntry);
        pnlChatEntry.add(btnSend);
        add(pnlChatEntry, BorderLayout.PAGE_END);
    }

    private JPanel createPnlTextEntry(){
        txtEntryField = new JTextField(15);
        btnUpload = new JButton("➕");
        btnUpload.setBackground(txtEntryField.getBackground());

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.X_AXIS));

        txtEntryField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //Sends a message to a network
                    ConnectionsController.sendMessage(txtEntryField.getText());
                    txtEntryField.setText("");
                }
            }
        });

        jPanel.add(btnUpload);
        jPanel.add(txtEntryField);
        jPanel.setBackground(txtEntryField.getBackground());
        jPanel.setBorder(txtEntryField.getBorder());
        txtEntryField.setBorder(null);
        return jPanel;
    }

    public JLabel getLblTitle() {
        return lblTitle;
    }

    public void addMessage(String from, String message){
        if (from.isEmpty()) throw new IllegalArgumentException("No From entity");
        if (!message.isEmpty()) {
            txtChatArea.append("\n" + from + " : " + message);
        }
    }
}


