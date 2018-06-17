/*
 * JTreeCellRenderer.java    Sep 12, 2010, 20:12
 *
 * Copyright 2010, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *
 * @author Amos Shi
 */
public class JTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 4876543219876500000L;

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

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

                if (fileComp.isDetailAvailable() == false) {
                    this.setText(fileComp.getText());
                } else {
                    this.setText("<html><font color=blue><u>" + fileComp.getText());
                }
            }
        }

        return this;
    }
}
