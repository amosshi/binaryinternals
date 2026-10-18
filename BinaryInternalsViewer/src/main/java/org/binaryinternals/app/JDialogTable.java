/*
 * JDialogTable.java
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A modal dialog showing a read-only table and a Close button.
 *
 * @author Amos Shi
 */
abstract class JDialogTable extends JDialog {

    private static final long serialVersionUID = 4876543219876500010L;
    private static final int COLUMN_MIN_WIDTH = 50;
    private static final int COLUMN_PADDING = 10;
    private static final int BORDER = 10;

    JDialogTable(final Frame owner, final String title, final TableModel model) {
        super(owner, title, true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.initComponents(model);
        this.pack();
        this.setResizable(true);
    }

    private void initComponents(final TableModel model) {
        this.setLayout(new BorderLayout());

        final JButton buttonClose = new JButton("Close");
        buttonClose.addActionListener((final ActionEvent e) -> this.setVisible(false));

        final JTable table = new JTable(model);
        resizeColumnWidth(table);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setFillsViewportHeight(true);
        final JPanel tablePane = new JPanel(new BorderLayout());
        tablePane.add(new JScrollPane(table), BorderLayout.CENTER);
        tablePane.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));

        // Lay out the buttons from left to right.
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, BORDER, BORDER, BORDER));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(buttonClose);

        final Container contentPane = this.getContentPane();
        contentPane.add(tablePane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
    }

    private static void resizeColumnWidth(final JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = COLUMN_MIN_WIDTH;
            for (int row = 0; row < table.getRowCount(); row++) {
                final TableCellRenderer renderer = table.getCellRenderer(row, column);
                final Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width + COLUMN_PADDING);
        }
    }
}
