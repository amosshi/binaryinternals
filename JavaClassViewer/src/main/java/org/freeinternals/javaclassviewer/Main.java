/*
 * Main.java    23:11, Apr 07, 2009
 *
 * Copyright 2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.freeinternals.commonlib.ui.JFrameTool;
import org.freeinternals.commonlib.ui.JPanelForTree;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.javaclassviewer.ui.JSplitPaneClassFile;
import org.freeinternals.javaclassviewer.ui.JTreeNodeZipFile;
import org.freeinternals.javaclassviewer.ui.JTreeZipFile;

/**
 *
 * @author Amos Shi
 * @since JDK 6.0
 */
public final class Main extends JFrame {

    private static final long serialVersionUID = 4876543219876500000L;
    private JTreeZipFile zftree;
    private JPanelForTree zftreeContainer;
    private JSplitPaneClassFile cfPane;

    private Main() {
        this.setTitle("Java Class Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JFrameTool.centerJFrame(this);
        this.createMenu();
        this.setVisible(true);
    }

    private void createMenu() {
        final JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        // File
        final JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menuFile);

        // File --> Open
        final JMenuItem menuItem_FileOpen = new JMenuItem("Open...", UIManager.getIcon("FileView.directoryIcon"));
        menuItem_FileOpen.setMnemonic(KeyEvent.VK_O);
        menuItem_FileOpen.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        menuItem_FileOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                menu_FileOpen();
            }
        });
        menuFile.add(menuItem_FileOpen);

        // File --> Close
        final JMenuItem menuItem_FileClose = new JMenuItem("Close");
        menuItem_FileClose.setMnemonic(KeyEvent.VK_C);
        menuItem_FileClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                menu_FileClose();
            }
        });
        menuFile.add(menuItem_FileClose);

        //
        //menuFile.addSeparator();
        // File --> Recent Files
        //JMenu menu_FileRecentFile = new JMenu("Recent Files");
        //menu_FileRecentFile.setMnemonic(KeyEvent.VK_R);
        //menuFile.add(menu_FileRecentFile);
        //
        menuFile.addSeparator();

        // File --> Exit
        final JMenuItem menuItem_FileExit = new JMenuItem("Exit", UIManager.getIcon("Table.ascendingSortIcon"));
        menuItem_FileExit.setMnemonic(KeyEvent.VK_X);
        menuItem_FileExit.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_X,
                ActionEvent.ALT_MASK));
        menuItem_FileExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                System.exit(0);
            }
        });
        menuFile.add(menuItem_FileExit);

        // Help
        final JMenu menuHelp = new JMenu("Help");
        menuFile.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menuHelp);

        // Help --> Homepage
        final JMenuItem menuItem_HelpHomepage = new JMenuItem("Homepage");
        menuItem_HelpHomepage.setMnemonic(KeyEvent.VK_P);
        menuItem_HelpHomepage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                menu_HelpHomepage();
            }
        });
        menuHelp.add(menuItem_HelpHomepage);

        // Help --> About
        final JMenuItem menuItem_HelpAbout = new JMenuItem("About");
        menuItem_HelpAbout.setMnemonic(KeyEvent.VK_A);
        menuItem_HelpAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                menu_HelpAbout();
            }
        });
        menuHelp.add(menuItem_HelpAbout);

    }

    private void menu_FileOpen() {
        final FileNameExtensionFilter filterClass = new FileNameExtensionFilter("Class File", "class");
        final FileNameExtensionFilter filterJar = new FileNameExtensionFilter("Jar File", "jar");
        final JFileChooser chooser = new JFileChooser();
        chooser.addChoosableFileFilter(filterJar);
        chooser.addChoosableFileFilter(filterClass);

        final int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File file = chooser.getSelectedFile();
            this.clearContent();
            if (file.getName().endsWith(".jar")) {
                this.open_JarFile(chooser.getSelectedFile());
            } else if (file.getName().endsWith(".class")) {
                this.open_ClassFile(file);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        String.format("Un-supported file type. Please select a '.jar' or '.class' file. \nFile: %s", file.getPath()),
                        this.getTitle(),
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void open_JarFile(final File file) {
        try {
            this.zftree = new JTreeZipFile(new JarFile(
                    file,
                    false,
                    JarFile.OPEN_READ));
            this.zftree.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent e) {
                    if (e.getClickCount() != 2) {
                        return;
                    }
                    if (zftree.getRowForLocation(e.getX(), e.getY()) == -1) {
                        return;
                    }

                    zftree_DoubleClick(zftree.getPathForLocation(e.getX(), e.getY()));
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    this,
                    ex.toString(),
                    this.getTitle(),
                    JOptionPane.ERROR_MESSAGE);
        }

        if (this.zftree != null) {
            this.zftreeContainer = new JPanelForTree(this.zftree, this);
            this.add(this.zftreeContainer, BorderLayout.CENTER);

            this.resizeForContent();
        }
    }

    private void open_ClassFile(final File file) {
        this.cfPane = new JSplitPaneClassFile(Tool.readFileAsBytes(file), this);
        this.add(this.cfPane, BorderLayout.CENTER);

        this.resizeForContent();
    }

    private void resizeForContent() {
        this.setSize(this.getWidth() + 2, this.getHeight());
        this.setSize(this.getWidth() - 2, this.getHeight());
    }

    private void menu_FileClose() {
        this.clearContent();
        this.setSize(this.getWidth() - 1, this.getHeight());
    }

    private void menu_HelpAbout() {
        final JDialogAbout about = new JDialogAbout(this, "About");
        about.setLocationRelativeTo(this);
        about.setVisible(true);
    }

    private void menu_HelpHomepage() {
        try {
            Desktop.getDesktop().browse(new URI("https://github.com/amosshi/freeinternals/"));
        } catch (URISyntaxException | IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    this.getTitle(),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearContent() {
        if (this.zftreeContainer != null) {
            this.remove(this.zftreeContainer);
            this.validate();
        }
        this.zftreeContainer = null;
        this.zftree = null;

        if (this.cfPane != null) {
            this.remove(this.cfPane);
            this.validate();
        }
        this.cfPane = null;
    }

    private void zftree_DoubleClick(final TreePath tp) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.zftree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        if (node.isLeaf() == false) {
            return;
        }

        final Object objLast = tp.getLastPathComponent();
        if (objLast == null) {
            return;
        }

        if (objLast.toString().endsWith(".class") == false) {
            return;
        }

        final Object[] objArray = tp.getPath();
        if (objArray.length < 2) {
            return;
        }

        final Object userObj = node.getUserObject();
        if (!(userObj instanceof JTreeNodeZipFile)) {
            return;
        }

        final ZipEntry ze = ((JTreeNodeZipFile) userObj).getNodeObject();
        if (ze == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Node Object [zip entry] is emtpy.",
                    this.getTitle(),
                    JOptionPane.WARNING_MESSAGE);
        } else {
            this.showClassWindow(ze);
        }
    }

    private void showClassWindow(final ZipEntry ze) {

        final byte b[];
        try {
            b = Tool.readZipEntryAsBytes(zftree.getZipFile(), ze);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(
                    this,
                    String.format("Read the class file failed.\n%s", ex.getMessage()),
                    this.getTitle(),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        final StringBuffer sbTitle = new StringBuffer();
        sbTitle.append(this.zftree.getZipFile().getName());
        sbTitle.append(" - ");
        sbTitle.append(ze.getName());

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JFrameClassFile(
                        sbTitle.toString(),
                        b,
                        Main.this).setVisible(true);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
