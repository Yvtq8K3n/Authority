package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatPanel extends JPanel {
    //ChatTextWidget
    private JLabel lblChannel;
    private JTextArea txtChatArea;

    //ChatEntryWidget
    private JButton btnUpload;
    private JTextField txtEntryField;
    private JButton btnSend;

    public ChatPanel(){
        setLayout(new BorderLayout());
        initComponents();
        setMinimumSize(new Dimension(400, 50));
    }

    private void initComponents() {
        //Adds ChatArea
        lblChannel = new JLabel("Channel->That's so Hot:");
        lblChannel.setPreferredSize(new Dimension(75, 25));

        txtChatArea = new JTextArea();
        txtChatArea.setEditable(false);
        txtChatArea.setLineWrap(true);

        JScrollPane pnlScroll = new JScrollPane(txtChatArea);

        JPanel pnlPanel = new JPanel();
        pnlPanel.setLayout(new BoxLayout(pnlPanel, BoxLayout.Y_AXIS));
        pnlPanel.add(lblChannel);
        pnlPanel.add(pnlScroll);

        add(pnlPanel, BorderLayout.CENTER);


        //Adds ChatEntry
        JPanel pnlTextEntry = createPnlTextEntry();
        btnSend = new JButton("➤");
        btnSend.setToolTipText("Send Message");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtChatArea.append("\n Bob: "+ txtEntryField.getText());
                if (txtEntryField.getText().toLowerCase().contains("i'm bob"))
                    txtChatArea.append("\n That's what she said");
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
        jPanel.add(btnUpload);

        jPanel.add(txtEntryField);
        jPanel.setBackground(txtEntryField.getBackground());
        jPanel.setBorder(txtEntryField.getBorder());
        txtEntryField.setBorder(null);
        return jPanel;
    }
}


