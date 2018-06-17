/*
 * JPanelForTree.java    April 05, 2009, 22:58
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.commonlib.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * A panel containing a {@code JTree} object.
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public class JPanelForTree extends JPanel {

    private static final long serialVersionUID = 4876543219876500000L;
    private final JFrame topLevelFrame;
    private JPanel details_panel;
    private String details_title;
    public final JTree tree;
    public final JToolBar toolbar;
    public final JButton toolbarbtn_Details;
    private JEditorPane editorPaneDescription;

    /**
     * Create a panel tool bar to contain a {@code JTree} object.
     *
     * @param tree The tree to be contained
     * @param frame 
     */
    public JPanelForTree(final JTree tree, final JFrame frame) {
        if (tree == null) {
            throw new IllegalArgumentException("[tree] cannot be null.");
        }

        this.tree = tree;
        this.topLevelFrame = frame;
        this.tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(final javax.swing.event.TreeSelectionEvent evt) {
                tree_SelectionChanged(evt);
            }
        });

        this.toolbar = new JToolBar();
        this.toolbarbtn_Details = new JButton("Details");
        this.initToolbar();

        this.layoutComponents();
    }

    private void layoutComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JPanel panel_Toolbar = new JPanel();
        panel_Toolbar.setLayout(new BoxLayout(panel_Toolbar, BoxLayout.X_AXIS));
        panel_Toolbar.add(this.toolbar);

        final Component glue = Box.createGlue();
        glue.setMaximumSize(new Dimension(Short.MAX_VALUE, this.toolbar.getHeight()));
        panel_Toolbar.add(glue);

        final JPanel panel_toolbar_tree = new JPanel();
        panel_toolbar_tree.setLayout(new BoxLayout(panel_toolbar_tree, BoxLayout.Y_AXIS));
        panel_toolbar_tree.add(panel_Toolbar);
        panel_toolbar_tree.add(new JScrollPane(this.tree));

        this.editorPaneDescription = new JEditorPane();
        this.editorPaneDescription.setContentType("text/html");
        this.editorPaneDescription.setEditable(false);
        this.editorPaneDescription.setPreferredSize(new Dimension(100, 120));

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(panel_toolbar_tree);
        splitPane.setBottomComponent(new JScrollPane(this.editorPaneDescription));
        splitPane.setResizeWeight(1.0);
        this.add(splitPane, BorderLayout.CENTER);
    }

    private void initToolbar() {
        this.toolbar.setRollover(true);
        //this.toolbar.setFloatable(false);

        // Button: Expand All
        final JButton buttonExpandAll = new JButton("Expand All");
        buttonExpandAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                toolbar_ExpandAll();
            }
        });
        this.toolbar.add(buttonExpandAll);

        // Button: Collapse All
        final JButton buttonCollapseAll = new JButton("Collapse All");
        buttonCollapseAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                toolbar_CollapseAll();
            }
        });
        this.toolbar.add(buttonCollapseAll);

        // Button: Details
        this.toolbarbtn_Details.setVisible(false);
        this.toolbarbtn_Details.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                toolbar_ShowDetails();
            }
        });
        //this.toolbar.addSeparator();
        this.toolbar.add(this.toolbarbtn_Details);

        // Button: Find
        //this.toolbar.add(new JButton("Find"));
    }

    private void toolbar_ExpandAll() {
        if (this.tree == null) {
            return;
        }

        int old = 0;
        int now = 0;
        do {
            old = this.tree.getRowCount();
            for (int i = 0; i < old; i++) {
                this.tree.expandRow(i);
            }
            now = this.tree.getRowCount();
        } while (now > old);
    }

    private void toolbar_CollapseAll() {
        if (this.tree == null) {
            return;
        }

        this.tree.collapseRow(0);
    }

    private void toolbar_ShowDetails() {
        JFrameTool.showPopup(this.topLevelFrame, this.details_panel, this.details_title);
    }

    private void tree_SelectionChanged(final TreeSelectionEvent e) {

        this.editorPaneDescription.setText("");
        this.toolbarbtn_Details.setVisible(false);
        this.details_panel = null;
        this.details_title = null;

        Object obj = e.getPath().getLastPathComponent();
        if (obj instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode objDmtn = (DefaultMutableTreeNode) obj;
            obj = objDmtn.getUserObject();
            if (obj instanceof JTreeNodeFileComponent) {
                JTreeNodeFileComponent fileComp = (JTreeNodeFileComponent) obj;
                String s = fileComp.getDescription();
                if (s != null && s.length() > 0) {
                    this.editorPaneDescription.setText(s);
                }
                if (fileComp.isDetailAvailable()) {
                    this.toolbarbtn_Details.setVisible(true);
                    this.details_panel = fileComp.getDetailPanel();
                    this.details_title = fileComp.getText();
                }
            }
        }
    }
}
