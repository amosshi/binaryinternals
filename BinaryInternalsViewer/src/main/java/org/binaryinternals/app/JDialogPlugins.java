/*
 * JDialogPlugins.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.app;

import java.awt.Frame;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.binaryinternals.plugin.PluginManager;

/// Shows the loaded plug-ins.
///
/// @author Amos Shi
final class JDialogPlugins extends JDialogTable {

    private static final long serialVersionUID = 4876543219876500006L;

    JDialogPlugins(final Frame owner, final String title) {
        super(owner, title, new PluginsModel());
    }

    private static final class PluginsModel extends AbstractTableModel {

        private static final long serialVersionUID = 4876543219876500013L;
        private static final List<String> COLUMN_NAMES = List.of("Jar file", "Format Class", "Extension Description");

        private final transient List<Row> rowData = PluginManager.getPlugins().values().stream()
                .map(plugin -> new Row(
                        PluginManager.getJarName(plugin.getClass()),
                        plugin.getFileFormatClass().getName(),
                        plugin.getExtensionDescription()))
                .toList();

        /// One table row.
        private record Row(String jar, String formatClass, String description) {
        }

        @Override
        public int getRowCount() {
            return this.rowData.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.size();
        }

        @Override
        public String getColumnName(final int column) {
            return COLUMN_NAMES.get(column);
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            final Row row = this.rowData.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> row.jar();
                case 1 -> row.formatClass();
                case 2 -> row.description();
                default -> throw new IllegalArgumentException("Invalid column " + columnIndex);
            };
        }
    }
}
