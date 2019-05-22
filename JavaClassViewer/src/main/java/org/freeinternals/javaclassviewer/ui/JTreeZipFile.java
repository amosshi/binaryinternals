/*
 * JTreeZipFile.java    September 26, 2007, 9:36 AM
 *
 * Copyright 2007, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * A tree file created from a {@code JarFile} or {@code ZipFile} containing the 
 * Java {@code class} file(s).
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class JTreeZipFile extends JTree {

    private static final long serialVersionUID = 4876543219876500000L;
    private final ZipFile zipFile;
    private DefaultMutableTreeNode root = null;

    /**
     * Creates a tree from a {@code JarFile} or {@code ZipFile}.
     *
     * @param zipFile The Zip file (usually a Jar file)
     */
    public JTreeZipFile(final ZipFile zipFile) {
        if (zipFile == null) {
            throw new IllegalArgumentException("zipFile cannot be null.");
        }

        this.zipFile = zipFile;
        this.generateTreeNodes();
        this.setModel(new DefaultTreeModel(this.root));
    }

    /**
     * Get the {@code JarFile} or {@code ZipFile} of this tree.
     *
     * @return The {@code JarFile} or {@code ZipFile} in the tree
     */
    public ZipFile getZipFile() {
        return this.zipFile;
    }

    private void generateTreeNodes() {
        this.root = new DefaultMutableTreeNode(this.zipFile.getName());

        final Enumeration zipEntries = this.zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            this.addTreeNode((ZipEntry) zipEntries.nextElement());
        }
    }

    private void addTreeNode(final ZipEntry ze) {
        boolean isFolder = false;

        String nodePath = ze.getName();
        if (nodePath == null || nodePath.isEmpty()) {
            return;
        }
        if (nodePath.endsWith("/")) //  This is for windows platform
        {
            isFolder = true;
            nodePath = nodePath.substring(0, nodePath.length() - 1);
            if (nodePath.isEmpty()) {
                return;
            }
        }

        final String[] nodePaths = nodePath.split("/");
        DefaultMutableTreeNode node = this.root;
        for (int i = 0; i < nodePaths.length; i++) {
            if ((i + 1) == nodePaths.length) {
                node = this.addTreeNode(node, nodePaths[i], isFolder, ze);
            } else {
                node = this.addTreeNode(node, nodePaths[i], isFolder, null);
            }
        }
    }

    private DefaultMutableTreeNode addTreeNode(final DefaultMutableTreeNode parent, final String childText, final boolean isFolder, final ZipEntry ze) {
        if (parent == null) {
            throw new IllegalArgumentException("parent node cannot be null.");
        }
        if (childText == null || childText.isEmpty()) {
            throw new IllegalArgumentException("child node text cannot be null or empty.");
        }

        DefaultMutableTreeNode child = null;

        if (parent.isLeaf()) {
            child = new DefaultMutableTreeNode(
                    new JTreeNodeZipFile(childText, ze));
            parent.add(child);
        } else {
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                TreeNode childTreeNode = parent.getChildAt(i);

                if (childText.equals(childTreeNode.toString()) && childTreeNode instanceof DefaultMutableTreeNode) {
                    child = (DefaultMutableTreeNode) childTreeNode;
                }
            }

            if (child == null) {
                // Change the tree node icon here accroding to 'isFolder'.
                child = new DefaultMutableTreeNode(
                        new JTreeNodeZipFile(childText, ze));
                parent.add(child);
            }
        }

        return child;
    }
}
