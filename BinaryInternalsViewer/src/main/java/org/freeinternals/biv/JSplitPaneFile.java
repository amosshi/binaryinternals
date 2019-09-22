/*
 * JSplitPaneFile.java    Apr 12, 2011, 10:50
 *
 * Copyright 2011, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.biv.plugin.PluginManager;
import org.freeinternals.commonlib.ui.JBinaryViewer;
import org.freeinternals.commonlib.ui.UITool;
import org.freeinternals.commonlib.ui.JPanelForTree;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.core.FileFormatException;

/**
 * A split panel created from a class file byte array.
 *
 * @author Amos Shi
 */
public class JSplitPaneFile extends JSplitPane {

    private static final long serialVersionUID = 4876543219876500000L;
    private final JFrame topLevelFrame;
    private final FileFormat file;
    private JBinaryViewer binaryViewer = null;
    private JScrollPane binaryViewerView = null;

    /**
     * Creates a split panel from a Java class file byte array.
     *
     * @param file
     * @param frame
     * @throws FileFormatException
     */
    public JSplitPaneFile(final File file, final JFrame frame) throws FileFormatException, Throwable {
        this.file = PluginManager.getFile(file);
        this.topLevelFrame = frame;
        this.createAndShowGUI();
    }

    private void createAndShowGUI() {

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                0,
                this.file.fileByteArray.length,
                this.file.fileName));
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

                if (value instanceof DefaultMutableTreeNode) {
                    if (((DefaultMutableTreeNode) value).getUserObject() instanceof JTreeNodeFileComponent) {
                        JTreeNodeFileComponent fileComp = (JTreeNodeFileComponent) ((DefaultMutableTreeNode) value).getUserObject();
                        final Icon icon = fileComp.getIcon();
                        if (icon != null) {
                            this.setIcon(icon);
                        }

                        if (fileComp.isDetailAvailable()) {
                            this.setText("<html><font color=blue><u>" + fileComp.getText());
                        } else {
                            this.setText(fileComp.getText());
                        }
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
                if (selPath != null) {
                    Object obj = selPath.getLastPathComponent();
                    if (obj instanceof DefaultMutableTreeNode) {
                        final DefaultMutableTreeNode objDmtn = (DefaultMutableTreeNode) obj;
                        if (objDmtn.getUserObject() instanceof JTreeNodeFileComponent) {
                            JTreeNodeFileComponent fileComp = (JTreeNodeFileComponent) objDmtn.getUserObject();
                            if (fileComp.isDetailAvailable() && e.getClickCount() == 2) {
                                treeDoubleClickPopup(fileComp.getDetailPanel(), fileComp.getText());
                            }
                        }
                    }
                }
            }
        });

        final JPanelForTree panel = new JPanelForTree(tree, this.topLevelFrame);

        final JTabbedPane tabbedPane = new JTabbedPane();
        this.binaryViewer = new JBinaryViewer();
        this.binaryViewer.setData(this.file.fileByteArray);
        this.binaryViewerView = new JScrollPane(this.binaryViewer);
        this.binaryViewerView.getVerticalScrollBar().setValue(0);
        tabbedPane.add(this.file.getContentTabName(), this.binaryViewerView);

        this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.setDividerSize(5);
        this.setDividerLocation(390);
        this.setLeftComponent(panel);
        this.setRightComponent(tabbedPane);

        this.binaryViewerView.getVerticalScrollBar().setValue(0);
    }

    private void treeSelectionChanged(final TreeSelectionEvent evt) {
        Object obj = evt.getPath().getLastPathComponent();
        if (obj instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode objDmtn = (DefaultMutableTreeNode) obj;
            obj = objDmtn.getUserObject();
            if (obj instanceof JTreeNodeFileComponent) {
                final JTreeNodeFileComponent objTnfc = (JTreeNodeFileComponent) obj;
                this.binaryViewer.setSelection(objTnfc.getStartPos(), objTnfc.getLength());
            }
        }
    }

    private void treeDoubleClickPopup(JPanel panel, String title) {
        UITool.showPopup(this.topLevelFrame, panel, title);
    }
}
