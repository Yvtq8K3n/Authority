package pt.ipleiria.authority.view;

import pt.ipleiria.authority.Sender;
import pt.ipleiria.authority.controller.ContactController;
import pt.ipleiria.authority.model.Contact;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ToolBar extends JToolBar{

    private JButton btnShout;
    private JButton btnProfile;
    private JButton btn3;

    public ToolBar() {
        initComponents();
    }

    private void initComponents() {

        btnShout = new JButton(new ImageIcon("./images/shoutface32x32.png"));
        btnShout.setToolTipText("Shout to retrieve other nodes");
        btnShout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sender.broadcast();
            }
        });

        btnProfile =  new JButton(new ImageIcon("./images/profile32x32.png"));
        btnProfile.setToolTipText("Apply Changes to your profile");
        btnProfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog( "What's your nickname?");
                if(name!=null) {
                    try {
                        ContactController.updateMyContactName(name);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });




        add(btnShout);
        add(btnProfile);
        //add(btn3);
    }
}
