package pt.ipleiria.authority.view;

import pt.ipleiria.authority.UDPBroadcast;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.IconUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ToolBar extends JToolBar{

    private JButton btnShout;
    private JButton btn2;
    private JButton btn3;

    public ToolBar() {
        initComponents();
    }

    private void initComponents() {
        btnShout = new JButton(new ImageIcon("images/shoutface32x32.png"));
        btnShout.setToolTipText("Shout to retrieve contacts");
        btnShout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UDPBroadcast.broadcast();
            }
        });
        //btn2 =  new JButton(new ImageIcon("images/shoutface32x32.png"));
        //btn3 = new JButton(new ImageIcon("images/shoutface32x32.png"));

        add(btnShout);
        //add(btn2);
        //add(btn3);
    }
}
