/*
 * JDialogAbout.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.freeinternals.commonlib.ui.JLabelHyperLink;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
class JDialogAbout extends JDialog {

    private static final long serialVersionUID = 4876543219876500000L;

    /**
     * 
     * @param owner
     * @param title
     */
    JDialogAbout(final Frame owner, final String title) {
        super(owner, title);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setModal(true);

        this.initComponents();
        this.pack();
        this.setResizable(false);
    }

    private void initComponents() {
        this.setLayout(new FlowLayout());

        final JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                buttonOK_Clicked();
            }
        });

//      Lay out the labels from top to bottom.
        final JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

        final JLabel label = new JLabel("Binary Internals Viewer");
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(new JLabel("Watch binary file visually & interactively for the meaning of every byte"));
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(new JLabel("Version: 0.1"));
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(new JLabel("Author: Amos Shi"));
        listPane.add(Box.createRigidArea(new Dimension(0, 20)));
        listPane.add(new JLabelHyperLink(
                "https://github.com/amosshi/freeinternals",
                "https://github.com/amosshi/freeinternals"));
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(new JLabel("Free Tools to reach the Internals"));
        listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//      Lay out the buttons from left to right.
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(buttonClose);

//      Put everything together, using the content pane's BorderLayout.
        final Container contentPane = this.getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

    }

    private void buttonOK_Clicked() {
        this.setVisible(false);
    }
}
