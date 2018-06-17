/*
 * JDialogAbout.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import org.freeinternals.biv.plugin.PluginDescriptor;
import org.freeinternals.biv.plugin.PluginManager;

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
        final JTable table = new JTable(new PluginsModel());
        this.resizeColumnWidth(table);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        final JPanel treePane = new JPanel();
        treePane.setLayout(new BorderLayout());
        treePane.add(scrollPane, BorderLayout.CENTER);
        treePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//      Lay out the buttons from left to right.
        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(buttonClose);

//      Put everything together, using the content pane's BorderLayout.
        final Container contentPane = this.getContentPane();
        contentPane.add(treePane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
    }

    private void buttonOK_Clicked() {
        this.setVisible(false);
    }

    private void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 50; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width, width);
            }
            columnModel.getColumn(column).setPreferredWidth(width + 10);
        }
    }

    public class PluginsModel extends AbstractTableModel {

        List<String> columnNames = new LinkedList<>();
        List<List<String>> rowData = new LinkedList<>();

        PluginsModel() {
            Map<String, PluginDescriptor> plugins = PluginManager.getPlugins();

            // Column Names
            this.columnNames.add("Jar file");
            this.columnNames.add("Format Class");
            this.columnNames.add("Extension Description");

            // Row Data
            for (Map.Entry pair : plugins.entrySet()) {
                PluginDescriptor value = (PluginDescriptor) pair.getValue();
                List<String> row = new LinkedList<>();
                row.add(pair.getKey().toString());
                row.add(value.getFileFormatClass().getName());
                row.add(value.getExtensionDescription());
                this.rowData.add(row);
            }
        }

        @Override
        public int getRowCount() {
            return this.rowData.size() + 2;
        }

        @Override
        public int getColumnCount() {
            return this.columnNames.size();
        }

        @Override
        public String getColumnName(int column) {
            return this.columnNames.get(column);
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex < this.rowData.size()) {
                return this.rowData.get(rowIndex).get(columnIndex);
            } else {
                return "";
            }
        }
    }
}
