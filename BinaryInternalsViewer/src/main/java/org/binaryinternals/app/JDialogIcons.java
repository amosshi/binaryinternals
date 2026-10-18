/*
 * JDialogIcons.java    Sep 21, 2021
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.app;

import java.awt.Frame;
import javax.swing.Icon;
import javax.swing.table.DefaultTableModel;
import org.binaryinternals.commonlib.ui.Icons;

/**
 * Shows all the icons.
 *
 * @author Amos Shi
 */
class JDialogIcons extends JDialogTable {

    private static final long serialVersionUID = 4876543219876500007L;
    private static final Icons[] ICONS = Icons.values();

    JDialogIcons(final Frame owner, final String title) {
        super(owner, title, getIconsTableModel());
    }

    /**
     * Prepare data model for table.
     *
     * @see <a href="https://stackoverflow.com/questions/4941372/">How to Insert Image into JTable Cell</a>
     */
    private static DefaultTableModel getIconsTableModel() {

        String[] columnNames = {"Icon", "Name"};
        return new DefaultTableModel(columnNames, ICONS.length) {
            /**
             * Returning the Class of each column will allow different renderers to be used based on Class.
             */
            @Override
            public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 0 -> Icon.class;
                    case 1 -> String.class;
                    default -> throw new IllegalArgumentException(String.format("The column=%d do not exist", column));
                };
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Object getValueAt(int row, int column) {
                final Icons item = ICONS[row];
                return switch (column) {
                    case 0 -> item.getIcon();
                    case 1 -> item.name();
                    default -> throw new IllegalArgumentException(String.format("The row=%d column=%d do not exist", row, column));
                };
            }

        };
    }
}
