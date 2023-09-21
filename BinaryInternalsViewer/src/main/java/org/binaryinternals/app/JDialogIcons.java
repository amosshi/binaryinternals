/*
 * JDialogIcons.java    Sep 21, 2021
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.app;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.binaryinternals.commonlib.ui.Icons;

/**
 *
 * @author Amos Shi
 */
class JDialogIcons extends JDialog {

    private static final long serialVersionUID = 4876543219876500000L;

    JDialogIcons(final Frame owner, final String title) {
        super(owner, title);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setModal(true);

        this.initComponents();
        this.pack();
        this.setResizable(true);
    }

    private void closeButtonOKClicked() {
        this.setVisible(false);
    }

    /**
     * Prepare data model for table.
     *
     * @see <a href="https://stackoverflow.com/questions/4941372/">How to Insert Image into JTable Cell</a>
     */
    private DefaultTableModel getIconsTableModel(){

        String[] columnNames = {"Icon", "Name"};
        return new DefaultTableModel(columnNames, Icons.values().length) {
            /**
             * Returning the Class of each column will allow different renderers to be used based on Class.
             */
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Icon.class;
                    case 1:
                        return String.class;
                    default:
                        throw new IllegalArgumentException(String.format("The column=%d do not exist", column));
                }
            }

            @Override
            public Object getValueAt(int row, int column) {
                Icons item = Icons.values()[row];
                switch (column) {
                    case 0:
                        return item.getIcon();
                    case 1:
                        return item.name();
                    default:
                        throw new IllegalArgumentException(String.format("The row=%d column=%d do not exist", row, column));
                }
            }

        };
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        final JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener((final ActionEvent e) -> closeButtonOKClicked());

        // Lay out the labels from top to bottom.
        final JTable table = new JTable(this.getIconsTableModel());
        JDialogPlugins.resizeColumnWidth(table);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        final JPanel treePane = new JPanel();
        treePane.setLayout(new BorderLayout());
        treePane.add(scrollPane, BorderLayout.CENTER);
        treePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Lay out the buttons from left to right.
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(buttonClose);

        // Put everything together, using the content pane's BorderLayout.
        final Container contentPane = this.getContentPane();
        contentPane.add(treePane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
    }
}
