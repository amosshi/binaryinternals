/*
 * FileFormat.java    Apr 12, 2011, 13:02
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.format.FileFormatException;

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
     * Raw byte array of the file.
     */
    public final byte[] fileByteArray;
    
    /**
     * The parsed file components.
     */
    protected final SortedMap<Long, FileComponent> components = new TreeMap<>();


    public FileFormat(final File file) throws IOException, FileFormatException {
        this.fileName = file.getName();

        if (file.length() == 0) {
            throw new FileFormatException(
                    String.format("The file content is empty.\nname = %s", file.getPath()));
        }
        this.fileByteArray = Tool.readFileAsBytes(file);
    }
    
    /**
     * Add the <code>comp</code> to the {@link #components}.
     * 
     * @param comp The {@link FileComponent} to be added
     */
    protected void addFileComponent(FileComponent comp){
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
     * Get part of the file byte array.
     * The array begins at the specified {@code startIndex} and extends to
     * the byte at {@code startIndex}+{@code length}.
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
        for (int i = 0; i < length; i++) {
            data[i] = this.fileByteArray[startIndex + i];
        }

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
}
