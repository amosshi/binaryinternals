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
import java.lang.reflect.InvocationTargetException;
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
import org.freeinternals.binaryviewer.JBinaryViewer;
import org.freeinternals.biv.plugin.PluginManager;
import org.freeinternals.commonlib.core.FileFormat;
import org.freeinternals.commonlib.core.FileFormatException;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.commonlib.ui.UITool;

/**
 * A split panel created from a class file byte array.
 *
 * @author Amos Shi
 */
public class JSplitPaneFile extends JSplitPane {

    private static final long serialVersionUID = 4876543219876500000L;
    private final JFrame topLevelFrame;
    private final FileFormat file;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JBinaryViewer binaryViewer = new JBinaryViewer();

    /**
     * Creates a split panel from a Java class file byte array.
     *
     * @param file
     * @param frame
     * @throws FileFormatException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "We need it")
    public JSplitPaneFile(final File file, final JFrame frame) throws FileFormatException, NoSuchMethodException, SecurityException, InstantiationException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        this.file = PluginManager.getFile(file);
        this.topLevelFrame = frame;
        this.createAndShowGUI();
    }
    
    private int calcDividerLocation(){
        final double width = this.topLevelFrame.getWidth() * 0.4;
        if (width < 260) {
            return 260;
        } else if (width > 1200) {
            return 1200;
        } else {
            return (int) Math.floor(width);
        }
    }

    @SuppressWarnings("java:S3776") // Cognitive Complexity of methods should not be too high
    private void createAndShowGUI() {

        final DefaultMutableTreeNode root = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                0,
                this.file.fileByteArray.length,
                this.file.fileName,
                this.file.getIcon(),
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

                if (value instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode) value).getUserObject() instanceof JTreeNodeFileComponent) {
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

        this.binaryViewer.setData(this.file.fileByteArray);
        JScrollPane binaryViewerView = new JScrollPane(this.binaryViewer);
        binaryViewerView.getVerticalScrollBar().setValue(0);
        this.tabbedPane.add(this.file.getContentTabName(), binaryViewerView);

        this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.setDividerSize(5);
        this.setDividerLocation(this.calcDividerLocation());
        this.setLeftComponent(panel);
        this.setRightComponent(tabbedPane);

        binaryViewerView.getVerticalScrollBar().setValue(0);
    }

    private void treeSelectionChanged(final TreeSelectionEvent evt) {
        Object obj = evt.getPath().getLastPathComponent();
        if (obj instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode objDmtn = (DefaultMutableTreeNode) obj;
            obj = objDmtn.getUserObject();
            if (obj instanceof JTreeNodeFileComponent) {
                final JTreeNodeFileComponent objTnfc = (JTreeNodeFileComponent) obj;
                this.binaryViewer.setSelection(objTnfc.getStartPos(), objTnfc.getLength());
                this.file.treeSelectionChanged(objTnfc, this.tabbedPane);
            }
        }
    }

    private void treeDoubleClickPopup(JPanel panel, String title) {
        UITool.showPopup(this.topLevelFrame, panel, title);
    }
}
