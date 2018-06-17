package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;
import org.freeinternals.format.pdf.PDFStatics;
import org.freeinternals.format.pdf.Texts;

/**
 * PDF basic object Array, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.6</code>: Array Objects.
 *
 * @author Amos Shi
 */
public class Array extends FileComponent implements GenerateTreeNode {

    /**
     * Component of current object.
     */
    private List<FileComponent> components = Collections.synchronizedList(new ArrayList<FileComponent>(31));

    Array(PosDataInputStream stream) throws IOException {
        // System.out.println("==== PDF Array");   // Deubg output
        super.startPos = stream.getPos();
        this.parse(stream);
        this.organizeArray();
        super.length = stream.getPos() - super.startPos;
    }

    private void parse(PosDataInputStream stream) throws IOException {
        // The '[' sign
        stream.skip(1);

        FileComponent comp;
        Analysis analysis = new Analysis();

        byte next1;
        while (stream.hasNext()) {
            comp = analysis.ParseNextObject(stream, this.components);
            if (comp == null) {
                next1 = stream.readByte();
                if (next1 == PDFStatics.DelimiterCharacter.RS) {
                    // Stop current Dictionary Object
                    // System.out.println("==== PDF Array -- Ends at " + stream.getPos());   // Deubg output
                    break;
                } else {
                    System.out.println(String.format("ERROR ======== Array.parse() - Location = %d (%X), Value = %s",
                            stream.getPos(), stream.getPos(), String.valueOf((char) next1)));
                }
            }
        } // End While
    }

    private void organizeArray() {
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Array Object");
        DefaultMutableTreeNode nodeArray = new DefaultMutableTreeNode(nodeComp);

        int pos = super.startPos;
        nodeArray.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.LS)));
        pos += 1;

        int contLen = super.length - 1 - 1;
        DefaultMutableTreeNode nodeContent;
        nodeArray.add(nodeContent = new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                contLen,
                "Array Content")));

        for (FileComponent comp : this.components) {
            if (comp instanceof GenerateTreeNode) {
                ((GenerateTreeNode) comp).generateTreeNode(nodeContent);
            }
        }

        pos += contLen;
        nodeArray.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                pos,
                1,
                Texts.Signature + (char) PDFStatics.DelimiterCharacter.RS)));

        parentNode.add(nodeArray);
    }
}
