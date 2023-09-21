/*
 * JDialogAbout.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author Amos Shi
 */
class JDialogAbout extends JDialog {

    private static final long serialVersionUID = 4876543219876500000L;

    JDialogAbout(final Frame owner, final String title) {
        super(owner, title);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setModal(true);

        this.initComponents();
        this.pack();
        this.setResizable(false);
    }

    private void buttonOKClicked() {
        this.setVisible(false);
    }

    private JTextField getLine(String label){
        JTextField field = new JTextField(label);
        field.setEditable(false);
        field.setBorder(BorderFactory.createEmptyBorder());
        return field;
    }

    private void initComponents() {
        this.setLayout(new FlowLayout());

        final JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener((final ActionEvent e) -> buttonOKClicked());

        // Lay out the labels from top to bottom.
        final JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

        final JLabel label = new JLabel("Binary Internals Viewer");
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        listPane.add(label);

        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(new JLabelHyperLink(
                "https://github.com/amosshi/binaryinternals",
                "https://github.com/amosshi/binaryinternals"));

        listPane.add(Box.createRigidArea(new Dimension(0, 20)));
        listPane.add(this.getLine("Watch binary file internals visually & interactively for the meaning of every bit"));
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(this.getLine("Version: 3.5"));
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(this.getLine("Author: Amos Shi"));

        listPane.add(Box.createRigidArea(new Dimension(0, 20)));
        listPane.add(this.getLine("System Properties:"));
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        JTextArea sysinfo = new JTextArea(8, 40);
        System.getProperties().forEach((key, value) -> sysinfo.append(key + ": " + value + System.lineSeparator()));
        sysinfo.setCaretPosition(0);
        sysinfo.setEditable(false);
        sysinfo.setLineWrap(true);
        listPane.add(new JScrollPane(sysinfo));

        listPane.add(Box.createRigidArea(new Dimension(0, 20)));
        listPane.add(this.getLine("Icons are freely provided by https://icons8.com/"));

        listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Lay out the buttons from left to right.
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(buttonClose);

        // Put everything together, using the content pane's BorderLayout.
        final Container contentPane = this.getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);

    }
}
