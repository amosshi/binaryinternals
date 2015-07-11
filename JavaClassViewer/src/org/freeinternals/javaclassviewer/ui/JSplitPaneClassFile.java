/*
 * JSplitPaneClassFile.java    19:58, April 06, 2009
 *
 * Copyright  2009, FreeInternals.org. All rights reserved.
 * Use is subject to license terms.
 */
package org.freeinternals.javaclassviewer.ui;

import java.awt.Component;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.ui.HTMLKit;
import org.freeinternals.commonlib.ui.JBinaryViewer;
import org.freeinternals.commonlib.ui.JPanelForTree;
import org.freeinternals.commonlib.util.Tool;
import org.freeinternals.format.FileFormatException;
import org.freeinternals.format.classfile.CPInfo;
import org.freeinternals.format.classfile.ClassFile;
import org.freeinternals.format.classfile.FieldInfo;
import org.freeinternals.format.classfile.MethodInfo;
import org.freeinternals.format.classfile.Opcode;
import org.freeinternals.format.classfile.Opcode.InstructionResult;

/**
 * A split panel created from a class file byte array.
 *
 * @author Amos Shi
 * @since JDK 6.0
 * @see ClassFile
 */
public class JSplitPaneClassFile extends JSplitPane {

    private static final long serialVersionUID = 4876543219876500000L;
    private ClassFile classFile;
    private JBinaryViewer binaryViewer = null;
    private JScrollPane binaryViewerView = null;
    private JTextPane opcode = null;
    private JTextPane report = null;

    /**
     * Creates a split panel from a Java class file byte array.
     *
     * @param byteArray Java class file byte array
     * @param top Top level window
     */
    public JSplitPaneClassFile(final byte[] byteArray, JFrame top) {
        try {
            this.classFile = new ClassFile(byteArray.clone());
        } catch (IOException | FileFormatException ex) {
            Logger.getLogger(JSplitPaneClassFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.createAndShowGUI(top);
        this.generateClassReport();
    }

    private void createAndShowGUI(JFrame top) {

        // Construct class file viewer
        final JTreeClassFile jTreeClassFile = new JTreeClassFile(this.classFile);
        jTreeClassFile.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(final javax.swing.event.TreeSelectionEvent evt) {
                jTreeClassFileSelectionChanged(evt);
            }
        });
        final JPanelForTree panel = new JPanelForTree(jTreeClassFile, top);

        final JTabbedPane tabbedPane = new JTabbedPane();

        // Construct binary viewer
        this.binaryViewer = new JBinaryViewer();
        this.binaryViewer.setData(this.classFile.getClassByteArray());
        this.binaryViewerView = new JScrollPane(this.binaryViewer);
        this.binaryViewerView.getVerticalScrollBar().setValue(0);
        tabbedPane.add("Class File", this.binaryViewerView);

        // Construct opcode viewer
        this.opcode = new JTextPane();
        this.opcode.setAlignmentX(Component.LEFT_ALIGNMENT);
        // this.opcode.setFont(new Font(Font.DIALOG_INPUT, Font.PLAIN, 14));
        this.opcode.setEditable(false);
        this.opcode.setBorder(null);
        this.opcode.setContentType("text/html");
        tabbedPane.add("Opcode", new JScrollPane(this.opcode));

        // Class report
        this.report = new JTextPane();
        this.report.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.report.setEditable(false);
        this.report.setBorder(null);
        this.report.setContentType("text/html");
        tabbedPane.add("Report", new JScrollPane(this.report));

        this.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.setDividerSize(5);
        this.setDividerLocation(280);
        this.setLeftComponent(panel);
        this.setRightComponent(tabbedPane);

        this.binaryViewerView.getVerticalScrollBar().setValue(0);
    }

    private void jTreeClassFileSelectionChanged(final TreeSelectionEvent evt) {
        Object obj = evt.getPath().getLastPathComponent();
        if (obj instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode objDmtn = (DefaultMutableTreeNode) obj;
            obj = objDmtn.getUserObject();
            if (obj instanceof JTreeNodeClassComponent) {
                final JTreeNodeClassComponent objTncc = (JTreeNodeClassComponent) obj;

                // Select the bytes of this data
                this.binaryViewer.setSelection(objTncc.getStartPos(), objTncc.getLength());

                // clear opcode values;
                this.opcode.setText(null);
                // Get the code bytes
                if (JTreeAttribute.ATTRIBUTE_CODE_NODE.equals(objTncc.getText())) {
                    final byte[] data = this.classFile.getClassByteArray(objTncc.getStartPos(), objTncc.getLength());
                    this.generateOpcodeParseResult(data);
                }
            }
        }
    }

    private void generateOpcodeParseResult(byte[] opcodeData) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(HTMLKit.Start());

        int cpindexCounter = 0;

        // The Extracted Code
        sb.append("<pre>");
        sb.append(Tool.getByteDataHexView(opcodeData));
        sb.append('\n');
        List<Opcode.InstructionResult> codeResult = Opcode.parseCode(opcodeData, this.classFile);
        for (InstructionResult iResult : codeResult) {
            sb.append(iResult.toString());
            sb.append('\n');
            if (iResult.cpIndex1 > -1) {
                cpindexCounter++;
            }
        }
        sb.append("</pre>");

        // The Reference Object
        if (cpindexCounter > 0) {
            sb.append("<ol>");
            for (InstructionResult iResult : codeResult) {
                if (iResult.cpIndex1 > -1) {
                    sb.append(String.format("<li>%s</li>", HTMLKit.EscapeFilter(
                            this.classFile.getCPDescription(iResult.cpIndex1 ))));
                }
            }
            sb.append("</ol>");
        }

        sb.append(HTMLKit.End());
        this.opcode.setText(sb.toString());

    }

    private void generateClassReport() {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(HTMLKit.Start());

        int count;

        // Constant Pool
        count = this.classFile.constant_pool_count.getValue();
        sb.append(String.format("Constant Pool Count: %d", count));
        sb.append(HTMLKit.NewLine());
        if (count > 0) {
            CPInfo[] CPInfoList = this.classFile.getConstantPool();

            // Constant Pool - by Type
            sb.append("Constant Pool - Class");
            this.generateCPTypeReport(sb, CPInfoList, count, CPInfo.ConstantType.CONSTANT_Class.tag);
            sb.append("Constant Pool - Field");
            this.generateCPTypeReport(sb, CPInfoList, count, CPInfo.ConstantType.CONSTANT_Fieldref.tag);
            sb.append("Constant Pool - Method");
            this.generateCPTypeReport(sb, CPInfoList, count, CPInfo.ConstantType.CONSTANT_Methodref.tag);

            // Constant Pool Object List
            sb.append("Constant Pool Object List");
            sb.append(HTMLKit.NewLine());
            sb.append("<ol>");
            for (int i = 1; i < count; i++) {
                sb.append(String.format("<li>%s</li>", HTMLKit.EscapeFilter(this.classFile.getCPDescription(i))));
            }
            sb.append("</ol>");
        }

        // Fields
        count = this.classFile.fields_count.getValue();
        sb.append(String.format("Field Count: %d", count));
        sb.append(HTMLKit.NewLine());
        if (count > 0) {
            FieldInfo[] fields = this.classFile.getFields();
            sb.append("<ol>");
            for (FieldInfo field : fields) {
                sb.append(String.format("<li>%s</li>", HTMLKit.EscapeFilter(field.getDeclaration())));
            }
            sb.append("</ol>");
        }
        sb.append(HTMLKit.NewLine());

        // Methods
        count = this.classFile.methods_count.getValue();
        sb.append(String.format("Method Count: %d", count));
        sb.append(HTMLKit.NewLine());
        if (count > 0) {
            MethodInfo[] methods = this.classFile.getMethods();
            sb.append("<ol>");
            for (MethodInfo method : methods) {
                sb.append(String.format("<li>%s</li>", HTMLKit.EscapeFilter(method.getDeclaration())));
            }
            sb.append("</ol>");
        }
        sb.append(HTMLKit.NewLine());

        sb.append(HTMLKit.End());
        this.report.setText(sb.toString());
    }

    private void generateCPTypeReport(StringBuilder sb, CPInfo[] CPInfoList, int count, short tag) {
        sb.append(HTMLKit.NewLine());
        sb.append("<ul>");
        for (int i = 1; i < count; i++) {
            if (CPInfoList[i] != null && CPInfoList[i].tag.value == tag) {
                sb.append(String.format("<li>%d. %s</li>", i,
                        HTMLKit.EscapeFilter(this.classFile.getCPDescription(i))));
            }
        }
        sb.append("</ul>");
    }
}
