package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * PDF basic object Null, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.9</code>: Null Object.
 *
 * @author Amos Shi
 */
public final class Null extends FileComponent implements GenerateTreeNode {

    public static String VALUE = "null";
    /**
     * Name text in
     * <code>Raw</code> format.
     */
    private String RawText;

    Null(PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.parse(stream);
        this.length = stream.getPos() - super.startPos;

        // System.out.println(this.toString());   // Deubg output
    }

    private void parse(PosDataInputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder(6);
        byte b1 = stream.readByte();
        builder.append((char) b1);
        builder.append(stream.readASCII(VALUE.length() - 1));
        this.RawText = builder.toString();
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Null Object");
        DefaultMutableTreeNode nodeName = new DefaultMutableTreeNode(nodeComp);

        nodeName.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                this.RawText)));

        parentNode.add(nodeName);
    }

    @Override
    public String toString() {
        return String.format("Null Object: Start Position = %d (0x%X), Length = %d, Raw Text = '%s'",
                super.startPos,
                super.startPos,
                super.length,
                this.RawText);
    }

    public static class StartByte {

        /**
         * Start byte for
         * <code>null</code>.
         */
        public static final byte NULL = 'n';
    }
}
