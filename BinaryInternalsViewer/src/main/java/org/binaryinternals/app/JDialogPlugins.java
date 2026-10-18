/*
 * JDialogPlugins.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.app;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.binaryinternals.commonlib.core.PluginDescriptor;
import org.binaryinternals.plugin.PluginManager;

/**
 * Shows the loaded plug-ins.
 *
 * @author Amos Shi
 */
class JDialogPlugins extends JDialogTable {

    private static final long serialVersionUID = 4876543219876500006L;

    JDialogPlugins(final Frame owner, final String title) {
        super(owner, title, new PluginsModel());
    }

    static class PluginsModel extends AbstractTableModel {

        private static final long serialVersionUID = 4876543219876500013L;

        final transient List<String> columnNames = List.of("Jar file", "Format Class", "Extension Description");
        final transient List<Row> rowData = new ArrayList<>();

        /**
         * One table row.
         */
        record Row(String jar, String formatClass, String description) {
        }

        PluginsModel() {
            // Row Data
            for (PluginDescriptor plugin : PluginManager.getPlugins().values()) {
                this.rowData.add(new Row(
                        PluginManager.getJarName(plugin.getClass()),
                        plugin.getFileFormatClass().getName(),
                        plugin.getExtensionDescription()));
            }
        }

        @Override
        public int getRowCount() {
            return this.rowData.size();
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
