/*
 * JDialogIcons.java    Sep 21, 2021
 *
 * Copyright 2021, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */

package org.binaryinternals.app;

import java.awt.Frame;
import java.util.List;
import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;
import org.binaryinternals.commonlib.Icons;

/// Shows all the icons.
///
/// @author Amos Shi
final class JDialogIcons extends JDialogTable {

    private static final long serialVersionUID = 4876543219876500007L;
    private static final List<Icons> ICONS = List.of(Icons.values());

    JDialogIcons(final Frame owner, final String title) {
        super(owner, title, new IconsModel());
    }

    /// Data model for table.
    ///
    /// @see <a href="https://stackoverflow.com/questions/4941372/">How to Insert Image into JTable Cell</a>
    private static final class IconsModel extends AbstractTableModel {

        private static final long serialVersionUID = 4876543219876500011L;
        private static final List<String> COLUMN_NAMES = List.of("Icon", "Name");

        @Override
        public int getRowCount() {
            return ICONS.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.size();
        }

        @Override
        public String getColumnName(final int column) {
            return COLUMN_NAMES.get(column);
        }

        /// Returning the Class of each column will allow different renderers to be used based on Class.
        @Override
        public Class<?> getColumnClass(final int column) {
            return switch (column) {
                case 0 -> Icon.class;
                case 1 -> String.class;
                default -> throw new IllegalArgumentException("Invalid column " + column);
            };
        }

        @Override
        public Object getValueAt(final int row, final int column) {
            final Icons item = ICONS.get(row);
            return switch (column) {
                case 0 -> item.getIcon();
                case 1 -> item.name();
                default -> throw new IllegalArgumentException("Invalid column " + column);
            };
        }
    }
}
