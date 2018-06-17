package org.freeinternals.format.pdf.basicobj;

import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;
import org.freeinternals.commonlib.core.FileComponent;
import org.freeinternals.commonlib.core.PosDataInputStream;
import org.freeinternals.commonlib.ui.GenerateTreeNode;
import org.freeinternals.commonlib.ui.JTreeNodeFileComponent;

/**
 * PDF basic object Name, see
 * <pre>PDF 32000-1:2008</pre>
 * <code>7.3.5</code>: Name Objects.
 *
 * @author Amos Shi
 */
public final class Boolean extends FileComponent implements GenerateTreeNode {

    public static String VALUE_TRUE = "true";
    public static String VALUE_FALSE = "false";
    /**
     * Name text in
     * <code>Raw</code> format.
     */
    private String RawText;

    Boolean(PosDataInputStream stream) throws IOException {
        super.startPos = stream.getPos();
        this.parse(stream);
        this.length = stream.getPos() - super.startPos;
        
        // System.out.println(this.toString());   // Deubg output
    }

    private void parse(PosDataInputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder(6);
        byte b1 = stream.readByte();
        builder.append((char) b1);
        if (b1 == StartByte.TRUE) {
            builder.append(stream.readASCII(VALUE_TRUE.length() - 1));
        } else {
            builder.append(stream.readASCII(VALUE_FALSE.length() - 1));
        }
        this.RawText = builder.toString();
    }

    public boolean getValue() {
        boolean result = false;

        if (VALUE_TRUE.equalsIgnoreCase(this.RawText)) {
            result = true;
        } else if (VALUE_FALSE.equalsIgnoreCase(this.RawText)) {
            result = false;
        } else {
            throw new IllegalArgumentException(String.format(
                    "Unrecognized boolean value. Value text = '%s', Position = %d (0x%X)",
                    this.RawText,
                    this.startPos,
                    this.startPos));
        }

        return result;
    }

    public void generateTreeNode(DefaultMutableTreeNode parentNode) {
        JTreeNodeFileComponent nodeComp = new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                "Boolean Object");
        DefaultMutableTreeNode nodeName = new DefaultMutableTreeNode(nodeComp);

        nodeName.add(new DefaultMutableTreeNode(new JTreeNodeFileComponent(
                super.startPos,
                super.length,
                this.RawText)));

        parentNode.add(nodeName);
    }

    @Override
    public String toString() {
        return String.format("Boolean Object: Start Position = %d, Length = %d, Raw Text = '%s'",
                super.startPos,
                super.length,
                this.RawText);
    }

    public static class StartByte {

        /**
         * Start byte for
         * <code>true</code>.
         */
        public static final byte TRUE = 't';
        /**
         * Start byte for
         * <code>false</code>.
         */
        public static final byte FALSE = 'f';
    }
}
