/*
 * FileFormat.java    Apr 12, 2011, 13:02
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.core;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.Icons;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 *
 * @author Amos Shi
 */
public abstract class FileFormat {

    /**
     * The file name.
     */
    public final String fileName;
    /**
     * The file name.
     */
    public final String filePath;
    /**
     * Raw byte array of the file.
     */
    public final byte[] fileByteArray;

    /**
     * The parsed file components.
     */
    protected final SortedMap<Long, FileComponent> components = new TreeMap<>();

    /**
     * Parse the content from a {@link File} object.
     *
     * @param file {@link File} object
     * @throws IOException Failed to Read file
     * @throws FileFormatException The file is empty
     */
    protected FileFormat(final File file) throws IOException, FileFormatException {
        this.fileName = file.getName();
        this.filePath = file.getCanonicalPath();

        if (file.length() == 0) {
            throw new FileFormatException(
                    String.format("The file content is empty. name = %s", file.getPath()));
        }
        this.fileByteArray = BytesTool.readFileAsBytes(file);
    }

    /**
     * Parse a byte array (in memory) as a file.
     *
     * @param bytes Contents in bytes
     * @param fileName File name
     * @param filePath File path
     */
    protected FileFormat(final byte[] bytes, final String fileName, final String filePath) {
        final String inMemory = "In Memory Bytes data";
        this.fileName = (fileName == null) ? inMemory : fileName;
        this.filePath = (filePath == null) ? inMemory : filePath;
        this.fileByteArray = bytes;
    }

    /**
     * Add the <code>comp</code> to the {@link #components}.
     *
     * @param comp The {@link FileComponent} to be added
     */
    protected void addFileComponent(FileComponent comp) {
        this.components.put(Long.valueOf(comp.getStartPos()), comp);
    }

    /**
     * Get the content tab name for UI.
     *
     * @return A string for the tab name
     */
    public abstract String getContentTabName();

    /**
     * Generate the Tree node for the file.
     *
     * @param parentNode The root tree node
     */
    public abstract void generateTreeNode(final DefaultMutableTreeNode parentNode);

    /**
     * Get part of the file byte array. The array begins at the specified
     * {@code startIndex} and extends to the byte at
     * {@code startIndex}+{@code length}.
     *
     * @param startIndex The start index
     * @param length The length of the array
     * @return Part of the class byte array
     */
    public byte[] getFileByteArray(final int startIndex, final int length) {
        if ((startIndex < 0) || (length < 1)) {
            throw new IllegalArgumentException("startIndex or length is not valid. startIndex = " + startIndex + ", length = " + length);
        }
        if (startIndex + length - 1 > this.fileByteArray.length) {
            throw new ArrayIndexOutOfBoundsException("The last item index is bigger than class byte array size.");
        }

        byte[] data = new byte[length];
        System.arraycopy(this.fileByteArray, startIndex, data, 0, length);
        return data;
    }

    /**
     * Return the file components list.
     *
     * @return file component list
     */
    public Collection<FileComponent> getFileComponents() {
        return Collections.unmodifiableCollection(this.components.values());
    }

    /**
     * The child class may choose to provide an icon for the file format. This
     * method should be change to abstract if all children has provided an icon.
     *
     * @return <code>null</code> to use default icon, else use supplied icon
     */
    public Icons getIcon() {
        return null;
    }

    /**
     * Response to the tree node selection change event. Example: when choosing
     * method code node, the JVM Class file want to add a tab to display decoded
     * byte codes.
     *
     * @param userObj User Object on the Tree node
     * @param tabs Content tabs
     */
    public void treeSelectionChanged(final JTreeNodeFileComponent userObj, final JTabbedPane tabs) {
        // Remove the additional tab(s) first
        while (tabs.getTabCount() > 1) {
            tabs.remove(1);
        }
    }

    /**
     * Add a JTextPane to the tabs.
     *
     * @param tabs JTabbedPane control
     * @param title Title of the new tab
     * @return The new created JTextPane
     */
    protected JTextPane tabAddTextPane(final JTabbedPane tabs, final String title) {
        // Add detailed info pane for additional info
        JTextPane pane = new JTextPane();
        pane.setAlignmentX(Component.LEFT_ALIGNMENT);
        pane.setEditable(false);
        pane.setBorder(null);
        pane.setContentType("text/html");
        tabs.add(title, new JScrollPane(pane));
        return pane;
    }
}
