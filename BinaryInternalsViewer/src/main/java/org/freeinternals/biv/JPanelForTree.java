/*
 * JPanelForTree.java    April 05, 2009, 22:58
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.biv;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * A panel containing a {@code JTree} object.
 *
 * @author Amos Shi
 */
public final class JPanelForTree extends JPanel {

    private static final long serialVersionUID = 4876543219876500000L;
    /**
     * Default editor size.
     */
    private static final Dimension EDITOR_DEFAULT_SIZE = new Dimension(100, 120);
    /**
     * Top level window.
     */
    private final JFrame topLevelFrame;
    /**
     * Details panel.
     */
    private JPanel detailsPanel;
    /**
     * Title for details pane.
     */
    private String detailsTitle;
    /**
     * Tree component for the file components.
     */
    private final JTree tree;
    /**
     * Toolbar on the tree.
     */
    private final JToolBar toolbar;
    /**
     * Toolbar button for details.
     */
    private final JButton toolbarbtnDetails;
    /**
     * Editor pane at the right.
     */
    private JEditorPane editorPaneDescription;

    /**
     * Create a panel tool bar to contain a {@code JTree} object.
     *
     * @param jTree The tree to be contained
     * @param frame The parent window
     */
    @edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "We need it")
    public JPanelForTree(final JTree jTree, final JFrame frame) {
        if (jTree == null) {
            throw new IllegalArgumentException("[tree] cannot be null.");
        }

        this.tree = jTree;
        this.topLevelFrame = frame;
        this.tree.addTreeSelectionListener(this::treeSelectionChanged);

        this.toolbar = new JToolBar();
        this.toolbarbtnDetails = new JButton("Details");
        this.initToolbar();

        this.layoutComponents();
    }

    private void layoutComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        final JPanel panelToolbar = new JPanel();
        panelToolbar.setLayout(new BoxLayout(panelToolbar, BoxLayout.X_AXIS));
        panelToolbar.add(this.toolbar);

        final Component glue = Box.createGlue();
        glue.setMaximumSize(new Dimension(Short.MAX_VALUE, this.toolbar.getHeight()));
        panelToolbar.add(glue);

        final JPanel panelToolbarTree = new JPanel();
        panelToolbarTree.setLayout(new BoxLayout(panelToolbarTree, BoxLayout.Y_AXIS));
        panelToolbarTree.add(panelToolbar);
        panelToolbarTree.add(new JScrollPane(this.tree));

        this.editorPaneDescription = new JEditorPane();
        this.editorPaneDescription.setContentType("text/html");
        this.editorPaneDescription.setEditable(false);
        this.editorPaneDescription.setPreferredSize(EDITOR_DEFAULT_SIZE);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(panelToolbarTree);
        splitPane.setBottomComponent(new JScrollPane(this.editorPaneDescription));
        splitPane.setResizeWeight(1.0);
        this.add(splitPane, BorderLayout.CENTER);
    }

    private void initToolbar() {
        this.toolbar.setRollover(true);

        // Button: Expand All
        final JButton buttonExpandAll = new JButton("Expand All");
        buttonExpandAll.addActionListener((final ActionEvent e) -> toolbarExpandAll());
        this.toolbar.add(buttonExpandAll);

        // Button: Collapse All
        final JButton buttonCollapseAll = new JButton("Collapse All");
        buttonCollapseAll.addActionListener((final ActionEvent e) -> toolbarCollapseAll());
        this.toolbar.add(buttonCollapseAll);

        // Button: Details
        this.toolbarbtnDetails.setVisible(false);
        this.toolbarbtnDetails.addActionListener((final ActionEvent e) -> toolbarShowDetails());
        this.toolbar.add(this.toolbarbtnDetails);
    }

    private void toolbarExpandAll() {
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

    private void toolbarCollapseAll() {
        if (this.tree == null) {
            return;
        }

        this.tree.collapseRow(0);
    }

    private void toolbarShowDetails() {
        Main.showPopup(this.topLevelFrame, this.detailsPanel, this.detailsTitle);
    }

    private void treeSelectionChanged(final TreeSelectionEvent e) {

        this.editorPaneDescription.setText("");
        this.toolbarbtnDetails.setVisible(false);
        this.detailsPanel = null;
        this.detailsTitle = null;

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
                    this.toolbarbtnDetails.setVisible(true);
                    this.detailsPanel = fileComp.getDetailPanel();
                    this.detailsTitle = fileComp.getText();
                }
            }
        }
    }
}
