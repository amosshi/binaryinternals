/*
 * JSplitPaneFile.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, BinaryInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.binaryinternals.app;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.binaryinternals.binaryviewer.JBinaryViewer;
import org.binaryinternals.commonlib.core.FileFormat;
import org.binaryinternals.commonlib.ui.HTMLKit;
import org.binaryinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * A split panel showing the tree and the binary content of a parsed file.
 *
 * @author Amos Shi
 */
public final class JSplitPaneFile extends JSplitPane {

    private static final long serialVersionUID = 4876543219876500003L;
    private final JFrame topLevelFrame;
    private final transient FileFormat file;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JBinaryViewer binaryViewer = new JBinaryViewer();

    /**
     * Creates a split panel from an already parsed file.
     *
     * @param fileFormat Parsed file
     * @param frame Parent frame
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "We need it")
    public JSplitPaneFile(final FileFormat fileFormat, final JFrame frame) {
        this.file = fileFormat;
        this.topLevelFrame = frame;
        this.createAndShowGUI();
    }

    /**
     * Getter {@link #file} name.
     *
     * @return {@link #file} name
     */
    public String getFileFormatName() {
        return this.file.getClass().getSimpleName();
    }

    private int calcDividerLocation() {
        return (int) Math.clamp(this.topLevelFrame.getWidth() * 0.4, 260, 1200);
    }

    private void createAndShowGUI() {

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                0,
                this.file.fileByteArray.length,
                this.file.fileName,
                this.file.getIcon().orElse(null),
                this.file.filePath));
        this.file.generateTreeNode(root);
        final JTree tree = new JTree(new DefaultTreeModel(root));

        tree.setCellRenderer(new DefaultTreeCellRenderer() {

            @Override
            public Component getTreeCellRendererComponent(final JTree tree, final Object value,
                    final boolean sel, final boolean expanded, final boolean leaf, final int row,
                    final boolean hasFocus) {

                super.getTreeCellRendererComponent(tree, value,
                        sel, expanded, leaf, row,
                        hasFocus);

                if (value instanceof DefaultMutableTreeNode node && node.getUserObject() instanceof JTreeNodeFileComponent fileComp) {
                    final Icon icon = fileComp.getIcon();
                    if (icon != null) {
                        this.setIcon(icon);
                    }

                    if (fileComp.isDetailAvailable()) {
                        this.setText("<html><font color=blue><u>" + HTMLKit.escapeFilter(fileComp.getText()));
                    } else {
                        this.setText(fileComp.getText());
                    }
                }

                return this;
            }
        });

        tree.addTreeSelectionListener(this::treeSelectionChanged);
        tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selPath != null
                        && selPath.getLastPathComponent() instanceof DefaultMutableTreeNode node
                        && node.getUserObject() instanceof JTreeNodeFileComponent fileComp
                        && fileComp.isDetailAvailable() && e.getClickCount() == 2) {
                    treeDoubleClickPopup(fileComp.getDetailPanel(), fileComp.getText());
                }
            }
        });

        final JPanelForTree panel = new JPanelForTree(tree, this.topLevelFrame);

        this.binaryViewer.setData(this.file.fileByteArray);
        final JScrollPane binaryViewerView = new JScrollPane(this.binaryViewer);
        this.tabbedPane.add(this.file.getContentTabName(), binaryViewerView);

        this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.setDividerSize(5);
        this.setDividerLocation(this.calcDividerLocation());
        this.setLeftComponent(panel);
        this.setRightComponent(tabbedPane);

        binaryViewerView.getVerticalScrollBar().setValue(0);
    }

    private void treeSelectionChanged(final TreeSelectionEvent evt) {
        // The new selection, not evt.getPath() which can be a deselected path
        final TreePath path = evt.getNewLeadSelectionPath();
        if (path != null
                && path.getLastPathComponent() instanceof DefaultMutableTreeNode node
                && node.getUserObject() instanceof JTreeNodeFileComponent objTnfc) {
            this.binaryViewer.setSelection(objTnfc.getStartPos(), objTnfc.getLength());
            this.file.treeSelectionChanged(objTnfc, this.tabbedPane);
        } else if (path == null) {
            // Nothing selected any more, e.g. the only node was ctrl-clicked
            this.binaryViewer.clearSelection();
        }
    }

    private void treeDoubleClickPopup(JPanel panel, String title) {
        Main.showPopup(this.topLevelFrame, panel, title);
    }
}
