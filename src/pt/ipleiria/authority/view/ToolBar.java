package pt.ipleiria.authority.view;

import pt.ipleiria.authority.Sender;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolBar extends JToolBar{

    private JButton btnShout;
    private JButton btn2;
    private JButton btn3;

    public ToolBar() {
        initComponents();
    }

    private void initComponents() {
        btnShout = new JButton(new ImageIcon("images/shoutface32x32.png"));
        btnShout.setToolTipText("Shout to retrieve other nodes");
        btnShout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Sender.broadcast();
            }
        });
        //btn2 =  new JButton(new ImageIcon("images/shoutface32x32.png"));
        //btn3 = new JButton(new ImageIcon("images/shoutface32x32.png"));

        add(btnShout);
        //add(btn2);
        //add(btn3);
    }
}
