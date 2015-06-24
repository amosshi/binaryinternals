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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.biv.plugin.PluginManager;
import org.freeinternals.commonlib.ui.JLabelHyperLink;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
class JDialogPlugins extends JDialog {

    private static final long serialVersionUID = 4876543219876500000L;

    /**
     *
     * @param owner
     * @param title
     */
    JDialogPlugins(final Frame owner, final String title) {
        super(owner, title);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setModal(true);

        this.initComponents();
        this.pack();
        this.setResizable(true);
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        final JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                buttonOK_Clicked();
            }
        });

//      Lay out the labels from top to bottom.
        Map<String, PluginDescriptor> plugins = PluginManager.getPlugins();

        Vector columnNames = new Vector();
        columnNames.add("Jar file");
        columnNames.add("Format Class");
        columnNames.add("Extension Description");
        Vector rowData = new Vector();
        Iterator it = plugins.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            PluginDescriptor value = (PluginDescriptor) pair.getValue();
            Vector row = new Vector();
            row.add(pair.getKey().toString());
            row.add(value.getFileFormatClass().getName());
            row.add(value.getExtensionDescription());
            rowData.add(row);
        }

        final JTable table = new JTable(rowData, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

//      Lay out the buttons from left to right.
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(buttonClose);

//      Put everything together, using the content pane's BorderLayout.
        final Container contentPane = this.getContentPane();
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
    }

    private void buttonOK_Clicked() {
        this.setVisible(false);
    }
}
